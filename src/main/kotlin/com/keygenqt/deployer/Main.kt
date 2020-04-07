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
import com.keygenqt.deployer.components.Builder
import com.keygenqt.deployer.components.Builder.Companion.versionCodeUp
import com.keygenqt.deployer.components.Builder.Companion.versionNameUp
import com.keygenqt.deployer.components.Changelog
import com.keygenqt.deployer.utils.*
import com.keygenqt.deployer.web.OauthController
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import java.io.File


val PARAMS = hashMapOf(
    ARGS_PATH to File("").absolutePath,
    ARGS_SERVER to "false",
    ARGS_CHANGELOG to "false",
    ARGS_BUILD to "false",
    ARGS_BUILD_TYPE to "false",
    ARGS_EMAIL to "false",
    ARGS_BUILD_VERSION_CODE_UP to "false",
    ARGS_BUILD_VERSION_NAME_UP to "false",
    ARGS_MAILING to "false",
    ARGS_STORE_PASSWORD to "",
    ARGS_STORE_FILE to "",
    ARGS_UPLOAD_TRACK to "false",
    ARGS_UPLOAD_NOTE to "false",
    ARGS_UPLOAD_NOTE_VERSION to "false",
    ARGS_KEY_PASSWORD to "",
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
            ARGS_CHANGELOG -> PARAMS[ARGS_CHANGELOG] = "true"
            ARGS_DEBUG -> PARAMS[ARGS_DEBUG] = "true"
            ARGS_BUILD_VERSION_CODE_UP -> PARAMS[ARGS_BUILD_VERSION_CODE_UP] = "true"
            ARGS_BUILD_VERSION_NAME_UP -> PARAMS[ARGS_BUILD_VERSION_NAME_UP] = "true"
            ARGS_MAILING -> PARAMS[ARGS_MAILING] = "true"
            ARGS_UPLOAD_NOTE_VERSION -> PARAMS[ARGS_UPLOAD_NOTE_VERSION] = "true"
            ARGS_VERSION -> Info.showVersion()
            ARGS_HELP -> Info.showHelp()
            else -> {
                when {
                    item.contains("^$ARGS_PATH\\=.+".toRegex()) -> {
                        PARAMS[ARGS_PATH] = item.replace("$ARGS_PATH=", "")
                    }
                    item.contains("^$ARGS_BUILD\\=.+".toRegex()) -> {
                        PARAMS[ARGS_BUILD] = item.replace("$ARGS_BUILD=", "")
                    }
                    item.contains("^$ARGS_BUILD_TYPE\\=.+".toRegex()) -> {
                        PARAMS[ARGS_BUILD_TYPE] = item.replace("$ARGS_BUILD_TYPE=", "")
                    }
                    item.contains("^$ARGS_EMAIL\\=.+".toRegex()) -> {
                        PARAMS[ARGS_EMAIL] = item.replace("$ARGS_EMAIL=", "")
                    }
                    item.contains("^$ARGS_STORE_PASSWORD\\=.+".toRegex()) -> {
                        PARAMS[ARGS_STORE_PASSWORD] = item.replace("$ARGS_STORE_PASSWORD=", "")
                    }
                    item.contains("^$ARGS_STORE_FILE\\=.+".toRegex()) -> {
                        PARAMS[ARGS_STORE_FILE] = item.replace("$ARGS_STORE_FILE=", "")
                    }
                    item.contains("^$ARGS_KEY_PASSWORD\\=.+".toRegex()) -> {
                        PARAMS[ARGS_KEY_PASSWORD] = item.replace("$ARGS_KEY_PASSWORD=", "")
                    }
                    item.contains("^$ARGS_UPLOAD_TRACK\\=.+".toRegex()) -> {
                        PARAMS[ARGS_UPLOAD_TRACK] = item.replace("$ARGS_UPLOAD_TRACK=", "")
                    }
                    item.contains("^$ARGS_UPLOAD_NOTE\\=.+".toRegex()) -> {
                        PARAMS[ARGS_UPLOAD_NOTE] = item.replace("$ARGS_UPLOAD_NOTE=", "")
                    }
                }
            }
        }
    }

    if ("${PARAMS[ARGS_DEBUG]}" == "true") {
        println("$PARAMS}")
    }

    when {
        PARAMS[ARGS_BUILD] != "false" -> Builder.build(
            typeFile = "${PARAMS[ARGS_BUILD]}",
            typeGradle = "${PARAMS[ARGS_BUILD_TYPE]}",
            storePass = "${PARAMS[ARGS_STORE_PASSWORD]}",
            storeFile = "${PARAMS[ARGS_STORE_FILE]}",
            keyPass = "${PARAMS[ARGS_KEY_PASSWORD]}",
            versionUp = "${PARAMS[ARGS_BUILD_VERSION_CODE_UP]}" == "true",
            nameUp = "${PARAMS[ARGS_BUILD_VERSION_NAME_UP]}" == "true"
        )
        PARAMS[ARGS_BUILD_VERSION_CODE_UP] == "true" -> {
            versionCodeUp("${PARAMS[ARGS_PATH]}")?.let {
                print(it)
            } ?: run {
                Info.errorGetVersionCode()
            }
        }
        PARAMS[ARGS_BUILD_VERSION_NAME_UP] == "true" -> {
            versionNameUp("${PARAMS[ARGS_PATH]}")?.let {
                print(it)
            } ?: run {
                Info.errorGetVersionName()
            }
        }
        PARAMS[ARGS_CHANGELOG] == "true" -> Changelog.generate("${PARAMS[ARGS_PATH]}")
        PARAMS[ARGS_SERVER] == "true" -> {
            if (Checker.checkServerParams()) {
                SpringApplication.run(StartWebApplication::class.java, *args)
            } else {
                Info.setRequiredParams()
            }
        }
    }

    ConnectDb.close()
}