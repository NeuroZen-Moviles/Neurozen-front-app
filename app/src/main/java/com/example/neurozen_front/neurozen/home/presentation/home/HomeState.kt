package com.example.neurozen_front.neurozen.home.presentation.home

import com.example.neurozen_front.neurozen.data.local.AppointmentEntity

data class NeurozenUser(
    val name: String = "Joao",
    val email: String = "joao@neurozen.app",
    val streakDays: Int = 12,
    val themePreference: String = "Bosque claro", // "Bosque claro" o "Noche zen"
    val minutesToday: Int = 18,
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
    val healthMetrics: List<HealthMetric> = listOf(
        HealthMetric("Estrés", "4/10", "Bajo control", 0.40f),
        HealthMetric("Sueño", "7.4 h", "Objetivo saludable", 0.74f),
        HealthMetric("Energía", "7/10", "Buen ritmo", 0.70f)
    ),
    val showEmotionalForm: Boolean = false,
    val quickActions: List<WellnessMoment> = listOf(
        WellnessMoment("Respiración 4-7-8", "Calma inmediata en 60 s", "BR"),
        WellnessMoment("Meditación guiada", "Pausa mental con voz suave", "MD"),
        WellnessMoment("Chequeo emocional", "Identifica cómo te sientes hoy", "EQ")
    ),
    val sessions: List<MeditationSession> = listOf(
        MeditationSession(
            id = "session-forest",
            title = "Paz en la Naturaleza",
            durationMinutes = 10,
            type = "Guiada",
            difficulty = "Principiante",
            description = "Conecta con los sonidos del bosque para reducir la ansiedad.",
            benefit = "Paz mental y relajación profunda.",
            status = "Disponible",
            imageUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?q=80&w=1000",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        ),
        MeditationSession(
            id = "session-breath",
            title = "Control del Estrés",
            durationMinutes = 6,
            type = "Respiración",
            difficulty = "Todos los niveles",
            description = "Técnicas de respiración consciente para momentos de alta tensión.",
            benefit = "Reducción inmediata del cortisol.",
            status = "Recomendada",
            imageUrl = "https://images.unsplash.com/photo-1506126613408-eca07ce68773?q=80&w=1000",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
        ),
        MeditationSession(
            id = "session-sleep",
            title = "Sueño Profundo",
            durationMinutes = 15,
            type = "Sueño",
            difficulty = "Principiante",
            description = "Libera la mente de preocupaciones antes de dormir.",
            benefit = "Mejor calidad de descanso nocturno.",
            status = "Nueva",
            imageUrl = "https://images.unsplash.com/photo-1511295742364-917e7037a8ce?q=80&w=1000",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
        ),
        MeditationSession(
            id = "session-focus",
            title = "Enfoque Productivo",
            durationMinutes = 8,
            type = "Mental",
            difficulty = "Intermedio",
            description = "Elimina distracciones y mejora tu concentración laboral.",
            benefit = "Mayor claridad mental.",
            status = "Disponible",
            imageUrl = "https://images.unsplash.com/photo-1528715471579-d1bcf0ba5e83?q=80&w=1000",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"
        )
    ),
    val healthHistory: List<com.example.neurozen_front.neurozen.data.network.HealthMetricResource> = emptyList()
)
