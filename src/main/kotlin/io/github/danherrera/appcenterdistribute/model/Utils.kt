package io.github.danherrera.appcenterdistribute.model

internal fun String.getStringValue(key: String): String {
    return this.substringAfter(key)
        .substringAfter("\"")
        .substringAfter("\"")
        .substringBefore("\"")
}
