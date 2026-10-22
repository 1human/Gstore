package zed.rainxch.githubstore.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureJvmTarget() {
    extensions.configure<KotlinMultiplatformExtension> {
        jvm()
    }
}
