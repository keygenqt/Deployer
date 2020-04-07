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

import com.keygenqt.deployer.utils.*
import org.apache.tomcat.util.http.fileupload.FileUtils
import org.springframework.core.io.ClassPathResource
import org.springframework.util.StreamUtils
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.PatternSyntaxException

class Checker {
    companion object {
        fun checkServerParams(): Boolean {
            if (Configuration.getGoogleOauthClientId().isEmpty()) {
                return false
            }
            if (Configuration.getGoogleOauthClientSecret().isEmpty()) {
                return false
            }
            if (Configuration.getGoogleOauthRedirectUri().isEmpty()) {
                return false
            }
            return true
        }

        fun folderProject(value: String): String {
            if (value.isEmpty()) {
                return ""
            }
            val f = File(value)
            if (!f.exists() || f.isFile) {
                return ""
            }
            return value
        }

        fun regex(value: String): String {
            if (value.isEmpty()) {
                return ""
            }
            try {
                value.toRegex()
            } catch (exception: PatternSyntaxException) {
                println(exception.message)
                return ""
            }
            return value
        }


        fun terminal(value: String): String {
            return when (value) {
                ARGS_SERVER -> "true"
                ARGS_CHANGELOG -> "true"
                ARGS_BUILD -> "true"
                ARGS_DEBUG -> "true"
                else -> {

                    println(value)

                    if (!value.contains("=")) {
                        ""
                    } else {
                        val arr = value.split("=")
                        println(arr[1])
                        arr[1]
                    }
                }
            }
        }

        fun tempDir() {
            val tempDir = File(PATH_APP_TEMP_DIR)

            if (!tempDir.exists()) {
                try {
                    FileUtils.forceMkdir(tempDir)
                    val content: String =
                        StreamUtils.copyToString(
                            ClassPathResource("other/deployer.json").inputStream,
                            Charset.defaultCharset()
                        )
                    Files.write(Paths.get("$tempDir/deployer.json"), content.toByteArray())
                } catch (ex: Exception) {
                    Info.errorTempDir()
                }
            } else if (tempDir.isFile) {
                Info.errorTempDir()
            }
        }
    }
}