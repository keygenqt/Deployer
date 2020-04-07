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

import com.keygenqt.deployer.PARAMS
import com.keygenqt.deployer.base.Info
import com.keygenqt.deployer.google.GooglePlayUpload
import com.keygenqt.deployer.models.ModelBuildProcess
import com.keygenqt.deployer.utils.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.concurrent.schedule

class Builder {
    companion object {

        private var stop = false

        fun build(
            typeFile: String,
            typeGradle: String,
            storePass: String,
            storeFile: String,
            keyPass: String,
            versionUp: Boolean,
            nameUp: Boolean
        ) {

            val pathDir = "${PARAMS[ARGS_PATH]}"

            if (!File("$pathDir/gradlew").exists()) {
                Info.notFoundGradlew(pathDir)
            }

            if (typeFile != "apk" && typeFile != "bundle") {
                Info.errorFileType()
            }

            val type = when (typeFile) {
                "apk" -> "assemble${typeGradle.capitalize()}"
                "bundle" -> "bundle${typeGradle.capitalize()}"
                else -> ""
            }

            val pathTempFile = "${PARAMS[ARGS_PATH]}/$TEMP_BUILD_FILE"

            Files.deleteIfExists(File(pathTempFile).toPath())

            if (versionUp) {
                if (versionCodeUp(pathDir) == null) {
                    Info.errorVersionCodeUp()
                }
            }
            if (nameUp) {
                if (versionNameUp(pathDir) == null) {
                    Info.errorVersionNameUp()
                }
            }

            Bash.exec(CMD_BUILD_PERMISSION)
            Bash.exec(CMD_BUILD_CLEAN)

            stop = false

            if (storePass.isNotEmpty() && storeFile.isNotEmpty() && keyPass.isNotEmpty()) {
                Bash.execRunnable(
                    CMD_BUILD_KEY.replace("{assemble}", type)
                        .replace("{storePass}", storePass)
                        .replace("{storeFile}", storeFile)
                        .replace("{keyPass}", keyPass)
                        .replace("{alias}", getApplicationId(pathDir))
                ) {
                    stop = true
                }
            } else {
                Bash.execRunnable(CMD_BUILD.replace("{assemble}", type)) {
                    stop = true
                }
            }

            processInfo(pathDir, pathTempFile)
        }

        public fun getVersionCodeUp(path: String): String? {
            val code = getVersionCode(path)
            code?.let {
                return "${code + 1}"
            } ?: run {
                return null
            }
        }

        public fun getVersionNameUp(path: String): String? {
            val code = getVersionName(path)
            if (code.isNotEmpty() && code.contains(".")) {
                return try {
                    val update = code.split(".").toMutableList()
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
            return null
        }

        private fun processInfo(pathDir: String, pathTempFile: String) {

            val model = ModelBuildProcess.findByPath(pathDir) ?: ModelBuildProcess()

            if (model.count == 0) {
                model.count = 90
            }

            var count = 0
            var percent = "0%"
            Timer().schedule(500L) {
                val self = this
                count++
                var p = ((count * 100) / model.count)
                if (p >= 100) {
                    p = 99
                }
                if (percent != "$p%") {
                    percent = "$p%"
                    print("\rBUILD PROGRESS: $p%")
                }
                if (!stop) {
                    Timer().schedule(500L) { self.run() }
                } else {
                    model.path = pathDir
                    model.count = count
                    p = 100
                    print("\rBUILD PROGRESS: $p%\n")
                    val result = String(Files.readAllBytes(File(pathTempFile).toPath()))
                    Files.deleteIfExists(File(pathTempFile).toPath())
                    val index = result.indexOf("BUILD SUCCESSFUL")
                    if (index == -1) {
                        println("BUILD FAILED")
                        if ("${PARAMS[ARGS_DEBUG]}" == "true") {
                            println(result)
                        }
                        exit()
                    } else {
                        model.save()
                        println(result.substring(index, result.length))
                        runUpload(pathDir)
                    }
                }
            }
        }

        private fun getVersionCode(path: String): Int? {
            val string = String(Files.readAllBytes(File("$path/app/build.gradle").toPath()))
            return string.replace("\n", "").replace(".*versionCode\\s(\\d+).*".toRegex(), "$1").toIntOrNull()
        }

        private fun getVersionName(path: String): String {
            val string = String(Files.readAllBytes(File("$path/app/build.gradle").toPath()))
            var res = string.replace("\n", "").replace(""".*versionName\s"([\dA-z.\-:]+)".*""".toRegex(), "$1")
            if (res.contains("versionName")) {
                res = string.replace("\n", "").replace(""".*versionName\s'([\dA-z.\-:]+)'.*""".toRegex(), "$1")
            }
            if (res.contains("versionName")) {
                return ""
            }
            return res
        }

        private fun getApplicationId(path: String): String {
            val string = String(Files.readAllBytes(File("$path/app/build.gradle").toPath()))
            var res = string.replace("\n", "").replace(""".*applicationId\s"([A-z.]+)".*""".toRegex(), "$1")
            if (res.contains("applicationId")) {
                res = string.replace("\n", "").replace(""".*applicationId\s'([A-z.]+)'.*""".toRegex(), "$1")
            }
            if (res.contains("applicationId")) {
                return ""
            }
            return res
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

        private fun runUpload(pathDir: String) {
            if ("${PARAMS[ARGS_UPLOAD_TRACK]}" != "false") {
                if (PARAMS[ARGS_BUILD] == "false" || PARAMS[ARGS_BUILD_TYPE] == "false" || PARAMS[ARGS_EMAIL] == "false") {
                    Info.buildRequired()
                    exit()
                } else {
                    GooglePlayUpload.upload(
                        applicationId = getApplicationId(pathDir),
                        versionCode = getVersionCode(pathDir).toString(),
                        versionName = getVersionName(pathDir),
                        uploadTrack = "${PARAMS[ARGS_UPLOAD_TRACK]}",
                        typeFile = "${PARAMS[ARGS_BUILD]}",
                        typeGradle = "${PARAMS[ARGS_BUILD_TYPE]}",
                        email = "${PARAMS[ARGS_EMAIL]}"
                    )
                }
            } else {
                exit()
            }
        }
    }
}