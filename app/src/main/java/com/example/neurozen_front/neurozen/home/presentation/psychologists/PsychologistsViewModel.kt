package com.example.neurozen_front.neurozen.home.presentation.psychologists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurozen_front.neurozen.data.local.AppointmentDao
import com.example.neurozen_front.neurozen.data.local.AppointmentEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PsychologistsViewModel @Inject constructor(
    private val appointmentDao: AppointmentDao
) : ViewModel() {

    val appointments = appointmentDao.getAllAppointments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun scheduleAppointment(psychologist: Psychologist, dateMillis: Long) {
        viewModelScope.launch {
            appointmentDao.insertAppointment(
                AppointmentEntity(
                    psychologistId = psychologist.id,
                    psychologistName = psychologist.name,
                    dateMillis = dateMillis
                )
            )
        }
    }
}
