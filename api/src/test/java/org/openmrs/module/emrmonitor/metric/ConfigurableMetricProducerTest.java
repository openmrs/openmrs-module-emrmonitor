/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.emrmonitor.metric;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsClassLoader;
import oshi.SystemInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Tests ServerInfoMetricProducer
 */
public class ConfigurableMetricProducerTest extends BaseModuleContextSensitiveTest {protected final Log log = LogFactory.getLog(this.getClass());
	
	@Test
	public void shouldProduceSingleMetricFromSql() throws Exception {
	    copyResourceToConfigurationFile("openmrs.cd4s.total.sql");

		ConfigurableMetricProducer producer = new ConfigurableMetricProducer();
        Map<String, String> metrics = producer.produceMetrics();

        Assert.assertEquals(1, metrics.size());

        List<String> keys = new ArrayList<String>(metrics.keySet());
        List<String> values = new ArrayList<String>(metrics.values());

        Assert.assertEquals("openmrs.cd4s.total", keys.get(0));
        Assert.assertEquals("2", values.get(0));
	}

    @Test
    public void shouldProduceMultipleMetricsFromTwoColTwoRowSql() throws Exception {
        copyResourceToConfigurationFile("openmrs.encounters.sql");

        ConfigurableMetricProducer producer = new ConfigurableMetricProducer();
        Map<String, String> metrics = producer.produceMetrics();

        Assert.assertEquals(2, metrics.size());

        List<String> keys = new ArrayList<String>(metrics.keySet());
        List<String> values = new ArrayList<String>(metrics.values());

        Assert.assertEquals("openmrs.encounters.Emergency", keys.get(0));
        Assert.assertEquals("1", values.get(0));

        Assert.assertEquals("openmrs.encounters.Scheduled", keys.get(1));
        Assert.assertEquals("2", values.get(1));
    }

    @Test
    public void shouldProduceSingleMetricFromOneColTwoRowSql() throws Exception {
        copyResourceToConfigurationFile("openmrs.definedLabTests.sql");

        ConfigurableMetricProducer producer = new ConfigurableMetricProducer();
        Map<String, String> metrics = producer.produceMetrics();

        Assert.assertEquals(1, metrics.size());

        List<String> keys = new ArrayList<String>(metrics.keySet());
        List<String> values = new ArrayList<String>(metrics.values());

        Assert.assertEquals("openmrs.definedLabTests", keys.get(0));
        Assert.assertEquals("CD4,WT", values.get(0));
    }

    @Test
    public void shouldProduceMultipleMetricFromThreeColTwoRowSql() throws Exception {
        copyResourceToConfigurationFile("openmrs.monthlyEncounters.sql");

        ConfigurableMetricProducer producer = new ConfigurableMetricProducer();
        Map<String, String> metrics = producer.produceMetrics();

        Assert.assertEquals(3, metrics.size());

        List<String> keys = new ArrayList<String>(metrics.keySet());
        List<String> values = new ArrayList<String>(metrics.values());

        Assert.assertEquals("openmrs.monthlyEncounters.Emergency.20080801", keys.get(0));
        Assert.assertEquals("1", values.get(0));

        Assert.assertEquals("openmrs.monthlyEncounters.Scheduled.20080815", keys.get(1));
        Assert.assertEquals("1", values.get(1));

        Assert.assertEquals("openmrs.monthlyEncounters.Scheduled.20080819", keys.get(2));
        Assert.assertEquals("1", values.get(2));
    }

    @Test
    public void shouldProduceSingleMetricFromShellScript() throws Exception {
        if (isLinux()) {
            copyResourceToConfigurationFile("system.lscpu.sh");

            ConfigurableMetricProducer producer = new ConfigurableMetricProducer();

            Map<String, String> metrics = producer.produceMetrics();

            Assert.assertEquals(1, metrics.size());

            List<String> keys = new ArrayList<String>(metrics.keySet());
            List<String> values = new ArrayList<String>(metrics.values());

            Assert.assertEquals("system.lscpu", keys.get(0));
            Assert.assertTrue(values.get(0).contains("Architecture:"));
        }
    }

    @Test
    public void shouldProduceMultipleMetricsFromShellScript() throws Exception {
        if (isLinux()) {
            copyResourceToConfigurationFile("system.info.sh");

            ConfigurableMetricProducer producer = new ConfigurableMetricProducer();

            Map<String, String> metrics = producer.produceMetrics();

            Assert.assertEquals(2, metrics.size());

            List<String> keys = new ArrayList<String>(metrics.keySet());
            List<String> values = new ArrayList<String>(metrics.values());

            Assert.assertEquals("system.info.currentDate", keys.get(0));
            Assert.assertEquals(DateFormatUtils.format(new Date(), "yyyy-MM-dd"), values.get(0));

            Assert.assertEquals("system.info.myVar", keys.get(1));
            Assert.assertEquals("myVal", values.get(1));
        }
    }

    protected boolean isLinux() {
        SystemInfo si = new SystemInfo();
        return "GNU/Linux".equals(si.getOperatingSystem().getManufacturer());
    }

    protected void copyResourceToConfigurationFile(String filename) throws Exception {
        InputStream in = null;
        OutputStream out = null;
        File outFile = new File(ConfigurableMetricProducer.getConfigurationDirectory(), filename);
        try {
            in = OpenmrsClassLoader.getInstance().getResourceAsStream("org/openmrs/module/emrmonitor/" + filename);
            out = new FileOutputStream(outFile);
            IOUtils.copy(in, out);
        }
        finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }
        outFile.setExecutable(true);
    }
}
