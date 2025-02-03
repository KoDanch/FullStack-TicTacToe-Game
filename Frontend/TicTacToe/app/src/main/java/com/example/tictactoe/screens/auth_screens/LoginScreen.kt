package com.example.tictactoe.screens.auth_screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tictactoe.ui.theme.PurpleGrey40
import com.example.tictactoe.view_model.fields_view_model.AuthViewModel
import com.example.tictactoe.view_model.fields_view_model.GameViewModel
import com.example.tictactoe.view_model.route_view_model.AuthRouteViewModel


@Composable
fun LoginScreen(authRouteViewModel: AuthRouteViewModel) {
    val authFields: AuthViewModel = viewModel()
    val gameViewModel: GameViewModel = viewModel()
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 64.dp, start = 32.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color.Red, shape = CircleShape)
                .align(Alignment.TopStart)
                .clickable {
                    authRouteViewModel.updateLoginState()
                    authFields.clearPassword()
                }, contentAlignment = Alignment.Center
        ) {
            Text(text = "X", color = Color.White, fontSize = 16.sp)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                modifier = Modifier.padding(end = 32.dp),
                text = "Login",
                fontSize = 32.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(32.dp))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 32.dp),
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                placeholder = { Text(text = "Username") },
                value = authFields.username.value,
                onValueChange = { name ->
                    authFields.username.value = name
                })
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 32.dp),
                shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                placeholder = { Text(text = "Password") },
                value = authFields.password.value,
                onValueChange = { password ->
                    authFields.password.value = password
                })
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = PurpleGrey40
                ),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    authRouteViewModel.updateProcessBarState()
                    when (authFields.username.value.isNotEmpty() && authFields.password.value.isNotEmpty()) {
                        true -> {
                            authFields.signInUser()
                        }

                        false -> {
                            Toast.makeText(
                                context,
                                "Please fill in the required fields",
                                Toast.LENGTH_SHORT
                            ).show()
                            authRouteViewModel.updateProcessBarState()
                        }
                    }
                }) {
                Text(text = "Login")
            }
        }
        if (authRouteViewModel.processBarState.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 32.dp)
            )

        } else {
            if (authRouteViewModel.respMessage.value.isNotEmpty()) {
                Toast.makeText(context, authRouteViewModel.respMessage.value, Toast.LENGTH_SHORT).show()
                authRouteViewModel.clearMessage()
                authFields.clearPassword()
                if (authRouteViewModel.respToken.value.isNotEmpty()) {
                    gameViewModel.updateToken(authRouteViewModel.respToken.value)
                    gameViewModel.getUserData(authRouteViewModel.respToken.value)
                    gameViewModel.token.value = authRouteViewModel.respToken.value
                    authFields.updateToken(authRouteViewModel.respToken.value)
                }
            }
        }
    }
}