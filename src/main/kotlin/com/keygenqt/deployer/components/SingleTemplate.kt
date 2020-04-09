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

import org.springframework.core.io.ClassPathResource
import org.springframework.util.StreamUtils
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.StringTemplateResolver
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths


class SingleTemplate {

    companion object {

        private fun getTemplateEngine(mode: TemplateMode): TemplateEngine {
            val engine = TemplateEngine()
            val templateResolver = StringTemplateResolver()
            templateResolver.templateMode = mode
            templateResolver.checkExistence = true
            templateResolver.isCacheable = false
            engine.setTemplateResolver(templateResolver)
            return engine
        }

        fun getTemplateChangelog(path: String, context: Context): String {
            val file = File(path)
            return if (file.exists()) {
                getTemplateEngine(TemplateMode.TEXT).process(
                    String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8),
                    context
                )
            } else {
                val content: String =
                    StreamUtils.copyToString(
                        ClassPathResource("other/changelog.template").inputStream,
                        Charset.defaultCharset()
                    )
                getTemplateEngine(TemplateMode.TEXT).process(content, context)
            }
        }

        fun getTemplateResource(path: String, context: Context): String {
            val content: String =
                StreamUtils.copyToString(
                    ClassPathResource(path).inputStream,
                    Charset.defaultCharset()
                )
            return getTemplateEngine(TemplateMode.HTML).process(content, context)
        }
    }

}