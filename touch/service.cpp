/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "vendor.lineage.touch@1.0-service-OP4AA7"

#include <android-base/logging.h>
#include <hidl/HidlTransportSupport.h>
#include "HighTouchPollingRate.h"
#include "TouchscreenGesture.h"

using android::OK;
using android::sp;
using android::status_t;
using android::hardware::configureRpcThreadpool;
using android::hardware::joinRpcThreadpool;

using vendor::lineage::touch::V1_0::IHighTouchPollingRate;
using vendor::lineage::touch::V1_0::ITouchscreenGesture;
using vendor::lineage::touch::V1_0::implementation::HighTouchPollingRate;
using vendor::lineage::touch::V1_0::implementation::TouchscreenGesture;

int main() {
    status_t status = OK;

    LOG(INFO) << "Touch HAL service is starting.";

    sp<HighTouchPollingRate> htpr = new HighTouchPollingRate();
    sp<TouchscreenGesture> tg = new TouchscreenGesture();

    configureRpcThreadpool(1, true /*callerWillJoin*/);

    status = htpr->registerAsService();
    if (status != OK) {
        LOG(ERROR) << "Could not register service for Touch HAL HighTouchPollingRate Iface ("
                   << status << ")";
        goto shutdown;
    }

    status = tg->registerAsService();
    if (status != OK) {
        LOG(ERROR) << "Could not register service for Touch HAL TouchscreenGesture Iface ("
                   << status << ")";
        goto shutdown;
    }

    LOG(INFO) << "Touch HAL service is ready.";
    joinRpcThreadpool();
    // Should not pass this line

shutdown:
    // In normal operation, we don't expect the thread pool to shutdown
    LOG(ERROR) << "Touch HAL service is shutting down.";
    return 1;
}
