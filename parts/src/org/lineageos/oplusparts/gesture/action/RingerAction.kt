/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture.action

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.VibrationEffect
import android.os.VibratorManager
import android.util.Log

/**
 * Cycle the ringer mode normal → vibrate → silent → normal, with a short
 * vibration on entering vibrate and a beep on returning to normal.
 *
 * Uses [VibratorManager] + [VibrationEffect] (the deprecated
 * `Context.VIBRATOR_SERVICE` / `Vibrator.vibrate(long)` path is gone).
 */
object RingerAction : GestureAction {
    private const val TAG = "OPlusParts.RingerAction"

    override fun tokens() = setOf(ActionTokens.RING_VIB_SILENT)

    override fun execute(context: Context, token: String): Boolean {
        return try {
            val am = context.getSystemService(AudioManager::class.java)
            if (am == null) {
                false
            } else {
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
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ringer cycle failed", e)
            false
        }
    }

    private fun vibrate(context: Context) {
        try {
            val vm = context.getSystemService(VibratorManager::class.java) ?: return
            vm.defaultVibrator.vibrate(
                VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } catch (_: Exception) {
        }
    }

    private fun beep() {
        var tg: ToneGenerator? = null
        try {
            tg = ToneGenerator(
                AudioManager.STREAM_NOTIFICATION,
                (ToneGenerator.MAX_VOLUME * 0.85).toInt(),
            )
            tg.startTone(ToneGenerator.TONE_PROP_BEEP)
        } catch (_: Exception) {
        } finally {
            try {
                tg?.release()
            } catch (_: Exception) {
            }
        }
    }
}
