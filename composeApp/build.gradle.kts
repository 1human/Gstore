import com.android.build.api.dsl.ApplicationExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.convention.cmp.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.hot.reload)
}

extensions.configure<ApplicationExtension> {
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.core.splashscreen)

            implementation(libs.koin.android)
            implementation(libs.androidx.work.runtime)
            implementation(libs.shizuku.provider)
        }
        commonMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.core.domain)
            implementation(projects.core.presentation)

            // Coil SVG decoder registered in App() via SingletonImageLoaderFactory
            implementation(libs.coil3.compose)
            implementation(libs.coil3.svg)

            implementation(projects.feature.apps.domain)
            implementation(projects.feature.apps.data)
            implementation(projects.feature.apps.presentation)

            implementation(projects.feature.auth.domain)
            implementation(projects.feature.auth.data)
            implementation(projects.feature.auth.presentation)

            implementation(projects.feature.details.domain)
            implementation(projects.feature.details.data)
            implementation(projects.feature.details.presentation)

            implementation(projects.feature.devProfile.domain)
            implementation(projects.feature.devProfile.data)
            implementation(projects.feature.devProfile.presentation)

            implementation(projects.feature.repoPages.domain)
            implementation(projects.feature.repoPages.data)
            implementation(projects.feature.repoPages.presentation)

            implementation(projects.feature.favourites.domain)
            implementation(projects.feature.favourites.data)
            implementation(projects.feature.favourites.presentation)

            implementation(projects.feature.home.domain)
            implementation(projects.feature.home.data)
            implementation(projects.feature.home.presentation)

            implementation(projects.feature.feed.domain)
            implementation(projects.feature.feed.data)
            implementation(projects.feature.feed.presentation)

            implementation(projects.feature.search.domain)
            implementation(projects.feature.search.data)
            implementation(projects.feature.search.presentation)

            implementation(projects.feature.profile.presentation)

            implementation(projects.feature.starred.domain)
            implementation(projects.feature.starred.data)
            implementation(projects.feature.starred.presentation)

            implementation(projects.feature.recentlyViewed.presentation)
            implementation(projects.feature.tweaks.presentation)

            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.bundles.koin.common)
            implementation(libs.jetbrains.compose.material.icons.extended)

            implementation(libs.touchlab.kermit)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.components.resources)

            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.jetbrains.compose.viewmodel)
            implementation(libs.jetbrains.lifecycle.compose)
        }

        jvmMain {
            dependencies {
                implementation(projects.core.presentation)
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.kotlin.stdlib)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.koin.compose.viewmodel)

                implementation(libs.slf4j.simple)
                implementation(libs.jna.platform)
            }
        }
    }
}

// jlink needs a full JDK (jmods + jpackage). The daemon JVM that
// `gradle-daemon-jvm.properties` provisions is a runtime image without jmods, and
// packaging with it fails with "This JDK does not contain packaged modules".
// PACKAGING_JAVA_HOME wins, otherwise pick an installed Java 21 toolchain.
val packagingJavaHome: String? =
    System.getenv("PACKAGING_JAVA_HOME")?.takeIf { it.isNotBlank() }
        ?: runCatching {
            extensions
                .getByType<org.gradle.jvm.toolchain.JavaToolchainService>()
                .launcherFor { languageVersion.set(JavaLanguageVersion.of(21)) }
                .orNull
                ?.metadata
                ?.installationPath
                ?.asFile
                ?.absolutePath
        }.getOrNull()

compose.desktop {
    application {
        packagingJavaHome?.let { javaHome = it }
        mainClass = "zed.rainxch.githubstore.DesktopAppKt"
        nativeDistributions {
            packageName = "Komi-Store"
            packageVersion =
                project
                    .findProperty("projectVersionName")
                    ?.toString()
                    ?: "1.0"
            vendor = "rainxchzed"
            // includeAllModules would copy every JDK module into the bundle: the runtime
            // image alone came out at 460 MB. Ship the modules the app actually needs.
            modules(
                "java.desktop",
                "java.logging",
                "java.management",
                "java.naming",
                "java.prefs",
                "java.sql",
                "jdk.charsets",
                "jdk.crypto.ec",
                "jdk.unsupported",
            )

            val currentOs =
                org.gradle.internal.os.OperatingSystem
                    .current()
            targetFormats(
                *when {
                    currentOs.isWindows -> arrayOf(TargetFormat.Exe, TargetFormat.Msi)
                    currentOs.isMacOsX -> arrayOf(TargetFormat.Dmg, TargetFormat.Pkg)
                    currentOs.isLinux -> arrayOf(TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.AppImage)
                    else -> emptyArray()
                },
            )
            windows {
                iconFile.set(project.file("src/jvmMain/resources/logo/app_icon.ico"))
                menuGroup = "Komi Store"
                shortcut = true
                perUserInstall = true
            }
            macOS {
                iconFile.set(project.file("src/jvmMain/resources/logo/app_icon.icns"))
                bundleID = "zed.rainxch.githubstore"

                infoPlist {
                    extraKeysRawXml =
                        """
                        <key>CFBundleURLTypes</key>
                        <array>
                            <dict>
                                <key>CFBundleURLName</key>
                                <string>Komi Store Deep Link</string>
                                <key>CFBundleURLSchemes</key>
                                <array>
                                    <string>githubstore</string>
                                </array>
                            </dict>
                        </array>
                        """.trimIndent()
                }
            }
            linux {
                iconFile.set(project.file("src/jvmMain/resources/logo/app_icon.png"))
                appRelease =
                    project
                        .findProperty("projectVersionName")
                        ?.toString()
                        ?: "1.0"
                debPackageVersion =
                    project
                        .findProperty("projectVersionName")
                        ?.toString()
                        ?: "1.0"
                menuGroup = "Development"
                appCategory = "Development"
            }
        }
    }
}

tasks.register("printRuntimeDependencies") {
    description = "Lists all runtime dependencies for licenses.json maintenance."
    group = "verification"
    val coordinatesProvider: Provider<List<String>> =
        project.provider {
            val cfg =
                configurations.findByName("releaseRuntimeClasspath")
                    ?: configurations.findByName("androidReleaseRuntimeClasspath")
                    ?: error("No release runtime classpath configuration found.")
            cfg.incoming.resolutionResult.allComponents
                .asSequence()
                .map { it.id }
                .filterIsInstance<ModuleComponentIdentifier>()
                .map { "${it.group}:${it.module}:${it.version}" }
                .distinct()
                .sorted()
                .toList()
        }
    notCompatibleWithConfigurationCache("Resolves Android variant configurations at execution time.")
    doLast {
        coordinatesProvider.get().forEach { println(it) }
    }
}
