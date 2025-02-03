package com.example.tictactoe.screens.game_screens

import android.util.Log
import androidx.compose.animation.Animatable
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tictactoe.ui.theme.PurpleGrey80
import com.example.tictactoe.view_model.fields_view_model.AuthViewModel
import com.example.tictactoe.view_model.fields_view_model.GameViewModel
import com.example.tictactoe.view_model.route_view_model.GameRouteViewModel
import kotlinx.coroutines.delay

@Composable
fun GameProcessScreen(
    gameRouteViewModel: GameRouteViewModel
) {
    val gameViewModel: GameViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val timerForHelp = remember { mutableStateOf(0) }
    val triggerGetHelp = remember { mutableStateOf(false) }
    getHelpLogic(gameViewModel, timerForHelp, triggerGetHelp)
    if (gameViewModel.photoReport.value) {
        gameRouteViewModel.updatePhotoReportScreenState()
        gameViewModel.photoReport.value = false

    }
    Scaffold(topBar = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .padding(start = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(PurpleGrey80, shape = CircleShape)
                        .align(Alignment.BottomStart)
                        .clickable {
                            gameRouteViewModel.updateGameAlertDialogState()

                        }, contentAlignment = Alignment.Center
                ) {
                    Text(text = "X", color = Color.Red, fontSize = 16.sp)
                    Text(text = "O", color = Color.Blue, fontSize = 16.sp)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp),
                        text = authViewModel.username.value,
                        fontSize = 24.sp
                    )
                }
                Box(modifier = Modifier.weight(0.5f)) {
                    Card(
                        modifier = Modifier.align(Alignment.Center),
                        elevation = CardDefaults.cardElevation(8.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "${gameViewModel.winnerPlayer.value}",
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("-", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${gameViewModel.winnerBot.value}",
                                fontSize = 24.sp
                            )
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp),
                        text = "Bot",
                        fontSize = 24.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter), thickness = 1.dp)

        }
    }, content = { padding ->
        gameAlertDialog(gameRouteViewModel)
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 16.dp),
                text = gameViewModel.winnerGame.value,
                textAlign = TextAlign.Center,
                fontSize = 32.sp
            )
            PlayingField(timerForHelp, triggerGetHelp)
            Crossfade(targetState = !gameViewModel.clickableField.value, label = "") { state ->
                if (state) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(modifier = Modifier.width(160.dp), onClick = {
                            gameViewModel.newGameState()
                        }) {
                            Text(text = "New game")
                        }
                        Button(modifier = Modifier.width(160.dp), onClick = {
                            gameViewModel.continueGameState()
                        }) {
                            Text(text = "Continue playing")
                        }
                    }
                }

            }
        }
    })
}

@Composable
fun PlayingField(timerForHelp: MutableState<Int>, triggerGetHelp: MutableState<Boolean>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CreatePlayingField(timerForHelp, triggerGetHelp)
    }
}

@Composable
fun CreatePlayingField(timerForHelp: MutableState<Int>, triggerGetHelp: MutableState<Boolean>) {
    val gameViewModel: GameViewModel = viewModel()
    val flashingHelpCross = remember { androidx.compose.animation.core.Animatable(1f) }

    for (i in 0 until 3) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (j in 0 until 3) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clickable(enabled = gameViewModel.clickableField.value) {
                            gameViewModel.position.value = i * 3 + j

                            if (!gameViewModel.clickableArray[i][j].value) {
                                gameViewModel.makeMoveState()
                            }
                            gameViewModel.clearHelpPosition()
                            timerForHelp.value = 0
                            triggerGetHelp.value = !triggerGetHelp.value

                        }
                        .drawBehind {
                            val strokeWidth = 2.dp.toPx()
                            val halfStrokeWidth = strokeWidth / 2

                            if (i > 0) {
                                drawLine(
                                    color = Color.Black,
                                    start = Offset(0f, halfStrokeWidth),
                                    end = Offset(size.width, halfStrokeWidth),
                                    strokeWidth = strokeWidth
                                )
                            }

                            if (j > 0) {
                                drawLine(
                                    color = Color.Black,
                                    start = Offset(halfStrokeWidth, 0f),
                                    end = Offset(halfStrokeWidth, size.height),
                                    strokeWidth = strokeWidth
                                )
                            }
                        }
                ) {
                    if (gameViewModel.board.value.isNotEmpty()) {
                        val char = gameViewModel.board.value[i * 3 + j]
                        if (char == 'X') {
                            gameViewModel.updateclickableArrayState(i, j)
                            drawCross(
                                Modifier
                                    .matchParentSize()
                                    .padding(16.dp), Color.Blue
                            )
                        } else if (char == 'O') {
                            gameViewModel.updateclickableArrayState(i, j)
                            drawCircle(
                                Modifier
                                    .matchParentSize()
                                    .padding(16.dp)
                            )
                        } else if (gameViewModel.helpPosition.value != null && gameViewModel.helpPosition.value == i * 3 + j) {
                                LaunchedEffect(true) {
                                    while (gameViewModel.helpPosition.value != null) {
                                        flashingHelpCross.animateTo(
                                            targetValue = 0f,
                                            animationSpec = tween(500)
                                        )
                                        flashingHelpCross.animateTo(
                                            targetValue = 1f,
                                            animationSpec = tween(500)
                                        )
                                    }
                                }

                            drawCross(
                                Modifier
                                    .matchParentSize()
                                    .padding(16.dp).alpha(flashingHelpCross.value), Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun gameAlertDialog(gameRouteViewModel: GameRouteViewModel) {
    val gameViewModel: GameViewModel = viewModel()
    if (gameRouteViewModel.gameAlertDialog.value) {
        AlertDialog(
            onDismissRequest = { gameRouteViewModel.gameAlertDialog.value = false },
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Attention",
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
                        text = "Game data will be lost.",
                        textAlign = TextAlign.Center
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            modifier = Modifier.width(100.dp),
                            shape = RoundedCornerShape(16.dp),
                            onClick = {
                                gameRouteViewModel.updateStartGameProcessState()
                                gameViewModel.clearGameStatsState()
                                gameRouteViewModel.gameAlertDialog.value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            )
                        ) {
                            Text("OK")
                        }
                        Button(
                            modifier = Modifier.width(100.dp),
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
                                gameRouteViewModel.gameAlertDialog.value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Blue,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            },

            confirmButton = {},
            dismissButton = {}
        )


    }
}

@Composable
fun drawCross(modifier: Modifier, color: Color) {
    Canvas(modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val strokeWidth = 4f

        val lineStart1 = Offset(0f, 0f)
        val lineEnd1 = Offset(canvasWidth, canvasHeight)

        val lineStart2 = Offset(0f, canvasHeight)
        val lineEnd2 = Offset(canvasWidth, 0f)

        drawLine(
            color = color,
            start = lineStart1,
            end = lineEnd1,
            strokeWidth = strokeWidth
        )
        drawLine(
            color = color,
            start = lineStart2,
            end = lineEnd2,
            strokeWidth = strokeWidth
        )
    }
}

@Composable
fun drawCircle(modifier: Modifier) {
    Canvas(
        modifier
    ) {
        drawCircle(color = Color.Red, style = Stroke(4f))
    }
}

@Composable
fun getHelpLogic(
    gameViewModel: GameViewModel,
    timerForHelp: MutableState<Int>,
    triggerGetHelp: MutableState<Boolean>
) {
    val timerTwentySecond = 5
    if (gameViewModel.winnerGame.value.isEmpty()) {
        if (gameViewModel.helpRadioButtonState.value) {
            LaunchedEffect(triggerGetHelp.value) {
                while (timerForHelp.value < timerTwentySecond) {
                    delay(1000)
                    timerForHelp.value++
                    Log.d("helpMove", "getHelpLogic: ${timerForHelp.value}")
                }
                gameViewModel.getHelpMovePosition()
            }
        }
    }
}

