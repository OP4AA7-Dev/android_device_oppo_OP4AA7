/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "vendor.lineage.touch@1.0-service.OP4AA7"

#include <android-base/file.h>
#include <android-base/logging.h>
#include <android-base/strings.h>
#include "TouchscreenGestureConfig.h"

using ::android::base::ReadFileToString;
using ::android::base::Trim;
using ::android::base::WriteStringToFile;

namespace {
constexpr const char* kGestureEnableIndepPath = "/proc/touchpanel/double_tap_enable_indep";
}  // anonymous namespace

namespace vendor {
namespace lineage {
namespace touch {
namespace V1_0 {
namespace implementation {

Return<void> TouchscreenGesture::getSupportedGestures(getSupportedGestures_cb resultCb) {
    std::vector<Gesture> gestures;

    for (const auto& [id, name] : kGestureNames) {
        if (kSupportedGestures & (1 << id)) {
            gestures.push_back({static_cast<int>(gestures.size()), name, kGestureStartKey + id});
        }
    }

    resultCb(gestures);
    return Void();
}

Return<bool> TouchscreenGesture::setGestureEnabled(const Gesture& gesture, bool enabled) {
    std::string tmp;
    int contents = 0;

    // The kernel node is read back in hex and written in decimal.
    if (ReadFileToString(kGestureEnableIndepPath, &tmp)) {
        contents = std::stoi(Trim(tmp), nullptr, 16);
    }

    if (enabled) {
        contents |= (1 << (gesture.keycode - kGestureStartKey));
    } else {
        contents &= ~(1 << (gesture.keycode - kGestureStartKey));
    }

    if (!WriteStringToFile(std::to_string(contents), kGestureEnableIndepPath, true)) {
        LOG(ERROR) << "Failed to write gesture enable state";
        return false;
    }

    return true;
}

}  // namespace implementation
}  // namespace V1_0
}  // namespace touch
}  // namespace lineage
}  // namespace vendor
