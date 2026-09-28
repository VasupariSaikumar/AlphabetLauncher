package com.example.alphabetlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.alphabetlauncher.ui.theme.AlphabetLauncherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlphabetLauncherTheme {
                LauncherScreen()
            }
        }
    }
}


@Composable
private fun LauncherScreen() {
    val context = LocalContext.current
    var selectedLetter by remember { mutableStateOf<Char?>(null) }
    val favorites = remember { defaultFavorites(context) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            val letter = selectedLetter
            if (letter == null) {
                ClockAndFavorites(
                    favoriteApps = favorites,
                    onLaunch = { app -> launchApp(context, app) }
                )
            } else {
                FilteredAppList(
                    letter = letter,
                    apps = remember(letter) { AppRepository.appsStartingWith(context, letter) },
                    onLaunch = { app -> launchApp(context, app) }
                )
            }

            AlphabetBar(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight()
                    .padding(top = 160.dp, bottom = 16.dp, end = 8.dp),
                onLetterChanged = { selectedLetter = it }
            )
        }
    }
}

private fun launchApp(context: android.content.Context, app: AppInfo) {
    context.packageManager.getLaunchIntentForPackage(app.packageName)?.let {
        context.startActivity(it)
    }
}
