package com.example.tictactoe.callback.leaderboard_callback

import com.example.tictactoe.data_class.leaderboard_data_class.LeaderboardDataClass

interface LeaderboardListener {
    fun getLeaderboardSuccess(leaderboardDataClass: List<LeaderboardDataClass>)
    fun getLeaderboardFail(responseMessage: String)
}