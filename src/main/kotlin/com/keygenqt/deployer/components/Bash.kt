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
import com.keygenqt.deployer.utils.ARGS_DEBUG
import com.keygenqt.deployer.utils.ARGS_PATH
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.concurrent.Executors


class Bash {
    companion object {
        fun exec(command: String): ArrayList<String> {

            if ("${PARAMS[ARGS_DEBUG]}" == "true") {
                println(command)
            }

            val result = arrayListOf<String>()
            /* "cmd.exe", "/c" */
            val builder = ProcessBuilder("sh", "-c", command).directory(File(PARAMS[ARGS_PATH] ?: ""))
            val process = builder.start()
            BufferedReader(InputStreamReader(process.inputStream)).forEachLine { result.add(it) }
            return result
        }

        fun execRunnable(command: String, listener: () -> Unit) {

            if ("${PARAMS[ARGS_DEBUG]}" == "true") {
                println(command)
            }

            val executor = Executors.newFixedThreadPool(4)
            val builder = ProcessBuilder("sh", "-c", command).directory(File(PARAMS[ARGS_PATH] ?: ""))
            executor.execute {
                val p: Process = builder.start()
                p.waitFor()
                listener.invoke()
            }
        }
    }
}