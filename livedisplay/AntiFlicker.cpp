/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "AntiFlickerService"

#include <android-base/file.h>
#include <android-base/logging.h>
#include <android-base/strings.h>
#include <livedisplay/AntiFlicker.h>

namespace vendor {
namespace lineage {
namespace livedisplay {
namespace V2_1 {
namespace implementation {

namespace {
constexpr const char* kDimlayerBlEn = "/sys/kernel/oppo_display/dimlayer_bl_en";
}  // anonymous namespace

AntiFlicker::AntiFlicker() {}

Return<bool> AntiFlicker::isEnabled() {
    std::string tmp;
    if (::android::base::ReadFileToString(kDimlayerBlEn, &tmp)) {
        return ::android::base::Trim(tmp) == "1";
    }
    LOG(ERROR) << "Failed to read current AntiFlicker state from " << kDimlayerBlEn;
    return false;
}

Return<bool> AntiFlicker::setEnabled(bool enabled) {
    if (!::android::base::WriteStringToFile(enabled ? "1" : "0", kDimlayerBlEn, true)) {
        LOG(ERROR) << "Failed to set AntiFlicker state on " << kDimlayerBlEn;
        return false;
    }
    return true;
}

}  // namespace implementation
}  // namespace V2_1
}  // namespace livedisplay
}  // namespace lineage
}  // namespace vendor
