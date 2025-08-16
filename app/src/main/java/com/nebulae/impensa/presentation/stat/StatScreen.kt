package com.nebulae.impensa.presentation.stat

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nebulae.impensa.presentation.components.EmptyScreen
import com.nebulae.impensa.presentation.components.ExpenseCard
import com.nebulae.impensa.presentation.components.PeriodMenu
import com.nebulae.impensa.presentation.components.PieChartColumn
import com.nebulae.impensa.presentation.home.HomeViewModel

@Composable
fun StatScreen (
    navController: NavController,
    viewModel: HomeViewModel
) {
    val periodList = mapOf(
        0 to "Current Month", //+ LocalDate.now().month.toString() + " " + LocalDate.now().year.toString(),
        1 to "This year", //+ LocalDate.now().year.toString(),
        2 to "All time",
    )
    //var selectedValue by rememberSaveable { mutableStateOf(2) }
    val selectedValue = viewModel.stateScreenState.collectAsState()
    val totalAmount = viewModel.getTotalExpense(selectedValue.value)
    val (pieChartSlices, totalMap) = viewModel.getPieRatio(selectedValue.value)
    val (colorMap, amountMap) = totalMap
    var expanded by remember{ mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .padding(10.dp, 20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Stats",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Light,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            PeriodMenu(
                expanded = expanded,
                period = periodList[selectedValue.value]?:"Unknown",
                list = periodList,
                onClick = {
                    if (it == -1) {
                        expanded = !expanded
                    } else {
                        viewModel.setStatsScreenState(it)
                        expanded = false
                    }
                },
                onDismiss = { expanded = false }
            )
            Spacer(modifier = Modifier.height(15.dp))
            if(amountMap.isEmpty()) {
                Column (
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { EmptyScreen() }
            }
            else {
                ExpenseCard(
                    period = periodList[selectedValue.value]?:"Unknown",
                    totalAmount = totalAmount
                )
                PieChartColumn(
                    pieChartSlices = pieChartSlices,
                    colorMap = colorMap,
                    amountMap = amountMap,
                    period = periodList[selectedValue.value]?:"Unknown"
                )
            }
        }
    }
}

