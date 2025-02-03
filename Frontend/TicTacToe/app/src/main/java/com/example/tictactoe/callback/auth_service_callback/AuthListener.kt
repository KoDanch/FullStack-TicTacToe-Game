package com.example.tictactoe.callback.auth_service_callback

interface AuthListener {
    fun loginSuccess(responseCode: Int, responseMessage: String, token: String)
    fun loginFail(responseCode: Int, responseMessage: String)
    fun signUpSuccess(responseCode: Int, responseMessage: String)
    fun signUpFail(responseCode: Int, responseMessage: String)
}