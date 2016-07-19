## OpenMRS EMR Monitor Module

### Description
OpenMRS module that can be used to monitor various metrics over time, and to share these metrics with a central server

### OpenMRS Wiki
[EMR Monitor Module](https://wiki.openmrs.org/display/projects/EMR+Monitor+Module)

### Overview

#### Server
An __EmrMonitorServer__ represents a particular OpenMRS instance that is available for monitoring.  There are two types of servers:
* LOCAL:  The OpenMRS instance that this module is installed into
* CHILD:  A remote OpenMRS instance that has registered itself into this server and submits reports of it's metrics on a regular basis

The expected usage is that, within a network of servers, one server would be designated as the "parent", and the rest would be designated as "child".
Each of the child servers would report their metrics up to the parent.  In order to configure a server to report it's metrics up to a parent, the following
runtime properties should be added to the child:

* emrmonitor.parentUrl=http://localhost:8081/openmrs-standalone
* emrmonitor.parentUsername=admin
* emrmonitor.parentPassword=test

#### Report
An __EmrMonitorReport__ encapsulates a particular collection of metrics that are generated at a particular time for a particular server.
All metrics are associated with a Report and a Report is also the unit of transmission between a child and a parent server.

#### Report Metric
An __EmrMonitorReportMetric__ is a particular data element that is obtained on a particular server at a particular point in time.  Metrics are generated
by a particular instance of a __MetricProducer__.  Although these can be invoked on demand, they are typically run in the context of producing a periodic
__EmrMonitorReport__ for a particular server.

#### Metric Producer
A __MetricProducer__ is a component registered with Spring that implements the MetricProducer interface and returns a Map<String, String> of metrics for a
given server as of that particular execution time.  Implementations can extend the emrmonitor module to capture their own custom metrics by adding their own
__MetricProducer__ implementations in a module.

There are several built-in metric producers that run by default.  An implementation can disable any of these metric producers explicitly by adding their
namespace (comma-separated) to the global property named __emrmonitor.disabledMetricProducers__

There is also a special metric producer __ConfigurableMetricProducer__ that support implementation-defined metrics added at runtime.  This works by iterating over
and executing any files located in the **{application_data_dir}/configuration/emrmonitor** directory.
The name of any file in this directory will represent the "namespace" of the metrics that it produces.  The following files are supported:

1. SQL files:  Any file with a ".sql" extension will be executed.
 * If a single value is returned by the query, then the metric is the filename and the value is the result
 * If multiple rows with a single column is returned, then the value is a comma separated list
 * If multiple columns are returned, then all but the last column are appended to the metric name, and the last column is the value

2. Shell Scripts:  Any file with a ".sh" extension will be executed.
 * If multiple lines of output are returned and each is in the format of key=value, then the key will be considered part of the metric, and the value the value
 * Otherwise, the full contents of output will be the value of a single metric

### Workflow

1. A "LOCAL" __EmrMonitorServer__ server is automatically created on a given OpenMRS instance.
2. A scheduled task is configured to execute every minute, check to see if it is due to run, and automatically executes if so, 
   generating a new __EmrMonitorReport__ for the LOCAL server.  This process iterates across all __MetricProducer__ components and
   adds an __EmrMonitorReportMetric__ to the report for all generated metrics.  By default this will run once per day.  
   This can be overridden by configuring the global property:  __emrmonitor.minutesBetweenReports__.  
3. A history of these reports is available for viewing on the local server
4. If this server has a "parent" configured (via OpenMRS runtime properties), another scheduled task executes every few minutes to check for any __EmrMonitorReport__ that has
   been generated but not transmitted to the parent.  An __EmrMonitorReport__ can have one of the following statuses:
   * LOCAL_ONLY:  No parent is configured, do not transmit
   * WAITING_TO_SEND:  A parent is configured, but the Report has not yet been sent successfully to the parent (only found on a child server)
   * SENT:  A parent is configured and the report has successfully been sent and recieved by the parent (only found on a child server)
   * RECEIVED:  A report was successfully received on this server after it was submitted by a child (only found on a parent server)

### Rest Interface

The module provides a REST API for managing servers and reports.

- http://localhost:8080/openmrs/ws/rest/v1/emrmonitor/server
- http://localhost:8080/openmrs/ws/rest/v1/emrmonitor/report

### TODO

* Test page for displaying available metric producers and enabling generating metrics for viewing on demand
* Document REST interface above
* Groovy support in ConfigurableMetricProducer
* Add indexes where we need to speed up particular queries
* Task to clean up history of log files or reports (if desired, to save space)
* Add last connection time to the server table, and a task with web service for hitting it from child more frequently than daily
* ReportConfig:
  * trigger_type (cron, startup, context_refresh, event)
  * trigger_config (used by the type)
  * template (simple template that allows for text replacement of metrics by key)
  * transmission_url
  * transmission_username
  * transmission_password
* Investigate whether we can use key-based authentication +/- a request/approval system rather than username/password authentication
