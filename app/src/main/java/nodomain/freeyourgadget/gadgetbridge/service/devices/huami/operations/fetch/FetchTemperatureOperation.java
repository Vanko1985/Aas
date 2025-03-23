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
package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations.fetch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.GregorianCalendar;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;
import nodomain.freeyourgadget.gadgetbridge.util.GB;

/**
 * An operation that fetches temperature data.
 */
public class FetchTemperatureOperation extends AbstractRepeatingFetchOperation {
    private static final Logger LOG = LoggerFactory.getLogger(FetchTemperatureOperation.class);

    public FetchTemperatureOperation(final HuamiSupport support) {
        super(support, HuamiFetchDataType.TEMPERATURE);
    }

    @Override
    protected String taskDescription() {
        return getContext().getString(R.string.busy_task_fetch_temperature);
    }

    @Override
    protected boolean handleActivityData(final GregorianCalendar timestamp, final byte[] bytes) {
        if (bytes.length % 8 != 0) {
            LOG.info("Unexpected buffered temperature data size {} is not a multiple of 8", bytes.length);
            return false;
        }

        final ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);

        while (buffer.position() < bytes.length) {
            final int temp1 = buffer.getShort();
            final int temp2 = buffer.getShort();
            final int temp3 = buffer.getShort();
            final int temp4 = buffer.getShort();

            // TODO persist / parse rest
            LOG.warn("temp1: {}, temp2={}, temp3: {}, temp4={}", temp1, temp2, temp3, temp4);
        }

        return false;
    }

    @Override
    protected String getLastSyncTimeKey() {
        return "lastTemperatureTimeMillis";
    }
}
