/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.emrmonitor.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.api.EmrMonitorService;
import org.openmrs.module.sync.SyncClass;
import org.openmrs.module.sync.SyncServerClass;
import org.openmrs.module.sync.api.SyncService;
import org.openmrs.module.sync.server.RemoteServer;

import java.util.HashSet;

/**
 * Provides functionality to disable sync module from syncing emrmonitor objects
 */
public class SyncUtil {

    protected static Log log = LogFactory.getLog(SyncUtil.class);

    public static final String EMRMONITOR_SYNC_CLASS = "org.openmrs.module.emrmonitor";

    public static void disableSyncForEmrMonitor() {

        SyncService syncService = Context.getService(SyncService.class);
        EmrMonitorService emrMonitorService = Context.getService(EmrMonitorService.class);

        // First we need to ensure there is a sync class representing the emrmonitor package of domain objects
        SyncClass sc = syncService.getSyncClassByName(EMRMONITOR_SYNC_CLASS);
        if (sc == null) {
            sc = new SyncClass();
            sc.setName(EMRMONITOR_SYNC_CLASS);
            sc.setDefaultReceiveFrom(false);
            sc.setDefaultSendTo(false);
            syncService.saveSyncClass(sc);
        }

        // Next, we need to iterate across all configured remote servers and ensure they are configured with this class
        for (RemoteServer syncServer : syncService.getRemoteServers()) {
            if (syncServer.getServerClasses() == null) {
                syncServer.setServerClasses(new HashSet<SyncServerClass>());
            }
            // Unfortunately SyncServerClass and SyncClass do not have equals or hashcode methods, so we need to do this
            boolean found = false;
            for (SyncServerClass ssc : syncServer.getServerClasses()) {
                if (EMRMONITOR_SYNC_CLASS.equals(ssc.getSyncClass().getName())) {
                    found = true;
                }
            }
            if (!found) {
                syncServer.getServerClasses().add(new SyncServerClass(syncServer, sc));
                emrMonitorService.saveObject(syncServer); // This is needed since the save method was not introduced into sync until 1.3
            }
        }

        syncService.saveSyncClass(sc); // We do this again here because in 1.3 this refreshes a static cache that we need to refresh
    }
}
