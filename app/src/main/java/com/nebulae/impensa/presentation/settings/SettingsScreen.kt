package com.nebulae.impensa.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nebulae.impensa.presentation.components.AdditionSettingsItem
import com.nebulae.impensa.presentation.components.ButtonSettingsItem
import com.nebulae.impensa.presentation.components.ToggleSettingsItem
import com.nebulae.impensa.presentation.home.HomeViewModel
import com.nebulae.impensa.presentation.navigation.Routes.CategorySettings

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val isDark by viewModel.isDarkTheme.collectAsState()
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .padding(10.dp, 20.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Settings",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Light,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(25.dp))
            ToggleSettingsItem(
                title = "Dark Mode",
                isChecked = isDark,
                onChecked = {
                    viewModel.setTheme(it)
                }
            )
            ButtonSettingsItem(
                title = "Edit Categories",
                onClick = {
                    navController.navigate(CategorySettings)
                }
            )
        }
    }
}