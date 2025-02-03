package com.example.tictactoe.data_class.game_data_class

import com.google.gson.annotations.SerializedName

data class MoveResponseDataClass(
    @SerializedName("message") val message: String,
    @SerializedName("board") val board: String,
    @SerializedName("winner") val winner: Int?,
    @SerializedName("current_turn") val current_turn: String
)