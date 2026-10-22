package zed.rainxch.core.domain.model.library

import kotlinx.serialization.Serializable

// A repository the user added to the library without installing anything: it is a reminder that
// lives next to the installed apps and is marked as not installed until a release is installed.
@Serializable
data class TrackedRepo(
    val repoId: Long,
    val owner: String,
    val repo: String,
    val description: String? = null,
    val stars: Int? = null,
    val sourceHost: String? = null,
    val addedAt: Long = 0L,
) {
    val fullName: String get() = "$owner/$repo"
}
