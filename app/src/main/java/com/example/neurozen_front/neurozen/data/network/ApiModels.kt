package com.example.neurozen_front.neurozen.data.network

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class ApiError(
    val code: String? = null,
    val message: String? = null
)

data class ApiPagination(
    val page: Int? = null,
    val size: Int? = null,
    val totalElements: Int? = null,
    val totalPages: Int? = null
)

data class ApiUser(
    val id: Long? = null,
    val email: String? = null,
    val name: String? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class ApiHealthMetric(
    val id: Long? = null,
    val userId: Long? = null,
    val heartRate: Int? = null,
    val bloodPressure: String? = null,
    val sleepHours: Double? = null,
    val stressLevel: Int? = null,
    val exerciseMinutes: Int? = null,
    val notes: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class ApiMeditationSession(
    val id: Long? = null,
    val userId: Long? = null,
    val title: String? = null,
    val duration: Int? = null,
    val type: String? = null,
    val difficulty: String? = null,
    val description: String? = null,
    val status: String? = null,
    val startedAt: String? = null,
    val endedAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class LoginData(
    val token: String? = null,
    val refreshToken: String? = null,
    val expiresIn: Long? = null,
    val user: ApiUser? = null
)

data class LoginResponse(
    val token: String? = null,
    val refreshToken: String? = null,
    val expiresIn: Long? = null,
    val user: ApiUser? = null,
    val data: LoginData? = null,
    val error: ApiError? = null
)

data class SingleResponse<T>(
    val data: T? = null,
    val error: ApiError? = null
)

data class ListResponse<T>(
    val data: List<T>? = null,
    val pagination: ApiPagination? = null,
    val error: ApiError? = null
)

data class AuthSession(
    val token: String,
    val refreshToken: String? = null,
    val expiresIn: Long? = null,
    val userId: Long,
    val email: String,
    val name: String
)

data class DashboardPayload(
    val user: ApiUser,
    val metrics: List<ApiHealthMetric>,
    val sessions: List<ApiMeditationSession>
)

