package com.example.tictactoe.view_model.fields_view_model

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tictactoe.api.deletingUser
import com.example.tictactoe.api.getHelpMove
import com.example.tictactoe.api.getLeaderboard
import com.example.tictactoe.api.makeMove
import com.example.tictactoe.api.parsingUserData
import com.example.tictactoe.api.startGame
import com.example.tictactoe.api.uploadPhoto
import com.example.tictactoe.callback.game_service_callback.GameHandler
import com.example.tictactoe.callback.game_service_callback.GameListener
import com.example.tictactoe.callback.leaderboard_callback.LeaderboardHandler
import com.example.tictactoe.callback.leaderboard_callback.LeaderboardListener
import com.example.tictactoe.data_class.leaderboard_data_class.LeaderboardDataClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel(), GameListener, LeaderboardListener {
    val selectedDifficulty = mutableStateOf(0)
    val selectedDifficultyText = mutableStateOf("NEWBEE")
    val helpRadioButtonState = mutableStateOf(false)
    val token = mutableStateOf("")
    val gameId = mutableStateOf(0)
    val position = mutableStateOf(0)
    private val player = mutableStateOf("X")
    val board = mutableStateOf("")
    val helpPosition = mutableStateOf<Int?>(null)
    val currentTurn = mutableStateOf("X")
    val responseMessage = mutableStateOf("")
    val clickableField = mutableStateOf(true)
    var winnerGame = mutableStateOf("")
    val clickableArray = Array(3) { Array(3) { mutableStateOf(false) } }
    val photoReport = mutableStateOf(false)
    val winnerPlayer = mutableStateOf(0)
    val winnerBot = mutableStateOf(0)
    val allPlayed = mutableStateOf(1)
    private val _locationFlow = MutableStateFlow("")
    val locationFlow: StateFlow<String> = _locationFlow
    val leaderboarData = mutableStateOf<List<LeaderboardDataClass?>>(emptyList())
    val tempURI = mutableStateOf<Uri?>(null)


    init {
        GameHandler.setListener(this)
        LeaderboardHandler.setListener(this)
    }

    fun updateclickableArrayState(i: Int, j: Int) {
        clickableArray[i][j].value = true
        if (!clickableField.value) {
            clickableArray.forEach { row ->
                row.forEach { cell ->
                    cell.value = false
                }
            }
        }
    }
    fun updateHelpRadioButtonState() {
        helpRadioButtonState.value = !helpRadioButtonState.value
    }

    fun clearHelpPosition() {
        helpPosition.value = null
    }

    fun getHelpMovePosition() {
        CoroutineScope(Dispatchers.IO).launch {
            getHelpMove(gameId.value)
        }
    }

    fun uploadPhotoReport(context: Context, photoNumber: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            uploadPhoto(tempURI.value!!, context, token.value, photoNumber)
        }
    }

    fun updateLocation(newLocation: String) {
        viewModelScope.launch {
            _locationFlow.value = newLocation
        }
    }

    fun updateLeaderboardData(newData: List<LeaderboardDataClass>) {
        leaderboarData.value = newData
    }

    fun parsingLeaderboard() {
        CoroutineScope(Dispatchers.IO).launch {
            getLeaderboard()
        }
    }

    fun updateDifficulty(newDifficulty: Int, newDifficultyText: String) {
        selectedDifficulty.value = newDifficulty
        selectedDifficultyText.value = newDifficultyText
        Log.d("newDifficulty", "updateDifficulty: $newDifficulty")
    }

    fun updateToken(newToken: String) {
        token.value = newToken
    }

    private fun clearWinnerGame() {
        winnerGame.value = ""
    }

    fun startGameState() {
        CoroutineScope(Dispatchers.IO).launch {
            startGame(token.value, _locationFlow.value, currentTurn.value, selectedDifficulty.value)
        }

    }

    fun makeMoveState() {
        CoroutineScope(Dispatchers.IO).launch {
            makeMove(gameId.value, position.value, player.value, currentTurn.value, selectedDifficulty.value)
        }
    }

    fun clearGameStatsState() {
        board.value = ""
        winnerPlayer.value = 0
        currentTurn.value = ""
        winnerBot.value = 0
        allPlayed.value = 1
        clickableField.value = true
        updateLocation("")
        clearHelpPosition()
        clearWinnerGame()
    }

    fun newGameState() {
        clearGameStatsState()
        startGameState()
    }

    fun continueGameState() {
        board.value = ""
        clickableField.value = true
        allPlayed.value += 1
        clearWinnerGame()
        startGameState()
        makeMoveState()
    }

    fun getUserData(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            parsingUserData(token)
        }
    }

    fun deleteUser(password: String, deleteCode: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            deletingUser(password, token.value, deleteCode)
        }
    }

    override fun startGameSuccess(gameId: Int, board: String) {
        this.gameId.value = gameId
        this.board.value = board
    }

    override fun startGameFail(responseMessage: String) {
        this.responseMessage.value = responseMessage
    }

    override fun makeMoveSuccess(
        responseMessage: String,
        board: String,
        current_turn: String,
        winner: Int?
    ) {
        this.responseMessage.value = responseMessage
        this.board.value = board
        this.currentTurn.value = current_turn
        if (winner != null) {
            if (winner == 2) {
                winnerPlayer.value += 1
                winnerGame.value = "You`re Winner!"
                clickableField.value = false
            } else if (winner == 0) {
                winnerBot.value += 1
                winnerGame.value = "You Lose"
                clickableField.value = false
            } else if (winner == 1) {
                winnerPlayer.value += 1
                winnerBot.value += 1
                winnerGame.value = "Draw"
                clickableField.value = false
            }
            if (allPlayed.value == 5) {
                photoReport.value = true
                winnerPlayer.value = 0
                winnerBot.value = 0
                allPlayed.value = 1
                clearWinnerGame()
            }
        }
    }

    override fun makeMoveFail(responseMessage: String) {
        this.responseMessage.value = responseMessage
    }

    override fun getHelpMoveSuccess(helpBoard: String) {

        val currentBoardToList = this.board.value.toList()
        val helpBoardToList = helpBoard.toList()

        helpPosition.value = getHelpPosition(currentBoardToList, helpBoardToList)
    }

    private fun getHelpPosition(oldBoard: List<Char>, helpBoard: List<Char>):Int? {

        for (i in helpBoard.indices) {
            if (helpBoard[i] == 'X' && oldBoard[i] != 'X') {
                return i
            }
        }
        return null
    }

    override fun getHelpMoveFail(responseMessage: String) {
        this.responseMessage.value = responseMessage
    }

    override fun getLocationSuccess(location: String) {
        updateLocation(location)
    }

    override fun getLocationFail(errorMessage: String) {
        responseMessage.value = errorMessage
    }

    override fun getLeaderboardSuccess(leaderboardDataClass: List<LeaderboardDataClass>) {
        updateLeaderboardData(leaderboardDataClass)
    }

    override fun getLeaderboardFail(responseMessage: String) {
        this.responseMessage.value = responseMessage
    }


}