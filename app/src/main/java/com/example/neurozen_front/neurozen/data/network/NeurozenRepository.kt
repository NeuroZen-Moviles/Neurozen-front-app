package com.example.neurozen_front.neurozen.data.network

class NeurozenRepository(
    private val apiService: NeurozenApiService = NeurozenApiClient.service
) {

    suspend fun login(email: String, password: String): Result<AuthSession> {
        return try {
            val response = apiService.login(LoginRequest(email = email, password = password))
            if (!response.isSuccessful) {
                return Result.failure(IllegalStateException("Error ${response.code()}: ${response.message()}"))
            }

            val body = response.body()
            val token = body?.token ?: body?.data?.token
            val user = body?.user ?: body?.data?.user

            if (token.isNullOrBlank() || user?.id == null) {
                return Result.failure(IllegalStateException("Respuesta incompleta del servidor"))
            }

            Result.success(
                AuthSession(
                    token = token,
                    refreshToken = body?.refreshToken ?: body?.data?.refreshToken,
                    expiresIn = body?.expiresIn ?: body?.data?.expiresIn,
                    userId = user.id,
                    email = user.email ?: "",
                    name = user.name ?: ""
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<ApiUser> {
        return try {
            val response = apiService.register(RegisterRequest(name, email, password))
            if (!response.isSuccessful) {
                return Result.failure(IllegalStateException("Error ${response.code()}: ${response.message()}"))
            }
            val user = response.body()?.data ?: return Result.failure(IllegalStateException("Respuesta vacía"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchDashboard(userId: Long, bearerToken: String): Result<DashboardPayload> {
        return try {
            val userResponse = apiService.getUser(userId, bearerToken)
            if (!userResponse.isSuccessful) {
                return Result.failure(IllegalStateException("Error al obtener perfil"))
            }

            val user = userResponse.body()?.data ?: return Result.failure(IllegalStateException("Perfil vacío"))

            val metricsResponse = apiService.getUserHealthMetrics(userId, bearerToken)
            val sessionsResponse = apiService.getUserMeditationSessions(userId, bearerToken)

            val metrics = if (metricsResponse.isSuccessful) metricsResponse.body()?.data.orEmpty() else emptyList()
            val sessions = if (sessionsResponse.isSuccessful) sessionsResponse.body()?.data.orEmpty() else emptyList()

            Result.success(
                DashboardPayload(
                    user = user,
                    metrics = metrics,
                    sessions = sessions
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
