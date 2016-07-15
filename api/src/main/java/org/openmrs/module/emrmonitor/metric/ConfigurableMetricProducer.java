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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Produces core metrics around server information that is not specific to OpenMRS usage
 */
@Component
public class ConfigurableMetricProducer implements MetricProducer {

    protected final Log log = LogFactory.getLog(this.getClass());

    @Override
    public String getNamespace() {
        return "";
    }

    /**
     * @return true if this metric producer should run in the given environment (eg. based on O/S, modules running, etc)
     */
    @Override
    public boolean isEnabled() {
        return getConfigurationFiles().length > 0;
    }

    /**
     * @return a list of produced metrics
     */
    @Override
    public Map<String, String> produceMetrics() {

        Map<String, String> metrics = new LinkedHashMap<String, String>();

        for (File f : getConfigurationFiles()) {
            String fileName = f.getName();
            try {
                log.debug("Producing metrics for configured file: " + fileName);
                int extensionIndex = fileName.lastIndexOf(".");
                if (extensionIndex < 0) {
                    throw new IllegalArgumentException("Invalid configuration file - no extension defined for file " + fileName);
                }
                String namespace = fileName.substring(0, extensionIndex);
                if (fileName.endsWith(".sql")) {
                    handleSqlFile(metrics, namespace, FileUtils.readFileToString(f, "UTF-8"));
                }
                else if (fileName.endsWith(".sh")) {
                    handleShellScript(metrics, namespace, f);
                }
                else {
                    throw new IllegalArgumentException("Metric configuration files of this type (" + fileName + ") are not supported ");
                }
            }
            catch (Exception e) {
                log.warn("Error generating metrics from configuration at " + f.getName(), e);
            }
        }

        return metrics;
    }

    /**
     * If a single value is returned by the query, then the metric is the filename and the value is the result
     * If multiple rows with a single column is returned, then the value is a comma separated list
     * If multiple columns are returned, then all but the last column are appended to the metric name, and the last column is the value
     */
    protected void handleSqlFile(Map<String, String> metrics, String filename, String sqlToExecute) {
        List<Object[]> data = getEmrMonitorService().executeQuery(sqlToExecute);
        int numRows = data.size();
        if (numRows > 0) {
            int numColumns = data.get(0).length;
            if (numColumns > 0) {
                if (numColumns == 1) {
                    metrics.put(filename, toString(data));
                }
                else {
                    for (Object[] row : data) {
                        StringBuilder metric = new StringBuilder(filename);
                        for (int i=0; i<numColumns-1; i++) {
                            metric.append("." + row[i]);
                        }
                        metrics.put(metric.toString(), toString(row[numColumns-1]));
                    }
                }
            }
        }
    }

    /**
     * If multiple lines of output are returned and each is in the format of key=value, then the key will be considered part of the metric, and the value the value
     * Otherwise, the full contents of output will be the value of a single metric
     */
    protected void handleShellScript(Map<String, String> metrics, String namespace, File f) throws IOException {
        Process process = Runtime.getRuntime().exec(f.getAbsolutePath());
        StringBuilder singleValueMetric = new StringBuilder();
        Map<String, String> keyValueMetrics = new LinkedHashMap<String, String>();
        LineIterator successIterator = null;
        try {
            successIterator = IOUtils.lineIterator(process.getInputStream(), "UTF-8");
            while (successIterator.hasNext()) {
                String line = successIterator.nextLine();
                String[] split = StringUtils.split(line, "=", 1);
                if (split.length == 2) {
                    keyValueMetrics.put(split[0], split[1]);
                }
                else {
                    singleValueMetric.append(line).append(System.getProperty("line.separator"));
                }
            }
            if (singleValueMetric.length() > 0) {
                metrics.put(namespace, singleValueMetric.toString());
            }
            else {
                metrics.putAll(keyValueMetrics);
            }
        }
        finally {
            successIterator.close();
        }

        StringBuilder error = new StringBuilder();
        LineIterator errorIterator = null;
        try {
            errorIterator = IOUtils.lineIterator(process.getErrorStream(), "UTF-8");
            while (errorIterator.hasNext()) {
                String line = errorIterator.nextLine();
                error.append(System.getProperty("line.separator")).append(line);
            }
        }
        finally {
            errorIterator.close();
        }

        if (error.length() > 0) {
            throw new RuntimeException("An error occurred while executing shell script " + f.getName() + ": " + error);
        }
    }

    /**
     * @return a null-safe String value for the given object
     */
    protected String toString(Object o) {
        String ret = "";
        if (o != null) {
            if (o instanceof Object[]) {
                for (Object val : ((Object[]) o)) {
                    ret += (ret.length() > 0 ? "," : "") + toString(val);
                }
            }
            else if (o instanceof Collection) {
                for (Object val : ((Collection) o)) {
                    ret += (ret.length() > 0 ? "," : "") + toString(val);
                }
            }
            else {
                ret = o.toString();
            }
        }
        return ret;
    }

    /**
     * @return the configuration directory for any user-defined metrics
     */
    public static File getConfigurationDirectory() {
        File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory("configuration" + File.separator + "emrmonitor" + File.separator + "metrics");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * @return the configuration files for any user-defined metrics
     */
    protected File[] getConfigurationFiles() {
        return getConfigurationDirectory().listFiles();
    }

    private EmrMonitorService getEmrMonitorService() {
        return Context.getService(EmrMonitorService.class);
    }
}
