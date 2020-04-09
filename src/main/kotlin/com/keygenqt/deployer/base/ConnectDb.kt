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

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import com.keygenqt.deployer.PARAMS
import com.keygenqt.deployer.utils.ARGS_DEBUG
import com.keygenqt.deployer.utils.PATH_APP_DB
import java.util.*

class ConnectDb {
    companion object {

        private lateinit var connectionSource: ConnectionSource
        private val daoList = HashMap<String, Any>()

        fun getConnect(): ConnectionSource {
            return connectionSource
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> dao(clazz: Class<T>): Dao<T, String> {
            if (!daoList.containsKey(clazz.canonicalName)) {
                daoList[clazz.canonicalName] = DaoManager.createDao(connectionSource, clazz)
            }
            return daoList[clazz.canonicalName] as Dao<T, String>
        }

        fun open(models: ArrayList<Class<*>>) {

            if ("${PARAMS[ARGS_DEBUG]}" == "false") {
                System.setProperty("com.j256.ormlite.logger.type", "LOCAL")
                System.setProperty("com.j256.ormlite.logger.level", "ERROR")
            }

            try {
                connectionSource = JdbcConnectionSource("jdbc:sqlite:${PATH_APP_DB}")
                for (value in models) {
                    TableUtils.createTableIfNotExists(connectionSource, value)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun close() {
            connectionSource.close()
        }
    }
}