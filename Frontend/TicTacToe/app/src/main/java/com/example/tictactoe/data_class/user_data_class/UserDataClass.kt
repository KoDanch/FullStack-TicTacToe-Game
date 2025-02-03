package com.example.tictactoe.data_class.user_data_class

import com.google.gson.annotations.SerializedName

data class UserDataClass (
    val username: String,
    val password: String,
    @SerializedName("player_draws") val currentUserDraws: String,
    @SerializedName("player_loses") val currentUserLoses: String,
    @SerializedName("player_wins") val currentUserWins: String,
    @SerializedName("rating") val currentUserRank: String,
    @SerializedName("auth_token") val token: String,
    val address: AddressDataClass
)