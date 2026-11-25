package com.dbit.nannashikshana.data.remote

import com.dbit.nannashikshana.data.remote.dto.LoginRequestDto
import com.dbit.nannashikshana.data.remote.dto.PackVersionCheckRequestDto
import com.dbit.nannashikshana.data.remote.dto.PackVersionCheckResponseDto
import com.dbit.nannashikshana.data.remote.dto.PaginatedLeaderboardDto
import com.dbit.nannashikshana.data.remote.dto.RegisterRequestDto
import com.dbit.nannashikshana.data.remote.dto.SyncPullResponseDto
import com.dbit.nannashikshana.data.remote.dto.SyncPushRequestDto
import com.dbit.nannashikshana.data.remote.dto.SyncPushResponseDto
import com.dbit.nannashikshana.data.remote.dto.TokenResponseDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface NannaApiService {
    @POST("auth/register")
    suspend fun register(@Body req: RegisterRequestDto): Response<TokenResponseDto>

    @POST("auth/login")
    suspend fun login(@Body req: LoginRequestDto): Response<TokenResponseDto>

    @POST("content/packs/version-check")
    suspend fun checkPackVersions(@Body req: PackVersionCheckRequestDto): Response<PackVersionCheckResponseDto>

    @Streaming
    @GET("content/packs/{packCode}/download")
    suspend fun downloadPack(@Path("packCode") packCode: String): Response<ResponseBody>

    @GET("gamification/leaderboard")
    suspend fun getLeaderboard(@Query("page") page: Int = 1, @Query("page_size") pageSize: Int = 20): Response<PaginatedLeaderboardDto>

    @POST("sync/push")
    suspend fun pushSyncEvents(@Body req: SyncPushRequestDto): Response<SyncPushResponseDto>

    @GET("sync/pull")
    suspend fun pullSyncChanges(@Query("since") since: Int, @Query("limit") limit: Int = 50): Response<SyncPullResponseDto>
}
