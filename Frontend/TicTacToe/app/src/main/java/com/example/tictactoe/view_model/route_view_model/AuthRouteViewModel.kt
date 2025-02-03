package com.example.tictactoe.view_model.route_view_model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.tictactoe.callback.auth_service_callback.AuthHandler
import com.example.tictactoe.callback.auth_service_callback.AuthListener

class AuthRouteViewModel : ViewModel(), AuthListener {

    val loginScreenState = mutableStateOf(false)
    val signUpScreenState = mutableStateOf(false)
    val startGameScreenState = mutableStateOf(false)
    var processBarState = mutableStateOf(false)
    var respMessage = mutableStateOf("")
    var respToken = mutableStateOf("")
    val validationDialog = mutableStateOf(false)
    val responseCode = mutableStateOf(0)


    init {
        AuthHandler.setListener(this)
    }

    fun updateValidationDialogState() {
        validationDialog.value = !validationDialog.value
    }

    fun clearMessage() {
        respMessage.value = ""
    }

    fun updateLoginState() {
        loginScreenState.value = !loginScreenState.value
    }

    fun updateSignUpState() {
        signUpScreenState.value = !signUpScreenState.value
    }

    fun updateProcessBarState() {
        processBarState.value = !processBarState.value
    }

    override fun loginSuccess(responseCode: Int, responseMessage: String, responseToken: String) {
        loginScreenState.value = false
        startGameScreenState.value = true
        processBarState.value = false
        respToken.value = responseToken
        respMessage.value = responseMessage
        this.responseCode.value = responseCode
    }

    override fun loginFail(responseCode: Int, responseMessage: String) {
        processBarState.value = false
        respMessage.value = responseMessage
        this.responseCode.value = responseCode
    }

    override fun signUpSuccess(responseCode: Int, responseMessage: String) {
        signUpScreenState.value = false
        processBarState.value = false
        respMessage.value = responseMessage
    }

    override fun signUpFail(responseCode: Int, responseMessage: String) {
        processBarState.value = false
        respMessage.value = responseMessage
    }


}