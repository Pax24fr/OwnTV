package tv.own.owntv.trakt

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.Surface
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tv.own.owntv.R

@Composable
fun TraktLoginScreen(
    traktManager: TraktManager,
    onLoginSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(60)
        runCatching { focusRequester.requestFocus() }
    }

    var userCode by remember { mutableStateOf<String?>(null) }
    var verificationUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Toutes les ressources string DOIVENT être récupérées ici (au niveau racine du composable)
    val initialStatus = stringResource(R.string.trakt_status_press)
    var statusMessage by remember { mutableStateOf(initialStatus) }

    val msgGenerating = stringResource(R.string.trakt_status_generating)
    val msgSuccess = stringResource(R.string.trakt_status_success)
    val msgExpired = stringResource(R.string.trakt_status_expired)
    val msgError = stringResource(R.string.trakt_status_error)
    val statusTemplate = stringResource(R.string.trakt_status_visit_format) // <-- Déplacé ici !

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.trakt_login_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface // <-- Ajouté ici pour le titre
            )

            if (userCode == null) {
                Button(
                    onClick = {
                        isLoading = true
                        statusMessage = msgGenerating
                        coroutineScope.launch {
                            val codeResponse = withContext(Dispatchers.IO) {
                                traktManager.requestDeviceCode()
                            }

                            if (codeResponse != null) {
                                userCode = codeResponse.userCode
                                verificationUrl = codeResponse.verificationUrl
                                isLoading = false
                                // On utilise la variable pré-chargée (pas de stringResource ici !)
                                statusMessage = statusTemplate.format(codeResponse.verificationUrl)

                                val success = withContext(Dispatchers.IO) {
                                    traktManager.pollForAccessToken(
                                        deviceCode = codeResponse.deviceCode,
                                        intervalSeconds = codeResponse.interval,
                                        expiresIn = codeResponse.expiresIn
                                    )
                                }

                                if (success) {
                                    statusMessage = msgSuccess
                                    onLoginSuccess()
                                } else {
                                    statusMessage = msgExpired
                                }
                            } else {
                                isLoading = false
                                statusMessage = msgError
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.focusRequester(focusRequester)
                ) {
                    Text(if (isLoading) stringResource(R.string.trakt_loading) else stringResource(R.string.trakt_assoc_button))
                }
            } else {
                Surface(
                    modifier = Modifier.padding(16.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(R.string.trakt_step_1), style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = verificationUrl ?: "https://trakt.tv/activate",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = stringResource(R.string.trakt_step_2), style = MaterialTheme.typography.bodyMedium)
                        userCode?.let { code ->
                            Text(
                                text = code,
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 4.sp
                            )
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.trakt_waiting_validation),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // <-- Texte secondaire lisible
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
