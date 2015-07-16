package org.openmrs.module.emrmonitor.api;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;

public class EmrMonitorProperties  {
    public static final String EMR_MONITOR_DIRECTORY = "emrMonitor";

    public static final Integer REMOTE_SERVER_TIMEOUT = 10000;

    protected static final Log log = LogFactory.getLog(EmrMonitorProperties.class);

    public static File getEmrMonitorDirectory(){
        File appDataDirectory = new File(OpenmrsUtil.getApplicationDataDirectory());
        String emrMonitorDirectoryPath = appDataDirectory.getAbsolutePath() + File.separatorChar + EMR_MONITOR_DIRECTORY;
        File emrMonitorDirectory = new File(emrMonitorDirectoryPath);
        if ( !emrMonitorDirectory.exists()) {
            if (emrMonitorDirectory.mkdir()) {
                log.debug("directory created: " + emrMonitorDirectoryPath );
            } else {
                log.error("failed to create directory: " + emrMonitorDirectoryPath);
            }
        }

        return emrMonitorDirectory;
    }
}
