package org.openmrs.module.emrmonitor.rest.controller;

import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + EmrMonitorRestController.EMRMONITOR_REST_NAMESPACE)
public class EmrMonitorRestController extends MainResourceController {

    public static final String EMRMONITOR_REST_NAMESPACE = "/emrmonitor";

    @Override
    public String getNamespace() {
        return RestConstants.VERSION_1 + EMRMONITOR_REST_NAMESPACE;
    }
}
