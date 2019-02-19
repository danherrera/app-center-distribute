package io.github.danherrera.appcenterdistribute

import org.gradle.api.Project

open class AppCenterDistributePluginExtension(private val project: Project) {
    var ownerName = ""
    var apiToken = ""
    var variantToAppName: Map<String, String> = emptyMap()
    var appCenterApiBaseUrl = "https://api.appcenter.ms/"
    var appCenterApiRootEndpoint = "v0.1/apps/"
    var distributionGroups = listOf("Collaborators")
    val baseUrl
        get() = appCenterApiBaseUrl + appCenterApiRootEndpoint
}
