/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.oplusparts.gesture.action

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log

/**
 * Toggle the rear-facing camera flash (torch).
 *
 * Note: `torchEnabled` is in-process state and does not track torch state
 * changes originating elsewhere (e.g. the quick-settings tile). Keeping the
 * current behaviour; a TorchCallback-backed read is a future improvement.
 */
object TorchAction : GestureAction {
    private const val TAG = "OPlusParts.TorchAction"
    private var torchEnabled = false

    override fun tokens() = setOf(ActionTokens.TORCH)

    override fun execute(context: Context, token: String): Boolean {
        return try {
            val cameraManager = context.getSystemService(CameraManager::class.java)
            if (cameraManager == null) {
                false
            } else {
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
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Torch toggle failed", e)
            false
        }
    }
}
