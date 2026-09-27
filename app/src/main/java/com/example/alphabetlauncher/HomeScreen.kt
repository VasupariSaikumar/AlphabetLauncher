package com.example.alphabetlauncher

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

/**
 * Resting state: live clock + date, and a short favourites list.
 * The favourites list is just the first few cached apps — swap in a persisted
 * favourites set for the long-press-to-favourite bonus.
 */
@Composable
fun ClockAndFavorites(
    favoriteApps: List<AppInfo>,
    onLaunch: (AppInfo) -> Unit
) {
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            delay(1000L)
        }
    }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, top = 64.dp, end = 48.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = timeFormat.format(now),
            fontSize = 56.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = dateFormat.format(now),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        favoriteApps.forEach { app ->
            AppRow(app = app, onClick = { onLaunch(app) })
        }
    }
}

/** Dragging state: header for the selected letter + every app starting with it. */
@Composable
fun FilteredAppList(
    letter: Char,
    apps: List<AppInfo>,
    onLaunch: (AppInfo) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, top = 64.dp, end = 48.dp)
    ) {
        Text(
            text = letter.toString(),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (apps.isEmpty()) {
            Text(
                text = "No apps",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn {
                items(apps, key = { it.packageName }) { app ->
                    AppRow(app = app, onClick = { onLaunch(app) })
                }
            }
        }
    }
}

@Composable
private fun AppRow(app: AppInfo, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberDrawablePainter(app.icon),
            contentDescription = app.label,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = app.label, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}

fun defaultFavorites(context: Context, count: Int = 6): List<AppInfo> =
    AppRepository.getLaunchableApps(context).take(count)
