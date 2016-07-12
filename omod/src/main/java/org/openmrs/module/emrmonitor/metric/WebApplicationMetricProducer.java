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

import org.springframework.stereotype.Component;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Produces metrics related to the running web application
 */
@Component
public class WebApplicationMetricProducer extends HttpServlet implements MetricProducer {

    private static Map<String, String> metrics = null;

    @Override
    public String getNamespace() {
        return "webapp";
    }

    /**
     * @return true if this metric producer should run in the given environment (eg. based on O/S, modules running, etc)
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (metrics == null) {
            metrics = new LinkedHashMap<String, String>();
            metrics.put("serverInfo", config.getServletContext().getServerInfo());
        }
    }

    /**
     * @return a list of produced metrics
     */
    @Override
    public Map<String, String> produceMetrics() {
        return metrics;
    }
}
