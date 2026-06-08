package com.example.neurozen_front.neurozen.home.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainTab(
    val label: String,
    val icon: ImageVector
) {
    Home(label = "Inicio", icon = Icons.Default.Home),
    Psychologists(label = "Citas", icon = Icons.Default.DateRange),
    ZenBot(label = "ZenBot", icon = Icons.Default.Eco), // Hojita
    Sessions(label = "Sesiones", icon = Icons.Default.MenuBook), // Libro
    Profile(label = "Perfil", icon = Icons.Default.AccountCircle)
}
