/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.emrmonitor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.util.OpenmrsUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Encapsulates writing to and reading from / analyzing uptime logs
 */
public class UptimeLog {

    private static final Log log = LogFactory.getLog(UptimeLog.class);

    public static final String DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss";
    public static final String LOG_ENTRY_SEPARATOR = ",";
    public static final String LOG_STARTED_TOKEN = "S";
    public static final String LOG_ENTRY_TOKEN = "E";
    public static final String LOG_COMPLETED_TOKEN = "C";

    /**
     * Initializes a new log file if one does not exist
     */
    public static void initializeLogFile(Date date) {
        File logFile = getLogFile();
        if (!logFile.exists()) {
            writeToLog(date, LOG_STARTED_TOKEN);
        }
    }

    /**
     * Rotates the current uptime.log file to a new file with the current datetime
     */
    public static synchronized File rotate() {
        Date now = new Date();
        String filename = DateFormatUtils.format(now, DATE_FORMAT) + ".log";
        File destinationFile = new File(getLogDirectory(), filename);
        try {
            writeToLog(now, LOG_COMPLETED_TOKEN);
            FileUtils.moveFile(getLogFile(), destinationFile);
            initializeLogFile(now);
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to rotate log file to destination", e);
        }
        return destinationFile;
    }

    /**
     * @return a current line in the log file into a String[]. Returns null if the line is blank
     */
    public static String[] parseLine(String line) {
        if (StringUtils.isNotBlank(line)) {
            return StringUtils.splitByWholeSeparatorPreserveAllTokens(line, LOG_ENTRY_SEPARATOR);
        }
        return null;
    }

    /**
     * Writes and entry to the log file
     */
    public static synchronized void writeToLog(Date date, String entryType, String... entries) {
        StringBuilder line = new StringBuilder();
        line.append(date.getTime()); // Time in millis of this entry
        line.append(LOG_ENTRY_SEPARATOR);
        line.append(entryType); // Token indicating the type of log entry
        for (String entry : entries) {
            line.append(LOG_ENTRY_SEPARATOR);
            line.append(entry);
        }
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriterWithEncoding(getLogFile(), "UTF-8", true)));
            out.println(line.toString());
        }
        catch (Exception e) {
            log.warn("Error writing to uptime log", e);
        }
        finally {
            IOUtils.closeQuietly(out);
        }
    }

    // Methods for getting the log files

    public static synchronized File getLogFile() {
        return new File(getLogDirectory(), "uptime.log");
    }

    public static File getLogDirectory() {
        File baseDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory("emrmonitor");
        File dir = new File(baseDir, "logs");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
