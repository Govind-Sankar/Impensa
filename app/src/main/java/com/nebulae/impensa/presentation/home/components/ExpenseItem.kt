package com.nebulae.impensa.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nebulae.impensa.core.model.Expense
import com.nebulae.impensa.presentation.home.HomeViewModel
import java.time.LocalDate

@Composable
fun ExpenseItem(
    expense: Expense,
    viewModel: HomeViewModel,
    onClick: () -> Unit
){
    val context = LocalContext.current
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(Color.Transparent),
    ){
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = "₹ " + "%.2f".format(expense.amount),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(text = expense.category, color = MaterialTheme.colorScheme.onSurface)
        }
        Text(text = findDate(expense.date), color = MaterialTheme.colorScheme.onSurface)
        Column (
            modifier = Modifier
                .padding(start =15.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier
                    .padding(bottom = 2.dp)
                    .clickable {
                        viewModel.deleteExpense(expense)
                    }
                    .size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Delete",
                modifier = Modifier
                    .padding(top = 3.dp)
                    .clickable {
                        onClick()
                    }
                    .size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        thickness = 1.dp
    )
}

fun findDate(date: String): String {
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    fun getDayWithSuffix(day: Int): String {
        return when {
            day in 11..13 -> "${day}th"
            day % 10 == 1 -> "${day}st"
            day % 10 == 2 -> "${day}nd"
            day % 10 == 3 -> "${day}rd"
            else -> "${day}th"
        }
    }
    val year = date.substring(0, 4).toInt()
    val month = date.substring(5, 7).toInt()
    val day = date.substring(8, 10).toInt()
    val today = LocalDate.now()
    return when {
        year == today.year && month == today.monthValue && day == today.dayOfMonth -> "Today"
        year == today.year && month == today.monthValue && day == today.dayOfMonth - 1 -> "Yesterday"
        else -> "${getDayWithSuffix(day)} ${months[month - 1]}, $year"
    }
}