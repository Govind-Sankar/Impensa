package com.nebulae.impensa.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nebulae.impensa.presentation.components.AdditionSettingsItem
import com.nebulae.impensa.presentation.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySettingsScreen (
    navController: NavController,
    viewModel: HomeViewModel
) {
    val categoryMap = viewModel.customCategories.collectAsState().value
    var showDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<String?>(null) }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .padding(20.dp, 20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.22f)
        ) {
            Text(
                text = "Edit Categories",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Light,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(25.dp))
            AdditionSettingsItem(
                text = "Add a Category",
                onClick = {
                    viewModel.addCategory(it)
                }
            )
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
                .statusBarsPadding()
                .padding(0.dp, 15.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Current Categories",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                LazyColumn {
                    items(categoryMap.size) { index ->
                        val key = categoryMap.keys.elementAt(index)
                        val color = categoryMap.values.elementAt(index)
                        CategoryItem(
                            name = key,
                            color = color,
                            onDelete = {
                                categoryToDelete = key
                                showDialog = true
                            }
                        )
                    }
                }
            }}
        if(showDialog) {
            BasicAlertDialog(
                onDismissRequest = { showDialog = false },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.45f)
                    .clip(shape = RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
            ) {
                Column (
                    modifier = Modifier
                        .defaultMinSize(minHeight = 500.dp)
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Warning!",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Deleting a category will delete all expenses of that category. If you wish to keep those expenses, please update them to a new category. Proceed with Caution!",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton (
                            onClick = {
                                showDialog = false
                            }
                        ) {
                            Text("Close")
                        }
                        TextButton (
                            onClick = {
                                showDialog = false
                                viewModel.deleteCategory(categoryToDelete?:"Unknown")
                            }
                        ) {
                            Text(
                                text = "Confirm",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(name: String, color: Color, onDelete: () -> Unit) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 15.dp)
            .background(Color.Transparent),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = name,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            modifier = Modifier
                .padding(bottom = 2.dp)
                .clickable {
                    onDelete()
                }
                .size(20.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        thickness = 1.dp
    )
}