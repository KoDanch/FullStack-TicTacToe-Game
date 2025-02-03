package com.example.tictactoe.view_model.permissions_view_model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class PermissionViewModel: ViewModel() {

    val permissionCamera = mutableStateOf(false)
    val permissionLocation = mutableStateOf(false)

    fun updatePermissionCamera(newState: Boolean) {
        permissionCamera.value = newState
    }
    fun updatePermissionLocation(newState: Boolean) {
        permissionLocation.value = newState
    }

}