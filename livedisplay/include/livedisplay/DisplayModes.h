/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

#pragma once

#include <hidl/MQDescriptor.h>
#include <hidl/Status.h>
#include <vendor/lineage/livedisplay/2.1/IDisplayModes.h>

namespace vendor {
namespace lineage {
namespace livedisplay {
namespace V2_1 {
namespace implementation {

using ::android::hardware::Return;
using ::android::hardware::Void;
using ::android::sp;

class DisplayModes : public IDisplayModes {
  public:
    DisplayModes();

    // Methods from ::vendor::lineage::livedisplay::V2_0::IDisplayModes follow.
    Return<void> getDisplayModes(getDisplayModes_cb resultCb) override;
    Return<void> getCurrentDisplayMode(getCurrentDisplayMode_cb resultCb) override;
    Return<void> getDefaultDisplayMode(getDefaultDisplayMode_cb resultCb) override;
    Return<bool> setDisplayMode(int32_t modeID, bool makeDefault) override;

  private:
    // Mode id -> (name, seed value written to /sys/kernel/oppo_display/seed).
    // The Samsung AMS641RW01 panel implements SEED CRC LUTs for modes 0/1 and
    // falls back to the seed-off command for any other value.
    struct ModeInfo {
        std::string name;
        int seed;
    };

    const std::map<int32_t, ModeInfo> kModeMap = {
            {0, {"Vivid", 0}},
            {1, {"Natural", 1}},
            {2, {"Cinematic", -1}},
    };

    int32_t mCurrentModeId;
    int32_t mDefaultModeId;
};

}  // namespace implementation
}  // namespace V2_1
}  // namespace livedisplay
}  // namespace lineage
}  // namespace vendor
