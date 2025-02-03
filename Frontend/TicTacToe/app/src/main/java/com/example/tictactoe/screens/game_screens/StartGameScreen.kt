package com.example.tictactoe.screens.game_screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tictactoe.R
import com.example.tictactoe.geolocation.GeolocationService
import com.example.tictactoe.ui.theme.PurpleGrey80
import com.example.tictactoe.view_model.fields_view_model.AuthViewModel
import com.example.tictactoe.view_model.fields_view_model.GameViewModel
import com.example.tictactoe.view_model.route_view_model.GameRouteViewModel

@Composable
fun StartGameScreen(gameViewModel: GameViewModel, gameRouteViewModel: GameRouteViewModel) {
    val startGameClicked = remember { mutableStateOf(false) }
    UIStartGameState(gameRouteViewModel, gameViewModel, startGameClicked)
    if (startGameClicked.value) {
        Log.d("Game", "Clocckc")
        logicGameState(gameViewModel, gameRouteViewModel, startGameClicked)
    }
}

@Composable
fun UIStartGameState(
    gameRouteViewModel: GameRouteViewModel,
    gameViewModel: GameViewModel,
    startGameClicked: MutableState<Boolean>
) {
    val context = LocalContext.current
    val expandedMenu = remember { mutableStateOf(false) }
    val authViewModel: AuthViewModel = viewModel()
    val menuItemData = listOf("NEWBEE", "DEFENSE", "OFFENSE", "GURU", "AI")

    Scaffold(topBar = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .padding(end = 32.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .clickable {
                        gameRouteViewModel.updateEditingfScreenState()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.wrapContentWidth(),
                    text = authViewModel.username.value,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(Color.Red, shape = CircleShape)
                ) {
                    Image(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .align(Alignment.Center)
                            .padding(2.dp),
                        painter = painterResource(R.drawable.default_avatar),
                        contentScale = ContentScale.Crop,
                        contentDescription = "Avatar"
                    )
                    if (startGameClicked.value) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(60.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }, containerColor = Color.Black) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(bottom = 96.dp)
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 64.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Difficulty", fontSize = 24.sp, color = Color.White)
                Text(text = "⤸", fontSize = 32.sp, color = Color.White)

                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterEnd)) {
                        Text(text = "Help", fontSize = 24.sp, color = Color.White)
                        Text(text = "⤻", fontSize = 32.sp, color = Color.White)
                        RadioButton(
                            modifier = Modifier.size(32.dp),
                            selected = gameViewModel.helpRadioButtonState.value,
                            onClick = {
                                gameViewModel.updateHelpRadioButtonState()
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.Green,
                                unselectedColor = Color.Red
                            )
                        )
                    }
                }
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 64.dp, end = 64.dp)
                .background(
                    color = PurpleGrey80,
                    shape = RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp,
                        bottomStart = if (expandedMenu.value) 0.dp else 8.dp,
                        bottomEnd = if (expandedMenu.value) 0.dp else 8.dp,
                    )
                )
                .clickable { expandedMenu.value = !expandedMenu.value }) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = gameViewModel.selectedDifficultyText.value,
                    fontSize = 24.sp
                )
                DropdownMenu(
                    modifier = Modifier
                        .width(
                            (LocalDensity.current.run { LocalConfiguration.current.screenWidthDp.dp }) - 128.dp
                        )
                        .background(
                            color = PurpleGrey80
                        ), shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 8.dp,
                        bottomEnd = 8.dp
                    ),
                    expanded = expandedMenu.value,
                    onDismissRequest = {
                        expandedMenu.value = false
                    }) {
                    menuItemData.forEachIndexed { index, option ->
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DropdownMenuItem(text = {
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = option
                                )
                            }, onClick = {
                                gameViewModel.updateDifficulty(index, option)
                                expandedMenu.value = false
                            })
                        }
                    }
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 64.dp, end = 64.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    containerColor = PurpleGrey80
                ),
                onClick = {
                    when (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED) {
                        true -> {
                            startGameClicked.value = true
                        }

                        false -> {
                            Toast.makeText(
                                context,
                                "Please enable geolocation access in your device settings",
                                Toast.LENGTH_LONG
                            ).show()
                            startGameClicked.value = false
                        }
                    }
                }) {
                Text(text = "Start Game", fontSize = 32.sp)
            }
            Text(
                modifier = Modifier.padding(top = 32.dp),
                text = "Leaderboard",
                fontSize = 25.sp,
                color = Color.White
            )
            LeaderboardTable(gameViewModel)
        }
    }
}

@Composable
fun LeaderboardTable(gameViewModel: GameViewModel) {
    val authViewModel: AuthViewModel = viewModel()
    val currentUsername = authViewModel.username.value
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(0.4f),
                text = "Username",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = "wins", color = Color.White)
                Text(text = "draws", color = Color.White)
                Text(text = "loses", color = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "rating", color = Color.White)
            }

        }
        gameViewModel.leaderboarData.value.let { leaderboard ->
            val (otherPlayers, currentPlayer) = leaderboard.filterNotNull()
                .partition { it.currentUserName != currentUsername }

            val sortedLeaderboard = if (currentPlayer.isEmpty()) {
                otherPlayers
            } else {
                otherPlayers + currentPlayer.first()
            }
            sortedLeaderboard.forEach { player ->
                val isCurrentUser = player.currentUserName == currentUsername

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Black)
                        .verticalScroll(rememberScrollState())
                        .border(1.dp, color = Color.White, shape = RoundedCornerShape(8.dp))
                ) {
                    FieldsForLeaderboard(
                        player.currentUserName,
                        player.currentUserWins,
                        player.currentUserDraws,
                        player.currentUserLoses,
                        player.currentUserRank,
                        isCurrentUser
                    )
                }
            }
            if (currentPlayer.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .border(1.dp, color = Color.White, shape = RoundedCornerShape(8.dp))
                ) {
                    FieldsForLeaderboard(
                        authViewModel.username.value,
                        authViewModel.currentUserWins.value,
                        authViewModel.currentUserDraws.value,
                        authViewModel.currentUserLoses.value,
                        authViewModel.currentUserRank.value,
                        true
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun FieldsForLeaderboard(
    name: String,
    wins: String,
    draws: String,
    loses: String,
    rank: String,
    isCurrentUser: Boolean
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(0.4f),
            text = if (isCurrentUser) "You" else name,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            color = if (isCurrentUser) Color.Yellow else Color.White
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = wins,
                color = if (isCurrentUser) Color.Yellow else Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = draws,
                color = if (isCurrentUser) Color.Yellow else Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = loses,
                color = if (isCurrentUser) Color.Yellow else Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = rank,
                color = if (isCurrentUser) Color.Yellow else Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

    }
}

@Composable
fun logicGameState(
    gameViewModel: GameViewModel,
    gameRouteViewModel: GameRouteViewModel,
    startGameClicked: MutableState<Boolean>
) {
    val location = gameViewModel.locationFlow.collectAsState("")
    GeolocationService()
    LaunchedEffect(location.value) {
        if (location.value.isNotEmpty()) {
            Log.d("Game", "StartGameScreen: ${location.value}")
            startGameClicked.value = false
            gameViewModel.newGameState()
            gameRouteViewModel.updateStartGameProcessState()
            Log.d("Game", "StartGameScreen: ${gameViewModel.gameId}")
        }
    }

}