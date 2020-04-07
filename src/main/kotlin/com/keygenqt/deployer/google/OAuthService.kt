/*
 * Copyright 2020 Vitaliy Zarubin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.keygenqt.deployer.google

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.keygenqt.deployer.base.Configuration
import com.keygenqt.deployer.base.Info
import com.keygenqt.deployer.base.retrofit.RetrofitBuilder
import com.keygenqt.deployer.base.retrofit.RetrofitQuery
import com.keygenqt.deployer.models.ModelSettings
import com.keygenqt.deployer.models.ModelUser
import com.keygenqt.deployer.utils.OAUTH_URL
import com.keygenqt.deployer.utils.USER_INFO
import org.json.JSONObject
import java.util.*

class OAuthService {
    companion object {
        fun oauthRefreshToken(model: ModelSettings, listener: (status: Boolean) -> Unit) {
            if (model.refresh_token != "") {
                if (model.time + model.expires_in <= (System.currentTimeMillis() / 1000).toInt()) {
                    RetrofitBuilder.build()
                        .create(RetrofitQuery::class.java)
                        .oauth2RefreshToken(
                            OAUTH_URL,
                            "refresh_token",
                            model.refresh_token,
                            Configuration.getGoogleOauthClientId(),
                            Configuration.getGoogleOauthClientSecret()
                        )
                        .subscribe({ any ->
                            try {
                                val value = (Gson()).toJsonTree(any as LinkedTreeMap<*, *>).asJsonObject
                                val params = JSONObject(value.toString())
                                model.access_token = params.getString("access_token")
                                model.expires_in = params.getInt("expires_in")
                                model.scope = params.getString("scope")
                                model.token_type = params.getString("token_type")
                                model.id_token = params.getString("id_token")
                                model.time = (System.currentTimeMillis() / 1000).toInt()
                                model.save()
                                listener.invoke(true)
                                Info.refreshTokenSuccessful()
                            } catch (ex: Exception) {
                                listener.invoke(false)
                            }
                        }, { throwable ->
                            Info.error("ERROR (oauthRefreshToken): ${throwable.message}")
                            listener.invoke(false)
                        })
                } else {
                    listener.invoke(true)
                }
            } else {
                listener.invoke(false)
            }
        }

        fun oauthAuthorizationCode(code: String) {
            val settings = ModelSettings()
            settings.code = code
            settings.save()

            RetrofitBuilder.build()
                .create(RetrofitQuery::class.java)
                .oauth2(
                    OAUTH_URL,
                    "authorization_code",
                    code,
                    Configuration.getGoogleOauthClientId(),
                    Configuration.getGoogleOauthClientSecret(),
                    Configuration.getGoogleOauthRedirectUri()
                )
                .subscribe({ any ->

                    val value = (Gson()).toJsonTree(any as LinkedTreeMap<*, *>).asJsonObject
                    val params = JSONObject(value.toString())

                    settings.access_token = params.getString("access_token")
                    settings.refresh_token = params.getString("refresh_token")
                    settings.expires_in = params.getInt("expires_in")
                    settings.scope = params.getString("scope")
                    settings.token_type = params.getString("token_type")
                    settings.id_token = params.getString("id_token")
                    settings.time = (System.currentTimeMillis() / 1000).toInt()

                    settings.save()

                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            getUser(settings)
                        }
                    }, 100)

                }, { throwable ->
                    println("ERROR (oauthAuthorizationCode): ${throwable.message}")
                    ModelSettings.clear()
                })
        }

        fun getUser(settings: ModelSettings) {
            RetrofitBuilder.build(settings)
                .create(RetrofitQuery::class.java)
                .getUser(USER_INFO)
                .subscribe({ any ->

                    val value = (Gson()).toJsonTree(any as LinkedTreeMap<*, *>).asJsonObject
                    val params = JSONObject(value.toString())
                    val user = ModelUser.findByEmail(params.getString("email")) ?: ModelUser()

                    user.id = params.getString("id")
                    user.email = params.getString("email")
                    user.verified_email =
                        if (params.has("verified_email")) params.getBoolean("verified_email") else false
                    user.name = if (params.has("name")) params.getString("name") else ""
                    user.given_name = if (params.has("given_name")) params.getString("given_name") else ""
                    user.family_name = if (params.has("family_name")) params.getString("family_name") else ""
                    user.link = if (params.has("link")) params.getString("link") else ""
                    user.picture = if (params.has("picture")) params.getString("picture") else ""
                    user.gender = if (params.has("gender")) params.getString("gender") else ""
                    user.locale = if (params.has("locale")) params.getString("locale") else ""
                    user.hd = if (params.has("hd")) params.getString("hd") else ""

                    user.save()
                    settings.user_id = user.id
                    settings.save()

                }, { throwable ->
                    println("ERROR (getUser): ${throwable.message}")
                    ModelSettings.clear()
                })
        }

        fun getSettingsByEmail(email: String): ModelSettings? {
            val user = ModelUser.findByEmail(email)
            if (user != null) {
                return ModelSettings.findByUserId(user.id)
            }
            return null
        }

        fun getSettingsById(userId: String): ModelSettings? {
            return ModelSettings.findByUserId(userId)
        }
    }
}