package com.example.neurozen_front.neurozen.home.presentation.home

import android.util.Log
import android.text.format.DateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurozen_front.neurozen.data.network.ApiHealthMetric
import com.example.neurozen_front.neurozen.data.network.ApiMeditationSession
import com.example.neurozen_front.neurozen.data.network.DashboardPayload
import com.example.neurozen_front.neurozen.data.network.NeurozenRepository
import com.example.neurozen_front.neurozen.data.session.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NeurozenRepository
) : ViewModel() {
    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    init {
        // Al iniciar, cargamos demo primero para que la UI responda instantáneamente
        Log.d("HomeViewModel", "Iniciando con datos locales...")
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _homeState.update { it.copy(isLoading = true, error = null) }
            
            // Si no hay sesión, nos quedamos en modo demo sin siquiera intentar red
            if (!UserSession.hasActiveSession()) {
                delay(300)
                _homeState.update { it.copy(isLoading = false, connectedToApi = false) }
                return@launch
            }

            val session = UserSession.state.value
            val userId = session.userId ?: return@launch

            try {
                // Forzamos que la petición ocurra fuera del hilo de UI
                val result = withContext(Dispatchers.IO) {
                    try {
                        repository.fetchDashboard(
                            userId = userId,
                            bearerToken = UserSession.bearerTokenOrEmpty()
                        )
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                }

                result.onSuccess { dashboard ->
                    _homeState.update { current ->
                        current.copy(
                            user = current.user.copy(
                                name = dashboard.user.name ?: current.user.name,
                                email = dashboard.user.email ?: current.user.email
                            ),
                            healthMetrics = mapMetrics(dashboard.metrics),
                            sessions = mapSessions(dashboard.sessions),
                            connectedToApi = true,
                            isLoading = false,
                            error = null,
                            lastSync = "Sincronizado ${formatNow()}"
                        )
                    }
                }.onFailure {
                    _homeState.update { it.copy(
                        isLoading = false, 
                        connectedToApi = false,
                        error = "Servidor no disponible (usando modo local)"
                    ) }
                }
            } catch (e: Exception) {
                _homeState.update { it.copy(isLoading = false, error = "Error inesperado") }
            }
        }
    }

    private fun mapMetrics(metrics: List<ApiHealthMetric>): List<HealthMetric> {
        return metrics.take(3).map { 
            HealthMetric("Salud", "${it.stressLevel}/10", "Pulso: ${it.heartRate}", (it.stressLevel ?: 0) / 10f)
        }.ifEmpty { _homeState.value.healthMetrics }
    }

    private fun mapSessions(sessions: List<ApiMeditationSession>): List<MeditationSession> {
        return sessions.map { 
            MeditationSession(
                id = it.id?.toString() ?: "0",
                title = it.title ?: "Sesión",
                durationMinutes = it.duration ?: 10,
                type = it.type ?: "Guided",
                difficulty = it.difficulty ?: "Beginner",
                description = it.description ?: "",
                benefit = "Bienestar",
                status = it.status ?: "Disponible"
            )
        }.ifEmpty { _homeState.value.sessions }
    }

    fun setMood(mood: String) {
        _homeState.update { it.copy(selectedMood = mood) }
    }

    fun toggleFavorite(id: String) { /* ... */ }

    fun completeSession(sessionId: String) {
        _homeState.update { current ->
            current.copy(
                sessions = current.sessions.map {
                    if (it.id == sessionId) it.copy(status = "Completada") else it
                }
            )
        }
    }

    private fun formatNow(): String = DateFormat.format("HH:mm", Date()).toString()
}
