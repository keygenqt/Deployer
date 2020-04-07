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

package com.keygenqt.deployer.utils

const val STRING_FULL_HELP = """
Usage: java -jar deployer.jar COMMAND=ARG...

Deployer is a tool for:
    * Web interface for generate access token Oauth 2.0.
    * Building apk or bundle package default build types as well as custom
    * Upload to Google Play production, internal... (Play Developer API)
    * CHANGELOG.md generation based git history (Thymeleaf)
    * Newsletter after upload build in Google Play (Gmail API)
    * Connect webhooks slack after upload test or prod
    * One file for all features!

Options

  --path                        PATH to folder with project
    
  Build:
    --build                     Type: apk/bundle
    --build-type                Type gradle build (release, debug or other builds type)
    --store-password            Password store *.jks
    --key-password              Password key *.jks
    --store-file                Path to file *.jks
    --upload-track              Upload type build (production/internal/alpha/beta)
    --upload-note               Upload note text
    --upload-note-version       Add to note versionCode
    --version-code-up           Raising versionCode during assembly
    --version-name-up           Raising versionName during assembly (up path version)
    --mailing                   Newsletter (gmail, slack) when will upload build in Google Play

  Server:
    --server                    Run server oauth. (http://localhost:8080)
    
  Changelog:
    --changelog                 Generate CHANGELOG.md

  Other:
    --email                     Email user with oauth authentication (if use google api)
    --debug                     Enable processes logging terminal
    --version                   Show the version and exit
    --help                      Show help
"""

const val YOUR_UPDATE_IS_LIVE = "Your update sent {type}"
const val YOUR_TOKEN_CHECKED = "Your token checked"
const val CHANGELOG_CREATE_SUCCESS = "\nChangelog create success\n"
const val CHANGELOG_NOT_CREATE = "\nChangelog not create\n"
const val BUILD_NOT_FOUND_GRADLEW = "\nNot found gradlew in dir: {path}\n"
const val ERROR_VERSION_CODE_UP = "Error up version code"
const val ERROR_VERSION_NAME_UP = "Error up name version"
const val ERROR_BUILD_REQUIRED_BEFORE_PUSH =
    "Error push build. Check required params: $ARGS_EMAIL=(email oauth user), $ARGS_BUILD_TYPE=(build type from your gradle)"
const val ERROR_FILE_TYPE = "Use: ${ARGS_BUILD}=apk or ${ARGS_BUILD}=bundle"
const val NOT_FOUND_FILE_BUILD = "Not found file: {path}"
const val NOT_FOUND_SETTINGS = "Settings not found"
const val NOT_FOUND_USER = "User ({email}) not found"
const val START_UPLOAD = "Start upload"
const val REFRESH_TOKEN_SUCCESSFUL = "Refresh token successful"
const val GET_PROJECT_ID_SUCCESSFUL = "Get project id successful"
const val UPLOAD_FILE_SUCCESSFUL = "Upload file successful"
const val UPDATE_INFO_SUCCESSFUL = "Update info successful"
const val COMMIT_SUCCESSFUL = "Application push to Google Play successful"
const val ERROR_GMAIL_SERVICE = "Error Gmail API service"
const val ERROR_GET_VERSION_CODE = "Error get versionCode"
const val ERROR_GET_VERSION_NAME = "Error get versionName"

val SET_REQUIRED_PARAMS = "Check required params for OAuth: $PATH_APP_TEMP_DIR/$INNER_APP_NAME.json"
val ERROR_TEMP_DIR = "I can't create temp dir $PATH_APP_TEMP_DIR"