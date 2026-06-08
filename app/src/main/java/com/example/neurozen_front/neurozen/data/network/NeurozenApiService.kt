package com.example.neurozen_front.neurozen.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface NeurozenApiService {

    // --- Módulo de Autenticación (IAM) ---
    // Según AuthenticationController.cs: [Route("api/v1/[controller]")] -> api/v1/authentication
    
    @POST("api/v1/authentication/sign-in")
    suspend fun signIn(@Body request: SignInRequest): Response<SignInResponse>

    @POST("api/v1/authentication/sign-up")
    suspend fun signUp(@Body request: SignUpRequest): Response<SignUpResponse>

    // --- Módulo de Psicólogos (Professionals) ---
    // Según ProfessionalsController.cs en el backend

    @GET("api/v1/professionals")
    suspend fun getProfessionals(
        @Header("Authorization") bearerToken: String
    ): Response<List<ProfessionalResource>>

    @GET("api/v1/professionals/{id}")
    suspend fun getProfessionalById(
        @Path("id") id: String, // Cambiado de Long a String (Guid)
        @Header("Authorization") bearerToken: String
    ): Response<ProfessionalResource>

    // --- Módulo de Citas (Appointments) ---

    @POST("api/v1/appointments")
    suspend fun createAppointment(
        @Body request: AppointmentRequest,
        @Header("Authorization") bearerToken: String
    ): Response<AppointmentResponse>

    @GET("api/v1/patients/{patientId}/appointments")
    suspend fun getPatientAppointments(
        @Path("patientId") patientId: String, // Cambiado de Long a String (Guid)
        @Header("Authorization") bearerToken: String
    ): Response<List<AppointmentResponse>>

    // --- Módulo de Dashboard ---

    @GET("api/v1/users/{userId}/dashboard")
    suspend fun getDashboard(
        @Path("userId") userId: String,
        @Header("Authorization") bearerToken: String
    ): Response<DashboardResponse>

    // --- Módulo de Contenidos (Meditaciones) ---

    @GET("api/v1/contents/meditations")
    suspend fun getMeditations(
        @Header("Authorization") bearerToken: String
    ): Response<List<MeditationResource>>

    // --- Módulo de Suscripciones ---

    @POST("api/v1/subscriptions")
    suspend fun createSubscription(
        @Body request: SubscriptionRequest,
        @Header("Authorization") bearerToken: String
    ): Response<SubscriptionResource>

    @GET("api/v1/users/{userId}/subscription")
    suspend fun getUserSubscription(
        @Path("userId") userId: String,
        @Header("Authorization") bearerToken: String
    ): Response<SubscriptionResource>

    // --- Módulo de Métricas de Salud (Triggers/Emociones) ---

    @POST("api/v1/health_metrics")
    suspend fun createHealthMetric(
        @Body request: HealthMetricRequest,
        @Header("Authorization") bearerToken: String
    ): Response<HealthMetricResource>

    @GET("api/v1/users/{userId}/health_metrics")
    suspend fun getUserHealthMetrics(
        @Path("userId") userId: String,
        @Header("Authorization") bearerToken: String
    ): Response<List<HealthMetricResource>>
}
