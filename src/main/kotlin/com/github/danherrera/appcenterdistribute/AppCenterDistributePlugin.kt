package com.github.danherrera.appcenterdistribute

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class AppCenterDistributePlugin : Plugin<Project> {

    companion object {
        const val GROUP_NAME = "AppCenter"
    }

    override fun apply(project: Project) {
        project.extensions.create("appCenterDistribute", AppCenterDistributePluginExtension::class.java, project)

        if (project.plugins.hasPlugin(AppPlugin::class.java)) {
            val distributeAllTask: Task = project.tasks.create("distributeAllToAppCenter").apply {
                group = GROUP_NAME
                description = "Distribute all variants to App Center"
            }

            val android: AppExtension = project.extensions.getByName("android") as AppExtension
            android.applicationVariants.all { variant ->
                val distributeVariantTask = project.tasks.create(
                    "distribute${variant.name.capitalize()}ToAppCenter",
                    AppCenterDistributeTask::class.java
                )
                    .apply {
                        group = GROUP_NAME
                        dependsOn(variant.assembleProvider)
                        description = "Distribute ${variant.name} to App Center"
                        variantName = variant.name.capitalize()
                        outputFile = variant.outputs.first()?.outputFile
                    }
                distributeAllTask.dependsOn(distributeVariantTask)
            }
        }
    }
}