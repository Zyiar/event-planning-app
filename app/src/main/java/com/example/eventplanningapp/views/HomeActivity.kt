package com.example.eventplanningapp.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import androidx.compose.ui.platform.LocalContext
import com.example.eventplanningapp.fragments.BudgetManagementFragment
import com.example.eventplanningapp.fragments.EventFragment
import com.example.eventplanningapp.ui.theme.EventPlanningAppTheme
import com.example.eventplanningapp.fragments.GuestsFragment
import com.example.eventplanningapp.fragments.TaskManagementFragment
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController() // Initialize NavController
            EventPlanningAppTheme {
                HomeScreen(navController)
            }
        }
    }
}

private fun handleLogout(navController: NavController) {
    FirebaseAuth.getInstance().signOut()

    navController.navigate("login") {
        popUpTo("home") { inclusive = true }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }

    val appBarColor = MaterialTheme.colorScheme.primary
    val bottomBarColor = MaterialTheme.colorScheme.secondary
    val backgroundColor = MaterialTheme.colorScheme.background
    val iconColor = MaterialTheme.colorScheme.onPrimary
    val unselectedItemColor = MaterialTheme.colorScheme.onSecondary
    val selectedItemColor = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Event Planning App", color = MaterialTheme.colorScheme.onPrimary)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = appBarColor),
                actions = {
                    var expanded by remember { mutableStateOf(false) }

                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.Home, contentDescription = "Menu", tint = iconColor)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                handleLogout(navController)
                            },
                            text = { Text("Logout") }
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = bottomBarColor
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint = if (selectedTab == 0) selectedItemColor else unselectedItemColor) },
                    label = { Text("Home", color = if (selectedTab == 0) selectedItemColor else unselectedItemColor) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Guests", tint = if (selectedTab == 1) selectedItemColor else unselectedItemColor) },
                    label = { Text("Guests", color = if (selectedTab == 1) selectedItemColor else unselectedItemColor) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.List, contentDescription = "Tasks", tint = if (selectedTab == 2) selectedItemColor else unselectedItemColor) },
                    label = { Text("Tasks", color = if (selectedTab == 2) selectedItemColor else unselectedItemColor) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.CheckCircle, contentDescription = "Budget", tint = if (selectedTab == 3) selectedItemColor else unselectedItemColor) },
                    label = { Text("Budget", color = if (selectedTab == 3) selectedItemColor else unselectedItemColor) },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        },
        content = { paddingValues ->
            when (selectedTab) {
                0 -> EventFragment(navController = navController, modifier = Modifier.padding(paddingValues))
                1 -> GuestsFragment(Modifier.padding(paddingValues))
                2 -> TaskManagementFragment(navController = navController, modifier = Modifier.padding(paddingValues))
                3 -> BudgetManagementFragment(navController = navController)
            }
        },
        containerColor = backgroundColor
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val navController = TestNavHostController(LocalContext.current)
    EventPlanningAppTheme {
        HomeScreen(navController)
    }
}
