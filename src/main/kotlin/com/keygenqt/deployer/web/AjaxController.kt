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

package com.keygenqt.deployer.web

import com.keygenqt.deployer.models.ModelSettings
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping("/ajax")
class AjaxController {

    @Autowired
    private val context: HttpServletRequest? = null

    private fun isAjax(request: HttpServletRequest?): Boolean {
        if (request == null) {
            return false
        }
        val requestedWithHeader = request.getHeader("X-Requested-With")
        return "XMLHttpRequest" == requestedWithHeader
    }

    @GetMapping("/code", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun code(@RequestParam(name = "code", required = true) code: String): String {
        if (isAjax(context)) {
            val entity = JSONObject()
            entity.put("process", false)
            ModelSettings.findByCode(code)?.let {
                when {
                    it.user_id.isNotEmpty() -> {
                        entity.put("success", "Your token add to base successfully.")
                    }
                    else -> {
                        entity.put("process", true)
                    }
                }
            } ?: run {
                entity.put("error", "Error get token. Try later.")
            }
            return entity.toString()
        }
        return ""
    }
}