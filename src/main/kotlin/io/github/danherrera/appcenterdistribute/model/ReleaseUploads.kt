package io.github.danherrera.appcenterdistribute.model

data class ReleaseUploads(
    val upload_id: String,
    val upload_url: String
) {

    companion object {
        fun fromJson(json: String): ReleaseUploads {
            return ReleaseUploads(
                json.getStringValue("upload_id"),
                json.getStringValue("upload_url")
            )
        }
    }
}