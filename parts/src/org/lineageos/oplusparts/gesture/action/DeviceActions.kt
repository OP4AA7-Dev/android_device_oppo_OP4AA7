/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture.action

import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log

/**
 * Wake the device and launch the secure camera.
 *
 * Uses [PowerManager.isInteractive] (not the deprecated `isScreenOn`) and the
 * reason-bearing [PowerManager.wakeUp] overload. The legacy
 * `SCREEN_BRIGHT_WAKE_LOCK` is gone: waking explicitly first is sufficient
 * before starting the secure camera activity.
 */
object DeviceActions : GestureAction {
    private const val TAG = "OPlusParts.DeviceActions"

    override fun tokens() = setOf(ActionTokens.WAKE_DEVICE, ActionTokens.CAMERA)

    override fun execute(context: Context, token: String): Boolean = try {
        when (token) {
            ActionTokens.WAKE_DEVICE -> wakeDevice(context)
            ActionTokens.CAMERA -> launchSecureCamera(context)
        }
        true
    } catch (e: Exception) {
        Log.e(TAG, "Device action '$token' failed", e)
        false
    }

    private fun wakeDevice(context: Context) {
        val pm = context.getSystemService(PowerManager::class.java) ?: return
        if (!pm.isInteractive) {
            pm.wakeUp(SystemClock.uptimeMillis(), PowerManager.WAKE_REASON_GESTURE, "OPlusParts:screen_off_gesture")
        }
    }

    private fun launchSecureCamera(context: Context) {
        wakeDevice(context)
        val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_FROM_BACKGROUND)
        context.startActivity(intent)
    }
}
