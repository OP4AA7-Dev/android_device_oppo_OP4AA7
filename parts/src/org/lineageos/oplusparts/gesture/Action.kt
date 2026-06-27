/*
 * Copyright (C) 2014 SlimRoms Project
 * Copyright (C) 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.media.session.MediaSessionLegacyHelper
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import android.os.UserHandle
import android.os.Vibrator
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.InputDevice
import android.view.KeyEvent
import android.view.KeyCharacterMap
import android.hardware.input.InputManager
import java.net.URISyntaxException

/**
 * Executes a gesture action token (see [ActionConstants]).
 * Adapted from the SlimRoms/Omni action framework for Android 14.
 */
object Action {
    private const val TAG = "OPlusParts.Action"
    private const val PULSE_ACTION = "com.android.systemui.doze.pulse"

    private var torchEnabled = false

    fun processAction(context: Context, action: String?) {
        if (action.isNullOrEmpty() || action == ActionConstants.ACTION_NULL) {
            return
        }

        when (action) {
            ActionConstants.ACTION_WAKE_DEVICE -> wakeDevice(context)
            ActionConstants.ACTION_TORCH -> toggleTorch(context)
            ActionConstants.ACTION_CAMERA -> triggerCamera(context)
            ActionConstants.ACTION_MEDIA_PREVIOUS ->
                dispatchMediaKey(context, KeyEvent.KEYCODE_MEDIA_PREVIOUS)
            ActionConstants.ACTION_MEDIA_NEXT ->
                dispatchMediaKey(context, KeyEvent.KEYCODE_MEDIA_NEXT)
            ActionConstants.ACTION_MEDIA_PLAY_PAUSE ->
                dispatchMediaKey(context, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
            ActionConstants.ACTION_VIB_SILENT -> cycleRingerMode(context)
            ActionConstants.ACTION_VOLUME_UP ->
                triggerVirtualKeypress(KeyEvent.KEYCODE_VOLUME_UP)
            ActionConstants.ACTION_VOLUME_DOWN ->
                triggerVirtualKeypress(KeyEvent.KEYCODE_VOLUME_DOWN)
            ActionConstants.ACTION_ASSIST -> launchAssist(context)
            ActionConstants.ACTION_AMBIENT_DISPLAY -> pulseAmbient(context)
            else -> {
                // Custom app shortcut stored as Intent URI
                try {
                    val intent = Intent.parseUri(action, 0)
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    )
                    context.startActivityAsUser(intent, UserHandle.CURRENT)
                } catch (e: URISyntaxException) {
                    Log.e(TAG, "Bad action URI: $action", e)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to start $action", e)
                }
            }
        }
    }

    private fun wakeDevice(context: Context) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!pm.isScreenOn) {
            pm.wakeUp(SystemClock.uptimeMillis())
        }
    }

    private fun toggleTorch(context: Context) {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            for (cameraId in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val flashAvailable = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (flashAvailable == true &&
                    facing == CameraCharacteristics.LENS_FACING_BACK
                ) {
                    cameraManager.setTorchMode(cameraId, !torchEnabled)
                    torchEnabled = !torchEnabled
                    break
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Torch toggle failed", e)
        }
    }

    private fun triggerCamera(context: Context) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "OPlusParts.GestureCamera"
        )
        wl.acquire(500)
        try {
            val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_FROM_BACKGROUND)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Camera launch failed", e)
        } finally {
            try { wl.release() } catch (_: Exception) {}
        }
    }

    private fun dispatchMediaKey(context: Context, keycode: Int) {
        try {
            val now = SystemClock.uptimeMillis()
            var event = KeyEvent(now, now, KeyEvent.ACTION_DOWN, keycode, 0)
            MediaSessionLegacyHelper.getHelper(context).sendMediaButtonEvent(event, true)
            event = KeyEvent.changeAction(event, KeyEvent.ACTION_UP)
            MediaSessionLegacyHelper.getHelper(context).sendMediaButtonEvent(event, true)
        } catch (e: Exception) {
            Log.e(TAG, "Media key dispatch failed", e)
        }
    }

    private fun cycleRingerMode(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (am.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> {
                am.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                vibrate(context)
            }
            AudioManager.RINGER_MODE_VIBRATE -> {
                am.ringerMode = AudioManager.RINGER_MODE_SILENT
            }
            else -> {
                am.ringerMode = AudioManager.RINGER_MODE_NORMAL
                beep()
            }
        }
    }

    private fun triggerVirtualKeypress(keyCode: Int) {
        try {
            val im = InputManager.getInstance()
            val now = SystemClock.uptimeMillis()
            val flags = KeyEvent.FLAG_FROM_SYSTEM or KeyEvent.FLAG_VIRTUAL_HARD_KEY
            val down = KeyEvent(
                now, now, KeyEvent.ACTION_DOWN, keyCode, 0, 0,
                KeyCharacterMap.VIRTUAL_KEYBOARD, 0, flags, InputDevice.SOURCE_KEYBOARD
            )
            val up = KeyEvent(
                now, now, KeyEvent.ACTION_UP, keyCode, 0, 0,
                KeyCharacterMap.VIRTUAL_KEYBOARD, 0, flags, InputDevice.SOURCE_KEYBOARD
            )
            im.injectInputEvent(down, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC)
            im.injectInputEvent(up, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC)
        } catch (e: Exception) {
            Log.e(TAG, "Virtual keypress failed", e)
        }
    }

    private fun launchAssist(context: Context) {
        try {
            val sm = context.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            sm.launchAssist(Bundle())
        } catch (e: Exception) {
            Log.e(TAG, "launchAssist failed", e)
        }
    }

    private fun pulseAmbient(context: Context) {
        val dozeEnabled = Settings.Secure.getInt(
            context.contentResolver, Settings.Secure.DOZE_ENABLED, 1
        ) != 0
        if (dozeEnabled) {
            context.sendBroadcastAsUser(Intent(PULSE_ACTION), UserHandle.CURRENT)
        }
    }

    private fun vibrate(context: Context) {
        try {
            val vib = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vib.vibrate(50)
        } catch (_: Exception) {}
    }

    private fun beep() {
        try {
            val tg = ToneGenerator(
                AudioManager.STREAM_NOTIFICATION,
                (ToneGenerator.MAX_VOLUME * 0.85).toInt()
            )
            tg.startTone(ToneGenerator.TONE_PROP_BEEP)
        } catch (_: Exception) {}
    }
}
