package tv.own.owntv.features.settings

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import tv.own.owntv.R
import tv.own.owntv.trakt.TraktLoginScreen
import tv.own.owntv.trakt.TraktManager

@Composable
fun TraktAccountScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val traktManager = remember { TraktManager.getInstance(context) }

    val firstFocus = remember { FocusRequester() }
    val content = stringResource(R.string.settings_group_content_metadata)

    StageFullPage(
        parents = listOf(content),
        title = stringResource(R.string.trakt_account_title), // Utilise la ressource string
        count = "1",
        onBack = onBack,
        modifier = modifier,
        rowsFocus = firstFocus,
    ) {
        StageSettingsHeading(stringResource(R.string.trakt_heading), null, first = true) // Utilise la ressource string

        TraktLoginScreen(
            traktManager = traktManager,
            onLoginSuccess = {
                // Action une fois connecté
            }
        )
    }
}