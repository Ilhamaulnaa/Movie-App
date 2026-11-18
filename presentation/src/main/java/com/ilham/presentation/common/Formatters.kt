package com.ilham.presentation.common

import java.text.NumberFormat
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatters {

    private const val MINUTES_PER_HOUR = 60
    private const val YEAR_LENGTH = 4

    private val displayDate: DateTimeFormatter
        get() = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())

//    "2024-05-01" → "01 May 2024"
    fun releaseDate(raw: String?): String? =
        raw?.let { runCatching { LocalDate.parse(it).format(displayDate) }.getOrNull() }

//    "2024-05-01" → "2024"
    fun releaseYear(raw: String?): String? =
        raw?.take(YEAR_LENGTH)?.takeIf { it.length == YEAR_LENGTH && it.all(Char::isDigit) }

//    "2023-01-01T12:00:00.000Z" → "01 Jan 2023"
    fun reviewDate(raw: String?): String? =
        raw?.let { runCatching { OffsetDateTime.parse(it).format(displayDate) }.getOrNull() }

//    135 → "2h 15m"
    fun runtime(minutes: Int?): String? {
        if (minutes == null || minutes <= 0) return null
        val hours = minutes / MINUTES_PER_HOUR
        val remaining = minutes % MINUTES_PER_HOUR
        return if (hours > 0) "${hours}h ${remaining}m" else "${remaining}m"
    }

//    7.456 → "7.5"
    fun rating(value: Double): String = String.format(Locale.US, "%.1f", value)

//    12345 → "12,345"
    fun count(value: Int): String = NumberFormat.getIntegerInstance().format(value)
}