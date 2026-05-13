package pe.edu.upc.easyvet.home.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun Main() {

    val selectedTab = rememberSaveable {
        mutableStateOf(MainTab.Home)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                MainTab.entries.forEach { tab ->
                    val selected = selectedTab.value == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            selectedTab.value = tab
                        },
                        icon = {
                            Icon(
                                painter = painterResource(if (selected) tab.iconFilled else tab.icon),
                                contentDescription = tab.label,
                                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        label = {
                            Text(text = tab.label)
                        }
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

            when (selectedTab.value) {
                MainTab.Home -> HomeNavHost()
                else -> Text(selectedTab.value.label)
            }
        }

    }
}