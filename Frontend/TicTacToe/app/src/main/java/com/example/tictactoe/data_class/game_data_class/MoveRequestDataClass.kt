package com.example.tictactoe.data_class.game_data_class

import com.google.gson.annotations.SerializedName

data class MoveRequestDataClass (
    @SerializedName("position") val position: Int?,
    @SerializedName("player") val player: String,
    @SerializedName("current_turn") val current_turn: String,
    @SerializedName("difficulty") val selectedDifficulty: Int
)