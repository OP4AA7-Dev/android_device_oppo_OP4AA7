/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "DisplayModesService"

#include <android-base/file.h>
#include <android-base/logging.h>
#include <android-base/strings.h>
#include <fstream>
#include <livedisplay/DisplayModes.h>

namespace vendor {
namespace lineage {
namespace livedisplay {
namespace V2_1 {
namespace implementation {

namespace {
constexpr const char* kSeedPath = "/sys/kernel/oppo_display/seed";
constexpr const char* kDefaultPath = "/data/vendor/display/default_display_mode";
}  // anonymous namespace

DisplayModes::DisplayModes() : mCurrentModeId(0), mDefaultModeId(0) {
    std::ifstream defaultFile(kDefaultPath);

    defaultFile >> mDefaultModeId;
    LOG(DEBUG) << "Default file read result " << mDefaultModeId << " fail " << defaultFile.fail();
    if (defaultFile.fail() || kModeMap.find(mDefaultModeId) == kModeMap.end()) {
        mDefaultModeId = 0;
    }

    setDisplayMode(mDefaultModeId, false);
}

// Methods from ::vendor::lineage::livedisplay::V2_0::IDisplayModes follow.
Return<void> DisplayModes::getDisplayModes(getDisplayModes_cb resultCb) {
    std::vector<V2_0::DisplayMode> modes;

    for (const auto& entry : kModeMap) {
        modes.push_back({entry.first, entry.second.name});
    }
    resultCb(modes);
    return Void();
}

Return<void> DisplayModes::getCurrentDisplayMode(getCurrentDisplayMode_cb resultCb) {
    resultCb({mCurrentModeId, kModeMap.at(mCurrentModeId).name});
    return Void();
}

Return<void> DisplayModes::getDefaultDisplayMode(getDefaultDisplayMode_cb resultCb) {
    resultCb({mDefaultModeId, kModeMap.at(mDefaultModeId).name});
    return Void();
}

Return<bool> DisplayModes::setDisplayMode(int32_t modeID, bool makeDefault) {
    const auto iter = kModeMap.find(modeID);
    if (iter == kModeMap.end()) {
        return false;
    }

    if (!::android::base::WriteStringToFile(std::to_string(iter->second.seed), kSeedPath, true)) {
        LOG(ERROR) << "Failed to set seed mode on " << kSeedPath;
        return false;
    }
    mCurrentModeId = modeID;

    if (makeDefault) {
        std::ofstream defaultFile(kDefaultPath);
        defaultFile << iter->first;
        if (defaultFile.fail()) {
            return false;
        }
        mDefaultModeId = iter->first;
    }
    return true;
}

}  // namespace implementation
}  // namespace V2_1
}  // namespace livedisplay
}  // namespace lineage
}  // namespace vendor
