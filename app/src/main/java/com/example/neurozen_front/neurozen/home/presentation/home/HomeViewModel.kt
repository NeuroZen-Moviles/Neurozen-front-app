package com.example.neurozen_front.neurozen.home.presentation.home

import android.text.format.DateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurozen_front.neurozen.data.network.ApiHealthMetric
import com.example.neurozen_front.neurozen.data.network.ApiMeditationSession
import com.example.neurozen_front.neurozen.data.network.NeurozenRepository
import com.example.neurozen_front.neurozen.data.session.UserSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class HomeViewModel : ViewModel() {

    private val repository = NeurozenRepository()
    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _homeState.update { it.copy(isLoading = true, error = null) }
            delay(650)

            if (!UserSession.hasActiveSession()) {
                _homeState.update {
                    it.copy(
                        isLoading = false,
                        connectedToApi = false,
                        lastSync = "Vista local ${formatNow()}",
                        error = "No hay sesion activa, mostrando datos de demostracion"
                    )
                }
                return@launch
            }

            val session = UserSession.state.value
            val userId = session.userId
            if (userId == null) {
                _homeState.update {
                    it.copy(
                        isLoading = false,
                        connectedToApi = false,
                        error = "No se encontro userId en sesion"
                    )
                }
                return@launch
            }

            repository.fetchDashboard(
                userId = userId,
                bearerToken = UserSession.bearerTokenOrEmpty()
            ).onSuccess { dashboard ->
                val mappedMetrics = mapMetrics(dashboard.metrics)
                val mappedSessions = mapSessions(dashboard.sessions)
                val stress = dashboard.metrics.firstOrNull()?.stressLevel ?: 4
                val sleep = dashboard.metrics.firstOrNull()?.sleepHours?.toFloat() ?: 7.4f
                val energy = calculateEnergy(stress)

                _homeState.update { current ->
                    current.copy(
                        user = NeurozenUser(
                            name = dashboard.user.name ?: current.user.name,
                            email = dashboard.user.email ?: current.user.email,
                            streakDays = current.user.streakDays,
                            themePreference = current.user.themePreference,
                            minutesToday = mappedSessions.sumOf { it.durationMinutes }
                        ),
                        stressLevel = stress,
                        sleepHours = sleep,
                        energyLevel = energy,
                        healthMetrics = mappedMetrics,
                        sessions = mappedSessions,
                        connectedToApi = true,
                        isLoading = false,
                        error = null,
                        lastSync = "API ${formatNow()}"
                    )
                }
            }.onFailure { throwable ->
                _homeState.update {
                    it.copy(
                        isLoading = false,
                        connectedToApi = false,
                        error = throwable.message ?: "No se pudo sincronizar",
                        lastSync = "Fallo de API ${formatNow()}"
                    )
                }
            }
        }
    }

    fun setMood(mood: String) {
        val moodGreeting = when (mood) {
            "Enfoque" -> "Profundiza tu enfoque con respiraciones cortas"
            "Sueno" -> "Prepara tu mente para un descanso profundo"
            else -> "Respira, suelta y recarga"
        }
        _homeState.update { it.copy(selectedMood = mood, greeting = moodGreeting) }
    }

    fun completeSession(sessionId: String) {
        viewModelScope.launch {
            _homeState.update { current ->
                current.copy(
                    sessions = current.sessions.map { session ->
                        if (session.id == sessionId) session.copy(status = "Completada") else session
                    },
                    stressLevel = (current.stressLevel - 1).coerceAtLeast(1),
                    lastSync = "Sesion guardada ${formatNow()}"
                )
            }
        }
    }

    fun toggleFavorite(sessionId: String) {
        _homeState.update { current ->
            current.copy(
                sessions = current.sessions.map { session ->
                    if (session.id == sessionId) {
                        session.copy(isFavorite = !session.isFavorite)
                    } else session
                }
            )
        }
    }

    fun clearError() {
        _homeState.update { it.copy(error = null) }
    }

    private fun mapMetrics(metrics: List<ApiHealthMetric>): List<HealthMetric> {
        val first = metrics.firstOrNull()
        val stress = first?.stressLevel ?: 4
        val sleep = first?.sleepHours?.toFloat() ?: 7.4f
        val energy = calculateEnergy(stress)

        return listOf(
            HealthMetric("Estres", "$stress/10", "Estado actual", stress / 10f),
            HealthMetric("Sueno", "${"%.1f".format(sleep)} h", "Horas registradas", (sleep / 10f).coerceIn(0f, 1f)),
            HealthMetric("Energia", "$energy/10", "Estimado segun estres", energy / 10f)
        )
    }

    private fun mapSessions(sessions: List<ApiMeditationSession>): List<MeditationSession> {
        if (sessions.isEmpty()) return _homeState.value.sessions
        
        val currentFavorites = _homeState.value.sessions
            .filter { it.isFavorite }
            .map { it.id }
            .toSet()

        return sessions.map { session ->
            val id = (session.id ?: 0L).toString()
            MeditationSession(
                id = id,
                title = session.title ?: "Sesion sin titulo",
                durationMinutes = session.duration ?: 10,
                type = session.type ?: "guided",
                difficulty = session.difficulty ?: "beginner",
                description = session.description ?: "Sesion de bienestar en Neurozen",
                benefit = "Relaja mente y cuerpo",
                status = session.status ?: "Disponible",
                isFavorite = currentFavorites.contains(id)
            )
        }
    }

    private fun calculateEnergy(stressLevel: Int): Int {
        return (11 - stressLevel).coerceIn(1, 10)
    }

    private fun formatNow(): String = DateFormat.format("HH:mm", Date()).toString()
}
