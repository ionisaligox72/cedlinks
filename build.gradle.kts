@file:Suppress("MayBeConstant")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    kotlin("jvm") version "1.3.72"
    kotlin("kapt") version "1.3.72"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("application")
    id("com.heroku.sdk.heroku-gradle") version "2.0.0"
}

buildscript {
    repositories {
        jcenter()
        mavenCentral()
    }
}

object This {
    val version = "1.0"
    val groupId = "com.beust"
    val artifactId = "cedlinks"
    val description = "CedLinks"
    val url = "https://github.com/cbeust/cedlinks"
    val scm = "github.com/cbeust/cedlinks.git"

    // Should not need to change anything below
    val issueManagementUrl = "https://$scm/issues"
    val isSnapshot = version.contains("SNAPSHOT")
}

allprojects {
    group = This.groupId
    version = This.version
}

object Version {
    const val kotlin = "1.3.72"
    const val micronaut = "2.0.1"
}

repositories {
    jcenter()
    mavenCentral()
    maven { setUrl("https://plugins.gradle.org/m2") }
}

dependencies {
    kapt("io.micronaut:micronaut-inject-java:${Version.micronaut}")

    listOf(kotlin("stdlib"),
            "io.micronaut:micronaut-runtime:${Version.micronaut}",
            "io.micronaut:micronaut-http-server-netty:${Version.micronaut}",
            "ch.qos.logback:logback-classic:1.2.3").forEach {
        implementation(it)
    }

    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.7")

    listOf("com.github.spullara.mustache.java:compiler:0.9.6",
            "org.postgresql:postgresql:42.2.14.jre7",
            "org.jetbrains.exposed:exposed:0.12.1",
            "com.squareup.retrofit2:retrofit:2.9.0",
            "com.squareup.retrofit2:converter-gson:2.9.0",
            "com.squareup.okhttp3:logging-interceptor:3.9.0").forEach {
        implementation(it)
    }

    testCompile("org.testng:testng:6.14.3")
}

allOpen {
    annotation("io.micronaut.aop.Around")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        javaParameters = true
    }
}

kapt {
    arguments {
        arg("micronaut.processing.incremental", true)
        arg("micronaut.processing.annotations", "example.micronaut.*")
        arg("micronaut.processing.group", "example.micronaut")
        arg("micronaut.processing.module", "complete")
    }
}

application {
    mainClassName = "com.beust.cedlinks.MainKt"
}

tasks {
    named<ShadowJar>("shadowJar") {
        baseName = "ced-links"
        mergeServiceFiles()
//        excludes = listOf("META-INF/*.DSA", "META-INF/*.RSA", "META-INF/*.SF")
        manifest {
            attributes(mapOf(
                    "Implementation-Title" to rootProject.name,
                    "Implementation-Version" to rootProject.version,
                    "Implementation-Vendor-Id" to rootProject.group,
            //        attributes "Build-Time": ZonedDateTime.now(ZoneId.of("UTC"))
            //                .format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
//                    "Built-By" to java.net.InetAddress.localHost.hostName,
                    "Created-By" to "Gradle "+ gradle.gradleVersion,
                    "Main-Class" to "com.beust.cedlinks.MainKt"))
        }
    }
}

// Heroku

tasks.register("stage") {
    dependsOn("clean", "shadowJar")
}

with(heroku) {
    appName = "ced-links"
}
