package com.example.neurozen_front.neurozen.home.presentation.navigation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    val showSubscriptionState = remember { mutableStateOf(false) }
    val showOnlyFavorites = remember { mutableStateOf(false) }

    // El tema ahora responde dinámicamente a la preferencia del usuario
    com.example.neurozen_front.ui.theme.NeurozenTheme(
        darkTheme = state.user.themePreference == "Noche zen"
    ) {
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
                                showSubscriptionState.value = false
                                // Si cambiamos de pestaña, reseteamos el filtro de favoritos excepto si volvemos a Sesiones
                                if (tab != MainTab.Sessions) showOnlyFavorites.value = false
                            },
                            icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                val currentSession = findSession(state.sessions, selectedSessionIdState.value)
                
                if (currentSession != null) {
                    ProductDetail(
                        session = currentSession,
                        onBack = { selectedSessionIdState.value = null },
                        onComplete = {
                            homeViewModel.completeSession(currentSession.id)
                            selectedSessionIdState.value = null
                        },
                        onFavoriteToggle = { homeViewModel.toggleFavorite(it) }
                    )
                } else {
                    when (selectedTabState.value) {
                        MainTab.Home -> Home(
                            homeViewModel = homeViewModel,
                            onSessionClick = { selectedSessionIdState.value = it.id }
                        )
                        MainTab.Psychologists -> PsychologistsScreen()
                        MainTab.ZenBot -> ZenBotScreen()
                        MainTab.Sessions -> SessionsScreen(
                            sessions = if (showOnlyFavorites.value) state.sessions.filter { it.isFavorite } else state.sessions,
                            title = if (showOnlyFavorites.value) "Mis Favoritos" else "Sesiones para ti",
                            onSessionClick = { selectedSessionIdState.value = it.id },
                            onFavoriteToggle = { homeViewModel.toggleFavorite(it) }
                        )
                        MainTab.Profile -> ProfileScreen(
                            user = state.user,
                            onLogout = onLogout,
                            onNavigateToFavorites = {
                                showOnlyFavorites.value = true
                                selectedTabState.value = MainTab.Sessions
                            },
                            onManageSubscription = { showSubscriptionState.value = true },
                            onToggleTheme = { homeViewModel.toggleTheme() }
                        )
                    }
                }

                if (showSubscriptionState.value) {
                    SubscriptionScreen(
                        currentPlan = state.user.subscriptionPlan,
                        onBack = { showSubscriptionState.value = false },
                        onPlanSelected = { 
                            homeViewModel.updateSubscription(it)
                            showSubscriptionState.value = false
                        }
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
private fun SessionsScreen(
    sessions: List<MeditationSession>,
    title: String,
    onSessionClick: (MeditationSession) -> Unit,
    onFavoriteToggle: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                if (title == "Mis Favoritos") "Tus prácticas preferidas en un solo lugar" 
                else "Encuentra la práctica ideal para este momento", 
                style = MaterialTheme.typography.bodyMedium, 
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (sessions.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 80.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🌟", style = MaterialTheme.typography.displayMedium)
                        Text("Aún no tienes favoritos", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
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
private fun ProfileScreen(
    user: NeurozenUser,
    onLogout: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onManageSubscription: () -> Unit,
    onToggleTheme: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Image(
                painter = painterResource(id = R.drawable.usuario_demo),
                contentDescription = "Foto de perfil",
                modifier = Modifier.size(80.dp).clip(CircleShape).border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
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
                ProfileMenuItem("🎨", "Tema: ${user.themePreference}") { onToggleTheme() }
                ProfileMenuItem("🌟", "Mis Favoritos") { onNavigateToFavorites() }
                ProfileMenuItem("💳", "Gestionar Plan (${user.subscriptionPlan})") { onManageSubscription() }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onLogout, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
private fun SubscriptionScreen(currentPlan: String, onBack: () -> Unit, onPlanSelected: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp).background(MaterialTheme.colorScheme.background)) {
        TextButton(onClick = onBack) { Text("← Volver al perfil") }
        Text("Elige tu plan", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        
        val planes = listOf(
            Triple("Básico", "Gratis", listOf("Test de estrés", "Panel básico", "Artículos")),
            Triple("Avanzado", "$3.50", listOf("Todo en Básico", "Análisis biométrico", "Respiración guiada")),
            Triple("Zen+", "$7.80", listOf("Todo en Avanzado", "Psicólogos certificados", "Sesiones Pro"))
        )

        planes.forEach { (titulo, precio, features) ->
            PlanCard(title = titulo, price = precio, features = features, isCurrent = currentPlan.contains(titulo), onSelect = { onPlanSelected(titulo) })
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PlanCard(title: String, price: String, features: List<String>, isCurrent: Boolean, onSelect: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(price, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(12.dp))
            features.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onSelect, enabled = !isCurrent, modifier = Modifier.fillMaxWidth()) {
                Text(if (isCurrent) "Tu plan actual" else "Seleccionar")
            }
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
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, modifier = Modifier.padding(end = 16.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Text("›", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
