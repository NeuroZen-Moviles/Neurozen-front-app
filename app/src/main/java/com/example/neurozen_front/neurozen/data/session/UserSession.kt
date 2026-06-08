package com.example.neurozen_front.neurozen.data.session

import com.example.neurozen_front.neurozen.data.network.AuthSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSessionState(
    val token: String? = null,
    val refreshToken: String? = null,
    val expiresIn: Long? = null,
    val userId: String? = null, // Cambiado de Long a String para GUID de .NET
    val email: String? = null,
    val name: String? = null
)

object UserSession {
    private val _state = MutableStateFlow(UserSessionState())
    val state: StateFlow<UserSessionState> = _state.asStateFlow()

    fun save(authSession: AuthSession) {
        _state.value = UserSessionState(
            token = authSession.token,
            userId = authSession.userId,
            email = authSession.email,
            name = authSession.username // Backend usa username
        )
    }

    fun clear() {
        _state.value = UserSessionState()
    }

    val current: UserSessionState?
        get() = if (hasActiveSession()) _state.value else null

    fun hasActiveSession(): Boolean {
        return !_state.value.token.isNullOrBlank() && _state.value.userId != null
    }

    fun bearerTokenOrEmpty(): String {
        val token = _state.value.token.orEmpty()
        return if (token.startsWith("Bearer ")) token else "Bearer $token"
    }
}
