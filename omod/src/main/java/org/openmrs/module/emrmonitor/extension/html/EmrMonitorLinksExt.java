package org.openmrs.module.emrmonitor.extension.html;

import org.openmrs.module.Extension;
import org.openmrs.module.emrmonitor.EmrMonitorConstants;

import java.util.HashMap;
import java.util.Map;

/**
 *  Adds the EmrMonitor configuration page to the maintenance menu
 */
public class EmrMonitorLinksExt extends Extension {

    @Override
    public MEDIA_TYPE getMediaType() {
        return MEDIA_TYPE.html;
    }

    public Map<String, String> getLinks() {
        Map<String, String> links = new HashMap<String, String>();
        links.put("/emrmonitor/configEmrMonitorServer.page", "emrmonitor.config");
        return links;
    }

    public String getRequiredPrivilege() {
        return EmrMonitorConstants.PRIV_MANAGE_EMR_MONITOR;
    }

}
