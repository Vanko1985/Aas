/*  Copyright (C) 2019-2024 Andreas Shimokawa, Damien Gaignon, Daniel Dakhno,
    José Rebelo, mamucho, mkusnierz, Petr Vaněk

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>. */
package nodomain.freeyourgadget.gadgetbridge.devices.lenovo.watchxplus;

import android.app.Activity;
import android.bluetooth.le.ScanFilter;
import android.content.SharedPreferences;
import android.os.ParcelUuid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.activities.devicesettings.DeviceSettingsPreferenceConst;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractBLEDeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.devices.lenovo.LenovoWatchCalibrationActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.lenovo.LenovoWatchPairingActivity;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.ServiceDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.lenovo.watchxplus.WatchXPlusDeviceSupport;

import static nodomain.freeyourgadget.gadgetbridge.GBApplication.getContext;


public class WatchXPlusDeviceCoordinator extends AbstractBLEDeviceCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger(WatchXPlusDeviceCoordinator.class);

    private static final int FindPhone_ON = -1;
    public static final int FindPhone_OFF = 0;
    public static boolean isBPCalibrated = false;

    @NonNull
    @Override
    public Collection<? extends ScanFilter> createBLEScanFilters() {
        ParcelUuid watchXpService = new ParcelUuid(WatchXPlusConstants.UUID_SERVICE_WATCHXPLUS);
        ScanFilter filter = new ScanFilter.Builder().setServiceUuid(watchXpService).build();
        return Collections.singletonList(filter);
    }

    @Override
    protected void deleteDevice(@NonNull GBDevice gbDevice, @NonNull Device device, @NonNull DaoSession session) {

    }

    @Override
    public int getBondingStyle() {
        return BONDING_STYLE_NONE;
    }

    @Override
    public boolean supports(GBDeviceCandidate candidate) {
        String macAddress = candidate.getMacAddress().toUpperCase();
        String deviceName = candidate.getName().toUpperCase();
        if (candidate.supportsService(WatchXPlusConstants.UUID_SERVICE_WATCHXPLUS)) {
            return true;
        } else if (macAddress.startsWith("DC:41:E5")) {
            return true;
        } else if (deviceName.equalsIgnoreCase("WATCH XPLUS")) {
            return true;
            // add initial support for Watch X non-plus (forces Watch X to be recognized as Watch XPlus)
            // Watch X non-plus have same MAC address as Watch 9 (starts with "1C:87:79")
        } else if (deviceName.equalsIgnoreCase("WATCH X")) {
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Class<? extends Activity> getPairingActivity() {
        return LenovoWatchPairingActivity.class;
    }

    @Override
    public String[] getSupportedLanguageSettings(final GBDevice device) {
        return new String[]{
                "zh_CN",
                "en_US",
        };
    }

    @Override
    public boolean supportsActivityDataFetching() {
        return true;
    }

    @Override
    public boolean supportsActivityTracking() {
        return true;
    }

    @Override
    public SampleProvider<? extends ActivitySample> getSampleProvider(GBDevice device, DaoSession session) {
        return new WatchXPlusSampleProvider(device, session);
    }

    @Override
    public int getAlarmSlotCount(GBDevice device) {
        return 3;
    }

    @Override
    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return true;
    }

    @Override
    public String getManufacturer() {
        return "Lenovo";
    }

    @Override
    public boolean supportsWeather() {
        return true;
    }

    @Override
    public int[] getSupportedDeviceSpecificSettings(GBDevice device) {
        return new int[]{
                R.xml.devicesettings_liftwrist_display_noshed,
                R.xml.devicesettings_disconnectnotification_noshed,
                R.xml.devicesettings_donotdisturb_no_auto,
                R.xml.devicesettings_inactivity,
                R.xml.devicesettings_find_phone,
                R.xml.devicesettings_timeformat,
                R.xml.devicesettings_power_mode,
                R.xml.devicesettings_watchxplus,
                R.xml.devicesettings_transliteration
        };
    }

    @NonNull
    @Override
    public Class<? extends DeviceSupport> getDeviceSupportClass() {
        return WatchXPlusDeviceSupport.class;
    }

    @Override
    public EnumSet<ServiceDeviceSupport.Flags> getInitialFlags() {
        return EnumSet.of(ServiceDeviceSupport.Flags.THROTTLING, ServiceDeviceSupport.Flags.BUSY_CHECKING);
    }

// find phone settings
    /**
     * @return {@link #FindPhone_OFF}, {@link #FindPhone_ON}, or the duration
     */
    public static int getFindPhone(SharedPreferences sharedPrefs) {
        String findPhone = sharedPrefs.getString(DeviceSettingsPreferenceConst.PREF_FIND_PHONE, getContext().getString(R.string.p_off));

        if (findPhone.equals(getContext().getString(R.string.p_off))) {
            return FindPhone_OFF;
        } else if (findPhone.equals(getContext().getString(R.string.p_on))) {
            return FindPhone_ON;
        } else { // Duration
            String duration = sharedPrefs.getString(DeviceSettingsPreferenceConst.PREF_FIND_PHONE_DURATION, "0");

            try {
                int iDuration;

                try {
                    iDuration = Integer.valueOf(duration);
                } catch (Exception ex) {
                    iDuration = 60;
                }

                return iDuration;
            } catch (Exception e) {
                return FindPhone_ON;
            }
        }
    }


    /**
     * @param startOut out Only hour/minute are used.
     * @param endOut   out Only hour/minute are used.
     * @return True if DND hours are enabled.
     */
    public static boolean getDNDHours(String deviceAddress, Calendar startOut, Calendar endOut) {
        SharedPreferences prefs = GBApplication.getDeviceSpecificSharedPrefs(deviceAddress);
        String doNotDisturb = prefs.getString(DeviceSettingsPreferenceConst.PREF_DO_NOT_DISTURB_NOAUTO, getContext().getString(R.string.p_off));

        if (doNotDisturb.equals(getContext().getString(R.string.p_off))) {
            LOG.info(" DND is disabled ");
            return false;
        } else {

            String start = prefs.getString(DeviceSettingsPreferenceConst.PREF_DO_NOT_DISTURB_NOAUTO_START, "01:00");
            String end = prefs.getString(DeviceSettingsPreferenceConst.PREF_DO_NOT_DISTURB_NOAUTO_END, "06:00");

            DateFormat df = new SimpleDateFormat("HH:mm");

            try {
                startOut.setTime(df.parse(start));
                endOut.setTime(df.parse(end));

                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * @param startOut out Only hour/minute are used.
     * @param endOut   out Only hour/minute are used.
     * @return True if DND hours are enabled.
     */
    public static boolean getLongSitHours(String deviceAddress, Calendar startOut, Calendar endOut) {
        SharedPreferences prefs = GBApplication.getDeviceSpecificSharedPrefs(deviceAddress);
        boolean enabled = prefs.getBoolean(DeviceSettingsPreferenceConst.PREF_INACTIVITY_ENABLE, false);

        if (!enabled) {
            LOG.info(" Long sit reminder is disabled ");
            return false;
        } else {
            String start = prefs.getString(DeviceSettingsPreferenceConst.PREF_INACTIVITY_START, "06:00");
            String end = prefs.getString(DeviceSettingsPreferenceConst.PREF_INACTIVITY_END, "23:00");

            DateFormat df = new SimpleDateFormat("HH:mm");

            try {
                startOut.setTime(df.parse(start));
                endOut.setTime(df.parse(end));

                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    @Nullable
    @Override
    public Class<? extends Activity> getCalibrationActivity() {
        return LenovoWatchCalibrationActivity.class;
    }

    @Override
    public int getDeviceNameResource() {
        return R.string.devicetype_watchxplus;
    }

    @Override
    public int getDefaultIconResource() {
        return R.drawable.ic_device_watchxplus;
    }

    @Override
    public int getDisabledIconResource() {
        return R.drawable.ic_device_watchxplus_disabled;
    }
}
