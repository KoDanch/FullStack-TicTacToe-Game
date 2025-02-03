package com.example.tictactoe.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.tictactoe.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun SplashScreen() {
        lifecycleScope.launch {
            delay(2000)
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            finish()
        }
        val titleInEnterApp =
            listOf(
                "Hello, world!",
                "You will defeat AI?",
                "YOU ARE NOT A ZERO!",
                "Put a cross if it's cool :)",
                "What a wonderful day",
                "Do you want to play tic tac toe?",
                "Your enemies will play cross!",
                "Ah, ah, what? Are we playing?"
            ).random()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)) {
                Text(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    text = titleInEnterApp,
                    fontSize = 25.sp,
                    color = Color.White
                )
            }

            Image(
                modifier = Modifier
                    .size(150.dp)
                    .align(alignment = Alignment.Center)
                    .clip(shape = CircleShape),
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo"
            )
            Text(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 32.dp, start = 16.dp), text = "v. 1.0.0", color = Color.White
            )
            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp), text = "Developed by KoDan", color = Color.Yellow
            )

        }
    }
}
