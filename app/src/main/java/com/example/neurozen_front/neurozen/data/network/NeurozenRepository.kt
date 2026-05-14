package com.example.neurozen_front.neurozen.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NeurozenRepository(
    private val apiService: NeurozenApiService = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NeurozenApiService::class.java)
) {

    suspend fun login(email: String, password: String): Result<AuthSession> {
        val response = apiService.login(LoginRequest(email = email, password = password))
        if (!response.isSuccessful) {
            return Result.failure(IllegalStateException("No se pudo iniciar sesion (${response.code()})"))
        }

        val body = response.body()
        val token = body?.token ?: body?.data?.token
        val refreshToken = body?.refreshToken ?: body?.data?.refreshToken
        val expiresIn = body?.expiresIn ?: body?.data?.expiresIn
        val user = body?.user ?: body?.data?.user

        if (token.isNullOrBlank() || user?.id == null || user.email.isNullOrBlank() || user.name.isNullOrBlank()) {
            return Result.failure(IllegalStateException("Respuesta de login incompleta"))
        }

        return Result.success(
            AuthSession(
                token = token,
                refreshToken = refreshToken,
                expiresIn = expiresIn,
                userId = user.id,
                email = user.email,
                name = user.name
            )
        )
    }

    suspend fun fetchDashboard(userId: Long, bearerToken: String): Result<DashboardPayload> {
        val userResponse = apiService.getUser(userId, bearerToken)
        if (!userResponse.isSuccessful || userResponse.body()?.data == null) {
            return Result.failure(IllegalStateException("No se pudo obtener perfil"))
        }

        val user = userResponse.body()?.data ?: return Result.failure(IllegalStateException("Perfil vacio"))

        val metricsResponse = apiService.getUserHealthMetrics(userId, bearerToken)
        val sessionsResponse = apiService.getUserMeditationSessions(userId, bearerToken)

        val metrics = if (metricsResponse.isSuccessful) {
            metricsResponse.body()?.data.orEmpty()
        } else {
            emptyList()
        }

        val sessions = if (sessionsResponse.isSuccessful) {
            sessionsResponse.body()?.data.orEmpty()
        } else {
            emptyList()
        }

        return Result.success(
            DashboardPayload(
                user = user,
                metrics = metrics,
                sessions = sessions
            )
        )
    }
}

