package org.openmrs.module.emrmonitor.extension.html;

import org.openmrs.module.emrmonitor.EmrMonitorConstants;
import org.openmrs.module.web.extension.AdministrationSectionExt;

import java.util.HashMap;
import java.util.Map;

/**
 *  Adds the EmrMonitor configuration page to the maintenance menu
 */
public class AdminPageMaintenanceLinksExt extends AdministrationSectionExt {

    @Override
    public String getTitle() {
        return "EMR Monitor";
    }

    @Override
    public Map<String, String> getLinks() {
        Map<String, String> links = new HashMap<String, String>();
        links.put("emrmonitor/index.page", "emrmonitor.title");
        return links;
    }

    @Override
    public String getRequiredPrivilege() {
        return EmrMonitorConstants.PRIV_MANAGE_EMR_MONITOR;
    }

}
