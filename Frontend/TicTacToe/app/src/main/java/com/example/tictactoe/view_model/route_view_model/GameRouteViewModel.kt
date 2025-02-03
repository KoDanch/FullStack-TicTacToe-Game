package com.example.tictactoe.view_model.route_view_model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class GameRouteViewModel: ViewModel() {
    val startGameProcessState = mutableStateOf(false)
    val gameAlertDialog = mutableStateOf(false)
    val editingUserDataScreenState = mutableStateOf(false)
    val photoReportScreenState = mutableStateOf(false)

    fun updateEditingfScreenState() {
        editingUserDataScreenState.value = !editingUserDataScreenState.value
    }

    fun updateStartGameProcessState() {
        startGameProcessState.value = !startGameProcessState.value
    }
    fun updateGameAlertDialogState() {
        gameAlertDialog.value = !gameAlertDialog.value
    }
    fun updatePhotoReportScreenState() {
        startGameProcessState.value = false
        photoReportScreenState.value = !photoReportScreenState.value
    }

    fun logoutState() {
        startGameProcessState.value = false
        gameAlertDialog.value = !gameAlertDialog.value
        editingUserDataScreenState.value = !editingUserDataScreenState.value
    }
}