package com.personal.android.androidpdfreader.util

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionCommonUtil(private val activity: Activity,
                           private val permissions: Array<String>,
                           private val readWritePermissionRequestCode: Int,
                           private val listener: OnPermissionRequestListener) {

    fun isReadWritePermissionAvailable(): Boolean {
        var isGranted = true
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false
                return@forEach
            }
        }
        return isGranted
    }

    fun requestReadWritePermission() {
        if (permissions.all { ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }) {
            listener.shouldShowRationalMessage()
            ActivityCompat.requestPermissions(activity, permissions, readWritePermissionRequestCode)
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(activity, permissions, readWritePermissionRequestCode)
        }
    }

    fun onPermissionResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            readWritePermissionRequestCode -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED })) {
                    listener.permissionGranted()
                } else {
                    listener.permissionDenied()
                }
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    interface OnPermissionRequestListener {
        fun permissionGranted()
        fun permissionDenied()
        fun shouldShowRationalMessage()
    }
}