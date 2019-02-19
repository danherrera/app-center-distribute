import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.21"
    `java-gradle-plugin`
}

group = "io.github.danherrera"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
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