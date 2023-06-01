package fr.beapp.interviews.bicloo.andro.ui.utils

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.hasPermission(permission: String): Boolean = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.hasPermissions(vararg permissions: String): Boolean = permissions.all { hasPermission(it) }

fun Context.hasForegroundLocationPermission(): Boolean = hasPermissions(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)