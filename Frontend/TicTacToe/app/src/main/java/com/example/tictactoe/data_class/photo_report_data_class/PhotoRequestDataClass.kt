package com.example.tictactoe.data_class.photo_report_data_class

import com.google.gson.annotations.SerializedName

data class PhotoRequestDataClass(
    @SerializedName("auth_token") val auth_token: String,
    @SerializedName("photo_num") val photo_num: Int
)