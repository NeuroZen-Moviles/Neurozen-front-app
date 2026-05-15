package com.example.neurozen_front.neurozen.home.presentation.navigation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.neurozen_front.R
import com.example.neurozen_front.neurozen.home.presentation.detail.ProductDetail
import com.example.neurozen_front.neurozen.home.presentation.home.Home
import com.example.neurozen_front.neurozen.home.presentation.home.HomeViewModel
import com.example.neurozen_front.neurozen.home.presentation.home.MeditationSession
import com.example.neurozen_front.neurozen.home.presentation.home.ProductCard
import com.example.neurozen_front.neurozen.home.presentation.home.NeurozenUser
import com.example.neurozen_front.neurozen.home.presentation.psychologists.PsychologistsScreen
import com.example.neurozen_front.neurozen.home.presentation.zenbot.ZenBotScreen

@Composable
fun HomeNavHost(
    onLogout: () -> Unit = {}
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val state by homeViewModel.homeState.collectAsState()
    val selectedTabState = remember { mutableStateOf(MainTab.Home) }
    val selectedSessionIdState = remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                MainTab.entries.forEach { tab ->
                    val selected = selectedTabState.value == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            selectedTabState.value = tab
                            selectedSessionIdState.value = null
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = if (selected) tab.selectedIconRes else tab.iconRes),
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val currentSession = findSession(state.sessions, selectedSessionIdState.value)) {
                null -> when (selectedTabState.value) {
                    MainTab.Home -> Home(
                        homeViewModel = homeViewModel,
                        onSessionClick = { selectedSessionIdState.value = it.id }
                    )
                    MainTab.Psychologists -> PsychologistsScreen()
                    MainTab.ZenBot -> ZenBotScreen()
                    MainTab.Sessions -> SessionsScreen(
                        sessions = state.sessions,
                        onSessionClick = { selectedSessionIdState.value = it.id },
                        onFavoriteToggle = { homeViewModel.toggleFavorite(it) }
                    )
                    MainTab.Profile -> ProfileScreen(
                        user = state.user,
                        onLogout = onLogout
                    )
                }

                else -> {
                    ProductDetail(
                        session = currentSession,
                        onBack = { selectedSessionIdState.value = null },
                        onComplete = {
                            homeViewModel.completeSession(currentSession.id)
                            selectedSessionIdState.value = null
                            selectedTabState.value = MainTab.Sessions
                        },
                        onFavoriteToggle = { homeViewModel.toggleFavorite(it) }
                    )
                }
            }
        }
    }
}

private fun findSession(sessions: List<MeditationSession>, sessionId: String?): MeditationSession? {
    return sessionId?.let { id -> sessions.firstOrNull { it.id == id } }
}

@Composable
private fun BreathingScreen() {
    var isRunning by remember { mutableStateOf(false) }
    var seconds by remember { mutableIntStateOf(45) }
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (seconds > 0) {
                kotlinx.coroutines.delay(1000)
                seconds--
            }
            isRunning = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Respiración guiada", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Inhala al expandirse, exhala al contraerse", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Box(modifier = Modifier.padding(vertical = 60.dp).size(260.dp), contentAlignment = Alignment.Center) {
            // Círculos de fondo animados
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.fillMaxSize(if (isRunning) scale else 0.8f)
            ) {}
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(140.dp),
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (!isRunning) "Listo" else if (scale > 1f) "Inhala" else "Exhala",
                        color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(text = "${seconds}s", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = { if (!isRunning) seconds = 45; isRunning = !isRunning },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(if (isRunning) "Detener" else "Comenzar")
        }
    }
}

@Composable
private fun SessionsScreen(
    sessions: List<MeditationSession>,
    onSessionClick: (MeditationSession) -> Unit,
    onFavoriteToggle: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Sesiones para ti", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Encuentra la práctica ideal para este momento", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        items(sessions, key = { it.id }) { session ->
            ProductCard(
                session = session,
                onClick = { onSessionClick(session) },
                onFavoriteToggle = onFavoriteToggle
            )
        }
    }
}

@Composable
private fun ProfileScreen(user: NeurozenUser, onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            // Imagen de usuario real configurada
            Image(
                painter = painterResource(id = R.drawable.usuario_demo),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(user.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(user.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Racha", "${user.streakDays}", "días", "🔥", Modifier.weight(1f))
            StatCard("Hoy", "${user.minutesToday}", "min", "⏱️", Modifier.weight(1f))
        }

        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ProfileMenuItem("🎨", "Tema: ${user.themePreference}")
                ProfileMenuItem("🔔", "Recordatorios diarios")
                ProfileMenuItem("🌟", "Sesiones favoritas (${user.streakDays})") {
                    // Acción para ver favoritos
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onLogout, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, unit: String, icon: String, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.3f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(icon)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(unit, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 2.dp, bottom = 4.dp))
            }
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun ProfileMenuItem(icon: String, label: String, onClick: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(icon)
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Text("›", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
