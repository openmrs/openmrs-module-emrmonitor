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
import org.junit.Test;

import java.util.Map;

/**
 * Tests ServerInfoMetricProducer
 */
public class JavaRuntimeMetricProducerTest {

    protected final Log log = LogFactory.getLog(this.getClass());
	
	@Test
	public void shouldGetServerInfoMetrics() {
		JavaRuntimeMetricProducer producer = new JavaRuntimeMetricProducer();
        Map<String, String> metrics = producer.produceMetrics();

        if (log.isDebugEnabled()) {
            log.debug("Metrics:");
            for (String key : metrics.keySet()) {
                log.debug(key + ": " + metrics.get(key));
            }
        }
	}
}
