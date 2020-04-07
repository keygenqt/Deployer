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

const val CMD_COUNT_TAGS = "git tag | wc -l"
const val CMD_GET_TAGS = "git tag"
const val CMD_GET_TAG_DATE = "git log -1 --format=%at {tag}"
const val CMD_FIND_CHANGE =
    "git log --grep='{grep}' -i -E --format=[\\`\\`\\`]%H\\`\\`\\`%n%B\\`\\`\\`%ae\\`\\`\\`%aN\\`\\`\\`%at {from}..{to}"
const val CMD_GET_FIRST_HASH = "git rev-list --max-parents=0 HEAD"

const val CMD_BUILD_PERMISSION = "chmod +x ./gradlew"
const val CMD_BUILD_CLEAN = "./gradlew clean > /dev/null"
const val CMD_BUILD = "./gradlew --console=plain --no-build-cache {assemble} > $TEMP_BUILD_FILE"
const val CMD_BUILD_KEY =
    "./gradlew --console=plain --no-build-cache {assemble} -Pandroid.injected.signing.store.password={storePass} -Pandroid.injected.signing.key.password={keyPass} -Pandroid.injected.signing.store.file={storeFile} -Pandroid.injected.signing.key.alias={alias} > $TEMP_BUILD_FILE"