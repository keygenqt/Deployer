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

package com.keygenqt.deployer.base

fun List<StringSort>.toStringArray(): ArrayList<String> {
    val array = arrayListOf<String>()
    for (string in this) {
        array.add(string.value)
    }
    return array
}

class StringSort(val value: String) : Comparable<StringSort> {

    override fun compareTo(other: StringSort): Int {
        val indexes = arrayListOf(0, 0, 0, 0)
        val chars = arrayListOf('0', '0')
        while (true) {

            indexes[3] = 0
            indexes[2] = indexes[3]
            chars[0] = charAt(value, indexes[0])
            chars[1] = charAt(other.value, indexes[1])

            while (Character.isSpaceChar(chars[0]) || chars[0] == '0') {
                when {
                    chars[0] == '0' -> indexes[2]++
                    else -> indexes[2] = 0
                }
                chars[0] = charAt(value, ++indexes[0])
            }
            while (Character.isSpaceChar(chars[1]) || chars[1] == '0') {
                when {
                    chars[1] == '0' -> indexes[3]++
                    else -> indexes[3] = 0
                }
                chars[1] = charAt(other.value, ++indexes[1])
            }
            when {
                Character.isDigit(chars[0]) && Character.isDigit(chars[1]) -> {
                    val shift: Int = compareRight(value.substring(indexes[0]), other.value.substring(indexes[1]))
                    if (shift != 0) {
                        return shift
                    }
                }
                chars[0].toInt() == 0 && chars[1].toInt() == 0 -> return compare(
                    value,
                    other.value,
                    indexes[2],
                    indexes[3]
                )
                chars[0] < chars[1] -> return -1
                chars[0] > chars[1] -> return +1
            }
            ++indexes[0]
            ++indexes[1]
        }
    }

    private fun compareRight(name1: String, name2: String): Int {
        val indexes = arrayListOf(0, 0, 0)
        val chars = arrayListOf('0', '0')
        while (true) {
            chars[0] = charAt(name1, indexes[1])
            chars[1] = charAt(name2, indexes[2])
            when {
                !isDigit(chars[0]) && !isDigit(chars[1]) -> return indexes[0]
                !isDigit(chars[0]) -> return -1
                !isDigit(chars[1]) -> return +1
                chars[0].toInt() == 0 && chars[1].toInt() == 0 -> return indexes[0]
                indexes[0] == 0 -> {
                    if (chars[0] < chars[1]) {
                        indexes[0] = -1
                    } else if (chars[0] > chars[1]) {
                        indexes[0] = +1
                    }
                }
            }
            indexes[1]++
            indexes[2]++
        }
    }

    private fun compare(name1: String, name2: String, temp1: Int, temp2: Int): Int {
        return when {
            temp1 - temp2 != 0 -> temp1 - temp2
            name1.length == name2.length -> name1.compareTo(name2)
            else -> name1.length - name2.length
        }
    }

    private fun charAt(value: String, i: Int): Char {
        return if (i >= value.length) '0'.dec() else value[i]
    }

    private fun isDigit(char: Char): Boolean {
        return Character.isDigit(char) || char == '.' || char == ','
    }
}