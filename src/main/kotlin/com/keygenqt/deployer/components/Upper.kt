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

package com.keygenqt.deployer.components

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class Upper {
    companion object {

        fun getApplicationId(path: String): String? {
            val string = String(Files.readAllBytes(File("$path/app/build.gradle").toPath()))
            var res = string.replace("\n", "").replace(""".*applicationId\s"([A-z.]+)".*""".toRegex(), "$1")
            if (res.contains("applicationId")) {
                res = string.replace("\n", "").replace(""".*applicationId\s'([A-z.]+)'.*""".toRegex(), "$1")
            }
            if (res.contains("applicationId")) {
                return null
            }
            return res
        }

        fun getVersionCode(path: String): Int? {
            val string = String(Files.readAllBytes(File("$path/app/build.gradle").toPath()))
            return string.replace("\n", "").replace(".*versionCode\\s(\\d+).*".toRegex(), "$1").toIntOrNull()
        }

        fun getVersionName(path: String): String? {
            val string = String(Files.readAllBytes(File("$path/app/build.gradle").toPath()))
            var res = string.replace("\n", "").replace(""".*versionName\s"([\dA-z.\-:]+)".*""".toRegex(), "$1")
            if (res.contains("versionName")) {
                res = string.replace("\n", "").replace(""".*versionName\s'([\dA-z.\-:]+)'.*""".toRegex(), "$1")
            }
            if (res.contains("versionName")) {
                return null
            }
            return res
        }

        fun getVersionCodeUp(path: String): String? {
            val code = getVersionCode(path)
            code?.let {
                return "${code + 1}"
            } ?: run {
                return null
            }
        }

        fun getVersionNameUp(path: String): String? {
            getVersionName(path)?.let {
                if (it.isNotEmpty() && it.contains(".")) {
                    return try {
                        val update = it.split(".").toMutableList()
                        val micro = update.last().replace("""(\d+).*""".toRegex(), "$1").toIntOrNull()
                        micro?.let {
                            val pref = update.last().replace(it.toString(), "")
                            update[update.size - 1] = "${micro + 1}$pref"
                            return update.joinToString(".")
                        } ?: run {
                            return null
                        }
                    } catch (ex: Exception) {
                        null
                    }
                }
            }
            return null
        }

        fun versionCodeUp(path: String): String? {
            val code1 = getVersionCode(path)
            val code2 = getVersionCodeUp(path)
            code2?.let {
                var string = String(Files.readAllBytes(File("$path/app/build.gradle").toPath()))
                string = string.replace("versionCode $code1", "versionCode $code2")
                Files.write(Paths.get("$path/app/build.gradle"), string.toByteArray())
                return code2
            } ?: run {
                return null
            }
        }

        fun versionNameUp(path: String): String? {
            val name1 = getVersionName(path)
            val name2 = getVersionNameUp(path)
            name2?.let {
                var string = String(Files.readAllBytes(File("$path/app/build.gradle").toPath()))
                string = string.replace("versionName '$name1'", "versionName '$name2'")
                string = string.replace("versionName \"$name1\"", "versionName \"$name2\"")
                Files.write(Paths.get("$path/app/build.gradle"), string.toByteArray())
                return name2
            } ?: run {
                return null
            }
        }
    }
}