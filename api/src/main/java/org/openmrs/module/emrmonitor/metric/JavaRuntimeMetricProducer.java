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

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.stereotype.Component;
import oshi.util.FormatUtil;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Produces core metrics around server information that is not specific to OpenMRS usage
 */
@Component
public class JavaRuntimeMetricProducer implements MetricProducer {

    @Override
    public String getNamespace() {
        return "java";
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

        // Memory
        Runtime runtime = Runtime.getRuntime();
        metrics.put("memory.total", FormatUtil.formatBytes(runtime.totalMemory()));
        metrics.put("memory.total.bytes", Long.toString(runtime.totalMemory()));
        metrics.put("memory.free", FormatUtil.formatBytes(runtime.freeMemory()));
        metrics.put("memory.free.bytes", Long.toString(runtime.freeMemory()));
        metrics.put("memory.maximum", FormatUtil.formatBytes(runtime.maxMemory()));
        metrics.put("memory.maximum.bytes", Long.toString(runtime.maxMemory()));

        // Date/time
        Calendar cal = Calendar.getInstance();
        metrics.put("datetime.display", DateFormat.getDateTimeInstance().format(cal.getTime()));
        metrics.put("datetime.date", DateFormatUtils.ISO_DATE_FORMAT.format(cal));
        metrics.put("datetime.time", DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(cal));
        metrics.put("datetime.timezone", cal.getTimeZone().getDisplayName());

        // Java
        Properties sp = System.getProperties();
        metrics.put("version", sp.getProperty("java.version"));
        metrics.put("vendor", sp.getProperty("java.vendor"));
        metrics.put("jvmVersion", sp.getProperty("java.vm.version"));
        metrics.put("jvmVendor", sp.getProperty("java.vm.vendor"));
        metrics.put("runtimeName", sp.getProperty("java.runtime.name"));
        metrics.put("runtimeVersion", sp.getProperty("java.runtime.version"));
        metrics.put("user.name", sp.getProperty("user.name"));
        metrics.put("user.language", sp.getProperty("user.language"));
        metrics.put("user.timezone", sp.getProperty("user.timezone"));
        metrics.put("user.directory", sp.getProperty("user.dir"));
        metrics.put("encoding", sp.getProperty("sun.jnu.encoding"));
        metrics.put("tempDirectory", sp.getProperty("java.io.tmpdir"));

        return metrics;
    }
}
