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
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * Produces core metrics around server information that is not specific to OpenMRS usage
 */
@Component
public class OpenmrsInstallMetricProducer implements MetricProducer {

    protected final Log log = LogFactory.getLog(this.getClass());

    @Override
    public String getNamespace() {
        return "openmrs.install";
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

        // Versions
        metrics.put("core.version", String.valueOf(OpenmrsConstants.OPENMRS_VERSION));

        Set<String> modules = new TreeSet<String>();
        for (Module module : ModuleFactory.getLoadedModules()) {
            modules.add(module.getModuleId());
        }
        metrics.put("modules.list", OpenmrsUtil.join(modules, ","));
        for (String moduleId : modules) {
            Module module = ModuleFactory.getModuleById(moduleId);
            metrics.put("module."+moduleId+".started", Boolean.toString(module.isStarted()));
            metrics.put("module."+moduleId+".version", module.getVersion());
        }

        // Database
        Properties rp = Context.getRuntimeProperties();
        metrics.put("database.name", OpenmrsConstants.DATABASE_NAME);
        metrics.put("database.url", rp.getProperty("connection.url"));


        /*
        Server Id

        openmrs data:
        num users, new patients, active patients, visits, encounters, observations, viral load results (all, last 6 months, last year), cd4 results, primary clinic days, hours

        Number Of System Starts
        OpenMRS Uptime (%)
        System Uptime - LastMonth (%)
        System Uptime - LastWeek (%)
        System Uptime - ThisWeek (%)

         */

        return metrics;
    }
}
