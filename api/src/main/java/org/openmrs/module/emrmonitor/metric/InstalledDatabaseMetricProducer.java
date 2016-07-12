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

import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Produces core metrics around server information that is not specific to OpenMRS usage
 */
@Component
public class InstalledDatabaseMetricProducer implements MetricProducer {

    @Override
    public String getNamespace() {
        return "database";
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

        // From runtime properties
        Properties rp = Context.getRuntimeProperties();
        metrics.put("databaseName", OpenmrsConstants.DATABASE_NAME);
        metrics.put("connectionUrl", rp.getProperty("connection.url"));

        // From connection / JDBC
        metrics.putAll(getEmrMonitorService().getDatabaseMetadata());

        return metrics;
    }

    private EmrMonitorService getEmrMonitorService() {
        return Context.getService(EmrMonitorService.class);
    }
}
