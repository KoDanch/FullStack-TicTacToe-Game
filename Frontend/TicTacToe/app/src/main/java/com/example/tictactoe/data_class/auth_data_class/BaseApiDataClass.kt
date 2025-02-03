package com.example.tictactoe.data_class.auth_data_class

import com.google.gson.annotations.SerializedName

data class BaseApiDataClass (
    @SerializedName("message") val message: String,

    @SerializedName("auth_token") val token: String,
)