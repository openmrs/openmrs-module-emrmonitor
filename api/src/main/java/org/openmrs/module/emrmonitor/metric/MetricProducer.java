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

import java.util.Map;

/**
 * Interface for a class that can generate metrics for monitoring
 */
public interface MetricProducer {

    /**
     * @return a unique namespace for these metrics
     */
    String getNamespace();

    /**
     * @return true if this metric producer should run in the given environment (eg. based on O/S, modules running, etc)
     */
    boolean isEnabled();

    /**
     * @return a list of produced metrics
     */
    Map<String, String> produceMetrics();
}
