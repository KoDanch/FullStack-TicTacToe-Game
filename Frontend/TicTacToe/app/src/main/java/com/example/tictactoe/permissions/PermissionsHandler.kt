package com.example.tictactoe.permissions

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.tictactoe.view_model.permissions_view_model.PermissionViewModel

@Composable
fun PermissionsHandler(permissionViewModel: PermissionViewModel) {

    val secondRequest = remember { mutableStateOf(false) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            permissionViewModel.updatePermissionCamera(isGranted)
            secondRequest.value = true

        }


    val locationLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            permissionViewModel.updatePermissionLocation(locationPermissionGranted)
        }

    if (!secondRequest.value && !permissionViewModel.permissionCamera.value) {
        SideEffect {
            cameraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (secondRequest.value && !permissionViewModel.permissionLocation.value) {
        SideEffect {
            locationLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}
