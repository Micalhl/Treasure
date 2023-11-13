plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.56"
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
}

taboolib {
    install("common")
    install("common-5")
    install("module-chat")
    install("module-configuration")
    install("module-nms")
    install("module-ui")
    install("module-nms-util")
    install("platform-bukkit")
    classifier = null
    version = "6.0.12-35"

    relocate("org.serverct.parrot.parrotx", "com.mcstarrysky.treasure.taboolib.module.parrotx")
    relocate("com.mcstarrysky.starrysky", "com.mcstarrysky.treasure.taboolib.module.starrysky")
}

repositories {
    mavenCentral()
}

dependencies {
    taboo("ink.ptms:um:1.0.0-beta-18")
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11605:11605")
    compileOnly("ink.ptms.core:v11902:11902:mapped")
    compileOnly("ink.ptms.core:v11902:11902:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))

    // include
    taboo("org.tabooproject.taboolib:module-parrotx:1.5.4")
    taboo("com.mcstarrysky.taboolib:module-starrysky:1.0.13-4")

    // other
    compileOnly("com.electronwill.night-config:core:3.6.6")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.tabooproject.org/repository/releases")
            credentials {
                username = project.findProperty("taboolibUsername").toString()
                password = project.findProperty("taboolibPassword").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = project.group.toString()
        }
    }
}