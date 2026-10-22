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
import zed.rainxch.core.domain.system.RepoMatchSuggestion
import zed.rainxch.core.presentation.components.text.KomiText
import zed.rainxch.core.presentation.components.text.KomiTextRole
import zed.rainxch.core.presentation.locals.LocalPersonality
import zed.rainxch.githubstore.core.presentation.res.Res
import zed.rainxch.githubstore.core.presentation.res.library_from_github
import zed.rainxch.githubstore.core.presentation.res.library_not_installed

// A repository the library found on GitHub rather than on disk: it reads like an app row, but
// the trailing label says nothing is installed yet.
@Composable
internal fun LibraryRemoteRepoRow(
    suggestion: RepoMatchSuggestion,
    onOpen: () -> Unit,
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
                text = "${suggestion.owner}/${suggestion.repo}",
                role = KomiTextRole.Label,
                color = colors.onSurface,
                maxLines = 1,
            )
            suggestion.description?.let { description ->
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
    }
}

// Opening a search hit means opening its repository, which is all the details screen needs; the
// library knows no local id for it.
internal fun RepoMatchSuggestion.toNavigateAction(): AppsAction.OnNavigateToRepo =
    AppsAction.OnNavigateToRepo(repoId = 0L, sourceHost = sourceHost, owner = owner, repo = repo)

// The remote half of the library search: a titled block that follows the installed apps.
@Composable
internal fun LibraryRemoteRepoList(
    suggestions: List<RepoMatchSuggestion>,
    onOpen: (RepoMatchSuggestion) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        KomiText(
            text = stringResource(Res.string.library_from_github),
            role = KomiTextRole.SectionTitle,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        suggestions.forEach { suggestion ->
            LibraryRemoteRepoRow(suggestion = suggestion, onOpen = { onOpen(suggestion) })
        }
    }
}
