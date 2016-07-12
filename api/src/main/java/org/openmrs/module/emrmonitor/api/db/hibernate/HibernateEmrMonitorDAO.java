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
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.emrmonitor.EmrMonitorReport;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.EmrMonitorServerType;
import org.openmrs.module.emrmonitor.api.db.EmrMonitorDAO;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    @Override
    public List<EmrMonitorServer> getAllEmrMonitorServers() {
        return sessionFactory.getCurrentSession().createCriteria(EmrMonitorServer.class).list();
    }

    @Override
    public EmrMonitorServer getEmrMonitorServerByUuid(String serverUuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EmrMonitorServer.class);
        criteria.add(Restrictions.eq("uuid", serverUuid));
        return (EmrMonitorServer)criteria.uniqueResult();
    }

    @Override
    public EmrMonitorServer getLocalServer() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EmrMonitorServer.class);
        criteria.add(Restrictions.eq("serverType", EmrMonitorServerType.LOCAL));
        return (EmrMonitorServer)criteria.uniqueResult();
    }

    @Override
    public List<EmrMonitorServer> getChildServers() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EmrMonitorServer.class);
        criteria.add(Restrictions.eq("serverType", EmrMonitorServerType.CHILD));
        return criteria.list();
    }

    @Override
    public EmrMonitorServer saveEmrMonitorServer(EmrMonitorServer server) {
        sessionFactory.getCurrentSession().saveOrUpdate(server);
        return server;
    }

    @Override
    public void deleteEmrMonitorServer(String uuid) {
        EmrMonitorServer server = getEmrMonitorServerByUuid(uuid);
        sessionFactory.getCurrentSession().delete(server);
    }

    @Override
    public EmrMonitorReport getEmrMonitorReportByUuid(String uuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EmrMonitorReport.class);
        criteria.add(Restrictions.eq("uuid", uuid));
        return (EmrMonitorReport)criteria.uniqueResult();
    }

    @Override
    public EmrMonitorReport saveEmrMonitorReport(EmrMonitorReport report) {
        sessionFactory.getCurrentSession().saveOrUpdate(report);
        return report;
    }

    @Override
    public void deleteEmrMonitorReport(EmrMonitorReport report) {
        sessionFactory.getCurrentSession().delete(report);
    }

    @Override
    public EmrMonitorReport getLatestEmrMonitorReport(EmrMonitorServer server) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EmrMonitorReport.class);
        criteria.add(Restrictions.eq("server", server));
        criteria.addOrder(Order.desc("dateCreated"));
        criteria.setFirstResult(0).setMaxResults(1);
        List<EmrMonitorReport> l = (List<EmrMonitorReport>)criteria.list();
        if (l != null && l.size() > 0) {
            return l.get(0);
        }
        return null;
    }

    @Override
    public List<EmrMonitorReport> getEmrMonitorReports(EmrMonitorServer server, EmrMonitorReport.SubmissionStatus... status) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EmrMonitorReport.class);
        criteria.add(Restrictions.eq("server", server));
        if (status != null && status.length > 0) {
            criteria.add(Restrictions.in("status", status));
        }
        return criteria.list();
    }

    @Override
    public Map<String, String> getDatabaseMetadata() {
        final Map<String, String> ret = new LinkedHashMap<String, String>();
        sessionFactory.getCurrentSession().doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                DatabaseMetaData md = connection.getMetaData();
                ret.put("product.name", md.getDatabaseProductName());
                ret.put("product.version", md.getDatabaseProductVersion());
                ret.put("product.majorVersion", Integer.toString(md.getDatabaseMajorVersion()));
                ret.put("product.minorVersion", Integer.toString(md.getDatabaseMinorVersion()));
            }
        });
        return ret;
    }

    @Override
	public Map<String, String> getOpenmrsData() {
		Map openmrsData	=new HashMap<String, Integer>();
		Session session=sessionFactory.getCurrentSession();
		
		String sql="SELECT count(*) FROM orders where voided=0";
	    SQLQuery query=session.createSQLQuery(sql);
	    BigInteger numOrders = (BigInteger) query.list().get(0);
	    openmrsData.put("orders", ""+numOrders);
	    
	    String sql2="select count(*) from patient where voided=0";
	    SQLQuery query2=session.createSQLQuery(sql2);
        BigInteger numPatients= (BigInteger) query2.list().get(0);
	    openmrsData.put("patients", ""+numPatients);
	    
	    String sql3="select count(*) from encounter where voided=0";
	    SQLQuery query3=session.createSQLQuery(sql3);
        BigInteger numEncounters= (BigInteger) query3.list().get(0);
        openmrsData.put("encounters", ""+numEncounters);
	    
	    String sql4="select count(*) from obs where voided=0";
	    SQLQuery query4=session.createSQLQuery(sql4);
        BigInteger numObs = (BigInteger) query4.list().get(0);
	    openmrsData.put("observations", ""+numObs);

        String sql6="SELECT VERSION()";
        SQLQuery query6=session.createSQLQuery(sql6);
        String mysqlVersion=query6.list().get(0).toString();
        openmrsData.put("mysqlVersion", ""+mysqlVersion);

        if (ModuleFactory.isModuleStarted("sync")) {

            String sql5 = "select count(*) from sync_record where state!='COMMITTED' and state!='NOT_SUPPOSED_TO_SYNC' and uuid=original_uuid";
            SQLQuery query5 = session.createSQLQuery(sql5);
            BigInteger numPendingRecords = (BigInteger) query5.list().get(0);
            openmrsData.put("pendingRecords", "" + numPendingRecords);

            String sql7 = "select count(*) from sync_record where state in ('FAILED','FAILED_AND_STOPPED') and uuid=original_uuid";
            SQLQuery query7 = session.createSQLQuery(sql7);
            BigInteger numFailedRecords = (BigInteger) query7.list().get(0);
            if (numFailedRecords.intValue() > 0) {
                openmrsData.put("failedRecord", "YES");
            }
            else {
                openmrsData.put("failedRecord", "NO");
            }

            String sql8 = "select contained_classes from sync_record where state='FAILED' and uuid=original_uuid";
            SQLQuery query8 = session.createSQLQuery(sql8);
            String objectFailedFull = "";
            if (query8.list().size() != 0) {
                objectFailedFull = query8.list().get(0).toString();
            }
            openmrsData.put("failedObject", objectFailedFull);

            String sql9 = "select contained_classes from sync_record where state='REJECTED' and uuid=original_uuid";
            SQLQuery query9 = session.createSQLQuery(sql9);
            int rejectedObject = query9.list().size();
            openmrsData.put("rejectedObject", "" + rejectedObject);

        }

		return openmrsData;	
		
	}
}
