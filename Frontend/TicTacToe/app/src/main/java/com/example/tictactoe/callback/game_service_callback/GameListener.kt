package com.example.tictactoe.callback.game_service_callback

interface GameListener {
    fun startGameSuccess(gameId: Int, board: String)
    fun startGameFail(responseMessage: String)
    fun makeMoveSuccess(responseMessage: String, board: String, current_turn: String, winner: Int?)
    fun makeMoveFail(responseMessage: String)
    fun getHelpMoveSuccess(helpBoard: String)
    fun getHelpMoveFail(responseMessage: String)
    fun getLocationSuccess(location: String)
    fun getLocationFail(errorMessage: String)

}