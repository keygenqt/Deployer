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
Usage: deployer COMMAND=ARG...

Deployer is a tool for:
    * Web interface for generate access token Oauth 2.0
    * Upload to Google Play production, internal... (Play Developer API)
    * Newsletter after upload build in Google Play (Gmail API)
    * Connect webhooks after upload test or prod (Slack API)
    * CHANGELOG.md generation based git history (Thymeleaf)

Options
    
  Upload:
    --path-build                Path to build for upload
    --upload-track              Upload type build (production/internal)
    --note-add                  Upload note text
    --note-add-version          Add to note versionCode (auto)
    --user-email                Email user with oauth authentication (if use google api)
    --mailing                   Newsletter (gmail, slack) when will upload build in Google Play
    --mailing-gmail             Newsletter only GMail
    --mailing-slack             Newsletter only Slack
    --mailing-slack-desc        Slack additional Information
    
  GradleHelper
    --path                      Path to folder with project
    --get-application-id        Get applicationId
    --get-version-code          Get versionCode
    --get-version-name          Get versionName
    --get-version-code-up       Get versionCode Up
    --get-version-name-up       Get versionName Up
    --version-code-up           Update versionCode - Up
    --version-name-up           Update versionName - Up (patch)

  Server:
    --path                      Path to folder with project
    --server                    Run server oauth. (http://localhost:8080)
    
  Changelog:
    --path                      Path to folder with project
    --changelog                 Generate CHANGELOG.md

  Other:
    --debug                     Enable processes logging terminal
    --version                   Show the version and exit
    --help                      Show help
"""

const val YOUR_UPDATE_IS_LIVE = "Your update sent {type}"
const val YOUR_TOKEN_CHECKED = "Your token checked"
const val CHANGELOG_CREATE_SUCCESS = "\nChangelog create success"
const val CHANGELOG_NOT_CREATE = "\nChangelog not create"
const val NOT_FOUND_FILE_BUILD = "Not found file: {path}"
const val NOT_FOUND_SETTINGS = "Settings not found"
const val NOT_FOUND_USER = "User ({email}) not found"
const val SELECT_USER = "Error:\n\n   --user-email={enter the email of the user authorized in oauth}"
const val START_UPLOAD = "Start upload"
const val REFRESH_TOKEN_SUCCESSFUL = "Refresh token successful"
const val GET_PROJECT_ID_SUCCESSFUL = "Get project id successful"
const val UPLOAD_FILE_SUCCESSFUL = "Upload file successful"
const val UPDATE_INFO_SUCCESSFUL = "Update info successful"
const val COMMIT_SUCCESSFUL = "Application push to Google Play successful"
const val ERROR_GMAIL_SERVICE = "Error Gmail API service"
const val ERROR_GET_APPLICATION_ID = "Error get applicationId"
const val ERROR_GET_VERSION_CODE = "Error get versionCode"
const val ERROR_GET_VERSION_NAME = "Error get versionName"
const val ERROR_UP_VERSION_CODE = "Error Up versionCode"
const val ERROR_UP_VERSION_NAME = "Error Up versionName"

val SET_REQUIRED_PARAMS = "Check required params for OAuth: $PATH_APP_TEMP_DIR/config.json"
val ERROR_TEMP_DIR = "I can't create temp dir $PATH_APP_TEMP_DIR"