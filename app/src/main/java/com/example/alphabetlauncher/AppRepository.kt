package com.example.alphabetlauncher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager


object AppRepository {

    @Volatile
    private var cachedApps: List<AppInfo>? = null

    fun getLaunchableApps(context: Context): List<AppInfo> {
        cachedApps?.let { return it }
        synchronized(this) {
            cachedApps?.let { return it }

            val pm = context.packageManager
            val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val resolveInfos = pm.queryIntentActivities(launcherIntent, PackageManager.MATCH_ALL)

            val apps = resolveInfos.mapNotNull { info ->
                val activityInfo = info.activityInfo ?: return@mapNotNull null
                val label = info.loadLabel(pm)?.toString()?.takeIf { it.isNotBlank() }
                    ?: return@mapNotNull null
                val icon = try {
                    info.loadIcon(pm)
                } catch (e: Exception) {
                    return@mapNotNull null
                }
                AppInfo(label = label, packageName = activityInfo.packageName, icon = icon)
            }
                .distinctBy { it.packageName }
                .sortedBy { it.label.lowercase() }

            cachedApps = apps
            return apps
        }
    }

    /** Groups the cached apps by first letter. Call after getLaunchableApps() has warmed the cache. */
    fun appsStartingWith(context: Context, letter: Char): List<AppInfo> =
        getLaunchableApps(context).filter { it.firstLetter == letter.uppercaseChar() }

    fun invalidate() {
        cachedApps = null
    }
}
