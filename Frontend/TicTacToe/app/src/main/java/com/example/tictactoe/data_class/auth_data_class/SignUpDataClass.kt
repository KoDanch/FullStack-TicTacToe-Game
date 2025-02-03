package com.example.tictactoe.data_class.auth_data_class

import com.google.gson.annotations.SerializedName

data class SignUpDataClass(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("street") val street: String,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("zipcode") val zipcode: String
)