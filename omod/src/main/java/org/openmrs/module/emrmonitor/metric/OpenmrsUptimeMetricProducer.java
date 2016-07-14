package org.openmrs.module.emrmonitor.metric;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.emrmonitor.UptimeLog;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class OpenmrsUptimeMetricProducer implements MetricProducer {

    private final Log log = LogFactory.getLog(this.getClass());

    @Override
    public String getNamespace() {
        return "openmrs.uptime";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, String> produceMetrics() {

        Map<String, String> metrics = new LinkedHashMap<String, String>();

        File logFileToAnalyze = UptimeLog.rotate();

        Long startMinute = null;
        Long endMinute = null;
        int totalLogEntries = 0;
        Long lastEntryMinute = null;
        int logEntryGaps = 0;
        long logEntryGapDuration = 0;
        Map<Integer, Integer> statusCounts = new HashMap<Integer, Integer>();

        LineIterator lineIterator = null;
        try {
            lineIterator = FileUtils.lineIterator(logFileToAnalyze, "UTF-8");
            while (lineIterator.hasNext()) {
                String line = lineIterator.nextLine();
                if (line != null) {
                    String[] entries = UptimeLog.parseLine(line);
                    long entryTime = Long.parseLong(entries[0]);
                    long entryMinute = entryTime/1000/60;
                    String entryType = entries[1];
                    if (entryType.equals(UptimeLog.LOG_STARTED_TOKEN)) {
                        metrics.put("periodStart", DateFormatUtils.format(entryTime, "yyyy-MM-dd HH:mm"));  // Report on the date that this uptime report starts
                        startMinute = entryMinute;
                    }
                    else if (entryType.equals(UptimeLog.LOG_COMPLETED_TOKEN)) {
                        metrics.put("periodStart", DateFormatUtils.format(entryTime, "yyyy-MM-dd HH:mm")); // Report on the date that this uptime report ends
                        endMinute = entryMinute;
                    }
                    else {
                        totalLogEntries++;
                        Integer connectionStatus = Integer.parseInt(entries[2]);
                        Integer numWithStatus = statusCounts.get(connectionStatus);
                        numWithStatus = (numWithStatus == null ? 1 : numWithStatus+1);
                        statusCounts.put(connectionStatus, numWithStatus);
                    }
                    if (lastEntryMinute != null) {
                        long durationFromLastEntry = entryMinute - lastEntryMinute;
                        if (durationFromLastEntry > 1) {
                            logEntryGaps++;
                            logEntryGapDuration += (durationFromLastEntry-1);
                        }
                    }
                    lastEntryMinute = entryMinute;
                }
            }
            if (startMinute != null && endMinute != null) {
                metrics.put("minutes.total", Long.toString(endMinute - startMinute)); // Report on the total number of minutes this period covers
            }

            metrics.put("minutes.logged", Integer.toString(totalLogEntries)); // Report on the total number of minutes the scheduler captured

            metrics.put("outages.num", Integer.toString(logEntryGaps));
            metrics.put("outages.minutes", Long.toString(logEntryGapDuration));

        }
        catch (Exception e) {
            throw new RuntimeException("An error occurred while reading from uptime log file", e);
        }
        finally {
            LineIterator.closeQuietly(lineIterator);
        }

        return metrics;
    }
}