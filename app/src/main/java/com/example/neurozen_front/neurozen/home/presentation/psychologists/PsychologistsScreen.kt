package com.example.neurozen_front.neurozen.home.presentation.psychologists

import coil3.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.neurozen_front.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Psychologist(
    val id: String,
    val name: String,
    val specialty: String,
    val rating: Double,
    val imageRes: Int,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PsychologistsScreen(
    viewModel: PsychologistsViewModel = hiltViewModel()
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedPsychologist by remember { mutableStateOf<Psychologist?>(null) }
    val appointments by viewModel.appointments.collectAsState()

    val psychologists = listOf(
        Psychologist("1", "Dra. Ana García", "Especialista en Ansiedad", 4.9, R.drawable.psicologo_ana, "Ayudo a personas a encontrar calma en el caos diario."),
        Psychologist("2", "Dra. Maria López", "Terapia de Pareja", 4.8, R.drawable.psicologo_maria, "Enfoque humanista para fortalecer vínculos emocionales."),
        Psychologist("3", "Dr. Carlos Ruiz", "Psicología Deportiva", 4.7, R.drawable.psicologo_carlos, "Optimiza tu rendimiento mental y alcanza tus metas.")
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedPsychologist?.let { psycho ->
                            viewModel.scheduleAppointment(psycho, millis)
                        }
                    }
                    showDatePicker = false
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Nuestros Especialistas",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Reserva una cita con profesionales certificados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (appointments.isNotEmpty()) {
                item {
                    Text(
                        "Tus próximas citas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(appointments) { appointment ->
                    AppointmentCard(appointment)
                }
                item {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }

            item {
                Text(
                    "Especialistas disponibles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(psychologists) { psychologist ->
                PsychologistCard(
                    psychologist = psychologist,
                    onBookClick = {
                        selectedPsychologist = psychologist
                        showDatePicker = true
                    }
                )
            }
        }
    }
}

@Composable
fun AppointmentCard(appointment: com.example.neurozen_front.neurozen.data.local.AppointmentEntity) {
    val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(appointment.dateMillis))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            Column {
                Text(appointment.psychologistName, fontWeight = FontWeight.Bold)
                Text("Fecha: $dateStr", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun PsychologistCard(psychologist: Psychologist, onBookClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = psychologist.imageRes,
                contentDescription = psychologist.name,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = psychologist.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = psychologist.specialty,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = " ${psychologist.rating}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onBookClick,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agendar", fontSize = 12.sp)
                }
            }
        }
    }
}
