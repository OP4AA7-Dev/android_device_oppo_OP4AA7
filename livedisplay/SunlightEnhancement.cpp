/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "SunlightEnhancementService"

#include <android-base/file.h>
#include <android-base/logging.h>
#include <android-base/strings.h>
#include <livedisplay/SunlightEnhancement.h>

namespace vendor {
namespace lineage {
namespace livedisplay {
namespace V2_1 {
namespace implementation {

namespace {
constexpr const char* kHbmPath = "/sys/kernel/oppo_display/hbm";
}  // anonymous namespace

SunlightEnhancement::SunlightEnhancement() {}

Return<bool> SunlightEnhancement::isEnabled() {
    std::string tmp;
    if (::android::base::ReadFileToString(kHbmPath, &tmp)) {
        return std::stoi(::android::base::Trim(tmp)) > 0;
    }
    LOG(ERROR) << "Failed to read current SunlightEnhancement state from " << kHbmPath;
    return false;
}

Return<bool> SunlightEnhancement::setEnabled(bool enabled) {
    // HBM mode 3 is the outdoor sunlight brightness level (mode 1 is the
    // on-screen fingerprint brightness bump).
    if (!::android::base::WriteStringToFile(enabled ? "3" : "0", kHbmPath, true)) {
        LOG(ERROR) << "Failed to set SunlightEnhancement state on " << kHbmPath;
        return false;
    }
    return true;
}

}  // namespace implementation
}  // namespace V2_1
}  // namespace livedisplay
}  // namespace lineage
}  // namespace vendor
