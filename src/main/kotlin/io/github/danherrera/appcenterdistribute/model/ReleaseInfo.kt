package io.github.danherrera.appcenterdistribute.model

data class ReleaseInfo(
    val release_id: String,
    val release_url: String
) {
    companion object {
        fun fromJson(json: String): ReleaseInfo {
            return ReleaseInfo(
                json.getStringValue("release_id"),
                json.getStringValue("release_url")
            )
        }
    }
}
