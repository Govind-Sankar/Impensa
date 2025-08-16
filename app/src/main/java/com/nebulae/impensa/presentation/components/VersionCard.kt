package com.nebulae.impensa.presentation.components

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.nebulae.impensa.presentation.home.HomeViewModel

@Composable
fun VersionCard(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.checkForUpdates(context)
    }
    val checkerState by viewModel.checkerState.collectAsState()
    val text =  when(checkerState) {
        0 -> "Checking for Updates"
        1 -> "You're on the latest Version!"
        2 -> "Update Available!"
        else -> "Error in Checking for Updates!"
    }
    val subText =  when(checkerState) {
        0 -> "Please wait..."
        1 -> "No updates available for now."
        2 -> "Click here to update."
        else -> "Raise an issue on Github!"
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .padding(vertical = 5.dp)
            .clickable(
                enabled = checkerState != 0,
                onClick = {
                    when (checkerState) {
                        1 -> {
                            viewModel.checkForUpdates(context)
                        }
                        2 -> {
                            viewModel.downloadAndInstallApk(context)
                        }
                        else -> {
                            val url = "https://github.com/Govind-Sankar/Impensa/issues"
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                            context.startActivity(intent)
                        }
                    }
                }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Normal
                    )
                )
                Text(
                    text = subText,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            if(checkerState == 0){
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterEnd),
                    color = MaterialTheme.colorScheme.onSurface,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}