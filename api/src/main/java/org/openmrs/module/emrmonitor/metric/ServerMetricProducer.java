/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.emrmonitor.metric;

import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Produces core metrics around server information that is not specific to OpenMRS usage
 */
@Component
public class ServerMetricProducer implements MetricProducer {

    @Override
    public String getNamespace() {
        return "system";
    }

    /**
     * @return true if this metric producer should run in the given environment (eg. based on O/S, modules running, etc)
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * @return a list of produced metrics
     */
    @Override
    public Map<String, String> produceMetrics() {

        Map<String, String> metrics = new LinkedHashMap<String, String>();

        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();

        // Memory
        GlobalMemory memory = hal.getMemory();
        metrics.put("memory.total", FormatUtil.formatBytes(memory.getTotal()));
        metrics.put("memory.total.bytes", Long.toString(memory.getTotal()));
        metrics.put("memory.available", FormatUtil.formatBytes(memory.getAvailable()));
        metrics.put("memory.available.bytes", Long.toString(memory.getAvailable()));
        metrics.put("memory.swapTotal", FormatUtil.formatBytes(memory.getSwapTotal()));
        metrics.put("memory.swapTotal.bytes", Long.toString(memory.getSwapTotal()));
        metrics.put("memory.swapUsed", FormatUtil.formatBytes(memory.getSwapUsed()));
        metrics.put("memory.swapUsed.bytes", Long.toString(memory.getSwapUsed()));

        // Operating System
        OperatingSystem operatingSystem = si.getOperatingSystem();
        metrics.put("os.family", operatingSystem.getFamily());
        metrics.put("os.manufacturer", operatingSystem.getManufacturer());
        metrics.put("os.version.version", operatingSystem.getVersion().getVersion());
        metrics.put("os.version.buildNumber", operatingSystem.getVersion().getBuildNumber());
        metrics.put("os.version.codeName", operatingSystem.getVersion().getCodeName());

        // CPU
        CentralProcessor processor = hal.getProcessor();
        metrics.put("cpu.systemLoadAverage", Double.toString(processor.getSystemLoadAverage()));
        metrics.put("cpu.systemUptime", FormatUtil.formatElapsedSecs(processor.getSystemUptime()));
        metrics.put("cpu.systemUptime.seconds", Double.toString(processor.getSystemUptime()));
        metrics.put("cpu.description", processor.getName());
        metrics.put("cpu.vendor", processor.getVendor());
        metrics.put("cpu.family", processor.getFamily());
        metrics.put("cpu.physicalCount", Integer.toString(processor.getPhysicalProcessorCount()));
        metrics.put("cpu.logicalCount", Integer.toString(processor.getLogicalProcessorCount()));
        // TODO: Use process information to get information on Tomcat, MySQL usage

        // Networks
        List<String> networks = new ArrayList<String>();
        for (NetworkIF networkIF : hal.getNetworkIFs()) {
            String networkName = networkIF.getName();
            networks.add(networkName);
            metrics.put("network." + networkName + ".name", networkIF.getName());
            metrics.put("network." + networkName + ".ipAddress", networkIF.getIPv4addr()[0]);
        }
        metrics.put("network.list", OpenmrsUtil.join(networks, ","));
        try {
            metrics.put("network.hostname", InetAddress.getLocalHost().getCanonicalHostName());
        }
        catch (Exception e) {
            metrics.put("network.hostname", "unknown");
        }

        // Disks
        long totalSpace = 0;
        long usableSpace = 0;
        for (OSFileStore fileStore : hal.getFileStores()) {
            totalSpace += fileStore.getTotalSpace();
            usableSpace += fileStore.getUsableSpace();
        }
        metrics.put("disk.totalSpace", FormatUtil.formatBytes(totalSpace));
        metrics.put("disk.totalSpace.bytes", Long.toString(totalSpace));
        metrics.put("disk.usableSpace", FormatUtil.formatBytes(usableSpace));
        metrics.put("disk.usableSpace.bytes", Long.toString(usableSpace));

        return metrics;
    }
}
