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
import com.keygenqt.deployer.PARAMS
import com.keygenqt.deployer.base.Info
import com.keygenqt.deployer.base.retrofit.RetrofitBuilder
import com.keygenqt.deployer.base.retrofit.RetrofitQuery
import com.keygenqt.deployer.models.ModelSettings
import com.keygenqt.deployer.models.ModelUser
import com.keygenqt.deployer.utils.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class GooglePlayUpload {
    companion object {

        fun upload(
            path: String,
            applicationId: String,
            versionCode: String,
            versionName: String,
            uploadTrack: String,
            user: ModelUser
        ) {

            Info.startUpload()

            val build = File(path)
            val typeFile = if (build.absolutePath.contains("bundle")) "bundles" else "apks"

            var text =
                if ("${PARAMS[ARGS_NOTE_ADD]}" == "false") "" else "${PARAMS[ARGS_NOTE_ADD]}".replace("<br>", "\n")

            if ("${PARAMS[ARGS_NOTE_ADD_VERSION]}" == "true") {
                text = "versionCode: $versionCode\n$text"
            }

            if (!build.exists() || build.isDirectory) {
                Info.notFoundFileBuild(build.absolutePath)
            } else {
                ModelSettings.findByUserId(user.id)?.let { settings ->
                    OAuthService.oauthRefreshToken(settings) {
                            queryGetId(settings, applicationId) { id ->
                                Info.getProjectIdSuccessful()
                                queryUploadFile(settings, applicationId, typeFile, build, id) {
                                    Info.uploadFileSuccessful()
                                    queryUpdateInfo(
                                        settings,
                                        applicationId,
                                        versionCode,
                                        versionName,
                                        uploadTrack,
                                        id,
                                        text
                                    ) {
                                        Info.updateInfoSuccessful()

                                        if ("${PARAMS[ARGS_MAILING]}" == "true"
                                            || "${PARAMS[ARGS_MAILING_GMAIL]}" == "true"
                                            || "${PARAMS[ARGS_MAILING_SLACK]}" == "true"
                                        ) {
                                            queryProjectInfo(settings, applicationId, id) { title ->

                                                if ("${PARAMS[ARGS_MAILING]}" == "true" || "${PARAMS[ARGS_MAILING_SLACK]}" == "true") {
                                                    Info.sendSlackWebhook(
                                                        title,
                                                        applicationId,
                                                        uploadTrack,
                                                        versionCode,
                                                        versionName,
                                                        "${PARAMS[ARGS_MAILING_SLACK_DESC]}",
                                                        user.email
                                                    )
                                                }

                                                if ("${PARAMS[ARGS_MAILING]}" == "true" || "${PARAMS[ARGS_MAILING_GMAIL]}" == "true") {
                                                    queryUploadCommit(settings, applicationId, id) {
                                                        Info.commitSuccessful()
                                                        Info.sendMailingUpload(
                                                            settings, title, applicationId, uploadTrack,
                                                            versionCode,
                                                            versionName
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            queryUploadCommit(settings, applicationId, id) {
                                                Info.commitSuccessful()
                                                exit()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } ?: run {
                        Info.settingsNotFound()
                    }

            }
        }

        private fun queryGetId(
            settings: ModelSettings,
            applicationId: String,
            listener: (id: String) -> Unit
        ) {
            RetrofitBuilder.build(settings)
                .create(RetrofitQuery::class.java)
                .getAppId(applicationId)
                .subscribe({ any ->
                    val value = (Gson()).toJsonTree(any as LinkedTreeMap<*, *>).asJsonObject
                    val params = JSONObject(value.toString())
                    if ("${PARAMS[ARGS_DEBUG]}" == "true") {
                        println(params)
                    }
                    listener.invoke(params.getString("id"))
                }, { throwable ->
                    Info.error("ERROR (GetId): ${throwable.message}")
                })
        }

        private fun queryUploadFile(
            settings: ModelSettings,
            applicationId: String,
            typeFile: String,
            build: File,
            id: String,
            listener: () -> Unit
        ) {
            RetrofitBuilder.build(settings)
                .create(RetrofitQuery::class.java)
                .googlePlayUpload(
                    applicationId = applicationId,
                    id = id,
                    type = typeFile,
                    build = build.asRequestBody("application/octet-stream".toMediaTypeOrNull())
                )
                .subscribe({ any ->
                    val value = (Gson()).toJsonTree(any as LinkedTreeMap<*, *>).asJsonObject
                    val params = JSONObject(value.toString())
                    if ("${PARAMS[ARGS_DEBUG]}" == "true") {
                        println(params)
                    }
                    listener.invoke()
                }, { throwable ->
                    Info.error("ERROR (UploadFile): ${throwable.message}")
                })
        }

        private fun queryUploadCommit(
            settings: ModelSettings,
            applicationId: String,
            id: String,
            listener: () -> Unit
        ) {
            RetrofitBuilder.build(settings)
                .create(RetrofitQuery::class.java)
                .googlePlayUploadCommit(
                    applicationId = applicationId,
                    id = id
                )
                .subscribe({ any ->
                    val value = (Gson()).toJsonTree(any as LinkedTreeMap<*, *>).asJsonObject
                    val params = JSONObject(value.toString())
                    if ("${PARAMS[ARGS_DEBUG]}" == "true") {
                        println(params)
                    }
                    listener.invoke()
                }, { throwable ->
                    Info.error("ERROR (UploadCommit): ${throwable.message}")
                })
        }

        private fun queryUpdateInfo(
            settings: ModelSettings,
            applicationId: String,
            versionCode: String,
            versionName: String,
            uploadTrack: String,
            id: String,
            text: String,
            listener: () -> Unit
        ) {
            RetrofitBuilder.build(settings)
                .create(RetrofitQuery::class.java)
                .googlePlayUpdateInfo(
                    applicationId = applicationId,
                    id = id,
                    typeTrack = uploadTrack,
                    info = generateInfo(
                        versionCode,
                        versionName,
                        uploadTrack,
                        text
                    ).toString().toRequestBody("application/json".toMediaTypeOrNull())
                )
                .subscribe({ any ->
                    val value = (Gson()).toJsonTree(any as LinkedTreeMap<*, *>).asJsonObject
                    val params = JSONObject(value.toString())
                    if ("${PARAMS[ARGS_DEBUG]}" == "true") {
                        println(params)
                    }
                    listener.invoke()
                }, { throwable ->
                    Info.error("ERROR (UpdateInfo): ${throwable.message}")
                })
        }

        private fun generateInfo(
            versionCode: String,
            versionName: String,
            uploadTrack: String,
            text: String
        ): JSONObject {
            val info = JSONObject()
            val releases = JSONArray()
            val release = JSONObject()
            val versionCodes = JSONArray()
            val releaseNotes = JSONArray()
            val releaseNote = JSONObject()

            releaseNote.put("language", "en-US")
            releaseNote.put("text", text)
            releaseNotes.put(releaseNote)

            versionCodes.put(versionCode)

            release.put("name", versionName)
            release.put("status", "completed")
            release.put("versionCodes", versionCodes)
            release.put("releaseNotes", releaseNotes)

            releases.put(release)

            info.put("track", uploadTrack)
            info.put("releases", releases)

            return info
        }

        private fun queryProjectInfo(
            settings: ModelSettings,
            applicationId: String,
            id: String,
            listener: (title: String) -> Unit
        ) {
            RetrofitBuilder.build(settings)
                .create(RetrofitQuery::class.java)
                .queryProjectInfo(applicationId, id)
                .subscribe({ any ->
                    val value = (Gson()).toJsonTree(any as LinkedTreeMap<*, *>).asJsonObject
                    val params = JSONObject(value.toString())
                    if ("${PARAMS[ARGS_DEBUG]}" == "true") {
                        println(params)
                    }
                    listener.invoke(params.getString("title"))
                }, { throwable ->
                    Info.error("ERROR (ProjectInfo): ${throwable.message}")
                })
        }
    }
}