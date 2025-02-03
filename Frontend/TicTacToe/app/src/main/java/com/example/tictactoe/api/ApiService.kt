package com.example.tictactoe.api

import com.example.tictactoe.data_class.auth_data_class.BaseApiDataClass
import com.example.tictactoe.data_class.auth_data_class.LogInDataClass
import com.example.tictactoe.data_class.auth_data_class.SignUpDataClass
import com.example.tictactoe.data_class.game_data_class.GetHelpMoveResponseDataClass
import com.example.tictactoe.data_class.game_data_class.MoveRequestDataClass
import com.example.tictactoe.data_class.game_data_class.MoveResponseDataClass
import com.example.tictactoe.data_class.game_data_class.StartGameRequestDataClass
import com.example.tictactoe.data_class.game_data_class.StartGameResponseDataClass
import com.example.tictactoe.data_class.leaderboard_data_class.LeaderboardResponseDataClass
import com.example.tictactoe.data_class.user_data_class.DeleteUserDataClass
import com.example.tictactoe.data_class.user_data_class.GetUserDataClass
import com.example.tictactoe.data_class.user_data_class.UserDataClass
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @GET("/test_db")
    suspend fun getTestDb(): BaseApiDataClass

    @POST("/register")
    suspend fun signUpProcess(@Body request: SignUpDataClass): Response<BaseApiDataClass>

    @POST("/login")
    suspend fun loginProcess(@Body request: LogInDataClass): Response<BaseApiDataClass>

    @POST("/get_user_data")
    suspend fun getUserData(@Body request: BaseApiDataClass): Response<GetUserDataClass>

    @POST("/edit_data_or_delete_user")
    suspend fun editUserData(@Body request: UserDataClass): Response<BaseApiDataClass>

    @POST("/edit_data_or_delete_user")
    suspend fun deleteUser(@Body request: DeleteUserDataClass): Response<DeleteUserDataClass>

    @POST("/start_game")
    suspend fun startGameProcess(@Body request: StartGameRequestDataClass): Response<StartGameResponseDataClass>

    @POST("/make_move/{game_id}")
    suspend fun makeMove(
        @Path("game_id") gameId: Int,
        @Body request: MoveRequestDataClass
    ): Response<MoveResponseDataClass>

    @GET("/get_leaderboard_data")
    suspend fun getLeaderboardData(): Response<LeaderboardResponseDataClass>

    @Multipart
    @POST("/photo_report_upload")
    suspend fun photoReportUpload(
        @Part file: MultipartBody.Part,
        @Part ("data") data: RequestBody
    ): Response<BaseApiDataClass>

    @GET("/get_help_move/{game_id}")
    suspend fun getHelpMove(
        @Path("game_id") gameId: Int
    ): Response<GetHelpMoveResponseDataClass>

}