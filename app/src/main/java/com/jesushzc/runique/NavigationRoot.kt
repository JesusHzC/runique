package com.jesushzc.runique

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.jesushzc.auth.presentation.intro.IntroScreenRoot
import com.jesushzc.auth.presentation.login.LoginScreenScreenRoot
import com.jesushzc.auth.presentation.register.RegisterScreenRoot
import com.jesushzc.core.util.Constants.GRAPH_AUTH
import com.jesushzc.core.util.Constants.GRAPH_RUN
import com.jesushzc.core.util.Constants.SCREEN_ACTIVE_RUN
import com.jesushzc.core.util.Constants.SCREEN_INTRO
import com.jesushzc.core.util.Constants.SCREEN_LOGIN
import com.jesushzc.core.util.Constants.SCREEN_REGISTER
import com.jesushzc.core.util.Constants.SCREEN_RUN_OVERVIEW
import com.jesushzc.run.presentation.active_run.ActiveRunScreenRoot
import com.jesushzc.run.presentation.active_run.service.ActiveRunService
import com.jesushzc.run.presentation.run_overview.RunOverviewScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
    isLoggedIn: Boolean,
    onAnalyticsClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) GRAPH_RUN else GRAPH_AUTH
    ) {
        authGraph(navController)
        runGraph(navController, onAnalyticsClick)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        startDestination = SCREEN_INTRO,
        route = GRAPH_AUTH
    ) {
        composable(route = SCREEN_INTRO) {
            IntroScreenRoot(
                onSignInClick = {
                    navController.navigate(SCREEN_LOGIN)
                },
                onSignUpClick = {
                    navController.navigate(SCREEN_REGISTER)
                }
            )
        }

        composable(route = SCREEN_REGISTER) {
            RegisterScreenRoot(
                onSignInClick = {
                    navController.navigate(SCREEN_LOGIN) {
                        popUpTo(SCREEN_REGISTER) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulRegistration = {
                    navController.navigate(SCREEN_LOGIN)
                }
            )
        }
        
        composable(route = SCREEN_LOGIN) {
            LoginScreenScreenRoot(
                onLoginSuccess = {
                    navController.navigate(GRAPH_RUN) {
                        popUpTo(GRAPH_AUTH) {
                            inclusive = true
                        }
                    }
                },
                onSignUpClick = {
                    navController.navigate(SCREEN_REGISTER) {
                        popUpTo(SCREEN_LOGIN) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }
    }
}

private fun NavGraphBuilder.runGraph(
    navController: NavHostController,
    onAnalyticsClick: () -> Unit
) {
    navigation(
        startDestination = SCREEN_RUN_OVERVIEW,
        route = GRAPH_RUN
    ) {
        composable(SCREEN_RUN_OVERVIEW) {
            RunOverviewScreenRoot(
                onStartRunClick = {
                    navController.navigate(SCREEN_ACTIVE_RUN)
                },
                onLogoutClick = {
                    navController.navigate(GRAPH_AUTH) {
                        popUpTo(GRAPH_RUN) {
                            inclusive = true
                        }
                    }
                },
                onAnalyticsClick = onAnalyticsClick
            )
        }

        composable(
            route = SCREEN_ACTIVE_RUN,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "runique://active_run"
                }
            )
        ) {
            val context = LocalContext.current
            ActiveRunScreenRoot(
                onBack = {
                    navController.navigateUp()
                },
                onFinish = {
                    navController.navigateUp()
                },
                onServiceToggle = { shouldServiceRun ->
                    if (shouldServiceRun) {
                        context.startService(
                            ActiveRunService.createStartIntent(
                                context = context,
                                activityClass = MainActivity::class.java
                            )
                        )
                    } else {
                        context.startService(
                            ActiveRunService.createStopIntent(
                                context = context
                            )
                        )
                    }
                }
            )
        }
    }
}

