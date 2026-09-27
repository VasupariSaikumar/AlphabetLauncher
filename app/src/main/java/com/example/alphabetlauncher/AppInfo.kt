package com.example.alphabetlauncher

import android.graphics.drawable.Drawable


data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable
) {
    /** First letter used for A–Z grouping, uppercased, falling back to '#' for odd/emoji names. */
    val firstLetter: Char
        get() = label.firstOrNull()?.uppercaseChar()?.takeIf { it in 'A'..'Z' } ?: '#'
}
