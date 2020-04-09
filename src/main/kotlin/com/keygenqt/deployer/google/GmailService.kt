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

package com.keygenqt.deployer.google

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.Base64
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message
import com.keygenqt.deployer.models.ModelSettings
import com.keygenqt.deployer.models.ModelUser
import com.keygenqt.deployer.utils.APPLICATION_NAME
import java.io.ByteArrayOutputStream
import java.util.*
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class GmailService(
    val userId: String? = null,
    val service: Gmail? = null
) {

    companion object {
        fun build(settings: ModelSettings?): GmailService? {
            if (settings != null && settings.access_token.isNotEmpty()) {
                val credential = GoogleCredential().setAccessToken(settings.access_token)
                return GmailService(
                    settings.user_id, Gmail.Builder(
                            NetHttpTransport(),
                            JacksonFactory.getDefaultInstance(),
                            credential
                        )
                        .setApplicationName(APPLICATION_NAME)
                        .build()
                )
            }
            return null
        }
    }

    fun createEmail(to: String, from: String, subject: String, bodyText: String): MimeMessage {
        val props = Properties()
        val session: Session = Session.getDefaultInstance(props, null)
        val email = MimeMessage(session)
        email.setFrom(InternetAddress(from))
        email.addRecipient(
            javax.mail.Message.RecipientType.TO,
            InternetAddress(to)
        )
        email.subject = subject
        email.setContent(bodyText, "text/html; charset=utf-8");
        return email
    }

    private fun createMessageWithEmail(emailContent: MimeMessage): Message {
        val buffer = ByteArrayOutputStream()
        emailContent.writeTo(buffer)
        val bytes: ByteArray = buffer.toByteArray()
        val encodedEmail: String = Base64.encodeBase64URLSafeString(bytes)
        val message = Message()
        message.raw = encodedEmail
        return message
    }

    fun sendMessage(settings: ModelSettings, to: String, subject: String, bodyText: String) {
        service?.let {
            OAuthService.oauthRefreshToken(settings) {
                ModelUser.findById(userId ?: "0")?.let {
                    val message: Message = createMessageWithEmail(createEmail(to, it.email, subject, bodyText))
                    service.users().messages().send(userId, message).execute()
                    println("Message sent success: $to")
                }
            }
        }
    }
}