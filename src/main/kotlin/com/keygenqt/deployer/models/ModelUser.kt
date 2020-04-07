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

@DatabaseTable(tableName = "users")
class ModelUser(
    @DatabaseField
    var id: String = "",
    @DatabaseField
    var email: String = "",
    @DatabaseField
    var verified_email: Boolean = false,
    @DatabaseField
    var name: String = "",
    @DatabaseField
    var given_name: String = "",
    @DatabaseField
    var family_name: String = "",
    @DatabaseField
    var link: String = "",
    @DatabaseField
    var picture: String = "",
    @DatabaseField
    var gender: String = "",
    @DatabaseField
    var locale: String = "",
    @DatabaseField
    var hd: String = ""
) {
    @DatabaseField(allowGeneratedIdInsert = true, generatedId = true)
    var uid: Int = 0

    companion object {
        fun findById(id: String): ModelUser? {
            val qb = ConnectDb.dao(ModelUser::class.java).queryBuilder()
            qb.where().eq("id", id)
            return try {
                ConnectDb.dao(ModelUser::class.java).query(qb.prepare()).first()
            } catch (ex: Exception) {
                null
            }
        }

        fun findByEmail(email: String): ModelUser? {
            val qb = ConnectDb.dao(ModelUser::class.java).queryBuilder()
            qb.where().eq("email", email)
            return try {
                ConnectDb.dao(ModelUser::class.java).query(qb.prepare()).first()
            } catch (ex: Exception) {
                null
            }
        }
    }

    fun save() {
        val qb = ConnectDb.dao(ModelUser::class.java).deleteBuilder()
        qb.where().eq("email", email)
        try {
            ConnectDb.dao(ModelUser::class.java).delete(qb.prepare())
        } catch (ex: Exception) {
        }
        ConnectDb.dao(ModelUser::class.java).create(this)
    }
}