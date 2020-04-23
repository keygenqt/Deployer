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

package com.keygenqt.deployer

import com.keygenqt.deployer.base.Checker
import com.keygenqt.deployer.base.ConnectDb
import com.keygenqt.deployer.base.Info
import com.keygenqt.deployer.components.Changelog
import com.keygenqt.deployer.components.HelperFile
import com.keygenqt.deployer.google.GooglePlayUpload
import com.keygenqt.deployer.models.ModelUser
import com.keygenqt.deployer.utils.*
import com.keygenqt.deployer.web.OauthController
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import java.io.File

val PARAMS = hashMapOf(
    ARGS_PATH to File("").absolutePath,

    ARGS_PATH_BUILD to "false",
    ARGS_UPLOAD_TRACK to "false",
    ARGS_NOTE_ADD to "false",
    ARGS_NOTE_ADD_VERSION to "false",
    ARGS_USER_EMAIL to "false",

    ARGS_APPLICATION_ID to "false",
    ARGS_GET_VERSION_CODE to "false",
    ARGS_GET_VERSION_NAME to "false",
    ARGS_VERSION_CODE_UP to "false",
    ARGS_VERSION_NAME_UP to "false",
    ARGS_GET_VERSION_CODE_UP to "false",
    ARGS_GET_VERSION_NAME_UP to "false",

    ARGS_MAILING to "false",
    ARGS_MAILING_GMAIL to "false",
    ARGS_MAILING_SLACK to "false",
    ARGS_MAILING_SLACK_DESC to "false",

    ARGS_SERVER to "false",
    ARGS_CHANGELOG to "false",
    ARGS_VERSION to "false",
    ARGS_HELP to "false",
    ARGS_DEBUG to "false"
)

@SpringBootApplication
@ComponentScan(basePackageClasses = [OauthController::class])
open class StartWebApplication

fun main(args: Array<String>) {

    Checker.tempDir()
    ConnectDb.open(MODELS)

    if (args.isEmpty()) {
        Info.showHelp()
    }

    for (item in args) {
        when (item) {
            ARGS_SERVER -> PARAMS[ARGS_SERVER] = "true"
            ARGS_NOTE_ADD_VERSION -> PARAMS[ARGS_NOTE_ADD_VERSION] = "true"
            ARGS_APPLICATION_NAME -> PARAMS[ARGS_APPLICATION_NAME] = "true"
            ARGS_APPLICATION_ID -> PARAMS[ARGS_APPLICATION_ID] = "true"
            ARGS_GET_VERSION_CODE -> PARAMS[ARGS_GET_VERSION_CODE] = "true"
            ARGS_GET_VERSION_NAME -> PARAMS[ARGS_GET_VERSION_NAME] = "true"
            ARGS_VERSION_CODE_UP -> PARAMS[ARGS_VERSION_CODE_UP] = "true"
            ARGS_VERSION_NAME_UP -> PARAMS[ARGS_VERSION_NAME_UP] = "true"
            ARGS_GET_VERSION_CODE_UP -> PARAMS[ARGS_GET_VERSION_CODE_UP] = "true"
            ARGS_GET_VERSION_NAME_UP -> PARAMS[ARGS_GET_VERSION_NAME_UP] = "true"
            ARGS_MAILING -> PARAMS[ARGS_MAILING] = "true"
            ARGS_MAILING_GMAIL -> PARAMS[ARGS_MAILING_GMAIL] = "true"
            ARGS_MAILING_SLACK -> PARAMS[ARGS_MAILING_SLACK] = "true"
            ARGS_CHANGELOG -> PARAMS[ARGS_CHANGELOG] = "true"
            ARGS_VERSION -> Info.showVersion()
            ARGS_HELP -> Info.showHelp()
            ARGS_DEBUG -> PARAMS[ARGS_DEBUG] = "true"
            else -> {
                when {
                    item.contains("^$ARGS_PATH\\=.+".toRegex()) -> {
                        PARAMS[ARGS_PATH] = item.replace("$ARGS_PATH=", "")
                    }
                    item.contains("^$ARGS_PATH_BUILD\\=.+".toRegex()) -> {
                        PARAMS[ARGS_PATH_BUILD] = item.replace("$ARGS_PATH_BUILD=", "")
                    }
                    item.contains("^$ARGS_UPLOAD_TRACK\\=.+".toRegex()) -> {
                        PARAMS[ARGS_UPLOAD_TRACK] = item.replace("$ARGS_UPLOAD_TRACK=", "")
                    }
                    item.contains("^$ARGS_NOTE_ADD\\=.+".toRegex()) -> {
                        PARAMS[ARGS_NOTE_ADD] = item.replace("$ARGS_NOTE_ADD=", "")
                    }
                    item.contains("^$ARGS_USER_EMAIL\\=.+".toRegex()) -> {
                        PARAMS[ARGS_USER_EMAIL] = item.replace("$ARGS_USER_EMAIL=", "")
                    }
                    item.contains("^$ARGS_MAILING_SLACK_DESC\\=.+".toRegex()) -> {
                        PARAMS[ARGS_MAILING_SLACK_DESC] = item.replace("$ARGS_MAILING_SLACK_DESC=", "")
                    }
                }
            }
        }
    }

    if ("${PARAMS[ARGS_PATH]}" == File("").absolutePath
        && "${PARAMS[ARGS_PATH_BUILD]}".isNotEmpty()
        && "${PARAMS[ARGS_PATH_BUILD]}".contains("/app/build")
    ) {
        PARAMS[ARGS_PATH] =
            "${PARAMS[ARGS_PATH_BUILD]}".substring(0, "${PARAMS[ARGS_PATH_BUILD]}".indexOf("/app/build"))
    }

    if ("${PARAMS[ARGS_DEBUG]}" == "true") {
        println("$PARAMS}")
    }

    when {
        PARAMS[ARGS_SERVER] == "true" -> {
            if (Checker.checkServerParams()) {
                SpringApplication.run(StartWebApplication::class.java, *args)
            } else {
                Info.setRequiredParams()
            }
        }
        PARAMS[ARGS_CHANGELOG] == "true" -> {
            Changelog.generate("${PARAMS[ARGS_PATH]}")
        }
        PARAMS[ARGS_APPLICATION_NAME] == "true" -> print(HelperFile.findAppName("${PARAMS[ARGS_PATH]}"))
        PARAMS[ARGS_APPLICATION_ID] == "true" -> print(HelperFile.getApplicationId("${PARAMS[ARGS_PATH]}"))
        PARAMS[ARGS_GET_VERSION_CODE] == "true" -> print(HelperFile.getVersionCode("${PARAMS[ARGS_PATH]}"))
        PARAMS[ARGS_GET_VERSION_NAME] == "true" -> HelperFile.getVersionName("${PARAMS[ARGS_PATH]}")
        PARAMS[ARGS_GET_VERSION_CODE_UP] == "true" -> print(HelperFile.getVersionCodeUp("${PARAMS[ARGS_PATH]}"))
        PARAMS[ARGS_VERSION_CODE_UP] == "true" -> {
            HelperFile.versionCodeUp("${PARAMS[ARGS_PATH]}")?.let {
                print(it)
            } ?: run {
                Info.errorUpVersionCode()
            }
        }
        PARAMS[ARGS_VERSION_NAME_UP] == "true" -> {
            HelperFile.versionNameUp("${PARAMS[ARGS_PATH]}")?.let {
                print(it)
            } ?: run {
                Info.errorUpVersionName()
            }
        }
        PARAMS[ARGS_GET_VERSION_NAME_UP] == "true" -> {
            HelperFile.getVersionNameUp("${PARAMS[ARGS_PATH]}")?.let {
                print(it)
            } ?: run {
                Info.errorUpVersionName()
            }
        }
        PARAMS[ARGS_PATH_BUILD] != "false" -> {
            if ("${PARAMS[ARGS_UPLOAD_TRACK]}" == "false") {
                Info.trackRequired()
            }
            if ("${PARAMS[ARGS_USER_EMAIL]}" == "false") {
                Info.selectUser()
            }
            val applicationId = HelperFile.getApplicationId("${PARAMS[ARGS_PATH]}")
            val versionCode = HelperFile.getVersionCode("${PARAMS[ARGS_PATH]}")
            val versionName = HelperFile.getVersionName("${PARAMS[ARGS_PATH]}")

            val user = ModelUser.findByEmail("${PARAMS[ARGS_USER_EMAIL]}")
            if (user == null) {
                Info.userNotFound("${PARAMS[ARGS_USER_EMAIL]}")
            }
            val file = File("${PARAMS[ARGS_PATH_BUILD]}")
            if (!file.exists() || file.isDirectory) {
                Info.notFoundFileBuild("${PARAMS[ARGS_PATH_BUILD]}")
            }
            GooglePlayUpload.upload(
                path = "${PARAMS[ARGS_PATH_BUILD]}",
                applicationId = applicationId,
                versionCode = "$versionCode",
                versionName = versionName,
                uploadTrack = "${PARAMS[ARGS_UPLOAD_TRACK]}",
                user = user ?: ModelUser()
            )
        }
        PARAMS[ARGS_MAILING_SLACK] != "false" -> {
            if ("${PARAMS[ARGS_UPLOAD_TRACK]}" == "false") {
                Info.trackRequired()
            }
            if ("${PARAMS[ARGS_USER_EMAIL]}" == "false") {
                Info.selectUser()
            }

            val applicationId = HelperFile.getApplicationId("${PARAMS[ARGS_PATH]}")
            val versionCode = HelperFile.getVersionCode("${PARAMS[ARGS_PATH]}")
            val versionName = HelperFile.getVersionName("${PARAMS[ARGS_PATH]}")
            val appName = HelperFile.findAppName("${PARAMS[ARGS_PATH]}")

            Info.sendSlackWebhook(
                appName,
                applicationId,
                "${PARAMS[ARGS_UPLOAD_TRACK]}",
                "$versionCode",
                versionName,
                "${PARAMS[ARGS_MAILING_SLACK_DESC]}",
                "${PARAMS[ARGS_USER_EMAIL]}"
            )
        }
    }

    exit()
}