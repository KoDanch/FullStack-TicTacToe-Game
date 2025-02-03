package com.example.tictactoe.callback.auth_service_callback

object AuthHandler {
    private var listener: AuthListener? = null

    fun setListener(authListener: AuthListener) {
        listener = authListener
    }
    fun notifyLoginSuccess(responseCode: Int, responseMessage: String, token: String) {
        listener?.loginSuccess(responseCode, responseMessage, token)
    }
    fun notifyLoginFail(responseCode: Int, responseMessage: String) {
        listener?.loginFail(responseCode, responseMessage)
    }
    fun notifySignUpSuccess(responseCode: Int, responseMessage: String) {
        listener?.signUpSuccess(responseCode,responseMessage)
    }
    fun notifySignUpFail(responseCode: Int, responseMessage: String) {
        listener?.signUpFail(responseCode, responseMessage)
    }
}