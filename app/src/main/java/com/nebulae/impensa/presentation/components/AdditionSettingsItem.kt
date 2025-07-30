package com.nebulae.impensa.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nebulae.impensa.ui.theme.ImpensaExpenseTrackerAppTheme

@Composable
fun AdditionSettingsItem(
    text: String,
    onClick: (String) -> Unit = {}
) {
    var category by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 25.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {
                    category = it
                },
                label = { Text(text) },
                colors = OutlinedTextFieldDefaults.colors(),
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            onClick(category)
                            category = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Category",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AdditionSettingsItemPreview() {
    ImpensaExpenseTrackerAppTheme {
        AdditionSettingsItem(
            text = "Add a Category",
            onClick = {}
        )
    }
}