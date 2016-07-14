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

        // Networks
        List<String> networks = new ArrayList<String>();
        for (NetworkIF networkIF : hal.getNetworkIFs()) {
            String networkName = networkIF.getName();
            networks.add(networkName);
            metrics.put("network." + networkName + ".name", networkIF.getName());
            metrics.put("network." + networkName + ".ipAddress", join(networkIF.getIPv4addr(), ","));
        }
        metrics.put("network.list", OpenmrsUtil.join(networks, ","));
        try {
            metrics.put("network.hostname", InetAddress.getLocalHost().getCanonicalHostName());
        }
        catch (Exception e) {
            metrics.put("network.hostname", "unknown");
        }

        // Disks
        int numFileStores = hal.getFileStores().length;
        metrics.put("filestore.numPresent", Integer.toString(numFileStores));
        for (int i=0; i<numFileStores; i++) {
            OSFileStore fs = hal.getFileStores()[i];
            String prefix = "disk." + i + ".";
            metrics.put(prefix + "name", fs.getName());
            metrics.put(prefix + "volume", fs.getVolume());
            metrics.put(prefix + "description", fs.getDescription());
            metrics.put(prefix + "mount", fs.getMount());
            metrics.put(prefix + "type", fs.getType());
            metrics.put(prefix + "totalSpace", FormatUtil.formatBytes(fs.getTotalSpace()));
            metrics.put(prefix + "totalSpace.bytes", Long.toString(fs.getTotalSpace()));
            metrics.put(prefix + "usableSpace", FormatUtil.formatBytes(fs.getUsableSpace()));
            metrics.put(prefix + "usableSpace.bytes", Long.toString(fs.getUsableSpace()));
        }

        return metrics;
    }

    private String join(String[] arr, String separator) {
        StringBuilder sb = new StringBuilder();
        if (arr != null) {
            for (String s : arr) {
                sb.append(sb.length() == 0 ? "" : separator).append(s);
            }
        }
        return sb.toString();
    }
}
