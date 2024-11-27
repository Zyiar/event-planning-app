package com.example.eventplanningapp.views

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventplanningapp.ui.theme.EventPlanningAppTheme
import com.example.eventplanningapp.fragments.GuestsFragment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventPlanningAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    PermissionHandler()
                }
            }
        }
    }
}

@Composable
fun PermissionHandler() {
    var hasPermission by remember { mutableStateOf(false) }

    RequestContactsPermission { isGranted ->
        hasPermission = isGranted
    }

    if (hasPermission) {
        NavigationHost()
    } else {
        PermissionDeniedScreen()
    }
}

@Composable
fun NavigationHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("guests") { GuestsFragment(Modifier) }
    }
}

// Function to handle permission requests
@Composable
fun RequestContactsPermission(onResult: (Boolean) -> Unit) {
    val context = LocalContext.current
    var permissionRequested by remember { mutableStateOf(false) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onResult(isGranted)
    }

    // Check and request permission
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            onResult(true)
        } else if (!permissionRequested) {
            permissionRequested = true
            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }
}

// Screen shown if permission is denied
@Composable
fun PermissionDeniedScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = "Contact access is required to use this feature. Please grant permission in settings."
        )
    }
}
