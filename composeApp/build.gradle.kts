import com.github.jk1.license.render.InventoryMarkdownReportRenderer
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.jaredsburrowsLicense)
}

kotlin {
    android {
        namespace = "net.adhikary.mrtbuddy.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        androidResources {
            enable = true
        }
        withHostTest { }
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.koin.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            //  implementation(libs.androidx.lifecycle.viewmodel)
            implementation(compose.materialIconsExtended)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutine)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.napier)
            implementation(libs.multiplatform.settings)
            implementation(libs.navigation.compose)
            implementation("io.github.kyant0:backdrop:2.0.1")

            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            api(libs.compose.webview.multiplatform)
            implementation(libs.multiplatform.markdown.renderer)

            implementation(libs.coil.compose)
            implementation(libs.zoomimage.compose.coil3)
            implementation(libs.compottie)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
            implementation(libs.kotlinx.coroutine.test)
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("androidRuntimeClasspath", compose.uiTooling)
}

room {
    schemaDirectory("$projectDir/schemas")
}

licenseReport {
    unionParentPomLicenses = false
    renderers = arrayOf(
        InventoryMarkdownReportRenderer(
            "open-source-licenses.md",
            "Open Source Libraries"
        )
    )
}

tasks.register("processLicenseReport") {
    dependsOn("generateLicenseReport")

    doLast {
        val reportPath = file("build/reports/dependency-license/open-source-licenses.md")
        val processedPath = file("build/reports/dependency-license/processed-open-source-licenses.md")

        if (!reportPath.exists()) {
            throw GradleException("License report not found at $reportPath")
        }

        val content = reportPath.readText()
        val processedContent = content.replace(
            Regex("_\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} [A-Z]+_"),
            ""
        )
        processedPath.writeText(processedContent)
    }
}

tasks.register("copyLicenseReportToAssets") {
    dependsOn("processLicenseReport")

    doLast {
        val processedPath = file("build/reports/dependency-license/processed-open-source-licenses.md")
        val commonAssetsPath = file("src/commonMain/composeResources/files")

        if (!processedPath.exists()) {
            throw GradleException("Processed license report not found at $processedPath")
        }

        copy {
            from(processedPath)
            into(commonAssetsPath)
            rename { "open-source-licenses.md" }
        }
    }
}
