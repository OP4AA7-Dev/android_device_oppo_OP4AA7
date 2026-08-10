/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

#pragma once

#include "TouchscreenGesture.h"

namespace vendor {
namespace lineage {
namespace touch {
namespace V1_0 {
namespace implementation {

// Gestures supported by the Synaptics S3706 firmware (F12_2D_CTRL27).
// Single tap, heart and letter S are not implemented by this panel.
const int TouchscreenGesture::kSupportedGestures = makeBitField(
        kGestureUpVee, kGestureDownVee, kGestureLeftVee, kGestureRightVee, kGestureCircle,
        kGestureDoubleSwipe, kGestureLeftToRight, kGestureRightToLeft, kGestureUpToDown,
        kGestureDownToUp, kGestureM, kGestureW);

}  // namespace implementation
}  // namespace V1_0
}  // namespace touch
}  // namespace lineage
}  // namespace vendor
