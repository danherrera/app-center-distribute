package io.github.danherrera.appcenterdistribute

import io.github.danherrera.appcenterdistribute.model.ReleaseInfo
import io.github.danherrera.appcenterdistribute.model.ReleaseUploads
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

open class AppCenterDistributeTask : DefaultTask() {

    companion object {
        const val LOG_TAG = "> App Center Distribute:"
    }

    @Input
    var variantName = ""
    @Input
    var outputFile: File? = null

    @TaskAction
    fun distribute() {
        val extension = project.extensions.getByType(AppCenterDistributePluginExtension::class.java)
        extension.run {

            if (apiToken.isBlank()) {
                logError("API token is missing")
                return
            }
            if (ownerName.isBlank()) {
                logError("Owner name is missing")
                return
            }
            if (variantToAppName.isEmpty()) {
                logError("No App Center app names defined for any variants")
                return
            }
            if (outputFile == null) {
                logError("Could not resolve outputFile for $variantName")
                return
            }
            if (variantToAppName[variantName].isNullOrBlank()) {
                logError("Could not resolve App Center app name for $variantName\nLooked in: $variantToAppName")
                return
            }

            val appName = variantToAppName[variantName]!!

            log("Initiating Upload sequence for $variantName")

            val headers = mapOf(
                "Content-Type" to "application/json",
                "Accept" to "application/json",
                "X-API-Token" to apiToken
            )

            val okHttpClient = OkHttpClient()
                .newBuilder()
                .addInterceptor {
                    val request = it.request()
                        .newBuilder()
                        .headers(Headers.of(headers))
                        .build()
                    it.proceed(request)
                }
                .build()

            val applicationJson = MediaType.parse("application/json")

            val releaseUploadsUrl = "$baseUrl$ownerName/$appName/release_uploads"

            // Call 1/4: Get Release Upload ID and URL
            log("1/4 Getting Upload ID and URL...")

            val json = try {
                okHttpClient.newCall(
                    Request.Builder()
                        .url(releaseUploadsUrl)
                        .post(
                            RequestBody.create(applicationJson, "")
                        )
                        .build()
                ).execute().body()!!.string()
            } catch (e: Exception) {
                logError("Failed to get upload ID and URL", e)
                return
            }

            val releaseUploads: ReleaseUploads = ReleaseUploads.fromJson(json)

            log("1/4 Successfully obtained ID(${releaseUploads.upload_id}) and URL(${releaseUploads.upload_url})")

            // Call 2/4: Upload File

            val fileToUploadPath = outputFile?.absolutePath.orEmpty()

            log("2/4 Uploading file: $fileToUploadPath")

            val uploadFileRequest = Request.Builder()
                .url(releaseUploads.upload_url)
                .post(
                    MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(
                            "ipa", outputFile!!.name,
                            RequestBody.create(MediaType.parse("text/csv"), outputFile!!)
                        )
                        .build()
                )
                .build()

            val uploadFileResponse = try {
                okHttpClient.newCall(uploadFileRequest).execute()
            } catch (e: Exception) {
                logError("Failed to upload file", e)
                return
            }

            if (uploadFileResponse.isSuccessful) {
                log("2/4 Successfully uploaded file.")
            } else {
                logError("Failed to upload file\nReason: (${uploadFileResponse.code()}) ${uploadFileResponse.message()}")
                return
            }

            // Call 3/4: Commit upload
            val commitRequest = Request.Builder()
                .url("$releaseUploadsUrl/${releaseUploads.upload_id}")
                .patch(
                    RequestBody.create(
                        MediaType.parse("application/json"),
                        "{ \"status\" : \"committed\" }"
                    )
                )
                .build()

            val commitResponse = try {
                okHttpClient.newCall(commitRequest).execute()
            } catch (e: Exception) {
                logError("Failed to commit upload", e)
                return
            }

            val releaseInfo: ReleaseInfo = try {
                ReleaseInfo.fromJson(commitResponse.body()!!.string())
            } catch (e: Exception) {
                logError("Failed to parse commit upload response", e)
                return
            }

            log("3/4 Successfully created release. Release ID=${releaseInfo.release_id}")

            // Call 4/4: Add to Distribution group
            distributionGroups.forEach { distributionGroup ->
                val distributionRequest = Request.Builder()
                    .url("$appCenterApiBaseUrl${releaseInfo.release_url}")
                    .patch(
                        RequestBody.create(
                            MediaType.parse("application/json"),
                            "{ \"destination_name\": \"$distributionGroup\"}"
                        )
                    )
                    .build()

                val distributionResponse = try {
                    okHttpClient.newCall(distributionRequest).execute()
                } catch (e: Exception) {
                    logError("4/4 Failed to execute call to add $variantName to `$distributionGroup` distribution group.")
                    return
                }

                if (distributionResponse.isSuccessful) {
                    log("4/4 Successfully added $variantName to `$distributionGroup` distribution group.")
                } else {
                    logError("4/4 Could not add $variantName to `$distributionGroup` distribution group.")
                }
            }
        }
    }

    private fun log(message: String) {
        logger.lifecycle("$LOG_TAG $message")
    }

    private fun logError(message: String, error: Throwable? = null) {
        logger.error("$LOG_TAG $message", error)
    }
}
