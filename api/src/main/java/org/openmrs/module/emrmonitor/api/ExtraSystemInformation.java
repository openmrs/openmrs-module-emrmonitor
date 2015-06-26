package org.openmrs.module.emrmonitor.api;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;

public class ExtraSystemInformation {

	private static final long serialVersionUID = 1L;
	
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private String totalPhyisycalMemory;

	private String freePhyisycalMemory;

	private String availablePhyisycalMemory;

	private String usedPhyisycalMemory;
	 
	private SessionFactory factory;
	
	public SessionFactory getFactory() {
		return factory;
	}

	public void setFactory(SessionFactory factory) {
		this.factory = factory;
	}


	File[] roots = File.listRoots();
    

	public String getTotalPhyisycalMemory() {
		
		for (File root : roots) {
			totalPhyisycalMemory=root.getTotalSpace()/(1024*1024*1024)+" GiB";
		}
		
		return totalPhyisycalMemory;
	}

	public void setTotalPhyisycalMemory(String totalPhyisycalMemory) {
		this.totalPhyisycalMemory = totalPhyisycalMemory;
	}

	public String getFreePhyisycalMemory() {
		
		for (File root : roots) {
			freePhyisycalMemory=root.getFreeSpace()/(1024*1024*1024)+" GiB";
		}
		
		return freePhyisycalMemory;
	}

	public void setFreePhyisycalMemory(String freePhyisycalMemory) {
		this.freePhyisycalMemory = freePhyisycalMemory;
	}

	public String getAvailablePhyisycalMemory() {
		
		for (File root : roots) {
			availablePhyisycalMemory=root.getUsableSpace()/(1024*1024*1024)+" GiB";
		}
		
		return availablePhyisycalMemory;
	}

	public void setAvailablePhyisycalMemory(String availablePhyisycalMemory) {
		this.availablePhyisycalMemory = availablePhyisycalMemory;
	}

	public String getUsedPhyisycalMemory() {
		
		for (File root : roots) {
			usedPhyisycalMemory=(root.getTotalSpace()-root.getFreeSpace())/(1024*1024*1024)+" GiB";
		}
		
		return usedPhyisycalMemory;
	}

	public void setUsedPhyisycalMemory(String usedPhyisycalMemory) {
		this.usedPhyisycalMemory = usedPhyisycalMemory;
	}
	
	public Map<String, Map<String, String>> getExtraSystemInformation(){
		
		Map<String, Map<String, String>> extraSystemInformation = new HashMap<String, Map<String,String>>();
		
		extraSystemInformation.put("SystemInfo.title.physicalMemoryInformation", new LinkedHashMap<String, String>() {
        	
        private static final long serialVersionUID = 1L;
			
			{
				put("SystemInfo.physicalMemory.totalMemory", getTotalPhyisycalMemory());
				put("SystemInfo.physicalMemory.freeMemory", getFreePhyisycalMemory());
				put("SystemInfo.physicalMemory.availableMemory", getAvailablePhyisycalMemory());
				put("SystemInfo.physicalMemory.usedMemory", getUsedPhyisycalMemory());				
			}
		});
        
        
		extraSystemInformation.put("SystemInfo.title.emrPatientsDataInformation", new LinkedHashMap<String, String>() {

            private static final long serialVersionUID = 1L;

            {
                
             	put("SystemInfo.emrDataInformation.numberOfOrders", ""+Context.getService(EmrMonitorService.class).getOpenmrsData().get("orders"));
             	put("SystemInfo.emrDataInformation.numberOfPatients", ""+Context.getService(EmrMonitorService.class).getOpenmrsData().get("patients"));
             	put("SystemInfo.emrDataInformation.numberOfEncounters", ""+Context.getService(EmrMonitorService.class).getOpenmrsData().get("encounters"));
             	put("SystemInfo.emrDataInformation.numberOfObservations", ""+Context.getService(EmrMonitorService.class).getOpenmrsData().get("observations"));
                
            }
        });
		
		
		extraSystemInformation.put("SystemInfo.title.emrSyncDataInformation", new LinkedHashMap<String, String>() {

            private static final long serialVersionUID = 1L;

            {
                
             	put("SystemInfo.emrSyncDataInformation.pendingRecords", ""+Context.getService(EmrMonitorService.class).getOpenmrsData().get("pendingRecords"));
             	      
            }
        });
		
		return extraSystemInformation;		
	}
	
}