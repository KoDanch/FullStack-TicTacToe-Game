package com.example.tictactoe.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.tictactoe.view_model.fields_view_model.GameViewModel
import com.example.tictactoe.view_model.route_view_model.GameRouteViewModel
import java.io.File

@Composable
fun PhotoReportScreen(
    gameRouteViewModel: GameRouteViewModel,
    gameViewModel: GameViewModel
) {
    val imageURI = remember { mutableStateOf<List<Uri>>(emptyList()) }

    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
            if (success && gameViewModel.tempURI.value != null) {
                if (imageURI.value.size < 3) {
                    imageURI.value += gameViewModel.tempURI.value!!
                    gameViewModel.uploadPhotoReport(context, imageURI.value.size)
                }
            }
        }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "The tournament is over", fontSize = 24.sp)
            Text(text = "Please upload 3 photos of the area where it was held", fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center
        ) {
            for (imageCount in 1..3) {
                Spacer(modifier = Modifier.width(16.dp))
                Card(
                    modifier = Modifier.size(150.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .clickable {
                                when (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED) {
                                    true -> {
                                        if (imageURI.value.size < imageCount) {
                                            val newImageFile = File(
                                                context.cacheDir,
                                                "ph_${System.currentTimeMillis()}_$imageCount.jpg"
                                            )
                                            gameViewModel.tempURI.value =
                                                FileProvider.getUriForFile(
                                                    context,
                                                    "${context.packageName}.fileprovider",
                                                    newImageFile
                                                )
                                            launcher.launch(gameViewModel.tempURI.value!!)

                                        }
                                    }

                                    false -> {
                                        Toast
                                            .makeText(
                                                context,
                                                "Please provide permission to take photos in your device settings",
                                                Toast.LENGTH_LONG
                                            )
                                            .show()
                                    }
                                }
                            }
                            .background(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageURI.value.size >= imageCount) {

                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(shape = RoundedCornerShape(8.dp)),
                                model = imageURI.value[imageCount - 1],
                                contentDescription = "Photo $imageCount",
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                modifier = Modifier.wrapContentSize(),
                                text = "Photo №$imageCount",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
        Button(modifier = Modifier.padding(32.dp), onClick = {
            if (imageURI.value.isNotEmpty()) {
                when (imageURI.value.size == 3) {
                    true -> gameRouteViewModel.photoReportScreenState.value = false
                    false -> Toast.makeText(
                        context,
                        "You need to upload 3 photos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(context, "No photos to upload", Toast.LENGTH_SHORT).show()
            }

        }) {
            Text(text = "Upload Photo")
        }
    }
}