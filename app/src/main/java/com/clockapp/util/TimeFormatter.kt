package com.clockapp.util

object TimeFormatter {
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
        val totalSeconds = (ms + 999) / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

fun formatStopwatchTime(ms: Long) = TimeFormatter.formatStopwatchTime(ms)
fun formatFullTime(ms: Long) = TimeFormatter.formatFullTime(ms)
fun formatTimerTime(ms: Long) = TimeFormatter.formatTimerTime(ms)
