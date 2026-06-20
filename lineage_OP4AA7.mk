#
# Copyright (C) 2026 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

# Inherit from those products. Most specific first.
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base_telephony.mk)

# Inherit some common Lineage stuff.
$(call inherit-product, vendor/lineage/config/common_full_phone.mk)

# Inherit from OP4AA7 device
$(call inherit-product, device/oppo/OP4AA7/device.mk)

PRODUCT_DEVICE := OP4AA7
PRODUCT_NAME := lineage_OP4AA7
PRODUCT_BRAND := OPPO
PRODUCT_MODEL := PCNM00
PRODUCT_MANUFACTURER := OPPO

PRODUCT_GMS_CLIENTID_BASE := android-oppo

PRODUCT_BUILD_PROP_OVERRIDES += \
    PRIVATE_BUILD_DESC="PCNM00-user 11 RKQ1.200903.002 1667282885804 release-keys"

BUILD_FINGERPRINT := OPPO/PCNM00/OP4AA7:11/RKQ1.200903.002/1667282885804:user/release-keys
