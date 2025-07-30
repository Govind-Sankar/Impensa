package com.nebulae.impensa.presentation.components

import java.text.NumberFormat
import java.util.Locale

fun formatNumber(amount: Double): String {
    val locale = Locale.Builder().setLanguage("en").setRegion("IN").build()
    val formatter = NumberFormat.getNumberInstance(locale).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    val formattedString = "₹ " + formatter.format(amount)
    return formattedString
}