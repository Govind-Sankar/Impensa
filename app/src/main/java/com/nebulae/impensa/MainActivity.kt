package com.nebulae.impensa

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nebulae.impensa.core.data.local.ExpenseDatabase
import com.nebulae.impensa.core.data.repository.ExpenseRepository
import com.nebulae.impensa.presentation.stat.StatScreen
import com.nebulae.impensa.presentation.home.HomeScreen
import com.nebulae.impensa.presentation.home.HomeViewModel
import com.nebulae.impensa.presentation.home.HomeViewModelFactory
import com.nebulae.impensa.presentation.navigation.Routes.Home
import com.nebulae.impensa.presentation.navigation.Routes.Stats
import com.nebulae.impensa.ui.theme.ImpensaExpenseTrackerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = ExpenseDatabase.getDatabase(this)
        val repository = ExpenseRepository(database.expenseDao())
        val viewModelFactory = HomeViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            ImpensaExpenseTrackerAppTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    viewModel: HomeViewModel
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    Scaffold (
        modifier = Modifier
            .fillMaxSize(),
        content = {
            NavHost(
                navController = navController,
                startDestination = Home,
            ) {
                composable(route = Home) {
                    HomeScreen(navController, viewModel)
                }
                composable(route = Stats) {
                    StatScreen(navController, viewModel)
                }
            }
        },
        bottomBar = {
            NavigationBar (
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
            ) {
                NavigationBarItem(
                    selected = currentRoute == Home,
                    onClick = {
                        navController.navigate(route = Home) {
                            popUpTo(0)
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    label = { Text(text = "Home") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        selectedIconColor = Color.White,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == Stats,
                    onClick = {
                        navController.navigate(route = Stats) {
                            popUpTo(0)
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.QueryStats,
                            contentDescription = "Stats",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    label = { Text(text = "Stats") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        selectedIconColor = Color.White,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainScreenPreview() {
    ImpensaExpenseTrackerAppTheme {

    }
}