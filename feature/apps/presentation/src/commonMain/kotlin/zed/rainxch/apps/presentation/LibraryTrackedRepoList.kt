package zed.rainxch.apps.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import zed.rainxch.core.domain.model.library.TrackedRepo
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.githubstore.core.presentation.res.Res
import zed.rainxch.githubstore.core.presentation.res.library_not_installed
import zed.rainxch.githubstore.core.presentation.res.library_remove
import zed.rainxch.githubstore.core.presentation.res.library_tracked_title

// Opening a tracked repository goes through the same details route the installed apps use.
internal fun TrackedRepo.toNavigateAction(): AppsAction.OnNavigateToRepo =
    AppsAction.OnNavigateToRepo(repoId = repoId, sourceHost = sourceHost, owner = owner, repo = repo)

// Repositories added from a card without installing anything: they sit in the library, say they
// are not installed, and open the repository when tapped.
@Composable
internal fun LibraryTrackedRepoList(
    repos: List<TrackedRepo>,
    onOpen: (TrackedRepo) -> Unit,
    onRemove: (TrackedRepo) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        KomiText(
            text = stringResource(Res.string.library_tracked_title),
            role = KomiTextRole.SectionTitle,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        repos.forEach { repo ->
            LibraryTrackedRepoRow(
                repo = repo,
                onOpen = { onOpen(repo) },
                onRemove = { onRemove(repo) },
            )
        }
    }
}

@Composable
private fun LibraryTrackedRepoRow(
    repo: TrackedRepo,
    onOpen: () -> Unit,
    onRemove: () -> Unit,
) {
    val personality = LocalPersonality.current
    val colors = personality.colors
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(personality.shape.cornerSmall))
                .clickable(onClick = onOpen)
                .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            KomiText(
                text = repo.fullName,
                role = KomiTextRole.Label,
                color = colors.onSurface,
                maxLines = 1,
            )
            repo.description?.let { description ->
                KomiText(
                    text = description,
                    role = KomiTextRole.Body,
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
        KomiText(
            text = stringResource(Res.string.library_not_installed),
            role = KomiTextRole.LabelSmall,
            color = colors.onSurfaceVariant,
        )
        KomiText(
            text = stringResource(Res.string.library_remove),
            role = KomiTextRole.LabelSmall,
            color = colors.primary,
            modifier = Modifier.clickable(onClick = onRemove).padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}
