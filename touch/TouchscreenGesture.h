/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

#pragma once

#include <hidl/MQDescriptor.h>
#include <hidl/Status.h>
#include <vendor/lineage/touch/1.0/ITouchscreenGesture.h>
#include <map>

namespace vendor {
namespace lineage {
namespace touch {
namespace V1_0 {
namespace implementation {

using ::android::hardware::Return;
using ::android::hardware::Void;
using ::vendor::lineage::touch::V1_0::Gesture;

class TouchscreenGesture : public ITouchscreenGesture {
  public:
    // Methods from ::vendor::lineage::touch::V1_0::ITouchscreenGesture follow.
    Return<void> getSupportedGestures(getSupportedGestures_cb resultCb) override;
    Return<bool> setGestureEnabled(const Gesture& gesture, bool enabled) override;

  private:
    // See: drivers/input/touchscreen/oplus_touchscreen/touchpanel_common.h
    // The touchpanel driver reports screen-off gestures as
    // KEY_GESTURE_START + gesture_type, gated by the per-gesture mask in
    // /proc/touchpanel/double_tap_enable_indep (bit N == gesture type N).
    static constexpr int kGestureStartKey = 246;
    enum {
        kGestureUnknown,
        kGestureDoubleTap,
        kGestureUpVee,
        kGestureDownVee,
        kGestureLeftVee,
        kGestureRightVee,
        kGestureCircle,
        kGestureDoubleSwipe,
        kGestureLeftToRight,
        kGestureRightToLeft,
        kGestureUpToDown,
        kGestureDownToUp,
        kGestureM,
        kGestureW,
        kGestureFingerprintDown,
        kGestureFingerprintUp,
        kGestureSingleTap,
        kGestureHeart,
        kGestureS,
    };

    const std::map<int, std::string> kGestureNames = {
            {kGestureUnknown, "Unknown"},
            {kGestureDoubleTap, "Double tap"},
            {kGestureUpVee, "Down arrow"},
            {kGestureDownVee, "Up arrow"},
            {kGestureLeftVee, "Right arrow"},
            {kGestureRightVee, "Left arrow"},
            {kGestureCircle, "Letter O"},
            {kGestureDoubleSwipe, "Two fingers down swipe"},
            {kGestureLeftToRight, "One finger right swipe"},
            {kGestureRightToLeft, "One finger left swipe"},
            {kGestureUpToDown, "One finger down swipe"},
            {kGestureDownToUp, "One finger up swipe"},
            {kGestureM, "Letter M"},
            {kGestureW, "Letter W"},
            {kGestureFingerprintDown, "Fingerprint down"},
            {kGestureFingerprintUp, "Fingerprint up"},
            {kGestureSingleTap, "Single tap"},
            {kGestureHeart, "Heart"},
            {kGestureS, "Letter S"},
    };

    template <typename H, typename... T>
    static constexpr int makeBitField(H head, T... tail) {
        return ((1 << head) | ... | (1 << tail));
    }
    static const int kSupportedGestures;
};

}  // namespace implementation
}  // namespace V1_0
}  // namespace touch
}  // namespace lineage
}  // namespace vendor
