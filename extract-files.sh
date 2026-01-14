#!/bin/bash
#
# Copyright (C) 2016 The CyanogenMod Project
# Copyright (C) 2017-2023 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

set -e

DEVICE=OP4AA7
VENDOR=oppo

# Load extract_utils and do some sanity checks
MY_DIR="${BASH_SOURCE%/*}"
if [[ ! -d "${MY_DIR}" ]]; then MY_DIR="${PWD}"; fi

ANDROID_ROOT="${MY_DIR}/../../.."

HELPER="${ANDROID_ROOT}/tools/extract-utils/extract_utils.sh"
if [ ! -f "${HELPER}" ]; then
    echo "Unable to find helper script at ${HELPER}"
    exit 1
fi
source "${HELPER}"

# Default to sanitizing the vendor folder before extraction
CLEAN_VENDOR=true

KANG=
SECTION=

while [ "${#}" -gt 0 ]; do
    case "${1}" in
        -n | --no-cleanup )
                CLEAN_VENDOR=false
                ;;
        -k | --kang )
                KANG="--kang"
                ;;
        -s | --section )
                SECTION="${2}"; shift
                CLEAN_VENDOR=false
                ;;
        * )
                SRC="${1}"
                ;;
    esac
    shift
done

if [ -z "${SRC}" ]; then
    SRC="adb"
fi

function blob_fixup() {
    case "${1}" in
        system_ext/lib64/lib-imsvideocodec.so)
            "${PATCHELF}" --add-needed libgui_shim.so "${2}"
        ;;
        vendor/lib64/sensors.ssc.so)
            sed -i "s/qti.sensor.wise_light/android.sensor.light\x00/" "${2}"
            sed -i "s/qti.sensor.proximity_fake/android.sensor.proximity\x00/" "${2}"
            "${SIGSCAN}" -p "F9 69 CA 84 52 49 3F A0 72" -P "F9 A9 00 80 52 09 00 A0 72" -f "${2}" # mov w9, #0x1fa2653 -> mov w9, #5
            "${SIGSCAN}" -p "F9 A9 C7 84 52 49 3F A0 72" -P "F9 09 01 80 52 1F 20 03 D5" -f "${2}" # mov w9, #0x1fa263d -> mov w9, #8
        ;;
    esac
}

# Initialize the helper
setup_vendor "${DEVICE}" "${VENDOR}" "${ANDROID_ROOT}" false "${CLEAN_VENDOR}"

extract "${MY_DIR}/proprietary-files.txt" "${SRC}" "${KANG}" --section "${SECTION}"

"${MY_DIR}/setup-makefiles.sh"
