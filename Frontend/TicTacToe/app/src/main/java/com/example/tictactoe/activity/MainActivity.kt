package com.example.tictactoe.activity

import AuthRoute
import android.os.Bundle
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tictactoe.permissions.PermissionsHandler
import com.example.tictactoe.route.GameRoute
import com.example.tictactoe.view_model.permissions_view_model.PermissionViewModel
import com.example.tictactoe.view_model.route_view_model.AuthRouteViewModel
import com.example.tictactoe.view_model.route_view_model.GameRouteViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authRouteViewModel: AuthRouteViewModel = viewModel()
            val gameRouteViewModel: GameRouteViewModel = viewModel()
            val permissionViewModel: PermissionViewModel = viewModel()
            PermissionsHandler(permissionViewModel)
            Crossfade(targetState = when(authRouteViewModel.startGameScreenState.value) {
                true -> "Game"
                false -> "Auth"
            } ) { stage ->
                when(stage) {
                    "Game" -> GameRoute(gameRouteViewModel)
                    "Auth" -> AuthRoute(authRouteViewModel)
                }
            }
        }
    }
}
