package com.example.neurozen_front.neurozen.home.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import com.example.neurozen_front.R
import com.example.neurozen_front.neurozen.home.presentation.components.CategoryItem

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
                TextButton(onClick = { /* Navegar a todas */ }) {
                    Text("Ver todas")
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
}

@Composable
private fun HeroHeader(state: HomeState, onRefresh: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        // Fondo con gradiente natural
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
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
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = state.greeting,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
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
