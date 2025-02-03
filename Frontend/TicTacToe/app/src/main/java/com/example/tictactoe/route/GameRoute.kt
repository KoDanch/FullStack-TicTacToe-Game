package com.example.tictactoe.route

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tictactoe.screens.EditUserDetailsScreen
import com.example.tictactoe.screens.PhotoReportScreen
import com.example.tictactoe.screens.game_screens.GameProcessScreen
import com.example.tictactoe.screens.game_screens.StartGameScreen
import com.example.tictactoe.view_model.fields_view_model.AuthViewModel
import com.example.tictactoe.view_model.fields_view_model.GameViewModel
import com.example.tictactoe.view_model.permissions_view_model.PermissionViewModel
import com.example.tictactoe.view_model.route_view_model.AuthRouteViewModel
import com.example.tictactoe.view_model.route_view_model.GameRouteViewModel

@Composable
fun GameRoute(gameRouteViewModel: GameRouteViewModel) {
    val gameViewModel: GameViewModel = viewModel()
    val authRouteViewModel: AuthRouteViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    Crossfade(
        targetState = when {
            gameRouteViewModel.editingUserDataScreenState.value && authRouteViewModel.startGameScreenState.value -> "EditProfile"
            gameRouteViewModel.photoReportScreenState.value && authRouteViewModel.startGameScreenState.value -> "PhotoReport"

            gameRouteViewModel.startGameProcessState.value ->"GameProcess"
            authRouteViewModel.startGameScreenState.value -> "StartGame"
            else -> "Error"
        }
    ) { screen ->
        when (screen) {
            "GameProcess" -> GameProcessScreen(gameRouteViewModel)
            "StartGame" -> {
                StartGameScreen(gameViewModel, gameRouteViewModel)
                gameViewModel.parsingLeaderboard()
            }
            "EditProfile" -> EditUserDetailsScreen(authViewModel)
            "PhotoReport" -> PhotoReportScreen(gameRouteViewModel, gameViewModel)
        }
    }
}