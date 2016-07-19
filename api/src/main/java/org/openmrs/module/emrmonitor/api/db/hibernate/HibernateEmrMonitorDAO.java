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
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.openmrs.module.emrmonitor.EmrMonitorReport;
import org.openmrs.module.emrmonitor.EmrMonitorServer;
import org.openmrs.module.emrmonitor.EmrMonitorServerType;
import org.openmrs.module.emrmonitor.api.db.EmrMonitorDAO;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
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
        criteria.addOrder(Order.desc("dateCreated"));
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
    public List<Object[]> executeQuery(final String query) {
        final List<Object[]> ret = new ArrayList<Object[]>();
        sessionFactory.getCurrentSession().doWork(new Work() {

            @Override
            public void execute(Connection connection) throws SQLException {
                PreparedStatement statement =  null;
                try {
                    statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet != null) {
                        ResultSetMetaData metaData = resultSet.getMetaData();
                        while (resultSet.next()) {
                            Object[] row = new Object[metaData.getColumnCount()];
                            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                                row[i - 1] = resultSet.getObject(i);
                            }
                            ret.add(row);
                        }
                    }
                }
                catch (Exception e) {
                    throw new IllegalArgumentException("Unable to execute query: " + query, e);
                }
                finally {
                    try {
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Exception e) {}
                }
            }
        });
        return ret;
    }

    @Override
    public <T> T executeSingleValueQuery(String query, Class<T> type) {
        List<Object[]> results = executeQuery(query);
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1) {
            throw new IllegalStateException("Expected single value for query [" + query + "] but got " + results.size() + " rows");
        }
        Object[] row = results.get(0);
        if (row.length != 1) {
            throw new IllegalStateException("Expected single value for query [" + query + "] but got " + row.length + " columns");
        }
        return (T)row[0];
    }

    @Override
    public <T> T saveObject(T obj) {
        sessionFactory.getCurrentSession().saveOrUpdate(obj);
        return obj;
    }
}
