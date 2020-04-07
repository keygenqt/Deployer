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

import com.keygenqt.deployer.utils.PATH_APP_CONFIG
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class Configuration {
    companion object {

        private var GOOGLE_OAUTH_CLIENT_ID = "Google OAuth Client ID"
        private var GOOGLE_OAUTH_CLIENT_SECRET = "Google OAuth Client Secret"
        private var GOOGLE_OAUTH_REDIRECT_URI = "Google OAuth Redirect Uri"
        private var EMAIL_SEND_PROD = "Email send prod"
        private var EMAIL_SEND_TEST = "Email send test"
        private var SLACK_WEBHOOK_URL_SEND_PROD = "Slack Webhook URL send prod"
        private var SLACK_WEBHOOK_URL_SEND_TEST = "Slack Webhook URL send test"
        private var CHANGELOG_DATE_FORMAT = "Changelog Date Format"
        private var CHANGELOG_DATE_FORMAT_GENERATE = "Changelog Date Format Generate"
        private var CHANGELOG_TYPES_LOG_GREP = "Changelog Types Log Grep"
        private var CHANGELOG_ORDER_TYPES = "Changelog Order Types"
        private var CHANGELOG_REGEX_ID_TASK = "Changelog Regex id task"
        private var CHANGELOG_REGEX_ID_TYPE = "Changelog Regex id type"

        private var PARAMS = hashMapOf(
            GOOGLE_OAUTH_CLIENT_ID to "",
            GOOGLE_OAUTH_CLIENT_SECRET to "",
            GOOGLE_OAUTH_REDIRECT_URI to "",

            EMAIL_SEND_PROD to "[]",
            EMAIL_SEND_TEST to "[]",

            SLACK_WEBHOOK_URL_SEND_PROD to "[]",
            SLACK_WEBHOOK_URL_SEND_TEST to "[]",

            CHANGELOG_DATE_FORMAT to "dd/MM/yy hh:mm a",
            CHANGELOG_DATE_FORMAT_GENERATE to "dd/MM/yy hh:mm a",
            CHANGELOG_TYPES_LOG_GREP to "\\[Feature\\]|\\[Bug\\]|\\[Change\\]",
            CHANGELOG_ORDER_TYPES to "[]",
            CHANGELOG_REGEX_ID_TASK to "^(\\w+\\-\\d+).*",
            CHANGELOG_REGEX_ID_TYPE to ".*(\\[\\w+\\]).*"
        )

        private fun checkParam(key: String): String {
            val f = File(PATH_APP_CONFIG)
            if (f.exists() && f.isFile) {
                try {
                    val params = JSONObject(BufferedReader(FileReader(f)).readText())
                    if (params.has(key)) {
                        when (val value = params.get(key)) {
                            is String -> {
                                PARAMS[key] = value
                            }
                            is JSONObject -> {
                                PARAMS[key] = value.toString()
                            }
                            is JSONArray -> {
                                PARAMS[key] = value.toString()
                            }
                        }
                    }
                } catch (ex: JSONException) {
                    println("Error parse config $key")
                    println(ex)
                }
            }
            return PARAMS[key] ?: ""
        }

        fun getGoogleOauthClientId(): String {
            return checkParam(GOOGLE_OAUTH_CLIENT_ID)
        }

        fun getGoogleOauthClientSecret(): String {
            return checkParam(GOOGLE_OAUTH_CLIENT_SECRET)
        }

        fun getChangelogDateFormat(): String {
            return checkParam(CHANGELOG_DATE_FORMAT)
        }

        fun getChangelogDateFormatGenerate(): String {
            return checkParam(CHANGELOG_DATE_FORMAT_GENERATE)
        }

        fun getChangelogTypesLogGrep(): String {
            return checkParam(CHANGELOG_TYPES_LOG_GREP)
        }

        fun getChangelogRegexIdTask(): String {
            return checkParam(CHANGELOG_REGEX_ID_TASK)
        }

        fun getChangelogRegexIdType(): String {
            return checkParam(CHANGELOG_REGEX_ID_TYPE)
        }

        fun getChangelogOrderTypes(): ArrayList<String> {
            val result = arrayListOf<String>()
            val value = checkParam(CHANGELOG_ORDER_TYPES)
            try {
                val emails = JSONArray(value)
                for (i in 0 until emails.length()) {
                    result.add(emails.getString(i))
                }
            } catch (ex: Exception) {
            }
            return result
        }

        fun getEmailSendProd(): ArrayList<String> {
            val result = arrayListOf<String>()
            val value = checkParam(EMAIL_SEND_PROD)
            try {
                val emails = JSONArray(value)
                for (i in 0 until emails.length()) {
                    result.add(emails.getString(i))
                }
            } catch (ex: Exception) {
            }
            return result
        }

        fun getEmailSendTest(): ArrayList<String> {
            val result = arrayListOf<String>()
            val value = checkParam(EMAIL_SEND_TEST)
            try {
                val emails = JSONArray(value)
                for (i in 0 until emails.length()) {
                    result.add(emails.getString(i))
                }
            } catch (ex: Exception) {
            }
            return result
        }

        fun getSlackWebhookSendProd(): ArrayList<String> {
            val result = arrayListOf<String>()
            val value = checkParam(SLACK_WEBHOOK_URL_SEND_PROD)
            try {
                val emails = JSONArray(value)
                for (i in 0 until emails.length()) {
                    result.add(emails.getString(i))
                }
            } catch (ex: Exception) {
            }
            return result
        }

        fun getSlackWebhookSendTest(): ArrayList<String> {
            val result = arrayListOf<String>()
            val value = checkParam(SLACK_WEBHOOK_URL_SEND_TEST)
            try {
                val emails = JSONArray(value)
                for (i in 0 until emails.length()) {
                    result.add(emails.getString(i))
                }
            } catch (ex: Exception) {
            }
            return result
        }

        fun getGoogleOauthRedirectUri(): String {
            return checkParam(GOOGLE_OAUTH_REDIRECT_URI)
        }
    }
}