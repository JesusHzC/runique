package com.jesushzc.runique

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.jesushzc.auth.presentation.intro.IntroScreenRoot
import com.jesushzc.auth.presentation.login.LoginScreenScreenRoot
import com.jesushzc.auth.presentation.register.RegisterScreenRoot
import com.jesushzc.core.util.Constants.GRAPH_AUTH
import com.jesushzc.core.util.Constants.GRAPH_RUN
import com.jesushzc.core.util.Constants.SCREEN_INTRO
import com.jesushzc.core.util.Constants.SCREEN_LOGIN
import com.jesushzc.core.util.Constants.SCREEN_REGISTER
import com.jesushzc.core.util.Constants.SCREEN_RUN_OVERVIEW

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = GRAPH_AUTH
    ) {
        authGraph(navController)
        runGraph(navController)
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

private fun NavGraphBuilder.runGraph(navController: NavHostController) {
    navigation(
        startDestination = SCREEN_RUN_OVERVIEW,
        route = GRAPH_RUN
    ) {
        composable(SCREEN_RUN_OVERVIEW) {
            Text("Run Overview")
        }
    }
}

