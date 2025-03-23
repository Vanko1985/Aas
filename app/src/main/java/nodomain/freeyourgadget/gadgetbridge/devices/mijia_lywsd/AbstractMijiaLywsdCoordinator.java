/*  Copyright (C) 2023-2024 José Rebelo

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
package nodomain.freeyourgadget.gadgetbridge.devices.mijia_lywsd;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.activities.devicesettings.DeviceSpecificSettingsCustomizer;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractBLEDeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.devices.TimeSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.TemperatureSample;
import nodomain.freeyourgadget.gadgetbridge.service.DeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.mijia_lywsd.MijiaLywsdSupport;

public abstract class AbstractMijiaLywsdCoordinator extends AbstractBLEDeviceCoordinator {
    @Override
    public int getBondingStyle() {
        return BONDING_STYLE_NONE;
    }

    @Override
    public InstallHandler findInstallHandler(Uri uri, Context context) {
        return null;
    }

    @Override
    public boolean supportsActivityDataFetching() {
        return false;
    }

    @Override
    public String getManufacturer() {
        return "Xiaomi";
    }

    @Override
    public DeviceSpecificSettingsCustomizer getDeviceSpecificSettingsCustomizer(final GBDevice device) {
        return new MijiaLywsdSettingsCustomizer();
    }

    @NonNull
    @Override
    public Class<? extends DeviceSupport> getDeviceSupportClass() {
        return MijiaLywsdSupport.class;
    }

    @Override
    public int[] getSupportedDeviceSpecificSettings(GBDevice device) {
        return new int[]{
                R.xml.devicesettings_mijia_lywsd,
                R.xml.devicesettings_temperature_scale_cf,
        };
    }

    @Override
    protected void deleteDevice(@NonNull GBDevice gbDevice, @NonNull Device device, @NonNull DaoSession session) {
        // nothing to delete, yet
    }

    @Override
    public TimeSampleProvider<? extends TemperatureSample> getTemperatureSampleProvider(final GBDevice device, final DaoSession session) {
        return new MijiaLywsdRealtimeSampleProvider(device, session);
    }

    @Override
    public boolean supportsTemperatureMeasurement(final GBDevice device) {
        return true;
    }

    @Override
    public boolean supportsContinuousTemperature(final GBDevice device) {
        return true;
    }

    @Override
    public boolean supportsActivityTracking() {
        return false; // FIXME: Enable this once temperature fetching is enabled
    }

    @Override
    public boolean supportsSleepMeasurement() {
        return false;
    }

    @Override
    public boolean supportsStepCounter() {
        return false;
    }

    @Override
    public boolean supportsSpeedzones() {
        return false;
    }

    @Override
    public boolean supportsActivityTabs() {
        return false;
    }

    public abstract boolean supportsSetTime();
}
