package com.example.tictactoe.data_class.game_data_class

import com.google.gson.annotations.SerializedName

data class StartGameRequestDataClass(
    @SerializedName("player_location") val location: String,
    @SerializedName("auth_token") val token: String,
    @SerializedName("current_turn") val currentTurn: String,
    @SerializedName("difficulty") val selectedDifficulty: Int
)