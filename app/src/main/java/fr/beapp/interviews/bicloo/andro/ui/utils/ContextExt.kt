package fr.beapp.interviews.bicloo.andro.ui.utils

import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun Context.hasPermission(permission: String): Boolean = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.hasPermissions(vararg permissions: String): Boolean = permissions.all { hasPermission(it) }

fun Context.hasForegroundLocationPermission(): Boolean = hasPermissions(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)
fun Activity.hasPermissionDenied(permission: String): Boolean = !ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun Activity.hasPermissionsDenied(vararg permissions: String): Boolean = permissions.all { hasPermissionDenied(it) }

fun Activity.hasForegroundLocationPermissionDenied(): Boolean = hasPermissionsDenied(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)