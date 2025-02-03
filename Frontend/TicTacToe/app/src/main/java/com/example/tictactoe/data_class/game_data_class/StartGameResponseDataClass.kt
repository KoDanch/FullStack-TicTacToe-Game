package com.example.tictactoe.data_class.game_data_class

import com.google.gson.annotations.SerializedName

data class StartGameResponseDataClass (
    @SerializedName("game_id") val gameId: Int,
    @SerializedName("board") val board: String
)
