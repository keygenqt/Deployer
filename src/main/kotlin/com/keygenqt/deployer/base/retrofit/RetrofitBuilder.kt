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

package com.keygenqt.deployer.base.retrofit

import com.google.gson.GsonBuilder
import com.keygenqt.deployer.PARAMS
import com.keygenqt.deployer.models.ModelSettings
import com.keygenqt.deployer.utils.ARGS_DEBUG
import com.keygenqt.deployer.utils.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitBuilder {
    companion object {

        fun build(settings: ModelSettings = ModelSettings()): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getClient(settings))
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
        }

        private fun getClient(settings: ModelSettings): OkHttpClient {
            val builder = OkHttpClient.Builder()

            builder.connectTimeout(40, TimeUnit.SECONDS)
            builder.writeTimeout(40, TimeUnit.SECONDS)
            builder.readTimeout(40, TimeUnit.SECONDS)

            builder.addInterceptor(getLoggingInterceptor())

            if (settings.token_type.isNotEmpty()) {
                builder.addInterceptor { chain ->
                    val original: Request = chain.request()
                    val request: Request = original.newBuilder()
                        .header("Authorization", "${settings.token_type} ${settings.access_token}")
                        .header("Content-Type", "application/json")
                        .method(original.method, original.body)
                        .build()
                    chain.proceed(request)
                }
            }
            return builder.build()
        }

        private fun getLoggingInterceptor(): HttpLoggingInterceptor {
            val interceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    if ("${PARAMS[ARGS_DEBUG]}" == "true") {
                        println(message)
                    } else {
                        try {
                            val jsonObject = JSONObject(message.trim())
                            if (jsonObject.has("error")) {
                                println(jsonObject.getJSONObject("error").getString("message"))
                            }
                        } catch (ex: JSONException) {
                            if ("${PARAMS[ARGS_DEBUG]}" == "true") {
                                println(message)
                            }
                        }
                    }
                }
            })
            interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
            return interceptor
        }
    }
}