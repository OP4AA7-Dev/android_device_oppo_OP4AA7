/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "vendor.lineage.touch@1.0-service.OP4AA7"

#include <android-base/file.h>
#include <android-base/logging.h>
#include <android-base/strings.h>
#include "HighTouchPollingRate.h"

using ::android::base::ReadFileToString;
using ::android::base::Trim;
using ::android::base::WriteStringToFile;

namespace {
constexpr const char* kGameSwitchEnablePath = "/proc/touchpanel/game_switch_enable";
}  // anonymous namespace

namespace vendor {
namespace lineage {
namespace touch {
namespace V1_0 {
namespace implementation {

Return<bool> HighTouchPollingRate::isEnabled() {
    std::string value;

    if (!ReadFileToString(kGameSwitchEnablePath, &value)) {
        LOG(ERROR) << "Failed to read current HighTouchPollingRate state";
        return false;
    }

    return Trim(value) != "0";
}

Return<bool> HighTouchPollingRate::setEnabled(bool enabled) {
    if (!WriteStringToFile(enabled ? "1" : "0", kGameSwitchEnablePath, true)) {
        LOG(ERROR) << "Failed to write HighTouchPollingRate state";
        return false;
    }

    return true;
}

}  // namespace implementation
}  // namespace V1_0
}  // namespace touch
}  // namespace lineage
}  // namespace vendor
