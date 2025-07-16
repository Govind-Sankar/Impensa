package com.nebulae.impensa.presentation.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nebulae.impensa.core.model.Expense
import com.nebulae.impensa.core.util.CATEGORIES
import com.nebulae.impensa.presentation.home.components.EmptyScreen
import com.nebulae.impensa.presentation.home.components.ExpenseItem
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    val newAmount by viewModel.amount.collectAsState()
    var addExpanded by remember { mutableStateOf(false) }
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories = CATEGORIES
    val recentExpenses by viewModel.recentExpenses.collectAsState()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val sortedExpenses = recentExpenses
        .sortedWith(
            compareByDescending<Expense> { LocalDate.parse(it.date) }
                .thenByDescending { it.id }
        )

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .padding(30.dp, 20.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "Add Expense",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Light,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column {
            OutlinedTextField(
                value = newAmount,
                onValueChange = {
                    val regex = Regex("^\\d*(\\.\\d{0,2})?$")
                    if (regex.matches(it)) {
                        viewModel.updateAmount(it)
                    } else {
                        Toast.makeText(context, "Please enter a valid amount!", Toast.LENGTH_SHORT).show()
                    }
                },
                label = { Text(text = "Amount", color = MaterialTheme.colorScheme.onSurface) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = Color.DarkGray,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(15.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .clickable { addExpanded = true }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(text = selectedCategory, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                DropdownMenu(
                    expanded = addExpanded,
                    onDismissRequest = { addExpanded = false },
                    modifier = Modifier
                        .height(200.dp)
                        .clip(shape = RoundedCornerShape(20.dp))
                        .fillMaxWidth(0.85f),
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    content = {
                        categories.forEach {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = it,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    viewModel.updateSelectedCategory(it)
                                    addExpanded = false
                                }
                            )
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(shape = RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        if(selectedCategory != "Select Category") {
                            viewModel.addExpense(
                                amount = newAmount.toDouble(),
                                category = selectedCategory,
                                date = LocalDate.now().toString()
                            )
                            navController.navigate("Home") {
                                popUpTo(0)
                            }
                        } else {
                            Toast.makeText(context, "Please select a category!", Toast.LENGTH_SHORT).show()
                        }
                    }
            ) {
                Text(
                    text = "Add Expense",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Recent Expenses",
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
            if(recentExpenses.isEmpty()) {
                EmptyScreen()
            }
            else {
                LazyColumn {
                    items(items = sortedExpenses, key = { it.id }){
                        ExpenseItem(
                            expense = it,
                            viewModel,
                            onClick = {
                                viewModel.setEditingExpense(it)
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }
        if(showDialog){
            var expanded by remember { mutableStateOf(false) }
            val editingExpense by viewModel.editingExpense.collectAsState()
            var editAmount by rememberSaveable { mutableStateOf("") }
            var editCategory by rememberSaveable { mutableStateOf("Select Category") }
            var showDateDialog by remember { mutableStateOf(false) }
            var selectedDateText by remember { mutableStateOf(editingExpense!!.date) }

            val initialMillis = remember(editingExpense) {
                val localDate = LocalDate.parse(editingExpense!!.date)
                localDate.atTime(12, 0)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            }
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

            LaunchedEffect(editingExpense) {
                if (editingExpense != null) {
                    editAmount = editingExpense!!.amount.toString()
                    editCategory = editingExpense!!.category
                }
            }
            BasicAlertDialog(
                onDismissRequest = {
                    showDialog = !showDialog
                    viewModel.setEditingExpense(null)
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.6f)
                    .clip(shape = RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Edit Expense",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(
                        modifier = Modifier,
                        thickness = DividerDefaults.Thickness,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedTextField(
                        value = editAmount,
                        onValueChange = {
                            val regex = Regex("^\\d*(\\.\\d{0,2})?$")
                            if (regex.matches(it)) {
                                editAmount = it
                            } else {
                                Toast.makeText(context, "Please enter a valid amount!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        label = { Text(text = "Amount", color = MaterialTheme.colorScheme.onSurface) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = Color.DarkGray,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                            cursorColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.onPrimary)
                            .clickable { expanded = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text(text = editCategory, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .height(200.dp)
                                .clip(shape = RoundedCornerShape(20.dp))
                                .fillMaxWidth(0.85f),
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            content = {
                                categories.forEach {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = it,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        onClick = {
                                            editCategory = it
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        )
                    }
                    Spacer(Modifier.height(15.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(shape = RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.onPrimary)
                            .clickable {
                                showDateDialog = true
                            }
                            .padding(horizontal = 15.dp, vertical = 15.dp)
                    ){
                        Text(
                            text = selectedDateText.substring(8,10) + selectedDateText.substring(4,8) + selectedDateText.substring(0,4),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    if (showDateDialog) {
                        DatePickerDialog(
                            onDismissRequest = { showDateDialog = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        datePickerState.selectedDateMillis?.let { millis ->
                                            val selectedDate = Instant.ofEpochMilli(millis)
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDate()
                                            selectedDateText = selectedDate.toString()
                                        }
                                        showDateDialog = false
                                    }
                                ) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDateDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(shape = RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable {
                                if (editingExpense != null && editCategory != "Select Category") {
                                    viewModel.updateExpense(
                                        expense = editingExpense!!,
                                        newAmount = editAmount.toDoubleOrNull() ?: 0.0,
                                        newCategory = editCategory,
                                        newDate = selectedDateText
                                    )
                                }
                                showDialog = false
                            }
                    ) {
                        Text(
                            text = if (editingExpense != null) "Update Expense" else "Add Expense",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(
                        modifier = Modifier,
                        thickness = DividerDefaults.Thickness,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton (
                        onClick = {
                            showDialog = false
                            viewModel.setEditingExpense(null)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}