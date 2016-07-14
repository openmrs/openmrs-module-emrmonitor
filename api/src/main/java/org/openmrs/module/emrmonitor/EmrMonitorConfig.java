/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.emrmonitor;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration methods used by the Emr Monitor module
 */
public class EmrMonitorConfig {

    // Privileges
    public static final String PRIV_MANAGE_EMR_MONITOR = "Manage EmrMonitor";

    // Constants
    public static final Integer REMOTE_SERVER_TIMEOUT = 10000;

    // Runtime Properties
    public static final String PARENT_URL_PROPERTY = "emrmonitor.parentUrl";
    public static final String PARENT_USERNAME_PROPERTY = "emrmonitor.parentUsername";
    public static final String PARENT_PASSWORD_PROPERTY = "emrmonitor.parentPassword";

    /**
     * @return true if a parent server is configured
     */
    public static boolean isParentServerConfigured() {
        return StringUtils.isNotBlank(getRuntimeProperty(EmrMonitorConfig.PARENT_URL_PROPERTY));
    }

    public static String getRuntimeProperty(String name) {
        return Context.getRuntimeProperties().getProperty(name);
    }

    // Global Properties
    public static final String GP_DISABLED_METRIC_PRODUCERS = "emrmonitor.disabledMetricProducers";

    public static List<String> getDisabledMetricProducers() {
        List<String> ret = new ArrayList<String>();
        String val = Context.getAdministrationService().getGlobalProperty(GP_DISABLED_METRIC_PRODUCERS);
        if (StringUtils.isNotBlank(val)) {
            for (String s : StringUtils.splitByWholeSeparator(val, ",")) {
                ret.add(s.trim());
            }
        }
        return ret;
    }
}
