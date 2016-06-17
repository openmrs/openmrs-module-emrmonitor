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
package org.openmrs.module.emrmonitor.api.db.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrmonitor.EmrMonitorReport;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.EmrMonitorServerType;
import org.openmrs.module.emrmonitor.api.db.EmrMonitorDAO;

import java.beans.Expression;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * It is a default implementation of  {@link EmrMonitorDAO}.
 */
public class HibernateEmrMonitorDAO implements EmrMonitorDAO {
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private SessionFactory sessionFactory;
	
	/**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
    
	/**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
	    return sessionFactory;
    }

    @Override
    public List<EmrMonitorServer> getAllEmrMonitorServers() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EmrMonitorServer.class);
        criteria.add(Restrictions.eq("retired", false));
        List<EmrMonitorServer> list = null;
        try {
            list = (List<EmrMonitorServer>) criteria.list();
        } catch (Exception e) {
            log.error("Failed to retrieve emr monitor servers", e);
        }
        return list;
    }

    @Override
    public EmrMonitorServer getEmrMonitorServerByUuid(String serverUuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EmrMonitorServer.class);
        criteria.add(Restrictions.eq("uuid", serverUuid));
        try {
            List<EmrMonitorServer> list =  (List<EmrMonitorServer>) criteria.list();
            if (list != null && list.size() > 0 ) {
                return (EmrMonitorServer) list.get(0);
            }
        } catch (Exception e) {
            log.error("Failed to retrieve emr monitor server record", e);
        }
        return null;
    }

    @Override
    public List<EmrMonitorServer> getEmrMonitorServerByType(EmrMonitorServerType serverType) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EmrMonitorServer.class);
        criteria.add(Restrictions.eq("retired", false));
        criteria.add(Restrictions.eq("serverType", serverType));
        try {
            List<EmrMonitorServer> list = (List<EmrMonitorServer>) criteria.list();
            if (list != null && list.size() > 0 ) {
                return list;
            }
        } catch (Exception e) {
            log.error("Failed to retrieve emr monitor servers", e);
        }
        return null;
    }

    @Override
    public EmrMonitorServer saveEmrMonitorServer(EmrMonitorServer server) {
        try{
            sessionFactory.getCurrentSession().saveOrUpdate(server);
        } catch (Exception e) {
            log.error("Error saving EmrMonitor Server", e);
        }
        return server;
    }

    @Override
    public void deleteEmrMonitorServer(EmrMonitorServer server) {
        sessionFactory.getCurrentSession().delete(server);
    }

    @Override
    public EmrMonitorReport saveEmrMonitorReport(EmrMonitorReport report) {
        try{
            sessionFactory.getCurrentSession().saveOrUpdate(report);
        } catch (Exception e) {
            log.error("Error saving EmrMonitorReport", e);
        }
        return report;
    }

    @Override
    public List<EmrMonitorReport> getEmrMonitorReportByServerAndStatus(EmrMonitorServer server, EmrMonitorReport.SubmissionStatus status) {

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EmrMonitorReport.class);
        criteria.add(Restrictions.eq("emrMonitorServer", server));
        criteria.add(Restrictions.eq("status", status));

        try {
            List<EmrMonitorReport> list = (List<EmrMonitorReport>)criteria.list();
            if (list != null && list.size() > 0) {
                return list;
            }
        } catch (Exception e) {
            log.error("failed to retrieve a list of reports", e);
        }

        return null;
    }

    @Override
	public Map<String, String> getOpenmrsData() {
		Map openmrsData	=new HashMap<String, Integer>();
		Session session=sessionFactory.getCurrentSession();
		
		String sql="SELECT patient_id FROM orders where voided=0";
	    SQLQuery query=session.createSQLQuery(sql);
	    int numOrders=query.list().size();
	    openmrsData.put("orders", ""+numOrders);
	    
	    String sql2="select patient_id from patient where voided=0";
	    SQLQuery query2=session.createSQLQuery(sql2);	    
	    int numPatients=query2.list().size();	    
	    openmrsData.put("patients", ""+numPatients);
	    
	    String sql3="select patient_id from encounter where voided=0";
	    SQLQuery query3=session.createSQLQuery(sql3);	    
	    int numEncounters=query3.list().size();	    
	    openmrsData.put("encounters", ""+numEncounters);
	    
	    String sql4="select person_id from obs where voided=0";
	    SQLQuery query4=session.createSQLQuery(sql4);	    
	    int numObs=query4.list().size();	    
	    openmrsData.put("observations", ""+numObs);
		
	    String sql5="select record_id from sync_record where state!='COMMITTED' and state!='NOT_SUPPOSED_TO_SYNC' and uuid=original_uuid";
	    SQLQuery query5=session.createSQLQuery(sql5);	    
	    int numPendingRecords=query5.list().size();	    
	    openmrsData.put("pendingRecords", ""+numPendingRecords);	    
		
	    String sql6="SELECT VERSION()";
	    SQLQuery query6=session.createSQLQuery(sql6);	    
	    String mysqlVersion=query6.list().get(0).toString();	    
	    openmrsData.put("mysqlVersion", ""+mysqlVersion);
	    
	    String sql7="select record_id from sync_record where state in ('FAILED','FAILED_AND_STOPPED') and uuid=original_uuid";
	    SQLQuery query7=session.createSQLQuery(sql7);	    
	    int numFailedRecords=query7.list().size();
	    if(numFailedRecords>0)
	    	openmrsData.put("failedRecord", "YES");
	    else
	    	openmrsData.put("failedRecord", "NO");
	    
	    
	    String sql8="select contained_classes from sync_record where state='FAILED' and uuid=original_uuid";
	    SQLQuery query8=session.createSQLQuery(sql8);	    
	    String objectFailedFull="";
	    if(query8.list().size()!=0){
	    	objectFailedFull=query8.list().get(0).toString();
	    }
	    openmrsData.put("failedObject", objectFailedFull);

	    String sql9="select contained_classes from sync_record where state='REJECTED' and uuid=original_uuid";
	    SQLQuery query9=session.createSQLQuery(sql9);	    
	    int rejectedObject=query9.list().size();
	    openmrsData.put("rejectedObject", ""+rejectedObject);
	    
	    
	    
		return openmrsData;	
		
	}
}
