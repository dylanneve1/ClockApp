package com.clockapp

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.HourglassBottom
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.clockapp.ui.components.NavItem
import com.clockapp.ui.components.ParticleBackground
import com.clockapp.ui.screens.ClockScreen
import com.clockapp.ui.screens.StopwatchScreen
import com.clockapp.ui.screens.TimerScreen
import com.clockapp.ui.theme.ClockAppTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ClockAppTheme {
                SetupSystemBars()
                ClockApp()
            }
        }
    }
}

@Composable
private fun SetupSystemBars() {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = false
        )
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = false,
            navigationBarContrastEnforced = false
        )
    }
}

@Composable
fun ClockApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "clock"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ParticleBackground()
        
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = { BottomNavBar(currentRoute, navController::navigate) }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "clock",
                modifier = Modifier.padding(paddingValues),
                enterTransition = {
                    fadeIn(animationSpec = tween(400, easing = EaseOutCubic)) +
                    scaleIn(initialScale = 0.92f, animationSpec = tween(400, easing = EaseOutCubic))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(200, easing = EaseInCubic)) +
                    scaleOut(targetScale = 1.05f, animationSpec = tween(200, easing = EaseInCubic))
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(400, easing = EaseOutCubic)) +
                    scaleIn(initialScale = 1.05f, animationSpec = tween(400, easing = EaseOutCubic))
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(200, easing = EaseInCubic)) +
                    scaleOut(targetScale = 0.92f, animationSpec = tween(200, easing = EaseInCubic))
                }
            ) {
                composable("clock") { ClockScreen() }
                composable("stopwatch") { StopwatchScreen() }
                composable("timer") { TimerScreen() }
            }
        }
    }
}

@Composable
private fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .navigationBarsPadding(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 8.dp,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavItem(
                icon = if (currentRoute == "clock") Icons.Rounded.AccessTime else Icons.Outlined.AccessTime,
                label = "Clock",
                selected = currentRoute == "clock",
                onClick = { onNavigate("clock") }
            )
            NavItem(
                icon = if (currentRoute == "stopwatch") Icons.Rounded.Timer else Icons.Outlined.Timer,
                label = "Stopwatch",
                selected = currentRoute == "stopwatch",
                onClick = { onNavigate("stopwatch") }
            )
            NavItem(
                icon = if (currentRoute == "timer") Icons.Rounded.HourglassBottom else Icons.Outlined.HourglassEmpty,
                label = "Timer",
                selected = currentRoute == "timer",
                onClick = { onNavigate("timer") }
            )
        }
    }
}
