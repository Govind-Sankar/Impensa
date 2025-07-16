package com.nebulae.impensa.presentation.stat

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import com.nebulae.impensa.R
import com.nebulae.impensa.presentation.home.HomeViewModel
import com.nebulae.impensa.presentation.home.components.EmptyScreen
import java.time.LocalDate
import kotlin.math.exp


@Composable
fun StatScreen (
    navController: NavController,
    viewModel: HomeViewModel
) {
    val (pieChartSlices, totalMap) = viewModel.getPieRatio()
    val (colorMap, amountMap) = totalMap
    val totalAmount = viewModel.getTotalMonthlyExpense()

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
            if(amountMap.isEmpty()) {
                Column (
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { EmptyScreen() }
            }
            else {
                Spacer(modifier = Modifier.height(25.dp))
                ExpenseCard(
                    period = LocalDate.now().month.toString() + " " + LocalDate.now().year.toString(),
                    totalAmount = totalAmount,
                )
                Column(
                    modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp)
                ) {
                    Text(
                        text = "Expenses by Category",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                    )
                    Text(
                        text = LocalDate.now().month.toString() + " " + LocalDate.now().year.toString(),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    val pieChartData = PieChartData(slices = pieChartSlices)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        PieChart(
                            pieChartData = pieChartData,
                            modifier = Modifier.size(200.dp),
                            animation = simpleChartAnimation(),
                            sliceDrawer = SimpleSliceDrawer(sliceThickness = 40f)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        amountMap.entries.sortedByDescending { it.value }
                            .forEach { (category, amount) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(colorMap[category]!!)
                                            .size(10.dp)
                                    ) {}
                                    Text(
                                        text = category,
                                        color = colorMap[category]!!,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "₹ " + "%.2f".format(amount),
                                        color = colorMap[category]!!,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseCard(
    period: String,
    totalAmount: Double,
    modifier: Modifier = Modifier
) {
    val formattedAmount = "%.2f".format(totalAmount)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
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
                    text = "Total Expenses",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = period,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = formattedAmount,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
            Image(
                painter = painterResource(id = R.drawable.expenses_icon),
                contentDescription = "Expenses Icon",
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.BottomEnd),
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            )
        }
    }
}