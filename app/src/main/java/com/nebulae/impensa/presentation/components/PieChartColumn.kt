package com.nebulae.impensa.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer

@Composable
fun PieChartColumn(
    pieChartSlices: List<PieChartData.Slice> = emptyList(),
    colorMap: Map<String, Color> = emptyMap(),
    amountMap: Map<String, Double> = emptyMap(),
    period: String = "Unknown"
){
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
            text = period,
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
                .fillMaxWidth(0.95f)
                .align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            amountMap.entries.sortedByDescending { it.value }
                .forEach { (category, amount) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(colorMap[category] ?: Color.Gray)
                                .size(8.dp)
                        ) {}
                        Text(
                            text = category,
                            color = colorMap[category] ?: Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = formatNumber(amount),
                            color = colorMap[category] ?: Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
        }
    }
}