package com.clockapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalogClock(
    hours: Int,
    minutes: Int,
    seconds: Float,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outline
    
    Canvas(modifier = modifier.fillMaxSize()) {
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
