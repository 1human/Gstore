package zed.rainxch.core.domain.model.library

import kotlinx.coroutines.flow.first
import zed.rainxch.core.domain.repository.TweaksRepository

/**
 * Adds a repository to "Added to library". The list is stored as one value, so every screen that
 * offers the action goes through this read-modify-write instead of repeating it.
 *
 * @return true when the repository was not in the library yet.
 */
suspend fun TweaksRepository.addToLibrary(repo: TrackedRepo): Boolean {
    val current = getTrackedRepos().first()
    if (current.any { it.repoId == repo.repoId }) return false

    setTrackedRepos(current + repo)
    return true
}
