package com.example.ui.components

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    val SPANISH_LOCALE = Locale("es", "ES")
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy", SPANISH_LOCALE)
    private val SHORT_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM", SPANISH_LOCALE)
    private val MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", SPANISH_LOCALE)

    fun todayEpochDay(): Long {
        return LocalDate.now().toEpochDay()
    }

    fun formatEpochDay(epochDay: Long): String {
        val date = LocalDate.ofEpochDay(epochDay)
        return date.format(DATE_FORMATTER)
    }

    fun formatShort(epochDay: Long): String {
        val date = LocalDate.ofEpochDay(epochDay)
        return date.format(SHORT_FORMATTER)
    }

    fun formatMonthYear(year: Int, month: Int): String {
        val date = LocalDate.of(year, month, 1)
        return date.format(MONTH_YEAR_FORMATTER).replaceFirstChar { it.uppercase() }
    }

    fun formatRelativeDays(epochDay: Long): String {
        val today = todayEpochDay()
        val diff = epochDay - today
        return when {
            diff < -1 -> "Vencido hace ${-diff} días"
            diff == -1L -> "Venció ayer"
            diff == 0L -> "Hoy"
            diff == 1L -> "Mañana"
            diff == 2L -> "En 2 días (¡Alerta previa!)"
            diff in 3..7 -> "En $diff días"
            else -> "En $diff días (${formatEpochDay(epochDay)})"
        }
    }

    fun isWithinNext48Hours(epochDay: Long): Boolean {
        val today = todayEpochDay()
        val diff = epochDay - today
        return diff in 0..2
    }

    fun isOverdue(epochDay: Long): Boolean {
        return epochDay < todayEpochDay()
    }

    fun formatTimestamp(timestamp: Long): String {
        val localDate = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
        return localDate.format(DATE_FORMATTER)
    }
}
