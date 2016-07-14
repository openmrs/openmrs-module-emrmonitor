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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Produces core metrics around server information that is not specific to OpenMRS usage
 */
@Component
public class OpenmrsDataMetricProducer implements MetricProducer {

    protected final Log log = LogFactory.getLog(this.getClass());

    @Override
    public String getNamespace() {
        return "openmrs.data";
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

        // TODO: Check if this is overridden by an implementation-configured file of queries?

        Map<String, String> queries = new LinkedHashMap<String, String>();
        queries.put("patients.total", "select count(*) from patient where voided = 0");
        queries.put("visits.total", "select count(*) from visit where voided = 0");
        queries.put("encounters.total", "select count(*) from encounter where voided = 0");
        queries.put("obs.total", "select count(*) from obs where voided = 0");
        queries.put("orders.total", "select count(*) from orders where voided = 0");
        queries.put("users.total", "select count(*) from users where retired = 0");

        // TODO: Consider adding other data - broken down with more granularity (active patients, obs by concept (eg. lab results), encounters by type, visits by type)

        for (Map.Entry<String, String> e : queries.entrySet()) {
            Number num = executeQuery(e.getValue(), Number.class);
            metrics.put(e.getKey(), num.toString());
        }

        return metrics;
    }

    private <T> T executeQuery(String query, Class<T> type) {
        return Context.getService(EmrMonitorService.class).executeSingleValueQuery(query, type);
    }
}
