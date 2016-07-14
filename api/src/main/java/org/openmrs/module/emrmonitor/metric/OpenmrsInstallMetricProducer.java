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
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
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

        for (String moduleId : modules) {
            Module module = ModuleFactory.getModuleById(moduleId);
            metrics.put("module."+moduleId+".started", Boolean.toString(module.isStarted()));
            metrics.put("module."+moduleId+".version", module.getVersion());
        }

        // Settings
        metrics.put("implementationId", getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_IMPLEMENTATION_ID));

        return metrics;
    }

    private String getGlobalProperty(String property) {
        return Context.getAdministrationService().getGlobalProperty(property, "");
    }
}
