/*
 * Copyright (C) 2019 The Android Open Source Project
 *               2024-2025 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#pragma once

#include <android/hardware/sensors/1.0/types.h>
#include <android/hardware/sensors/2.1/ISensorsCallback.h>
#include <android/hardware/sensors/2.1/types.h>

#include <poll.h>
#include <atomic>
#include <condition_variable>
#include <fstream>
#include <memory>
#include <mutex>
#include <string>
#include <thread>
#include <vector>

using ::android::hardware::sensors::V1_0::OperationMode;
using ::android::hardware::sensors::V1_0::Result;
using ::android::hardware::sensors::V2_1::Event;
using ::android::hardware::sensors::V2_1::SensorInfo;
using ::android::hardware::sensors::V2_1::SensorType;

namespace android {
namespace hardware {
namespace sensors {
namespace V2_1 {
namespace subhal {
namespace implementation {

class ISensorsEventCallback {
  public:
    virtual ~ISensorsEventCallback(){};
    virtual void postEvents(const std::vector<Event>& events, bool wakeup) = 0;
};

class Sensor {
  public:
    Sensor(int32_t sensorHandle, ISensorsEventCallback* callback);
    virtual ~Sensor();

    const SensorInfo& getSensorInfo() const;
    virtual void batch(int64_t samplingPeriodNs);
    virtual void activate(bool enable);
    virtual Result flush();

    virtual void setOperationMode(OperationMode mode);
    bool supportsDataInjection() const;
    Result injectEvent(const Event& event);

  protected:
    virtual void run();
    virtual std::vector<Event> readEvents();
    static void startThread(Sensor* sensor);

    bool isWakeUpSensor();

    bool mIsEnabled;
    int64_t mSamplingPeriodNs;
    int64_t mLastSampleTimeNs;
    SensorInfo mSensorInfo;

    std::atomic_bool mStopThread;
    std::condition_variable mWaitCV;
    std::mutex mRunMutex;
    std::thread mRunThread;

    ISensorsEventCallback* mCallback;

    OperationMode mMode;
};

class OneShotSensor : public Sensor {
  public:
    OneShotSensor(int32_t sensorHandle, ISensorsEventCallback* callback);

    virtual void batch(int64_t /* samplingPeriodNs */) override {}

    virtual Result flush() override { return Result::BAD_VALUE; }
};

/*
 * Polls /sys/kernel/oppo_display/fp_state. When the touchpanel driver detects
 * a finger landing on the UDFPS icon while the panel is off, it flips that node
 * to 1 (via oplus_display_set_fp_state), which wakes this sensor. We then emit
 * a one-shot UDFPS event carrying this device's fixed fingerprint center
 * coordinates so the framework UdfpsController triggers onFingerDown even with
 * the screen off.
 */
class UdfpsSensor : public OneShotSensor {
  public:
    UdfpsSensor(int32_t sensorHandle, ISensorsEventCallback* callback);
    virtual ~UdfpsSensor() override;

    virtual void activate(bool enable) override;
    virtual void setOperationMode(OperationMode mode) override;

  protected:
    virtual void run() override;
    virtual std::vector<Event> readEvents();

  private:
    void interruptPoll();

    struct pollfd mPolls[2];
    int mWaitPipeFd[2];
    int mPollFd;

    // Fixed UDFPS icon center (from config_udfps_sensor_props: 540,2034,98).
    int mScreenX;
    int mScreenY;
};

/*
 * Polls a sysfs node that the touchpanel driver notifies (sysfs_notify,
 * POLLPRI) when an event occurs, and writes 1/0 to an enable node on
 * activate/deactivate. Used by one-shot wake-up sensors such as double tap.
 */
class SysfsPollingOneShotSensor : public OneShotSensor {
  public:
    SysfsPollingOneShotSensor(int32_t sensorHandle, ISensorsEventCallback* callback,
                              const std::string& pollPath, const std::string& enablePath,
                              const std::string& name, const std::string& typeAsString,
                              SensorType type);
    virtual ~SysfsPollingOneShotSensor() override;

    virtual void activate(bool enable) override;
    virtual void activate(bool enable, bool notify, bool lock);
    virtual void writeEnable(bool enable);
    virtual void setOperationMode(OperationMode mode) override;
    virtual std::vector<Event> readEvents() override;
    virtual void fillEventData(Event& event);
    virtual bool readFd(const int fd);

  protected:
    virtual void run() override;

    std::ofstream mEnableStream;

  private:
    void interruptPoll();

    struct pollfd mPolls[2];
    int mWaitPipeFd[2];
    int mPollFd;
    std::string mEnablePath;
    std::once_flag mEnableOpenOnce;
};

/*
 * Double-tap-to-wake. While dozing, the framework arms this sensor through
 * DozeSensors (config_dozeDoubleTapSensorType); activating it writes 1 to
 * /sys/touchpanel/double_tap_enable so the touch firmware keeps double-tap
 * detection armed in suspend. A detected double tap flips
 * /sys/touchpanel/double_tap_state and sysfs_notify wakes this sensor, which
 * then emits a one-shot wake-up event so DozeTriggers wakes the device.
 */
class DoubleTapSensor : public SysfsPollingOneShotSensor {
  public:
    DoubleTapSensor(int32_t sensorHandle, ISensorsEventCallback* callback)
        : SysfsPollingOneShotSensor(
                  sensorHandle, callback, "/sys/touchpanel/double_tap_state",
                  "/sys/touchpanel/double_tap_enable", "Double Tap Sensor",
                  "org.lineageos.sensor.double_tap",
                  static_cast<SensorType>(static_cast<int32_t>(SensorType::DEVICE_PRIVATE_BASE) +
                                          2)) {}
};

}  // namespace implementation
}  // namespace subhal
}  // namespace V2_1
}  // namespace sensors
}  // namespace hardware
}  // namespace android
