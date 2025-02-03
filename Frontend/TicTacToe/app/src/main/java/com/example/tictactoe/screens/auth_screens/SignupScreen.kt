package com.example.tictactoe.screens.auth_screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tictactoe.ui.theme.PurpleGrey40
import com.example.tictactoe.view_model.fields_view_model.AuthViewModel
import com.example.tictactoe.view_model.route_view_model.AuthRouteViewModel

@Composable
fun SignupScreen(authRouteViewModel: AuthRouteViewModel) {
    val authFields: AuthViewModel = viewModel()
    var repeatPassword = remember { mutableStateOf("") }
    val nextPageState = remember { mutableStateOf(false) }
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
                .background(Color.Blue, shape = CircleShape)
                .align(Alignment.TopStart)
                .clickable {
                    authRouteViewModel.updateSignUpState()
                    authFields.clearPassword()
                }, contentAlignment = Alignment.Center
        ) {
            Text(text = "O", color = Color.White, fontSize = 16.sp)
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
                text = "Sign up",
                fontSize = 32.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(32.dp))
            AnimatedContent(targetState = nextPageState.value, transitionSpec = {
                if (targetState) {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                }.using(
                    SizeTransform(clip = false)
                )
            }, label = "") { state ->
                Column(
                    modifier = Modifier.wrapContentSize(),
                ) {
                    if (state) {
                        secondStageSignUp(authFields)
                    } else {
                        firstStageSignUp(authFields, repeatPassword)
                    }
                }
            }
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
                    when (checkFilledAllFields(authFields) && repeatPassword.value.isNotEmpty()) {
                        true -> if (authFields.password.value == repeatPassword.value) {
                            authFields.signUpUser()
                        } else {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT)
                                .show()
                        }

                        false -> {
                            Toast.makeText(
                                context,
                                "Please fill in the all required fields",
                                Toast.LENGTH_SHORT
                            ).show()
                            authRouteViewModel.updateProcessBarState()
                        }
                    }
                }) {
                Text(text = "Sign up")
            }
            val positionNextPage: Alignment
            val iconNextPage: ImageVector
            val colorNextPage: Color

            when (nextPageState.value) {
                true -> {
                    colorNextPage = Color.Red
                    positionNextPage = Alignment.CenterStart
                    iconNextPage = Icons.Filled.ChevronLeft
                }

                false -> {
                    colorNextPage = Color.Blue
                    positionNextPage = Alignment.CenterEnd
                    iconNextPage = Icons.Filled.ChevronRight
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 32.dp)
            ) {
                IconButton(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(positionNextPage),
                    onClick = { nextPageState.value = !nextPageState.value }) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorNextPage, shape = RoundedCornerShape(16.dp)),
                        imageVector = iconNextPage,
                        contentDescription = null,
                    )
                }
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
                Toast.makeText(context, authRouteViewModel.respMessage.value, Toast.LENGTH_SHORT)
                    .show()
                authRouteViewModel.clearMessage()
                authFields.clearPassword()
            }
        }
    }
}

fun checkFilledAllFields(authFields: AuthViewModel): Boolean {
    when (
        authFields.username.value.isNotEmpty() &&
                authFields.password.value.isNotEmpty() &&
                authFields.street.value.isNotEmpty() &&
                authFields.city.value.isNotEmpty() &&
                authFields.state.value.isNotEmpty() &&
                authFields.zipcode.value.isNotEmpty()
    ) {
        true -> return true
        false -> return false
    }
}

@Composable
fun firstStageSignUp(authFields: AuthViewModel, repeatPassword: MutableState<String>) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 32.dp),
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        placeholder = { Text(text = "Username") },
        value = authFields.username.value,
        onValueChange = { username ->
            authFields.username.value = username
        })
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 32.dp),
        shape = RoundedCornerShape(0.dp),
        placeholder = { Text(text = "Password") },
        value = authFields.password.value,
        onValueChange = { password ->
            authFields.password.value = password
        })
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 32.dp),
        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
        placeholder = { Text(text = "Repeat password") },
        value = repeatPassword.value,
        onValueChange = { repeatPass ->
            repeatPassword.value = repeatPass
        })
}

@Composable
fun secondStageSignUp(authFields: AuthViewModel) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 32.dp),
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        placeholder = { Text(text = "Street") },
        value = authFields.street.value,
        onValueChange = { street ->
            authFields.street.value = street
        })

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 32.dp),
        shape = RoundedCornerShape(0.dp),
        placeholder = { Text(text = "City") },
        value = authFields.city.value,
        onValueChange = { city ->
            authFields.city.value = city
        })
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 32.dp)
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth(0.7f),
            shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
            placeholder = { Text(text = "State") },
            value = authFields.state.value,
            onValueChange = { state ->
                authFields.state.value = state
            })
        Spacer(modifier = Modifier.width(4.dp))
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
            placeholder = { Text(text = "ZIP Code") },
            value = authFields.zipcode.value,
            onValueChange = { zipcode ->
                authFields.zipcode.value = zipcode
            }, textStyle = TextStyle(textAlign = TextAlign.Center)
        )
    }
}