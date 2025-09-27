package com.nebulae.impensa.presentation.settings

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.nebulae.impensa.R
import com.nebulae.impensa.presentation.components.ButtonSettingsItem
import com.nebulae.impensa.presentation.components.ToggleSettingsItem
import com.nebulae.impensa.presentation.components.VersionCard
import com.nebulae.impensa.presentation.home.HomeViewModel
import com.nebulae.impensa.presentation.navigation.Routes.CATEGORYSETTINGS

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
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
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
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
            VersionCard(
                viewModel = viewModel
            )
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
                    navController.navigate(CATEGORYSETTINGS)
                }
            )
        }
        Row (
            horizontalArrangement =  Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
        ) {
            IconButton(
                onClick = {
                    val url = "https://github.com/Govind-Sankar/"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.github_white),
                    contentDescription = "Github",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}