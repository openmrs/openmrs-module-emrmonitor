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

/**
 * Constants used by the Emr Monitor module
 */
public class EmrMonitorConstants {

    // Privileges
    public static final String PRIV_MANAGE_EMR_MONITOR = "Manage EmrMonitor";

    // Constants
    public static final Integer REMOTE_SERVER_TIMEOUT = 10000;

    public static final String PARENT_URL_PROPERTY = "emrmonitor.parentUrl";
    public static final String PARENT_USERNAME_PROPERTY = "emrmonitor.parentUsername";
    public static final String PARENT_PASSWORD_PROPERTY = "emrmonitor.parentPassword";

    /**
     * @return true if a parent server is configured
     */
    public static boolean isParentServerConfigured() {
        return StringUtils.isNotBlank(getRuntimeProperty(EmrMonitorConstants.PARENT_URL_PROPERTY));
    }

    public static String getRuntimeProperty(String name) {
        return Context.getRuntimeProperties().getProperty(name);
    }
}
