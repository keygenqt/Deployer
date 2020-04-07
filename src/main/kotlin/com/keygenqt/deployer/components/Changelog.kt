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
import com.keygenqt.deployer.base.Configuration
import com.keygenqt.deployer.base.Info
import com.keygenqt.deployer.utils.*
import org.thymeleaf.context.Context
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class Changelog {
    companion object {
        private fun getCountTags(): Int {
            return Bash.exec(CMD_COUNT_TAGS)[0].toInt()
        }

        private fun getCountTagsChanges(tags: ArrayList<HashMap<String, Any>>): Int {
            var count = 0;
            for (tag in tags) {
                if ((tag["items"] as ArrayList<*>).size > 0 && tag["to"] != "HEAD") {
                    count++
                }
            }
            return count
        }

        private fun getChanges(from: String = "HEAD", to: String = "HEAD"): ArrayList<HashMap<String, String>> {
            val format = Configuration.getChangelogDateFormat()

            val array = if (from == "HEAD" && to == "HEAD") {
                Bash.exec(
                    CMD_FIND_CHANGE.replace("{grep}", Configuration.getChangelogTypesLogGrep())
                        .replace(" {from}..{to}", "")
                )
            } else {
                var fromMod = from
                if (from == "HEAD") {
                    fromMod = Bash.exec(CMD_GET_FIRST_HASH)[0]
                }
                Bash.exec(
                    CMD_FIND_CHANGE.replace("{grep}", Configuration.getChangelogTypesLogGrep())
                        .replace("{from}", fromMod)
                        .replace("{to}", to)
                )
            }

            val result = arrayListOf<HashMap<String, String>>()

            for (item in array.joinToString("<br>").split("[```]")) {

                val items =
                    item.replace("((\\<br\\>)```|```(\\<br\\>)|\\<br\\>\$|^\\<br\\>)".toRegex(), "```").split("```")
                        .map { it.trim() }

                if (items.size < 5) {
                    continue
                }

                val hash = items[0]
                val email = items[2]
                val name = items[3]
                val date = SimpleDateFormat(format).format(Date(("${items[4]}000").toLong()))


                if (items[1].contains("<br>")) {
                    for (commit in items[1].split("<br>")) {
                        val id = commit.replace(Configuration.getChangelogRegexIdTask().toRegex(), "$1")
                        val type = commit.replace(Configuration.getChangelogRegexIdType().toRegex(), "$1")
                        result.add(
                            hashMapOf(
                                "id" to id,
                                "hash" to hash,
                                "email" to email,
                                "name" to name,
                                "date" to date,
                                "timestamp" to items[4],
                                "commit" to commit.replace(id, "").replace(type, "").trim(),
                                "type" to type
                            )
                        )
                    }
                } else {
                    val commit = items[1]
                    val id = commit.replace(Configuration.getChangelogRegexIdTask().toRegex(), "$1")
                    val type = commit.replace(Configuration.getChangelogRegexIdType().toRegex(), "$1")
                    result.add(
                        hashMapOf(
                            "id" to id,
                            "hash" to hash,
                            "email" to email,
                            "name" to name,
                            "date" to date,
                            "timestamp" to items[4],
                            "commit" to commit.replace(id, "").replace(type, "").trim(),
                            "type" to type
                        )
                    )
                }
            }
            return result
        }

        private fun getTags(): ArrayList<String> {
            val dates = arrayListOf<Long>()
            val data = LinkedHashMap<Long, String>()
            for (string in ArrayList(Bash.exec(CMD_GET_TAGS))) {
                val date = getDateTag(string)
                data[date] = string
                dates.add(date)
            }
            val tags = arrayListOf<String>()
            dates.sort()
            for (date in dates) {
                tags.add(data[date] ?: "")
            }
            return tags
        }

        private fun getLastTags(): String {
            val value = getTags()
            return if (value.isEmpty()) "" else value.last()
        }

        private fun getDateTag(tag: String): Long {
            val tags = Bash.exec(CMD_GET_TAG_DATE.replace("{tag}", tag))
            if (tags.isEmpty()) {
                return 0L
            }
            return "${tags[0]}000".toLong()
        }

        private fun getCommitsByTag(): ArrayList<HashMap<String, Any>> {
            val format = Configuration.getChangelogDateFormat()
            val dataTags = ArrayList<HashMap<String, Any>>()
            var to = "HEAD"
            val tags = getTags()
            tags.reverse()
            for (from in tags) {

                if ("${PARAMS[ARGS_DEBUG]}" == "true") {
                    println("from: $from -> to: $to")
                }

                val date = SimpleDateFormat(format).format(Date((getDateTag(to))))
                dataTags.add(
                    hashMapOf(
                        "date" to SimpleDateFormat(format).format(Date((getDateTag(to)))),
                        "to" to to,
                        "from" to from,
                        "items" to getChanges(from, to),
                        "items_by_type" to tagsByType(getChanges(from, to)),
                        "to_clear" to to.replace("""([^\d]+)""".toRegex(), ""),
                        "from_clear" to from.replace("""([^\d]+)""".toRegex(), ""),
                        "date_clear" to date.replace("""([^\d\s]+)""".toRegex(), "").replace(" ", "-"),
                        "meridiem" to if (date.toLowerCase().contains("pm")) "pm" else "am"
                    )
                )
                to = from
            }
            val from = "HEAD"
            val date = SimpleDateFormat(format).format(Date((getDateTag(to))))
            dataTags.add(
                hashMapOf(
                    "date" to SimpleDateFormat(format).format(Date((getDateTag(to)))),
                    "to" to to,
                    "from" to from,
                    "items" to getChanges(from, to),
                    "items_by_type" to tagsByType(getChanges(from, to)),
                    "to_clear" to to.replace("""([^\d]+)""".toRegex(), ""),
                    "from_clear" to from.replace("""([^\d]+)""".toRegex(), ""),
                    "date_clear" to date.replace("""([^\d\s]+)""".toRegex(), "").replace(" ", "-"),
                    "meridiem" to if (date.toLowerCase().contains("pm")) "pm" else "am"
                )
            )
            if ("${PARAMS[ARGS_DEBUG]}" == "true") {
                println("from: $from -> to: $to")
            }

//            if (dataTags.size == 1 && (dataTags[0]["items"] as ArrayList<*>).size == 0) {
//                return arrayListOf()
//            }
            return dataTags
        }

        private fun tagsByType(tags: ArrayList<HashMap<String, String>>): LinkedHashMap<String, ArrayList<HashMap<String, String>>> {
            val map = linkedMapOf<String, ArrayList<HashMap<String, String>>>()
            for (type in Configuration.getChangelogOrderTypes()) {
                map[type] = arrayListOf()
            }
            for (tag in tags) {
                val type = tag["type"] ?: ""
                if (map.containsKey(type)) {
                    map[type]?.add(tag)
                } else {
                    map[type] = arrayListOf()
                    map[type]?.add(tag)
                }
            }
            return map
        }

        fun generate(path: String) {
            val context = Context(Locale("US"))
            val tagsMap = getCommitsByTag()
            val last = getLastTags()
            context.setVariable("date", SimpleDateFormat(Configuration.getChangelogDateFormatGenerate()).format(Date()))
            context.setVariable("tags_last", if (last.isEmpty()) "undefined" else last)
            context.setVariable("tags_count", getCountTags())
            context.setVariable("tags_list", getTags())
            context.setVariable("tags_map", tagsMap)
            context.setVariable("tags_count_changes", getCountTagsChanges(tagsMap))

            val content = SingleTemplate.getTemplateChangelog(PATH_APP_TEMPLATE_CL, context)
            if (content.isNotEmpty()) {
                Files.write(
                    Paths.get("$path/CHANGELOG.md"),
                    content.toByteArray(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
                )
                Info.successGenerateChangelog()
            } else {
                Info.errorGenerateChangelog()
            }
        }
    }
}