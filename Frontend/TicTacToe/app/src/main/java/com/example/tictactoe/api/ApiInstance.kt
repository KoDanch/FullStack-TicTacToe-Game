package com.example.tictactoe.api

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.tictactoe.callback.auth_service_callback.AuthHandler
import com.example.tictactoe.callback.game_service_callback.GameHandler
import com.example.tictactoe.callback.leaderboard_callback.LeaderboardHandler
import com.example.tictactoe.callback.user_data_callback.UserDataHandler
import com.example.tictactoe.data_class.auth_data_class.BaseApiDataClass
import com.example.tictactoe.data_class.auth_data_class.LogInDataClass
import com.example.tictactoe.data_class.auth_data_class.SignUpDataClass
import com.example.tictactoe.data_class.game_data_class.MoveRequestDataClass
import com.example.tictactoe.data_class.game_data_class.MoveResponseDataClass
import com.example.tictactoe.data_class.game_data_class.StartGameRequestDataClass
import com.example.tictactoe.data_class.photo_report_data_class.PhotoRequestDataClass
import com.example.tictactoe.data_class.user_data_class.AddressDataClass
import com.example.tictactoe.data_class.user_data_class.DeleteUserDataClass
import com.example.tictactoe.data_class.user_data_class.GetUserDataClass
import com.example.tictactoe.data_class.user_data_class.UserDataClass
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("http://192.168.31.136:5000")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val apiService: ApiService = retrofit.create(ApiService::class.java)

suspend fun getTest() {
    val response = apiService.getTestDb()

    Log.d("Api", "getTest: ${response.message}")
}

suspend fun registerUser(
    username: String,
    password: String,
    street: String,
    city: String,
    state: String,
    zipcode: String
) {
    val request = SignUpDataClass(username, password, street, city, state, zipcode)
    val authHandler = AuthHandler
    try {
        val response = apiService.signUpProcess(request)
        if (response.code() in 400..599) {
            val responseBody = response.errorBody()?.string()
            val responseMessage = Gson().fromJson(responseBody, BaseApiDataClass::class.java)
            authHandler.notifySignUpFail(response.code(), responseMessage.message)
        } else {
            val responseMessage = response.body()?.message
            if (responseMessage != null) {
                authHandler.notifySignUpSuccess(response.code(), responseMessage.toString())
                Log.d("Api", "registerUserSuccess: ${response.body()?.message}")
            }
        }
    } catch (e: Exception) {
        Log.d("Api", "registerUser: ${e.stackTrace}")
        authHandler.notifySignUpFail(500, e.message.toString())

    }
}

suspend fun loginUser(username: String, password: String) {
    val request = LogInDataClass(username, password)
    val authHandler = AuthHandler
    try {
        val response = apiService.loginProcess(request)

        if (response.code() in 400..599) {
            val responseBody = response.errorBody()?.string()
            val responseMessage = Gson().fromJson(responseBody, BaseApiDataClass::class.java)
            authHandler.notifyLoginFail(response.code(), responseMessage.message)
            Log.d("Api", "login: ${responseMessage.message}")

        } else {
            Log.d("Api", "loginUser: ${response.code()}")
            val responseMessage = response.body()
            if (responseMessage != null) {
                authHandler.notifyLoginSuccess(
                    response.code(),
                    responseMessage.message,
                    responseMessage.token
                )
            }
            Log.d("Api", "login: ${responseMessage?.token}")
        }
    } catch (e: Exception) {
        Log.d("Api", "loginUser: ${e.message}")
        authHandler.notifyLoginFail(500, e.message.toString())
    }
}

suspend fun parsingUserData(token: String) {
    val request = BaseApiDataClass("", token)
    val userDataHandler = UserDataHandler
    try {
        val response = apiService.getUserData(request)

        if (response.code() in 400..599) {
            val responseBody = response.errorBody()?.string()
            val responseMessage = Gson().fromJson(responseBody, GetUserDataClass::class.java)
            userDataHandler.notifyGetUserDataFail(responseMessage.message)
            Log.d("Api", "Error: ${responseMessage.message}")
        } else {
            val responseBody = response.body()
            if (responseBody != null) {
                Log.d("Api", "User data: ${responseBody.user_data}")
                userDataHandler.notifyGetUserDataSuccess(
                    responseMessage = responseBody.message,
                    responseBody.user_data?.username ?: "",
                    responseBody.user_data?.currentUserDraws ?: "",
                    responseBody.user_data?.currentUserLoses ?: "",
                    responseBody.user_data?.currentUserWins ?: "",
                    responseBody.user_data?.currentUserRank ?: "",
                    responseBody.user_data?.address?.street ?: "",
                    responseBody.user_data?.address?.city ?: "",
                    responseBody.user_data?.address?.state ?: "",
                    responseBody.user_data?.address?.postal_code ?: ""
                )
                val user = responseBody.user_data
                Log.d("Api", "Username: ${user?.username}")
            }
        }
    } catch (e: Exception) {
        Log.e("Api", "fetchUserData: ${e.message}")
    }
}

suspend fun editUserData(
    username: String,
    password: String,
    token: String,
    street: String,
    city: String,
    state: String,
    postal_code: String
) {
    val request = UserDataClass(
        username,
        password,
        "",
        "",
        "",
        "",
        token,
        AddressDataClass(street, city, state, postal_code)
    )
    val userDataHandler = UserDataHandler
    try {
        val response = apiService.editUserData(request)

        if (response.code() in 400..599) {
            val responseBody = response.errorBody()?.string()
            val responseMessage = Gson().fromJson(responseBody, GetUserDataClass::class.java)
            userDataHandler.notifyEditUserDataFail(responseMessage.message)
            parsingUserData(token)
            Log.d("Api", "editUserData: ${responseMessage.message}")
        } else {
            val responseBody = response.body()
            if (responseBody != null) {
                userDataHandler.notifyEditUserDataSuccess(responseBody.message)
            }
        }


    } catch (e: Exception) {
        Log.e("Api", "editUserData: ${e.message}")
    }
}

suspend fun deletingUser(password: String, token: String, userDelete: Int) {
    val request = DeleteUserDataClass("", password, token, userDelete)
    val userDataHandler = UserDataHandler
    try {
        val response = apiService.deleteUser(request)

        if (response.code() in 400..599) {
            val responseBody = response.errorBody()?.string()
            val responseMessage = Gson().fromJson(responseBody, GetUserDataClass::class.java)
            userDataHandler.notifyUserDeleteFail(responseMessage.message)

            Log.d("Api", "deletingUser: ${responseMessage.message}")
        } else {
            val responseBody = response.body()
            if (responseBody != null) {
                userDataHandler.notifyUserDeleteSuccess(response.code(), responseBody.message)
            }
        }
    } catch (e: Exception) {
        Log.e("Api", "deletingUser: ${e.message}")
    }

}


suspend fun startGame(
    token: String,
    location: String,
    currentTurn: String,
    selectedDifficulty: Int
) {
    val request = StartGameRequestDataClass(location, token, currentTurn, selectedDifficulty)
    val gameHandler = GameHandler
    try {
        val response = apiService.startGameProcess(request)

        if (response.code() in 400..599) {
            val responseBody = response.errorBody()?.string()
            val responseMessage = Gson().fromJson(responseBody, BaseApiDataClass::class.java)
            gameHandler.notifyStartGameFail(responseMessage.message)
            Log.d("Api", "startGame: ${responseMessage.message}")

        } else {
            Log.d("Api", "startGame: ${response.code()}")
            val responseMessage = response.body()
            if (responseMessage != null) {
                gameHandler.notifyStartGameSuccess(
                    responseMessage.gameId,
                    responseMessage.board
                )
                Log.d("Api", "startGame: $responseMessage")
            }
        }

    } catch (e: Exception) {
        Log.d("Api", "startGame: ${e.message}")
    }
}

suspend fun makeMove(
    gameId: Int,
    position: Int,
    player: String,
    currentTurn: String,
    selectedDifficulty: Int
) {
    val request = MoveRequestDataClass(position, player, currentTurn, selectedDifficulty)
    val gameHandler = GameHandler
    try {
        val response = apiService.makeMove(gameId, request)

        if (response.code() in 400..599) {
            val responseBody = response.errorBody()?.string()
            val responseMessage = Gson().fromJson(responseBody, MoveResponseDataClass::class.java)
            gameHandler.notifyMakeMoveFail(responseMessage.message)
            Log.d("Api", "makeMove: ${responseMessage.message}")

        } else {
            Log.d("Api", "makeMove: ${response.code()}")
            val responseMessage = response.body()


            gameHandler.notifyMakeMoveSuccess(
                responseMessage?.message ?: "Default message",
                responseMessage?.board ?: ".........",
                responseMessage?.current_turn ?: "X",
                responseMessage?.winner
            )
            Log.d("Api", "makeMove: ${responseMessage?.winner}")


        }

    } catch (e: Exception) {
        Log.d("Api", "makeMove: ${e.message}")
    }
}

suspend fun getHelpMove(gameId: Int) {
    val gameHandler = GameHandler
    try {
        val response = apiService.getHelpMove(gameId)
        if (response.code() in 400..599) {
            val responseBody = response.errorBody()?.string()

            gameHandler.notifyGetHelpMoveFail(responseBody.toString())

        } else {
            Log.d("helpMove", "getHelp: ${response.code()}")
            val responseMessage = response.body()
            gameHandler.notifyGetHelpMoveSuccess(responseMessage?.helpBoard.toString())
            Log.d("helpMove", "makeMove: ${responseMessage?.helpBoard}")
        }

    } catch (e: Exception) {
        Log.d("Api", "getHelpMove: ${e.message}")
    }

}

suspend fun getLeaderboard() {
    val leaderboardHandler = LeaderboardHandler
    try {
        val response = apiService.getLeaderboardData()

        if (response.code() in 400..599) {
            val responseMessage = response.errorBody()
            leaderboardHandler.notifyGetLeaderboardFail(responseMessage.toString())
            Log.d("Api", "getLeaderboard: ${responseMessage.toString()}")

        } else {
            Log.d("Api", "getLeaderboard: ${response.code()}")
            val responseMessage = response.body()
            responseMessage?.leaderboard?.let { leaderboard ->
                leaderboardHandler.notifyGetLeaderboardSuccess(
                    leaderboard
                )
            }
            Log.d("Api", "getLeaderboard: ${responseMessage?.leaderboard}")
        }
    } catch (e: Exception) {
        Log.d("Api", "getLeaderboard: ${e.message}")
    }
}

suspend fun uploadPhoto(uri: Uri, context: Context, token: String, numberPhoto: Int) {
    val contextResolver = context.contentResolver
    val inputStream = contextResolver.openInputStream(uri)
    val tempFile = withContext(Dispatchers.IO) {
        File.createTempFile("upload_file", ".jpg", context.cacheDir)
    }

    inputStream?.use { input ->
        tempFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    val createData = PhotoRequestDataClass(token, numberPhoto)
    val dataToJson = Gson().toJson(createData)
    val data = dataToJson.toRequestBody("application/json".toMediaTypeOrNull())

    Log.d("Api", "uploadPhoto: $token + $numberPhoto")
    val photoBody = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
    val photoPart = MultipartBody.Part.createFormData("file", tempFile.name, photoBody)


    try {
        val response = apiService.photoReportUpload(photoPart, data)
        if (response.isSuccessful) {
            Log.d("Api", "Photo uploaded success")
            if (tempFile.exists()) {
                tempFile.delete()
            }
        } else {
            Log.d("Api", "Error uploadPhoto: ${response.errorBody()?.string()}")
        }

    } catch (e: Exception) {
        Log.d("Api", "Error uploadPhoto: ${e.message}")
    }
}
