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

import org.openmrs.BaseOpenmrsObject;

import java.io.Serializable;

/**
 * Represents a particular server that is registered with a given name
 */
public class EmrMonitorServer extends BaseOpenmrsObject implements Serializable {

    private Integer id;
    private String name;
    private EmrMonitorServerType serverType;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EmrMonitorServerType getServerType() {
        return serverType;
    }

    public void setServerType(EmrMonitorServerType serverType) {
        this.serverType = serverType;
    }

    @Override
    public String toString() {
        return name;
    }
}
