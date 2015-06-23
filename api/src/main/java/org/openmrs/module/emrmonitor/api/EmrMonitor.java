/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.emrmonitor.api;

import java.io.Serializable;
import java.util.Map;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.api.context.Context;

/**
 * It is a model class. It should extend either {@link BaseOpenmrsObject} or {@link BaseOpenmrsMetadata}.
 */
public class EmrMonitor extends BaseOpenmrsObject implements Serializable {

	private static final long serialVersionUID = 1L;

    private Map<String, Map<String, String>> systemInformation = null;

    private Integer id;
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}


    public Map<String, Map<String, String>> getSystemInformation() {
        Map<String, Map<String, String>> systemInformation = Context.getAdministrationService().getSystemInformation();

        return systemInformation;
    }

    public void setSystemInformation(Map<String, Map<String, String>> systemInformation) {
        this.systemInformation = systemInformation;
    }
}