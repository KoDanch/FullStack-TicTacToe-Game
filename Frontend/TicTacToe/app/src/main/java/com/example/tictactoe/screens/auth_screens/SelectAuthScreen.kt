package com.example.tictactoe.screens.auth_screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.R
import com.example.tictactoe.ui.theme.PurpleGrey40
import com.example.tictactoe.view_model.route_view_model.AuthRouteViewModel

@Composable
fun MainScreen(authRouteViewModel: AuthRouteViewModel) {
    Image(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .blur(16.dp),
        painter = painterResource(R.drawable.backgroundtic),
        contentDescription = null
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = "TicTacToe",
            fontSize = 32.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(64.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = PurpleGrey40
            ),
            shape = RoundedCornerShape(8.dp),
            onClick = {
                authRouteViewModel.updateLoginState()
            }) {
            Text(text = "Login")
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = PurpleGrey40
            ),
            onClick = {
                authRouteViewModel.updateSignUpState()
            }) {
            Text(text = "Sign up")
        }
    }
}