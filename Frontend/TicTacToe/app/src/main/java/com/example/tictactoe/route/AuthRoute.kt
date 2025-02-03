import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import com.example.tictactoe.screens.auth_screens.LoginScreen
import com.example.tictactoe.screens.auth_screens.MainScreen
import com.example.tictactoe.screens.auth_screens.SignupScreen
import com.example.tictactoe.view_model.route_view_model.AuthRouteViewModel

@Composable
fun AuthRoute(authRouteViewModel: AuthRouteViewModel) {
    Crossfade(targetState = when {
        !authRouteViewModel.loginScreenState.value && !authRouteViewModel.signUpScreenState.value -> "Main"
        authRouteViewModel.loginScreenState.value -> "Login"
        authRouteViewModel.signUpScreenState.value -> "Signup"
        else -> "ErrorScreen"
    }, label = "",
        animationSpec = tween(durationMillis = 300)
    ) { screen ->
        when (screen) {
            "Main" -> MainScreen(authRouteViewModel)
            "Login" -> LoginScreen(authRouteViewModel)
            "Signup" -> SignupScreen(authRouteViewModel)
        }
    }



}

