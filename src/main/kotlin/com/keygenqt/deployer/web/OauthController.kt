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

import com.keygenqt.deployer.base.Configuration
import com.keygenqt.deployer.google.OAuthService
import com.keygenqt.deployer.models.ModelSettings
import com.keygenqt.deployer.utils.SCOPES
import com.keygenqt.deployer.utils.VERSION
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller
class OauthController {

    @GetMapping("/")
    fun main(
        @RequestParam(name = "code", required = false, defaultValue = "") code: String = "", model: Model
    ): String {

        if (code.isNotEmpty()) {
//            return "redirect:" + "http://localhost:8080/?code=$code"

            when (ModelSettings.findByCode(code)) {
                null -> {
                    OAuthService.oauthAuthorizationCode(code)
                }
            }
        }

        model.addAttribute("version", VERSION)
        model.addAttribute("code", code)

        return "index"
    }

    @GetMapping("/oauth")
    fun oauth(): String {
        return "redirect:" + "https://accounts.google.com/o/oauth2/auth?scope=${SCOPES.joinToString("+")}&response_type=code&access_type=offline&prompt=consent&redirect_uri=${Configuration.getGoogleOauthRedirectUri()}&client_id=${Configuration.getGoogleOauthClientId()}"
    }
}