package com.example.neurozen_front.neurozen.home.presentation.zenbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Message(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class ZenBotState(
    val messages: List<Message> = listOf(
        Message("¡Hola! Soy ZenBot, tu asistente de bienestar. 🌿 ¿Cómo te sientes hoy o en qué puedo ayudarte con tu estrés?", false)
    ),
    val inputText: String = "",
    val isTyping: Boolean = false
)

@HiltViewModel
class ZenBotViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(ZenBotState())
    val state: StateFlow<ZenBotState> = _state.asStateFlow()

    private val predefinedResponses = mapOf(
        "hola" to "¡Hola! Estoy aquí para escucharte. ¿Sientes mucha presión hoy?",
        "estres" to "El estrés es una respuesta natural. Te recomiendo probar la técnica de respiración 4-7-8 que tenemos en la sección de Inicio. ¿Te gustaría saber más?",
        "dormir" to "Dormir bien es fundamental. Intenta alejar las pantallas 30 minutos antes de acostarte y prueba nuestra sesión de 'Sueño Reparador'.",
        "ansiedad" to "Lamento que te sientas así. Respira profundo. Inhala en 4 tiempos, mantén 4 y exhala en 4. Estoy contigo.",
        "ayuda" to "Puedo darte consejos sobre manejo de estrés, técnicas de respiración o recomendarte sesiones de meditación. ¿Qué prefieres?",
        "gracias" to "¡De nada! Tu bienestar es lo más importante. ¿Algo más en lo que pueda apoyarte?",
        "consejo" to "Un pequeño consejo: toma micro-pausas de 2 minutos cada hora para estirarte y cerrar los ojos. ¡Tu mente te lo agradecerá!",
        "meditar" to "La meditación ayuda a reentrenar tu cerebro para la calma. Recomiendo empezar con 'Bosque Interior' por 10 minutos.",
        "ejercicio" to "Incluso una caminata de 15 minutos puede reducir significativamente el cortisol (la hormona del estrés)."
    )

    fun onInputChange(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _state.value.inputText.trim()
        if (text.isEmpty()) return

        val userMessage = Message(text, true)
        _state.update { it.copy(
            messages = it.messages + userMessage,
            inputText = "",
            isTyping = true
        ) }

        viewModelScope.launch {
            delay(1500) // Simular pensamiento del bot
            val responseText = findResponse(text)
            val botMessage = Message(responseText, false)
            _state.update { it.copy(
                messages = it.messages + botMessage,
                isTyping = false
            ) }
        }
    }

    private fun findResponse(input: String): String {
        val lowerInput = input.lowercase()
        for ((key, response) in predefinedResponses) {
            if (lowerInput.contains(key)) return response
        }
        return "Entiendo. A veces solo expresar lo que sentimos ayuda. Recuerda que puedes ver a nuestros especialistas en la sección de 'Citas' si necesitas apoyo profesional. ✨"
    }
}
