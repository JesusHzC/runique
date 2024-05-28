@file:OptIn(ExperimentalMaterial3Api::class)

package com.jesushzc.core.presentation.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jesushzc.core.presentation.designsystem.AnalyticsIcon
import com.jesushzc.core.presentation.designsystem.LogoIcon
import com.jesushzc.core.presentation.designsystem.RunIcon
import com.jesushzc.core.presentation.designsystem.RuniqueGreen
import com.jesushzc.core.presentation.designsystem.RuniqueTheme
import com.jesushzc.core.presentation.designsystem.components.util.DropDownItem

@Composable
fun RuniqueScaffold(
    modifier: Modifier = Modifier,
    withGradient: Boolean = true,
    topAppBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = topAppBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = FabPosition.Center,
        modifier = modifier,
    ) { paddingValues ->
        if (withGradient) {
            GradientBackground {
                content(paddingValues)
            }
        } else {
            content(paddingValues)
        }
    }
}

@Preview
@Composable
private fun RuniqueScaffoldPreview() {
    RuniqueTheme {
        RuniqueScaffold(
            topAppBar = {
                RuniqueToolbar(
                    showBackButton = true,
                    title = "Runique",
                    modifier = Modifier.fillMaxWidth(),
                    startContent = {
                        Icon(
                            imageVector = LogoIcon,
                            contentDescription = null,
                            tint = RuniqueGreen,
                            modifier = Modifier.size(35.dp)
                        )
                    },
                    menuItems = listOf(
                        DropDownItem(
                            icon = AnalyticsIcon,
                            title = "Analytics"
                        )
                    )
                )
            },
            floatingActionButton = {
                RuniqueFloatingActionButton(
                    icon = RunIcon,
                    onClick = { /* Handle click */ }
                )
            },
        ) {
            // Content
        }
    }
}
