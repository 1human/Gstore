import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import zed.rainxch.githubstore.convention.configureKotlinAndroid

@Suppress("unused")
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
            }

            extensions.configure<ApplicationExtension> {
                namespace = "zed.rainxch.githubstore"
                compileSdk =
                    findProperty("projectCompileSdkVersion")?.toString()?.toInt() ?: 37
                val compileSdkExt =
                    findProperty("projectCompileSdkExtension")?.toString()?.toInt()
                if (compileSdkExt != null) {
                    compileSdkExtension = compileSdkExt
                }

                defaultConfig {
                    applicationId =
                        findProperty("projectApplicationId")?.toString()
                            ?: "zed.rainxch.githubstore"
                    minSdk =
                        findProperty("projectMinSdkVersion")?.toString()?.toInt() ?: 31
                    targetSdk =
                        findProperty("projectTargetSdkVersion")?.toString()?.toInt() ?: 37
                    versionCode =
                        findProperty("projectVersionCode")?.toString()?.toInt() ?: 1
                    versionName =
                        findProperty("projectVersionName")?.toString() ?: "1.0"
                }
                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = true
                        isShrinkResources = true

                        // Ship a single arm64-v8a slice: every device we support is 64-bit ARM,
                        // so the other ABIs only inflate the APK/AAB.
                        ndk {
                            abiFilters.clear()
                            abiFilters.add("arm64-v8a")
                        }

                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro",
                        )
                    }
                }

                configureKotlinAndroid(this)
            }
        }
    }
}
