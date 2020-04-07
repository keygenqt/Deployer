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

package com.keygenqt.deployer.base.retrofit

import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*

interface RetrofitQuery {

    @Headers("Content-Type: application/json")
    @POST
    fun slackWebhooks(
        @Url url: String,
        @Body info: RequestBody
    ): Observable<Any>

    @GET("https://www.googleapis.com/androidpublisher/v3/applications/{applicationId}/edits/{id}/listings/en-US")
    fun queryProjectInfo(
        @Path("applicationId") applicationId: String,
        @Path("id") id: String
    ): Observable<Any>

    @POST("https://www.googleapis.com/androidpublisher/v3/applications/{applicationId}/edits/{id}:commit")
    fun googlePlayUploadCommit(
        @Path("applicationId") applicationId: String,
        @Path("id") id: String
    ): Observable<Any>

    @POST("https://www.googleapis.com/upload/androidpublisher/v3/applications/{applicationId}/edits/{id}/{type}")
    fun googlePlayUpload(
        @Path("applicationId") applicationId: String,
        @Path("id") id: String,
        @Path("type") type: String,
        @Body build: RequestBody
    ): Observable<Any>

    @POST("https://www.googleapis.com/androidpublisher/v3/applications/{applicationId}/edits")
    fun getAppId(
        @Path("applicationId") applicationId: String
    ): Observable<Any>

    @Headers("Content-Type: application/json")
    @PUT("https://www.googleapis.com/androidpublisher/v3/applications/{applicationId}/edits/{id}/tracks/{typeTrack}")
    fun googlePlayUpdateInfo(
        @Path("applicationId") applicationId: String,
        @Path("id") id: String,
        @Path("typeTrack") typeTrack: String,
        @Body info: RequestBody
    ): Observable<Any>

    @FormUrlEncoded
    @POST
    fun oauth2(
        @Url url: String,
        @Field("grant_type") type: String,
        @Field("code") code: String,
        @Field("client_id") client_id: String,
        @Field("client_secret") client_secret: String,
        @Field("redirect_uri") redirect_uri: String
    ): Observable<Any>

    @FormUrlEncoded
    @POST
    fun oauth2RefreshToken(
        @Url url: String,
        @Field("grant_type") type: String,
        @Field("refresh_token") code: String,
        @Field("client_id") client_id: String,
        @Field("client_secret") client_secret: String
    ): Observable<Any>

    @GET
    fun getUser(@Url url: String): Observable<Any>
}