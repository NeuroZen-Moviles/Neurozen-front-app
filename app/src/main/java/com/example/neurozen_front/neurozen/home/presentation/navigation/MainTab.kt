package com.example.neurozen_front.neurozen.home.presentation.navigation

import com.example.neurozen_front.R

enum class MainTab(
    val label: String,
    val iconRes: Int,
    val selectedIconRes: Int
) {
    Home(label = "Inicio", iconRes = R.drawable.home, selectedIconRes = R.drawable.home_filled),
    Psychologists(label = "Citas", iconRes = R.drawable.person, selectedIconRes = R.drawable.person_filled),
    ZenBot(label = "ZenBot", iconRes = R.drawable.share, selectedIconRes = R.drawable.share),
    Sessions(label = "Sesiones", iconRes = R.drawable.share, selectedIconRes = R.drawable.share),
    Profile(label = "Perfil", iconRes = R.drawable.person, selectedIconRes = R.drawable.person_filled)
}
