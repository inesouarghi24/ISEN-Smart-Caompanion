package fr.isen.ines.isensmartcompanion.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController, items: List<NavBarItem>) {
    val currentRoute by navController.currentBackStackEntryAsState()
    val isDarkMode = isSystemInDarkTheme()

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = if (isDarkMode) Color.Black else Color(0xFFFFC0CB) // Rose en mode normal
    ) {
        items.forEach { screen ->
            val isSelected = currentRoute?.destination?.route == screen.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = screen.title,
                        tint = if (isDarkMode) Color.White else Color.Unspecified
                    )
                },
                label = {
                    Text(
                        screen.title,
                        color = if (isDarkMode) Color.White else Color.Black // Texte noir en mode normal
                    )
                },
                selected = isSelected,
                onClick = {
                    if (currentRoute?.destination?.route != screen.route) {
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
