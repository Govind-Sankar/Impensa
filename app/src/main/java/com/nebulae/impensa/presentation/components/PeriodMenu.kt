package com.nebulae.impensa.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun PeriodMenu(
    expanded: Boolean,
    period: String = "Unknown Period",
    list: Map<Int, String>,
    onClick: (Int) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Row (
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f))
                .clickable { onClick(-1) }
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.width(125.dp)
            ) {
                Text(
                    text = period,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onDismiss() },
                modifier = Modifier
                    .height(150.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .fillMaxWidth(0.6f),
                containerColor = MaterialTheme.colorScheme.onPrimary
            ) {
                list.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = value,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onClick(key)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}
