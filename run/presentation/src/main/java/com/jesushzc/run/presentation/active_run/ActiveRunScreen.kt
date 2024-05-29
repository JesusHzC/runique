@file:OptIn(ExperimentalMaterial3Api::class)

package com.jesushzc.run.presentation.active_run

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jesushzc.core.presentation.designsystem.RuniqueTheme
import com.jesushzc.core.presentation.designsystem.StartIcon
import com.jesushzc.core.presentation.designsystem.StopIcon
import com.jesushzc.core.presentation.designsystem.components.RuniqueDialog
import com.jesushzc.core.presentation.designsystem.components.RuniqueFloatingActionButton
import com.jesushzc.core.presentation.designsystem.components.RuniqueOutlinedActionButton
import com.jesushzc.core.presentation.designsystem.components.RuniqueScaffold
import com.jesushzc.core.presentation.designsystem.components.RuniqueToolbar
import com.jesushzc.run.presentation.R
import com.jesushzc.run.presentation.active_run.components.RunDataCard
import com.jesushzc.run.presentation.util.hasLocationPermission
import com.jesushzc.run.presentation.util.hasNotificationPermission
import com.jesushzc.run.presentation.util.shouldShowLocationPermissionRationale
import com.jesushzc.run.presentation.util.shouldShowNotificationPermissionRationale
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActiveRunScreenRoot(
    viewModel: ActiveRunViewModel = koinViewModel()
) {
    ActiveRunScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ActiveRunScreen(
    state: ActiveRunState,
    onAction: (ActiveRunAction) -> Unit
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity

    var showLocationRationale by remember {
        mutableStateOf(false)
    }
    var showNotificationRationale by remember {
        mutableStateOf(false)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val hasCourseLocationPermission =
            perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val hasFineLocationPermission =
            perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms[Manifest.permission.POST_NOTIFICATIONS] == true
        } else {
            true
        }

        showLocationRationale = activity.shouldShowLocationPermissionRationale()
        showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = hasCourseLocationPermission and hasFineLocationPermission,
                showLocationRationale = showLocationRationale
            )
        )
        onAction(
            ActiveRunAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = hasNotificationPermission,
                showNotificationRationale = showNotificationRationale
            )
        )
    }

    LaunchedEffect(key1 = true) {
        showLocationRationale = activity.shouldShowLocationPermissionRationale()
        showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = context.hasLocationPermission(),
                showLocationRationale = showLocationRationale
            )
        )
        onAction(
            ActiveRunAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = context.hasNotificationPermission(),
                showNotificationRationale = showNotificationRationale
            )
        )

        if (showLocationRationale.not() or showNotificationRationale.not()) {
            permissionLauncher.requestRuniquePermissions(context)
        }
    }

    RuniqueScaffold(
        withGradient = false,
        topAppBar = {
            RuniqueToolbar(
                showBackButton = true,
                title = stringResource(id = R.string.active_run),
                onBackClick = {
                    onAction(ActiveRunAction.OnBackClick)
                }
            )
        },
        floatingActionButton = {
            RuniqueFloatingActionButton(
                icon = if (state.shouldTrack) StopIcon else StartIcon,
                onClick = {
                    onAction(ActiveRunAction.OnToggleRunClick)
                },
                iconSize = 20.dp,
                contentDescription = if (state.shouldTrack) {
                    stringResource(id = R.string.pause_run)
                } else {
                    stringResource(id = R.string.start_run)
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            RunDataCard(
                elapsedTime = state.elapsedTime,
                runData = state.runData,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(padding)
                    .fillMaxWidth()
            )
        }
    }
    if (state.showLocationPermissionRationale or state.showNotificationPermissionRationale) {
        RuniqueDialog(
            title = stringResource(id = R.string.permission_required),
            onDismiss = { /* Normal dismissing not allowed for permission */ },
            description = when {
                state.showLocationPermissionRationale and state.showNotificationPermissionRationale -> {
                    stringResource(id = R.string.location_notification_rationale)
                }
                state.showLocationPermissionRationale -> {
                    stringResource(id = R.string.location_rationale)
                }
                else -> {
                    stringResource(id = R.string.notification_rationale)
                }
            },
            primaryButton = {
                RuniqueOutlinedActionButton(
                    text = stringResource(id = R.string.okay),
                    isLoading = false,
                    onClick = {
                        onAction(ActiveRunAction.DismissRationaleDialog)
                        permissionLauncher.requestRuniquePermissions(context)
                    }
                )
            }
        )
    }
}

private fun ActivityResultLauncher<Array<String>>.requestRuniquePermissions(
    context: Context
) {
    val hasLocationPermission = context.hasLocationPermission()
    val hasNotificationPermission = context.hasNotificationPermission()

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val notificationPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        emptyArray()
    }

    when {
        hasLocationPermission.not() and hasNotificationPermission.not() -> {
            launch(locationPermissions + notificationPermissions)
        }
        hasLocationPermission.not() -> {
            launch(locationPermissions)
        }
        hasNotificationPermission.not() -> {
            launch(notificationPermissions)
        }
    }
}

@Preview
@Composable
private fun ActiveRunScreenPreview() {
    RuniqueTheme {
        ActiveRunScreen(
            state = ActiveRunState(),
            onAction = {}
        )
    }
}

