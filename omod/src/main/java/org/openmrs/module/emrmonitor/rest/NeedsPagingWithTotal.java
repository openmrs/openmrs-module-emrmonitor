/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.emrmonitor.rest;

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

/**
 * Overrides the default NeedsPaging implementation to include total number of results
 */
public class NeedsPagingWithTotal<T> extends NeedsPaging<T> {

    private int numUnpagedResults = 0;

	public NeedsPagingWithTotal(List<T> unpagedResults, RequestContext context) {
        super(unpagedResults, context);
        numUnpagedResults = unpagedResults.size();
	}

    @Override
    public SimpleObject toSimpleObject(Converter preferredConverter) throws ResponseException {
        SimpleObject ret = super.toSimpleObject(preferredConverter);
        ret.put("totalResultNum", numUnpagedResults);
        return ret;
    }
}
