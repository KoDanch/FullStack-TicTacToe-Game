package com.example.tictactoe.data_class.leaderboard_data_class

import com.google.gson.annotations.SerializedName

data class LeaderboardDataClass(
    @SerializedName("player_draws") val currentUserDraws: String,
    @SerializedName("player_loses") val currentUserLoses: String,
    @SerializedName("player_wins") val currentUserWins: String,
    @SerializedName("rank") val currentUserRank: String,
    @SerializedName("win_rate") val currentUserWinRate: String,
    @SerializedName("username") val currentUserName: String,
)