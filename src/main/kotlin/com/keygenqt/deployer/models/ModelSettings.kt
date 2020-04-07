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

package com.keygenqt.deployer.models

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import com.keygenqt.deployer.base.ConnectDb

@DatabaseTable(tableName = "settings")
class ModelSettings(
    @DatabaseField
    var code: String = "",
    @DatabaseField
    var user_id: String = "",
    @DatabaseField
    var access_token: String = "",
    @DatabaseField
    var refresh_token: String = "",
    @DatabaseField
    var expires_in: Int = 0,
    @DatabaseField
    var scope: String = "",
    @DatabaseField
    var token_type: String = "",
    @DatabaseField
    var id_token: String = "",
    @DatabaseField
    var time: Int = 0
) {
    @DatabaseField(allowGeneratedIdInsert = true, generatedId = true)
    var uid: Int = 0

    companion object {
        fun findByUserId(userId: String): ModelSettings? {
            val qb = ConnectDb.dao(ModelSettings::class.java).queryBuilder()
            qb.where().eq("user_id", userId)
            return try {
                ConnectDb.dao(ModelSettings::class.java).query(qb.prepare()).first()
            } catch (ex: Exception) {
                null
            }
        }

        fun findByUid(uid: Int): ModelSettings? {
            val qb = ConnectDb.dao(ModelSettings::class.java).queryBuilder()
            qb.where().eq("uid", uid)
            return try {
                ConnectDb.dao(ModelSettings::class.java).query(qb.prepare()).first()
            } catch (ex: Exception) {
                null
            }
        }

        fun findByCode(code: String): ModelSettings? {
            val qb = ConnectDb.dao(ModelSettings::class.java).queryBuilder()
            qb.where().eq("code", code)
            return try {
                ConnectDb.dao(ModelSettings::class.java).query(qb.prepare()).first()
            } catch (ex: Exception) {
                null
            }
        }

        fun clear() {
            val qb = ConnectDb.dao(ModelSettings::class.java).deleteBuilder()
            qb.where().eq("user_id", "").or().eq("access_token", "-1")
            try {
                ConnectDb.dao(ModelSettings::class.java).delete(qb.prepare())
            } catch (ex: Exception) {
            }
        }
    }

    fun save() {
        clear()
        val qb = ConnectDb.dao(ModelSettings::class.java).deleteBuilder()
        qb.where().eq("user_id", user_id)
        try {
            ConnectDb.dao(ModelSettings::class.java).delete(qb.prepare())
        } catch (ex: Exception) {
        }
        ConnectDb.dao(ModelSettings::class.java).create(this)
    }
}