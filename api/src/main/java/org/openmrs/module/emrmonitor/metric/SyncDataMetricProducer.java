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
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Produces core metrics around server information that is not specific to OpenMRS usage
 */
@Component
public class SyncDataMetricProducer implements MetricProducer {

    protected final Log log = LogFactory.getLog(this.getClass());

    @Override
    public String getNamespace() {
        return "openmrs.sync";
    }

    /**
     * @return true if this metric producer should run in the given environment (eg. based on O/S, modules running, etc)
     */
    @Override
    public boolean isEnabled() {
        return ModuleFactory.isModuleStarted("sync");
    }

    /**
     * @return a list of produced metrics
     */
    @Override
    public Map<String, String> produceMetrics() {

        Map<String, String> metrics = new LinkedHashMap<String, String>();

        Map<String, String> queries = new LinkedHashMap<String, String>();
        queries.put("pendingRecords", "select count(*) from sync_record where state!= 'COMMITTED' and state!= 'NOT_SUPPOSED_TO_SYNC' and uuid = original_uuid");
        queries.put("failedRecords", "select count(*) from sync_record where state in ('FAILED', 'FAILED_AND_STOPPED') and uuid = original_uuid");
        queries.put("failedObjects", "select contained_classes from sync_record where state = 'FAILED' and uuid = original_uuid limit 1");
        queries.put("rejectedObjects", "select contained_classes from sync_record where state = 'REJECTED' and uuid = original_uuid limit 1");

        for (Map.Entry<String, String> e : queries.entrySet()) {
            Object val = executeQuery(e.getValue(), Object.class);
            metrics.put(e.getKey(), val == null ? "" : val.toString());
        }

        return metrics;
    }

    private <T> T executeQuery(String query, Class<T> type) {
        return Context.getService(EmrMonitorService.class).executeSingleValueQuery(query, type);
    }
}
