package com.example.neurozen_front.neurozen.home.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.neurozen_front.R
import com.example.neurozen_front.neurozen.data.local.AppointmentEntity
import com.example.neurozen_front.neurozen.home.presentation.components.CategoryItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Home(
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    onSessionClick: (MeditationSession) -> Unit = {}
) {
    val state by homeViewModel.homeState.collectAsState()
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Hero Section con bienvenida y estado de carga
        item {
            HeroHeader(state = state, onRefresh = homeViewModel::refresh)
        }

        // 2. Daily Quote (Simulado para dar paz)
        item {
            DailyQuoteCard(modifier = Modifier.padding(horizontal = 20.dp))
        }

        // Nuevo: Recordatorio de Próxima Cita
        if (state.upcomingAppointments.isNotEmpty()) {
            item {
                NextAppointmentSummary(
                    appointment = state.upcomingAppointments.first(),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        // 3. Mood Selector (Más intuitivo)
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "¿Cómo te sientes hoy?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val moods = listOf(
                        Triple("Calma", "🌿", MaterialTheme.colorScheme.primaryContainer),
                        Triple("Enfoque", "🎯", MaterialTheme.colorScheme.secondaryContainer),
                        Triple("Sueño", "🌙", MaterialTheme.colorScheme.tertiaryContainer),
                        Triple("Energía", "⚡", MaterialTheme.colorScheme.errorContainer)
                    )
                    moods.forEach { (name, icon, color) ->
                        MoodItem(
                            name = name,
                            icon = icon,
                            containerColor = color,
                            isSelected = state.selectedMood == name,
                            onClick = { homeViewModel.setMood(name) }
                        )
                    }
                }
            }
        }

        // 4. Health Metrics Carousel
        item {
            Column {
                Text(
                    text = "Tu equilibrio",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.healthMetrics) { metric ->
                        MetricCard(metric = metric, modifier = Modifier.width(160.dp))
                    }
                }
            }
        }

        // 5. Recommended Sessions
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recomendado para ti",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row {
                    if (state.healthHistory.isNotEmpty()) {
                        TextButton(onClick = { /* Ver historial */ }) {
                            Text("Ver Historial")
                        }
                    }
                    TextButton(onClick = { homeViewModel.setShowEmotionalForm(true) }) {
                        Text("Check-in")
                    }
                }
            }
        }

        items(state.sessions, key = { it.id }) { session ->
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                ProductCard(
                    session = session,
                    onClick = { onSessionClick(session) },
                    onFavoriteToggle = { homeViewModel.toggleFavorite(it) }
                )
            }
        }
    }

    if (state.showEmotionalForm) {
        EmotionalCheckInDialog(
            onDismiss = { homeViewModel.setShowEmotionalForm(false) },
            onSubmit = { stress, sleep, heart, notes ->
                homeViewModel.submitEmotionalCheck(stress, sleep, heart, notes)
                homeViewModel.setShowEmotionalForm(false)
            }
        )
    }
}

@Composable
fun EmotionalCheckInDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int, Int, Int, String) -> Unit
) {
    var stressLevel by remember { mutableFloatStateOf(5f) }
    var sleepHours by remember { mutableFloatStateOf(7f) }
    var heartRate by remember { mutableFloatStateOf(75f) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Cómo va tu día?", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text("Nivel de Estrés: ${stressLevel.toInt()}", style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = stressLevel,
                        onValueChange = { stressLevel = it },
                        valueRange = 1f..10f,
                        steps = 8
                    )
                }
                Column {
                    Text("Horas de Sueño: ${sleepHours.toInt()}h", style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = sleepHours,
                        onValueChange = { sleepHours = it },
                        valueRange = 0f..12f,
                        steps = 11
                    )
                }
                Column {
                    Text("Ritmo Cardiaco: ${heartRate.toInt()} bpm", style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = heartRate,
                        onValueChange = { heartRate = it },
                        valueRange = 40f..150f
                    )
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas o pensamientos") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                onSubmit(stressLevel.toInt(), sleepHours.toInt(), heartRate.toInt(), notes)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun NextAppointmentSummary(
    appointment: AppointmentEntity,
    modifier: Modifier = Modifier
) {
    val dateStr = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "PE")).format(Date(appointment.dateMillis))
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_my_calendar),
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Column {
                Text(
                    text = "Próxima sesión",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Con ${appointment.psychologistName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (appointment.psychologistSpecialty.isNotEmpty()) {
                    Text(
                        text = appointment.psychologistSpecialty,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = dateStr.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun HeroHeader(state: HomeState, onRefresh: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        // Fondo con imagen remota y gradiente
        AsyncImage(
            model = state.headerImageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent, Color.Black.copy(alpha = 0.7f))
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "Hola, ${state.user.name}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = state.greeting,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
        
        // Botón de refresh flotante pequeño
        FilledIconButton(
            onClick = onRefresh,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
            )
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.stat_notify_sync),
                contentDescription = "Sync",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun MoodItem(
    name: String,
    icon: String,
    containerColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(if (isSelected) containerColor else containerColor.copy(alpha = 0.2f))
                .then(if (isSelected) Modifier.border(2.dp, containerColor, RoundedCornerShape(20.dp)) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, style = MaterialTheme.typography.headlineSmall)
        }
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DailyQuoteCard(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("✨", style = MaterialTheme.typography.titleMedium)
                }
            }
            Text(
                text = "“La paz interior comienza cuando decides no permitir que otro controle tus emociones.”",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MetricCard(metric: HealthMetric, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = metric.label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = metric.value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            LinearProgressIndicator(
                progress = { metric.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            Text(
                text = metric.detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
