package com.example.neurozen_front.neurozen.home.presentation.psychologists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurozen_front.neurozen.data.local.AppointmentDao
import com.example.neurozen_front.neurozen.data.local.AppointmentEntity
import com.example.neurozen_front.neurozen.data.network.AppointmentRequest
import com.example.neurozen_front.neurozen.data.network.NeurozenRepository
import com.example.neurozen_front.neurozen.data.network.ProfessionalResource
import com.example.neurozen_front.neurozen.data.session.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class PsychologistsUiState(
    val isLoading: Boolean = false,
    val professionals: List<ProfessionalResource> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class PsychologistsViewModel @Inject constructor(
    private val appointmentDao: AppointmentDao,
    private val repository: NeurozenRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PsychologistsUiState())
    val uiState: StateFlow<PsychologistsUiState> = _uiState.asStateFlow()

    val appointments = appointmentDao.getAllAppointments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadProfessionals()
        loadAppointmentsFromBackend()
    }

    fun loadProfessionals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val token = UserSession.bearerTokenOrEmpty()
            if (token.length < 10) { // Validar que el token exista razonablemente
                _uiState.update { it.copy(isLoading = false, errorMessage = "Sesión no válida. Por favor, re-inicia sesión.") }
                return@launch
            }

            repository.getProfessionals(token)
                .onSuccess { list ->
                    _uiState.update { it.copy(isLoading = false, professionals = list) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error de red o servidor: ${error.message}") }
                }
        }
    }

    fun loadAppointmentsFromBackend() {
        viewModelScope.launch {
            val session = UserSession.current ?: return@launch
            val token = UserSession.bearerTokenOrEmpty()
            val userId = session.userId ?: return@launch

            repository.getPatientAppointments(userId, token)
                .onSuccess { list ->
                    list.forEach { remote ->
                        // Intentamos obtener el nombre del profesional si viene en la respuesta
                        val name = remote.professionalName ?: "Especialista Neurozen"
                        appointmentDao.insertAppointment(
                            AppointmentEntity(
                                psychologistId = remote.professionalId.toString(),
                                psychologistName = name,
                                psychologistSpecialty = "Consulta Programada",
                                dateMillis = parseIsoDate(remote.appointmentDateTime),
                                status = remote.status
                            )
                        )
                    }
                }
        }
    }

    private fun parseIsoDate(dateStr: String): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            format.parse(dateStr)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    fun scheduleAppointment(professional: ProfessionalResource, dateMillis: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val session = UserSession.current ?: return@launch
            val token = UserSession.bearerTokenOrEmpty()
            val userId = session.userId ?: return@launch
            
            // Usamos formato ISO 8601 con 'Z' para compatibilidad con .NET DateTime
            val isoDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }.format(Date(dateMillis))

            val request = AppointmentRequest(
                patientId = userId,
                professionalId = professional.id,
                appointmentDateTime = isoDate,
                appointmentType = 1,
                notasAdicionales = ""
            )
            
            repository.createAppointment(request, token)
                .onSuccess { response ->
                    appointmentDao.insertAppointment(
                        AppointmentEntity(
                            psychologistId = professional.id.toString(),
                            psychologistName = "${professional.firstName} ${professional.lastName}",
                            psychologistSpecialty = professional.specialization,
                            dateMillis = dateMillis,
                            status = response.status
                        )
                    )
                    _uiState.update { it.copy(
                        isLoading = false, 
                        successMessage = "¡Cita agendada con éxito!"
                    ) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(
                        isLoading = false, 
                        errorMessage = "Error al agendar: ${error.message}"
                    ) }
                }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
