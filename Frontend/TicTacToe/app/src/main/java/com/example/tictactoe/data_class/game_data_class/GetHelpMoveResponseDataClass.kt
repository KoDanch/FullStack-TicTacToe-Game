package com.example.tictactoe.data_class.game_data_class

import com.google.gson.annotations.SerializedName

class GetHelpMoveResponseDataClass(
    @SerializedName("help_board") val helpBoard: String
)