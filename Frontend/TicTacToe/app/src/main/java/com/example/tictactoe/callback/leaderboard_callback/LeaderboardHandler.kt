package com.example.tictactoe.callback.leaderboard_callback

import com.example.tictactoe.data_class.leaderboard_data_class.LeaderboardDataClass

object LeaderboardHandler {
    private var listener: LeaderboardListener? = null

    fun setListener(leaderboardListener: LeaderboardListener) {
        listener = leaderboardListener
    }

    fun notifyGetLeaderboardSuccess(leaderboardDataClass: List<LeaderboardDataClass>) {
        listener?.getLeaderboardSuccess(leaderboardDataClass)
    }

    fun notifyGetLeaderboardFail(responseMessage: String) {
        listener?.getLeaderboardFail(responseMessage)

    }

}