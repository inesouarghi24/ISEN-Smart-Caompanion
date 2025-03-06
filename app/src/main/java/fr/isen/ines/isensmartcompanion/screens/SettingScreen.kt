package fr.isen.ines.isensmartcompanion.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(themeViewModel: ThemeViewModel) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState(initial = false)
    var selectedLanguage by remember { mutableStateOf("Français") }

    val topBarColor = Color(0xFFFFC0CB)
    val textColor = Color.White
    val languages = listOf("Français", "Anglais", "Espagnol")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🎀 Paramètres 🎀",
                        color = textColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = topBarColor)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Général", style = MaterialTheme.typography.headlineSmall)

            SettingSwitchItem(
                title = "Mode sombre",
                icon = Icons.Filled.Star,
                checked = isDarkMode,
                onCheckedChange = { themeViewModel.setDarkMode(it) }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Langue", style = MaterialTheme.typography.headlineSmall)

            SettingDropdownItem(
                title = "Changer la langue",
                icon = Icons.Filled.List,
                selectedValue = selectedLanguage,
                items = languages,
                onItemSelected = { selectedLanguage = it }
            )
        }
    }
}

@Composable
fun SettingSwitchItem(title: String, icon: ImageVector, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = title, tint = Color(0xFFFFC0CB))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, modifier = Modifier.weight(1f))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFC0CB))
            )
        }
    }
}

@Composable
fun SettingDropdownItem(
    title: String,
    icon: ImageVector,
    selectedValue: String,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = true },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = title, tint = Color(0xFFFFC0CB))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, modifier = Modifier.weight(1f))
            Text(selectedValue, color = Color.Gray)

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language) },
                        onClick = {
                            onItemSelected(language)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
