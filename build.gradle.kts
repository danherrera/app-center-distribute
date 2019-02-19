import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.21"
    `java-gradle-plugin`
    maven
}

group = "com.github.danherrera"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

gradlePlugin {
    plugins {
        register("appCenterDistribute") {
            id = "com.github.danherrera.appcenterdistribute"
            implementationClass = "com.github.danherrera.appcenterdistribute.AppCenterDistributePlugin"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compileOnly(gradleApi())
    implementation("com.android.tools.build:gradle:3.3.1")
    implementation("com.squareup.okhttp3:okhttp:3.13.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}