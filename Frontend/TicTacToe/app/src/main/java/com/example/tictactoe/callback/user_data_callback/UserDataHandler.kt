package com.example.tictactoe.callback.user_data_callback

object UserDataHandler {
    private var listener: UserDataListener? = null

    fun setListener(userDataListener: UserDataListener) {
        listener = userDataListener
    }

    fun notifyGetUserDataSuccess(
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
    ) {
        listener?.getUserDataSuccess(
            responseMessage,
            username,
            currentUserDraws,
            currentUserLoses,
            currentUserWins,
            currentUserRank,
            street,
            city,
            state,
            postal_code
        )
    }

    fun notifyGetUserDataFail(responseMessage: String) {
        listener?.getUserDataFail(responseMessage)
    }

    fun notifyEditUserDataSuccess(responseMessage: String) {
        listener?.editUserDataSuccess(responseMessage)
    }

    fun notifyEditUserDataFail(responseMessage: String) {
        listener?.editUserDataFail(responseMessage)
    }

    fun notifyUserDeleteSuccess(responseCode: Int, responseMessage: String) {
        listener?.deleteUserSuccess(responseCode, responseMessage)
    }

    fun notifyUserDeleteFail(responseMessage: String) {
        listener?.deleteUserFail(responseMessage)
    }
}