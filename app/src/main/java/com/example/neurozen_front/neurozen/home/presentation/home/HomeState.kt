package com.example.neurozen_front.neurozen.home.presentation.home

import com.example.neurozen_front.neurozen.data.local.AppointmentEntity

data class NeurozenUser(
    val name: String = "",
    val email: String = "",
    val streakDays: Int = 0,
    val themePreference: String = "Bosque claro", // "Bosque claro" o "Noche zen"
    val minutesToday: Int = 0,
    val subscriptionPlan: String = "Gratuito"
)

data class HealthMetric(
    val label: String,
    val value: String,
    val detail: String,
    val progress: Float
)

data class WellnessMoment(
    val title: String,
    val subtitle: String,
    val badge: String
)

data class MeditationSession(
    val id: String,
    val title: String,
    val durationMinutes: Int,
    val type: String,
    val difficulty: String,
    val description: String,
    val benefit: String,
    val status: String,
    val isFavorite: Boolean = false,
    val imageUrl: String? = null,
    val audioUrl: String? = null
)

data class HomeState(
    val user: NeurozenUser = NeurozenUser(),
    val selectedMood: String = "Calma",
    val availableMoods: List<String> = listOf("Calma", "Enfoque", "Sueño"),
    val greeting: String = "Respira, suelta y recarga",
    val headerImageUrl: String = "https://images.unsplash.com/photo-1518199266791-5375a83190b7?q=80&w=1000",
    val stressLevel: Int = 4,
    val energyLevel: Int = 7,
    val sleepHours: Float = 7.4f,
    val lastSync: String = "Sincronización local",
    val isLoading: Boolean = false,
    val connectedToApi: Boolean = false,
    val error: String? = null,
    val upcomingAppointments: List<AppointmentEntity> = emptyList(),
    val healthMetrics: List<HealthMetric> = emptyList(),
    val showEmotionalForm: Boolean = false,
    val quickActions: List<WellnessMoment> = listOf(
        WellnessMoment("Respiración 4-7-8", "Calma inmediata en 60 s", "BR"),
        WellnessMoment("Meditación guiada", "Pausa mental con voz suave", "MD"),
        WellnessMoment("Chequeo emocional", "Identifica cómo te sientes hoy", "EQ")
    ),
    val sessions: List<MeditationSession> = emptyList(),
    val healthHistory: List<com.example.neurozen_front.neurozen.data.network.HealthMetricResource> = emptyList()
)
