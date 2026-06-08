package com.example.neurozen_front.neurozen.data.network

class NeurozenRepository(
    private val apiService: NeurozenApiService = NeurozenApiClient.service
) {

    suspend fun login(username: String, password: String): Result<AuthSession> {
        return try {
            val response = apiService.signIn(SignInRequest(username = username, password = password))
            if (!response.isSuccessful) {
                Result.failure<AuthSession>(IllegalStateException("Error ${response.code()}: ${response.message()}"))
            } else {
                val body = response.body() ?: throw IllegalStateException("Cuerpo de respuesta vacío")
                Result.success(
                    AuthSession(
                        token = body.token,
                        userId = body.id,
                        username = body.username,
                        email = "" 
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure<AuthSession>(e)
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<SignUpResponse> {
        return try {
            val response = apiService.signUp(SignUpRequest(username, password, email))
            if (!response.isSuccessful) {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure<SignUpResponse>(IllegalStateException("Error ${response.code()}: $errorMsg"))
            } else {
                val body = response.body() ?: throw IllegalStateException("Respuesta vacía")
                Result.success(body)
            }
        } catch (e: Exception) {
            Result.failure<SignUpResponse>(e)
        }
    }

    suspend fun fetchDashboard(userId: String, bearerToken: String): Result<DashboardResponse> {
        return try {
            val response = apiService.getDashboard(userId, bearerToken)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure<DashboardResponse>(IllegalStateException("Error al obtener dashboard"))
            }
        } catch (e: Exception) {
            Result.failure<DashboardResponse>(e)
        }
    }

    suspend fun getMeditations(token: String): Result<List<MeditationResource>> {
        return try {
            val response = apiService.getMeditations(token)
            if (response.isSuccessful) Result.success(response.body().orEmpty())
            else Result.failure<List<MeditationResource>>(IllegalStateException("Error al obtener meditaciones"))
        } catch (e: Exception) {
            Result.failure<List<MeditationResource>>(e)
        }
    }

    suspend fun getProfessionals(token: String): Result<List<ProfessionalResource>> {
        return try {
            val response = apiService.getProfessionals(token)
            if (response.isSuccessful) Result.success(response.body().orEmpty())
            else Result.failure<List<ProfessionalResource>>(IllegalStateException("Error al obtener profesionales"))
        } catch (e: Exception) {
            Result.failure<List<ProfessionalResource>>(e)
        }
    }

    suspend fun createAppointment(appointment: AppointmentRequest, token: String): Result<AppointmentResponse> {
        return try {
            val response = apiService.createAppointment(appointment, token)
            if (response.isSuccessful && response.body() != null) Result.success(response.body()!!)
            else Result.failure<AppointmentResponse>(IllegalStateException("Error al crear cita"))
        } catch (e: Exception) {
            Result.failure<AppointmentResponse>(e)
        }
    }

    suspend fun createSubscription(request: SubscriptionRequest, token: String): Result<SubscriptionResource> {
        return try {
            val response = apiService.createSubscription(request, token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error de servidor"
                Result.failure<SubscriptionResource>(IllegalStateException(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure<SubscriptionResource>(e)
        }
    }

    suspend fun getPatientAppointments(patientId: String, token: String): Result<List<AppointmentResponse>> {
        return try {
            val response = apiService.getPatientAppointments(patientId, token)
            if (response.isSuccessful) Result.success(response.body().orEmpty())
            else Result.failure<List<AppointmentResponse>>(IllegalStateException("Error al obtener citas"))
        } catch (e: Exception) {
            Result.failure<List<AppointmentResponse>>(e)
        }
    }

    suspend fun createHealthMetric(request: HealthMetricRequest, token: String): Result<HealthMetricResource> {
        return try {
            val response = apiService.createHealthMetric(request, token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure<HealthMetricResource>(IllegalStateException("Error al registrar métrica"))
            }
        } catch (e: Exception) {
            Result.failure<HealthMetricResource>(e)
        }
    }

    suspend fun getHealthHistory(userId: String, token: String): Result<List<HealthMetricResource>> {
        return try {
            val response = apiService.getUserHealthMetrics(userId, token)
            if (response.isSuccessful) Result.success(response.body().orEmpty())
            else Result.failure<List<HealthMetricResource>>(IllegalStateException("Error al obtener historial"))
        } catch (e: Exception) {
            Result.failure<List<HealthMetricResource>>(e)
        }
    }
}
