package com.example.neurozen_front.neurozen.home.presentation.home

data class NeurozenUser(
    val name: String = "Joao",
    val email: String = "joao@neurozen.app",
    val streakDays: Int = 12,
    val themePreference: String = "Bosque claro",
    val minutesToday: Int = 18
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
    val isFavorite: Boolean = false
)

data class HomeState(
    val user: NeurozenUser = NeurozenUser(),
    val selectedMood: String = "Calma",
    val availableMoods: List<String> = listOf("Calma", "Enfoque", "Sueno"),
    val greeting: String = "Respira, suelta y recarga",
    val stressLevel: Int = 4,
    val energyLevel: Int = 7,
    val sleepHours: Float = 7.4f,
    val lastSync: String = "Demo local",
    val isLoading: Boolean = false,
    val connectedToApi: Boolean = false,
    val error: String? = null,
    val healthMetrics: List<HealthMetric> = listOf(
        HealthMetric("Estres", "4/10", "Bajo control", 0.40f),
        HealthMetric("Sueno", "7.4 h", "Objetivo saludable", 0.74f),
        HealthMetric("Energia", "7/10", "Buen ritmo", 0.70f)
    ),
    val quickActions: List<WellnessMoment> = listOf(
        WellnessMoment("Respiracion 4-7-8", "Calma inmediata en 60 s", "BR"),
        WellnessMoment("Meditacion guiada", "Pausa mental con voz suave", "MD"),
        WellnessMoment("Chequeo emocional", "Identifica como te sientes hoy", "EQ")
    ),
    val sessions: List<MeditationSession> = listOf(
        MeditationSession(
            id = "session-forest",
            title = "Bosque interior",
            durationMinutes = 10,
            type = "guided",
            difficulty = "beginner",
            description = "Una practica breve para bajar pulsaciones y ordenar la mente.",
            benefit = "Reduce tension y mejora el enfoque.",
            status = "Disponible"
        ),
        MeditationSession(
            id = "session-breath",
            title = "Respiracion consciente",
            durationMinutes = 6,
            type = "breathing",
            difficulty = "all_levels",
            description = "Ritmo suave para recuperar aire y presencia.",
            benefit = "Ideal antes de estudiar o dormir.",
            status = "Recomendada"
        ),
        MeditationSession(
            id = "session-sleep",
            title = "Sueno reparador",
            durationMinutes = 15,
            type = "sleep",
            difficulty = "beginner",
            description = "Secuencia calmante para soltar la mente antes de acostarte.",
            benefit = "Mejora descanso y continuidad del sueno.",
            status = "Nueva"
        )
    )
)
