package com.example.neurozen_front.neurozen.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NeurozenApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("users/{id}")
    suspend fun getUser(
        @Path("id") userId: Long,
        @Header("Authorization") bearerToken: String
    ): Response<SingleResponse<ApiUser>>

    @GET("users/{id}/health_metrics")
    suspend fun getUserHealthMetrics(
        @Path("id") userId: Long,
        @Header("Authorization") bearerToken: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ListResponse<ApiHealthMetric>>

    @GET("users/{id}/meditation_sessions")
    suspend fun getUserMeditationSessions(
        @Path("id") userId: Long,
        @Header("Authorization") bearerToken: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ListResponse<ApiMeditationSession>>
}

