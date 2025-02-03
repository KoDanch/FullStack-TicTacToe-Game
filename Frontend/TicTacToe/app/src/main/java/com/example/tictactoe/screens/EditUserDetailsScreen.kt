package com.example.tictactoe.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tictactoe.ui.theme.PurpleGrey80
import com.example.tictactoe.view_model.fields_view_model.AuthViewModel
import com.example.tictactoe.view_model.fields_view_model.GameViewModel
import com.example.tictactoe.view_model.route_view_model.AuthRouteViewModel
import com.example.tictactoe.view_model.route_view_model.GameRouteViewModel

@Composable
fun EditUserDetailsScreen(authViewModel: AuthViewModel) {

    val authRouteViewModel: AuthRouteViewModel = viewModel()
    val gameRouteViewModel: GameRouteViewModel = viewModel()
    val gameViewModel: GameViewModel = viewModel()
    val enabledEditing = remember { mutableStateOf(false) }
    val stageAlertDialog = remember { mutableStateOf("") }
    val listUserData = listOf(
        "Username" to authViewModel.username,
        "Street" to authViewModel.street,
        "City" to authViewModel.city,
        "State" to authViewModel.state,
        "ZIP Code" to authViewModel.zipcode
    )
    if (authViewModel.logoutState.value) {
        gameRouteViewModel.logoutState()
        authRouteViewModel.startGameScreenState.value = false
        authViewModel.logoutState.value = false
        authViewModel.clearResponseMessage()
    }
    CheckOwnAccountAlertDialog(authRouteViewModel, gameViewModel, authViewModel, stageAlertDialog)
    Scaffold(topBar = {
        Box(
            modifier = Modifier
                .fillMaxSize(0.2f)
                .padding(start = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(PurpleGrey80, shape = CircleShape)
                    .align(Alignment.CenterStart)
                    .clickable {
                        gameRouteViewModel.updateEditingfScreenState()
                        authViewModel.clearResponseMessage()
                        gameViewModel.getUserData(authViewModel.token.value)
                    }, contentAlignment = Alignment.Center
            ) {
                Text(text = "X", color = Color.Red, fontSize = 16.sp)
                Text(text = "O", color = Color.Blue, fontSize = 16.sp)
            }
        }
    }, containerColor = Color.Black) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text(
                modifier = Modifier.padding(bottom = 32.dp),
                text = "Profile",
                color = Color.White,
                fontSize = 32.sp
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.5f)
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                listUserData.forEach { (placeholder, data) ->
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enabledEditing.value,
                        shape = RoundedCornerShape(8.dp),
                        placeholder = { Text(text = placeholder) },
                        value = data.value,
                        onValueChange = { newData ->
                            data.value = newData
                        })
                }


            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                when (enabledEditing.value) {
                    true -> {
                        Button(onClick = {
                            gameViewModel.getUserData(authViewModel.token.value)
                            enabledEditing.value = !enabledEditing.value
                        }) {
                            Text(
                                text = "Cancel"
                            )
                        }
                        Button(onClick = {
                            enabledEditing.value = !enabledEditing.value
                            stageAlertDialog.value = "EditProfile"
                            authRouteViewModel.updateValidationDialogState()

                        }) {
                            Text(
                                text = "Save changes"
                            )
                        }
                    }

                    false -> {
                        Button(onClick = { enabledEditing.value = !enabledEditing.value }) {
                            Text(
                                text = "Edit Credentials"
                            )
                        }
                    }
                }
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                text = authViewModel.responseMessage.value,
                color = Color.White,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    stageAlertDialog.value = "DeleteProfile"
                    authRouteViewModel.updateValidationDialogState()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Delete Profile", color = Color.White)
            }
        }
    }
}

@Composable
fun CheckOwnAccountAlertDialog(
    authRouteViewModel: AuthRouteViewModel,
    gameViewModel: GameViewModel,
    authViewModel: AuthViewModel,
    stageAlertDialog: MutableState<String>
) {
    val password = mutableStateOf("")
    if (authRouteViewModel.validationDialog.value) {
        AlertDialog(
            onDismissRequest = { authRouteViewModel.validationDialog.value = false },
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = when (stageAlertDialog.value) {
                        "DeleteProfile" -> "Attention"
                        "EditProfile" -> "Validation"
                        else -> "Unknown Stage"
                    },
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = when (stageAlertDialog.value) {
                            "DeleteProfile" -> "THIS ACTION IS IRREVERSIBLE.\n\nBy clicking YES after entering your password,YOU agree to delete all your user data"
                            "EditProfile" -> "Please confirm that you are the Account Owner."
                            else -> "Unknown Stage"
                        },
                        textAlign = TextAlign.Center
                    )
                    Column {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.LightGray,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            placeholder = { Text(text = "Password") },
                            value = when (stageAlertDialog.value) {
                                "DeleteProfile" -> password.value
                                "EditProfile" -> authViewModel.password.value
                                else -> ""
                            },
                            onValueChange = { newPassword ->
                                when (stageAlertDialog.value) {
                                    "DeleteProfile" -> password.value = newPassword
                                    "EditProfile" -> authViewModel.password.value = newPassword
                                    else -> ""
                                }
                            })
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Button(
                                modifier = Modifier.wrapContentWidth(),
                                shape = RoundedCornerShape(16.dp),
                                onClick = {
                                    authRouteViewModel.updateValidationDialogState()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Blue,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                modifier = Modifier.wrapContentWidth(),
                                shape = RoundedCornerShape(8.dp),
                                onClick = {
                                    if (password.value.isNotEmpty() || authViewModel.password.value.isNotEmpty()) {
                                        when (stageAlertDialog.value) {
                                            "DeleteProfile" -> {
                                                gameViewModel.deleteUser(
                                                    password.value,
                                                    1
                                                )
                                                password.value = ""
                                            }
                                            "EditProfile" -> authViewModel.editUserData()
                                            else -> ""
                                        }
                                    }
                                    authRouteViewModel.updateValidationDialogState()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = when (stageAlertDialog.value) {
                                        "DeleteProfile" -> "YES"
                                        "EditProfile" -> "Update changes"
                                        else -> "Unknown Stage"
                                    }
                                )
                            }
                        }
                    }

                }
            },
            confirmButton = {},
            dismissButton = {}
        )


    }
}