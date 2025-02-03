package com.example.tictactoe.data_class.user_data_class

import com.google.gson.annotations.SerializedName

data class DeleteUserDataClass (
    val message: String,
    val password: String,
    @SerializedName("auth_token") val token: String,
    @SerializedName("user_deleted") val deleteUser: Int
)