package com.example.tictactoe.callback.game_service_callback

object GameHandler {
    private var listener: GameListener? = null

    fun setListener(gameListener: GameListener) {
        listener = gameListener
    }

    fun notifyStartGameSuccess(gameId: Int, board: String) {
        listener?.startGameSuccess(gameId, board)
    }
    fun notifyStartGameFail(responseMessage: String) {
        listener?.startGameFail(responseMessage)
    }
    fun notifyMakeMoveSuccess(responseMessage: String, board: String, current_turn: String, winner: Int?) {
        listener?.makeMoveSuccess(responseMessage, board, current_turn, winner)
    }

    fun notifyMakeMoveFail(responseMessage: String) {
        listener?.makeMoveFail(responseMessage)
    }

    fun notifyGetHelpMoveSuccess(helpBoard: String) {
        listener?.getHelpMoveSuccess(helpBoard)
    }

    fun notifyGetHelpMoveFail(responseMessage: String) {
        listener?.getHelpMoveFail(responseMessage)
    }

    fun notifyGetLocationSucces(location: String) {
        listener?.getLocationSuccess(location)
    }

    fun notifyGetLocationFail(errorMessage: String) {
        listener?.getLocationFail(errorMessage)
    }
}