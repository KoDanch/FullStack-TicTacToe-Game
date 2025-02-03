package com.example.tictactoe.data_class.auth_data_class

import com.google.gson.annotations.SerializedName

data class LogInDataClass (
    @SerializedName("username") val username: String = "",
    @SerializedName("password") val password: String = ""
)