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

package com.keygenqt.deployer.base

import com.keygenqt.deployer.base.retrofit.RetrofitBuilder
import com.keygenqt.deployer.base.retrofit.RetrofitQuery
import com.keygenqt.deployer.components.SingleTemplate
import com.keygenqt.deployer.google.GmailService
import com.keygenqt.deployer.google.OAuthService
import com.keygenqt.deployer.models.ModelSettings
import com.keygenqt.deployer.utils.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.thymeleaf.context.Context
import java.text.SimpleDateFormat
import java.util.*

class Info {
    companion object {

        fun error(text: String) {
            println(text)
            exit()
        }

        fun errorGetApplicationId() {
            println(ERROR_GET_APPLICATION_ID)
            exit()
        }

        fun errorGetVersionCode() {
            println(ERROR_GET_VERSION_CODE)
            exit()
        }

        fun errorGetVersionName() {
            println(ERROR_GET_VERSION_NAME)
            exit()
        }

        fun errorUpVersionCode() {
            println(ERROR_UP_VERSION_CODE)
            exit()
        }

        fun errorUpVersionName() {
            println(ERROR_UP_VERSION_NAME)
            exit()
        }

        fun errorGmailService() {
            println(ERROR_GMAIL_SERVICE)
            exit()
        }

        fun setRequiredParams() {
            println(SET_REQUIRED_PARAMS)
            exit()
        }

        fun startUpload() {
            println(START_UPLOAD)
        }

        fun refreshTokenSuccessful() {
            println(REFRESH_TOKEN_SUCCESSFUL)
        }

        fun getProjectIdSuccessful() {
            println(GET_PROJECT_ID_SUCCESSFUL)
        }

        fun uploadFileSuccessful() {
            println(UPLOAD_FILE_SUCCESSFUL)
        }

        fun updateInfoSuccessful() {
            println(UPDATE_INFO_SUCCESSFUL)
        }

        fun commitSuccessful() {
            println(COMMIT_SUCCESSFUL)
        }

        fun settingsNotFound() {
            println(NOT_FOUND_SETTINGS)
            exit()
        }

        fun selectUser() {
            println(SELECT_USER)
            exit()
        }

        fun userNotFound(email: String) {
            println(NOT_FOUND_USER.replace("{email}", email))
            exit()
        }

        fun notFoundFileBuild(path: String) {
            println(NOT_FOUND_FILE_BUILD.replace("{path}", path))
            exit()
        }

        fun successGenerateChangelog() {
            println(CHANGELOG_CREATE_SUCCESS)
        }

        fun errorGenerateChangelog() {
            println(CHANGELOG_NOT_CREATE)
        }

        fun errorTempDir() {
            println(ERROR_TEMP_DIR)
            exit()
        }

        fun showHelp() {
            println(STRING_FULL_HELP)
            exit()
        }

        fun showVersion() {
            println("Deployer Ver $VERSION")
            exit()
        }

        fun sendMailingUpload(
            settings: ModelSettings,
            projectName: String,
            applicationId: String,
            uploadTrack: String,
            versionCode: String,
            versionName: String
        ) {
            val context = Context(Locale("US"))
            context.setVariable("versionCode", versionCode)
            context.setVariable("versionName", versionName)
            context.setVariable("projectName", projectName)
            context.setVariable("applicationId", applicationId)
            context.setVariable("date", SimpleDateFormat(Configuration.getChangelogDateFormatGenerate()).format(Date()))

            val content = SingleTemplate.getTemplateResource("templates/mailing.html", context)

            GmailService.build(settings)?.let { service ->
                val listEmails =
                    if (uploadTrack == "production") Configuration.getEmailSendProd() else Configuration.getEmailSendTest()
                for (to in listEmails) {
                    service.sendMessage(
                        settings,
                        to,
                        YOUR_UPDATE_IS_LIVE.replace("{type}", "($uploadTrack)"),
                        content
                    )
                }
                exit()
            } ?: run {
                errorGmailService()
            }
        }

        fun sendSlackWebhook(
            projectName: String,
            applicationId: String,
            uploadTrack: String,
            versionCode: String,
            versionName: String
        ) {

            val listUrl =
                if (uploadTrack == "production") Configuration.getSlackWebhookSendProd() else Configuration.getSlackWebhookSendTest()
            for (url in listUrl) {

                val json = JSONObject()
                val blocks = JSONArray()
                val block = JSONObject()
                val text = JSONObject()
                text.put("type", "mrkdwn")
                text.put(
                    "text", """
<https://play.google.com/store/apps/details?id=$applicationId|$projectName>  Google Play *"Pending publication"*

```versionCode: $versionCode
versionName "$versionName"```
"""
                )
                block.put("type", "section")
                block.put("text", text)
                blocks.put(block)
                json.put("blocks", blocks)

                RetrofitBuilder.build()
                    .create(RetrofitQuery::class.java)
                    .slackWebhooks(
                        url,
                        json.toString().toRequestBody("application/json".toMediaTypeOrNull())
                    )
                    .subscribe({}, {})
            }
        }

        fun sendCheck(settings: ModelSettings, userId: String, email: String) {
            val content =
                SingleTemplate.getTemplateResource("templates/mail_test.html", Context(Locale("US")))
            GmailService.build(OAuthService.getSettingsById(userId))
                ?.sendMessage(settings, email, YOUR_TOKEN_CHECKED, content)
        }
    }
}