package com.example.tictactoe.callback.user_data_callback

interface UserDataListener {
    fun getUserDataSuccess(
        responseMessage: String,
        username: String,
        currentUserDraws: String,
        currentUserLoses: String,
        currentUserWins: String,
        currentUserRank: String,
        street: String,
        city: String,
        state: String,
        postal_code: String
    )

    fun getUserDataFail(responseMessage: String)
    fun editUserDataSuccess(responseMessage: String)
    fun editUserDataFail(responseMessage: String)
    fun deleteUserSuccess(responseCode: Int, responseMessage: String)
    fun deleteUserFail(responseMessage: String)
}