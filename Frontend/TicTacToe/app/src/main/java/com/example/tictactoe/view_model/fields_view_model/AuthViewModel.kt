package com.example.tictactoe.view_model.fields_view_model

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.tictactoe.api.editUserData
import com.example.tictactoe.api.loginUser
import com.example.tictactoe.api.registerUser
import com.example.tictactoe.callback.user_data_callback.UserDataHandler
import com.example.tictactoe.callback.user_data_callback.UserDataListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel(), UserDataListener {
    val username = mutableStateOf("")
    val password = mutableStateOf("")
    val token = mutableStateOf("")
    val street = mutableStateOf("")
    val city = mutableStateOf("")
    val state = mutableStateOf("")
    val zipcode = mutableStateOf("")
    val responseMessage = mutableStateOf("")
    val logoutState = mutableStateOf(false)
    val currentUserDraws = mutableStateOf("")
    val currentUserLoses = mutableStateOf("")
    val currentUserWins = mutableStateOf("")
    val currentUserRank = mutableStateOf("")

    init {
        UserDataHandler.setListener(this)
    }

    fun clearPassword() {
        password.value = ""
    }
    fun clearResponseMessage() {
        responseMessage.value = ""
    }

    fun signInUser() {
        CoroutineScope(Dispatchers.IO).launch {
            loginUser(username.value, password.value)
        }
    }

    fun signUpUser() {
        CoroutineScope(Dispatchers.IO).launch {
            registerUser(
                username.value,
                password.value,
                street.value,
                city.value,
                state.value,
                zipcode.value
            )
            Log.d("Api", "signUpUser: ${username.value} + pas ${password.value}")
        }
    }
    fun updateToken(newToken: String) {
        token.value = newToken
    }

    fun editUserData() {
        CoroutineScope(Dispatchers.IO).launch {
            editUserData(
                username.value,
                password.value,
                token.value,
                street.value,
                city.value,
                state.value,
                zipcode.value
            )
            clearPassword()
        }
    }

    override fun getUserDataSuccess(
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
        this.username.value = username
        this.street.value = street
        this.city.value = city
        this.state.value = state
        zipcode.value = postal_code
        this.currentUserDraws.value = currentUserDraws
        this.currentUserLoses.value = currentUserLoses
        this.currentUserWins.value = currentUserWins
        this.currentUserRank.value = currentUserRank
    }

    override fun getUserDataFail(responseMessage: String) {
        this.responseMessage.value = responseMessage
    }

    override fun editUserDataSuccess(responseMessage: String) {
        this.responseMessage.value = responseMessage
    }

    override fun editUserDataFail(responseMessage: String) {
        this.responseMessage.value = responseMessage
    }

    override fun deleteUserSuccess(responseCode: Int, responseMessage: String) {
        this.responseMessage.value = responseMessage
        if (responseCode == 200) {
            logoutState.value = true
        }
    }

    override fun deleteUserFail(responseMessage: String) {
        this.responseMessage.value = responseMessage
    }

}