@file:OptIn(ExperimentalMaterial3Api::class)

package com.clockapp

import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

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
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = false
                
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )
                    systemUiController.setNavigationBarColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons,
                        navigationBarContrastEnforced = false
                    )
                }
                
                ClockApp()
            }
        }
    }
}

@Composable
fun ClockAppTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    
    val colorScheme = when {
        dynamicColor -> dynamicDarkColorScheme(context)
        else -> darkColorScheme(
            primary = Color(0xFFBB86FC),
            onPrimary = Color(0xFF000000),
            primaryContainer = Color(0xFF3700B3),
            onPrimaryContainer = Color(0xFFE8DEF8),
            secondary = Color(0xFF03DAC6),
            onSecondary = Color(0xFF000000),
            secondaryContainer = Color(0xFF005047),
            onSecondaryContainer = Color(0xFF70F5E8),
            tertiary = Color(0xFFCF6679),
            onTertiary = Color(0xFF000000),
            tertiaryContainer = Color(0xFF8C1D35),
            onTertiaryContainer = Color(0xFFFFD9DF),
            background = Color(0xFF0D0D12),
            onBackground = Color(0xFFE6E1E5),
            surface = Color(0xFF0D0D12),
            onSurface = Color(0xFFE6E1E5),
            surfaceVariant = Color(0xFF1D1D28),
            onSurfaceVariant = Color(0xFFCAC4D0),
            outline = Color(0xFF938F99)
        )
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
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
        // Animated background particles
        ParticleBackground()
        
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
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
                            onClick = { navController.navigate("clock") { popUpTo(0) } }
                        )
                        NavItem(
                            icon = if (currentRoute == "stopwatch") Icons.Rounded.Timer else Icons.Outlined.Timer,
                            label = "Stopwatch",
                            selected = currentRoute == "stopwatch",
                            onClick = { navController.navigate("stopwatch") { popUpTo(0) } }
                        )
                        NavItem(
                            icon = if (currentRoute == "timer") Icons.Rounded.HourglassBottom else Icons.Outlined.HourglassEmpty,
                            label = "Timer",
                            selected = currentRoute == "timer",
                            onClick = { navController.navigate("timer") { popUpTo(0) } }
                        )
                    }
                }
            }
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
fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.9f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "scale"
    )
    
    val animatedAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0.6f,
        animationSpec = tween(200),
        label = "alpha"
    )
    
    Surface(
        onClick = onClick,
        modifier = Modifier
            .scale(animatedScale)
            .alpha(animatedAlpha),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ParticleBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    
    val particles = remember {
        List(15) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 4f + 2f,
                speed = Random.nextFloat() * 0.3f + 0.1f,
                alpha = Random.nextFloat() * 0.3f + 0.1f
            )
        }
    }
    
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(100000, easing = LinearEasing)
        ),
        label = "time"
    )
    
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val x = ((particle.x + time * particle.speed * 0.001f) % 1.2f - 0.1f) * size.width
            val y = ((particle.y + sin((time * particle.speed * 0.01f + particle.x * 10f).toDouble()).toFloat() * 0.1f) % 1.2f - 0.1f) * size.height
            
            val color = if (particle.x > 0.5f) primaryColor else secondaryColor
            
            drawCircle(
                color = color.copy(alpha = particle.alpha),
                radius = particle.size.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

data class Particle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float
)

@Composable
fun ClockScreen() {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(50)  // Update more frequently for smooth second hand
        }
    }
    
    val calendar = remember(currentTime) {
        Calendar.getInstance().apply { timeInMillis = currentTime }
    }
    
    val hours = calendar.get(Calendar.HOUR)
    val minutes = calendar.get(Calendar.MINUTE)
    val seconds = calendar.get(Calendar.SECOND)
    val millis = calendar.get(Calendar.MILLISECOND)
    
    val secondsWithMillis = seconds + millis / 1000f
    
    val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    val fullDateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Analog clock
        Box(
            modifier = Modifier.size(300.dp),
            contentAlignment = Alignment.Center
        ) {
            AnalogClock(
                hours = hours,
                minutes = minutes,
                seconds = secondsWithMillis
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Digital time
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            AnimatedDigit(value = hours / 10)
            AnimatedDigit(value = hours % 10)
            Text(
                ":",
                fontSize = 64.sp,
                fontWeight = FontWeight.Thin,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            AnimatedDigit(value = minutes / 10)
            AnimatedDigit(value = minutes % 10)
            Text(
                ":",
                fontSize = 40.sp,
                fontWeight = FontWeight.Thin,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .offset(y = (-8).dp)
            )
            Text(
                String.format("%02d", seconds),
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.offset(y = (-4).dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Date cards with entrance animation
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassCard(
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Rounded.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        dateFormat.format(Date(currentTime)),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            GlassCard(
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Rounded.Event,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        fullDateFormat.format(Date(currentTime)),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun AnalogClock(
    hours: Int,
    minutes: Int,
    seconds: Float
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outline
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 2 - 16.dp.toPx()
        
        // Outer glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.1f),
                    Color.Transparent
                ),
                radius = radius * 1.3f
            ),
            radius = radius * 1.3f,
            center = Offset(centerX, centerY)
        )
        
        // Clock face background
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    surfaceColor.copy(alpha = 0.8f),
                    surfaceColor.copy(alpha = 0.4f)
                ),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )
        
        // Clock face border
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.5f),
                    secondaryColor.copy(alpha = 0.3f)
                )
            ),
            radius = radius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 3.dp.toPx())
        )
        
        // Hour markers
        for (i in 0 until 12) {
            val angle = (i * 30 - 90) * PI / 180
            val isMainHour = i % 3 == 0
            val markerLength = if (isMainHour) 20.dp.toPx() else 10.dp.toPx()
            val markerWidth = if (isMainHour) 4.dp.toPx() else 2.dp.toPx()
            
            val startRadius = radius - markerLength - 8.dp.toPx()
            val endRadius = radius - 8.dp.toPx()
            
            drawLine(
                color = if (isMainHour) primaryColor else outlineColor.copy(alpha = 0.5f),
                start = Offset(
                    centerX + (cos(angle) * startRadius).toFloat(),
                    centerY + (sin(angle) * startRadius).toFloat()
                ),
                end = Offset(
                    centerX + (cos(angle) * endRadius).toFloat(),
                    centerY + (sin(angle) * endRadius).toFloat()
                ),
                strokeWidth = markerWidth,
                cap = StrokeCap.Round
            )
        }
        
        // Hour hand
        val hourAngle = ((hours % 12 + minutes / 60f) * 30 - 90) * PI / 180
        val hourLength = radius * 0.5f
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(primaryColor, primaryColor.copy(alpha = 0.7f))
            ),
            start = Offset(centerX, centerY),
            end = Offset(
                centerX + (cos(hourAngle) * hourLength).toFloat(),
                centerY + (sin(hourAngle) * hourLength).toFloat()
            ),
            strokeWidth = 8.dp.toPx(),
            cap = StrokeCap.Round
        )
        
        // Minute hand
        val minuteAngle = ((minutes + seconds / 60f) * 6 - 90) * PI / 180
        val minuteLength = radius * 0.7f
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(secondaryColor, secondaryColor.copy(alpha = 0.7f))
            ),
            start = Offset(centerX, centerY),
            end = Offset(
                centerX + (cos(minuteAngle) * minuteLength).toFloat(),
                centerY + (sin(minuteAngle) * minuteLength).toFloat()
            ),
            strokeWidth = 5.dp.toPx(),
            cap = StrokeCap.Round
        )
        
        // Second hand with smooth movement
        val secondAngle = (seconds * 6 - 90) * PI / 180
        val secondLength = radius * 0.85f
        drawLine(
            color = tertiaryColor,
            start = Offset(
                centerX - (cos(secondAngle) * 20.dp.toPx()).toFloat(),
                centerY - (sin(secondAngle) * 20.dp.toPx()).toFloat()
            ),
            end = Offset(
                centerX + (cos(secondAngle) * secondLength).toFloat(),
                centerY + (sin(secondAngle) * secondLength).toFloat()
            ),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        
        // Center dot
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(tertiaryColor, tertiaryColor.copy(alpha = 0.5f))
            ),
            radius = 8.dp.toPx(),
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = surfaceColor,
            radius = 4.dp.toPx(),
            center = Offset(centerX, centerY)
        )
    }
}

@Composable
fun AnimatedDigit(value: Int) {
    AnimatedContent(
        targetState = value,
        transitionSpec = {
            slideInVertically { -it } + fadeIn() togetherWith
            slideOutVertically { it } + fadeOut()
        },
        label = "digit"
    ) { digit ->
        Text(
            digit.toString(),
            fontSize = 64.sp,
            fontWeight = FontWeight.Thin,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
        tonalElevation = 4.dp,
        shadowElevation = 8.dp
    ) {
        content()
    }
}

@Composable
fun StopwatchScreen() {
    var elapsedTime by remember { mutableLongStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var laps by remember { mutableStateOf(listOf<Long>()) }
    
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(16)  // ~60fps
            elapsedTime += 16
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "ring")
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "ringRotation"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Stopwatch display
        Box(
            modifier = Modifier.size(280.dp),
            contentAlignment = Alignment.Center
        ) {
            // Animated gradient ring when running
            if (isRunning) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(ringRotation)
                ) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFFBB86FC),
                                Color(0xFF03DAC6),
                                Color(0xFFCF6679),
                                Color(0xFFBB86FC)
                            )
                        ),
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            } else {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color(0xFF1D1D28),
                        style = Stroke(width = 8.dp.toPx())
                    )
                }
            }
            
            // Inner content
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    formatStopwatchTime(elapsedTime),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-1).sp
                )
                
                Text(
                    ".${String.format("%02d", (elapsedTime % 1000) / 10)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Control buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lap/Reset
            FloatingActionButton(
                onClick = {
                    if (isRunning) laps = laps + elapsedTime
                    else { elapsedTime = 0L; laps = emptyList() }
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    if (isRunning) Icons.Rounded.Flag else Icons.Rounded.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            // Start/Stop
            val buttonScale by animateFloatAsState(
                targetValue = if (isRunning) 1.1f else 1f,
                animationSpec = spring(dampingRatio = 0.5f),
                label = "buttonScale"
            )
            
            LargeFloatingActionButton(
                onClick = { isRunning = !isRunning },
                containerColor = if (isRunning) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                contentColor = if (isRunning) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .size(88.dp)
                    .scale(buttonScale)
            ) {
                Icon(
                    if (isRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Laps
        if (laps.isNotEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(laps.reversed()) { index, lapTime ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Lap ${laps.size - index}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                formatFullTime(lapTime),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimerScreen() {
    var totalSeconds by remember { mutableIntStateOf(300) }
    var remainingMs by remember { mutableLongStateOf(totalSeconds * 1000L) }
    var isRunning by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    
    LaunchedEffect(isRunning) {
        while (isRunning && remainingMs > 0) {
            delay(50)
            remainingMs -= 50
            if (remainingMs <= 0) {
                isRunning = false
                isFinished = true
                remainingMs = 0
            }
        }
    }
    
    val progress = remainingMs.toFloat() / (totalSeconds * 1000f)
    
    val infiniteTransition = rememberInfiniteTransition(label = "finished")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    val progressColor by animateColorAsState(
        targetValue = when {
            isFinished -> MaterialTheme.colorScheme.error
            progress < 0.2f -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(500),
        label = "progressColor"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Timer display
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(if (isFinished) pulseScale else 1f),
            contentAlignment = Alignment.Center
        ) {
            // Background track
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color(0xFF1D1D28),
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            
            // Progress arc
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = progress * 360f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isFinished) {
                    Icon(
                        Icons.Rounded.NotificationsActive,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Time's up!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text(
                        formatTimerTime(remainingMs),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Time presets
        if (!isRunning && !isFinished) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(1, 3, 5, 10, 15, 30).forEach { mins ->
                    val isSelected = totalSeconds == mins * 60
                    Surface(
                        onClick = {
                            totalSeconds = mins * 60
                            remainingMs = totalSeconds * 1000L
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                               else MaterialTheme.colorScheme.surface,
                        modifier = Modifier.animateContentSize()
                    ) {
                        Text(
                            "${mins}m",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                   else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FloatingActionButton(
                onClick = {
                    remainingMs = totalSeconds * 1000L
                    isFinished = false
                    isRunning = false
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    Icons.Rounded.Refresh,
                    contentDescription = "Reset",
                    modifier = Modifier.size(28.dp)
                )
            }
            
            LargeFloatingActionButton(
                onClick = {
                    if (isFinished) {
                        remainingMs = totalSeconds * 1000L
                        isFinished = false
                    }
                    isRunning = !isRunning
                },
                containerColor = if (isRunning) MaterialTheme.colorScheme.errorContainer
                                else MaterialTheme.colorScheme.primaryContainer,
                contentColor = if (isRunning) MaterialTheme.colorScheme.onErrorContainer
                              else MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(88.dp)
            ) {
                Icon(
                    if (isRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

fun formatStopwatchTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

fun formatFullTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val millis = (ms % 1000) / 10
    return String.format("%02d:%02d.%02d", minutes, seconds, millis)
}

fun formatTimerTime(ms: Long): String {
    val totalSeconds = (ms + 999) / 1000  // Round up
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
