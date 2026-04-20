import java.io.ByteArrayOutputStream

plugins {
    id("net.fabricmc.fabric-loom")
    kotlin("jvm") version "2.3.20"
    alias(libs.plugins.ksp)
    alias(libs.plugins.meowdding.auto.mixins)
    alias(libs.plugins.buildconfig)
    `versioned-catalogues`
    idea
}

val archiveName = "tooltipthingy"

group = "me.owdding"
version = "1.0.0"

loom {
    runs { forEach { it.ideConfigGenerated(it.environment == "client") } }
}

repositories {
    fun scopedMaven(url: String, vararg paths: String) = maven(url) { content { paths.forEach(::includeGroupAndSubgroups) } }

    scopedMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "me.djtheredstoner")
    scopedMaven("https://repo.hypixel.net/repository/Hypixel", "net.hypixel")
    scopedMaven("https://maven.parchmentmc.org/", "org.parchmentmc")
    scopedMaven("https://api.modrinth.com/maven", "maven.modrinth")
    scopedMaven(
        "https://maven.teamresourceful.com/repository/maven-public/",
        "earth.terrarium",
        "com.teamresourceful",
        "tech.thatgravyboat",
        "me.owdding",
        "com.terraformersmc"
    )
    scopedMaven("https://maven.nucleoid.xyz/", "eu.pb4")
    mavenCentral()
}


kotlin {
    jvmToolchain(25)
    compilerOptions {
        freeCompilerArgs.add("-Xname-based-destructuring=complete")
        freeCompilerArgs.add("-Xcontext-parameters")
        freeCompilerArgs.add("-Xnullability-annotations=@org.jspecify.annotations:warn")
    }
}

tasks.processResources {
    val range = if ("minecraft.range" in versionedCatalog.versions) {
        versionedCatalog.versions["minecraft.range"].toString()
    } else {
        val start = versionedCatalog.versions.getOrFallback("minecraft.start", "minecraft")
        val end = versionedCatalog.versions.getOrFallback("minecraft.end", "minecraft")
        ">=$start <=$end"
    }
    val replacements = mapOf(
        "version" to version,
        "minecraft_range" to range,
        "fabric_lang_kotlin" to versionedCatalog.versions["fabric.language.kotlin"],
        "sbapi" to versionedCatalog.versions["skyblockapi"],
        "rconfigkt" to versionedCatalog.versions["resourceful.configkt"],
        "rconfig" to versionedCatalog.versions["resourceful.config"],
    )
    inputs.properties(replacements)

    filesMatching("fabric.mod.json") {
        expand(replacements)
    }
    with(copySpec {
        from(rootProject.file("src/lang")).include("*.json").into("assets/tooltipthingy/lang")
    })
}

dependencies {
    minecraft(versionedCatalog["minecraft"])

    implementation(versionedCatalog["fabric.api"])
    implementation(libs.fabric.loader)

    implementation(libs.meowdding.ktmodules)
    ksp(libs.meowdding.ktmodules)

    "api"(versionedCatalog["skyblockapi"]) {
        capabilities { requireCapability("tech.thatgravyboat:skyblock-api-${stonecutter.current.version}") }
    }
    "include"(versionedCatalog["skyblockapi"]) {
        capabilities { requireCapability("tech.thatgravyboat:skyblock-api-${stonecutter.current.version}") }
    }
    "api"(versionedCatalog["meowdding.lib"]) {
        capabilities { requireCapability("me.owdding.meowdding-lib:meowdding-lib-${stonecutter.current.version}") }
    }
    "include"(versionedCatalog["meowdding.lib"]) {
        capabilities { requireCapability("me.owdding.meowdding-lib:meowdding-lib-${stonecutter.current.version}") }
    }

    implementation(versionedCatalog["resourceful.config"])
    include(versionedCatalog["resourceful.config"])
    implementation(versionedCatalog["resourceful.configkt"])
    include(versionedCatalog["resourceful.configkt"])
    implementation(versionedCatalog["placeholders"])

    implementation(versionedCatalog["olympus"])
    include(versionedCatalog["olympus"])
    implementation(versionedCatalog["resourceful.lib"])
    include(versionedCatalog["resourceful.lib"])
}

base {
    archivesName = archiveName
}

tasks.build {
    doLast {
        val sourceFile = rootProject.projectDir.resolve("versions/${project.name}/build/libs/${archiveName}-$version.jar")
        val targetFile = rootProject.projectDir.resolve("build/libs/${archiveName}-$version-${stonecutter.current.version}.jar")
        targetFile.parentFile.mkdirs()
        targetFile.writeBytes(sourceFile.readBytes())
    }
}

autoMixins {
    mixinPackage = "me.owdding.tooltipthingy.mixins"
    projectName = "tooltipthingy"
}

ksp {
    arg("meowdding.project_name", "TooltipThingy")
    arg("meowdding.package", "me.owdding.tooltipthingy.generated")
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/tooltipthingy.accesswidener")

    runs { forEach { it.ideConfigGenerated(it.environment == "client") } }
}


val gitRef = tasks.register<Exec>("gitRef") {
    outputs.upToDateWhen { false }
    standardOutput = ByteArrayOutputStream()
    commandLine("git", "rev-parse", "HEAD")
}

val gitBranch = tasks.register<Exec>("getBranch") {
    outputs.upToDateWhen { false }
    standardOutput = ByteArrayOutputStream()
    commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
}

tasks.generateBuildConfig {
    dependsOn(gitRef, gitBranch)
    mustRunAfter(gitRef, gitBranch)
}

buildConfig {
    packageName("me.owdding.tooltipthingy.generated")
    className("BuildInfo")

    buildConfigField("String", "VERSION", "\"${rootProject.version}\"")

    buildConfigField("String", "GIT_BRANCH", gitBranch.map { "\"${it.standardOutput.toString().substringBefore("\n")}\"" })
    buildConfigField("String", "GIT_REF", gitRef.map { "\"${it.standardOutput.toString().substringBefore("\n")}\"" })
}
