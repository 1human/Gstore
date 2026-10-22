package zed.rainxch.githubstore.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(commonExtension: ApplicationExtension) {
    commonExtension.compileSdk =
        findProperty("projectCompileSdkVersion")?.toString()?.toInt() ?: 37
    val compileSdkExt = findProperty("projectCompileSdkExtension")?.toString()?.toInt()
    if (compileSdkExt != null) {
        commonExtension.compileSdkExtension = compileSdkExt
    }

    commonExtension.defaultConfig.minSdk =
        findProperty("projectMinSdkVersion")?.toString()?.toInt() ?: 26

    commonExtension.compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    commonExtension.lint {
        baseline = file("lint-baseline.xml")
        disable += setOf("NewerVersionAvailable", "GradleDependency", "AndroidGradlePluginVersion")
        abortOnError = true
        checkReleaseBuilds = false
    }

    configureKotlin()

    dependencies {
        "coreLibraryDesugaring"(libs.findLibrary("android-desugarJdkLibs").get())
    }
}

internal fun Project.configureKotlinAndroid(commonExtension: LibraryExtension) {
    commonExtension.compileSdk =
        findProperty("projectCompileSdkVersion")?.toString()?.toInt() ?: 37
    val compileSdkExt = findProperty("projectCompileSdkExtension")?.toString()?.toInt()
    if (compileSdkExt != null) {
        commonExtension.compileSdkExtension = compileSdkExt
    }

    commonExtension.defaultConfig.minSdk =
        findProperty("projectMinSdkVersion")?.toString()?.toInt() ?: 26

    commonExtension.compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    commonExtension.lint {
        baseline = file("lint-baseline.xml")
        disable += setOf("NewerVersionAvailable", "GradleDependency", "AndroidGradlePluginVersion")
        abortOnError = true
        checkReleaseBuilds = false
    }

    configureKotlin()

    dependencies {
        "coreLibraryDesugaring"(libs.findLibrary("android-desugarJdkLibs").get())
    }
}

internal fun Project.configureKotlin() {
    val warningsAsErrors = findProperty("komi.warningsAsErrors")?.toString()?.toBoolean() ?: true
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            allWarningsAsErrors.set(warningsAsErrors)
        }
    }
}
