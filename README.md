## OpenMRS EMR Monitor Module

### Description
OpenMRS module that allows OpenMRS servers that are part of the same implementation to register with a parent server and to send on a scheduled interval updated system metrics to the central server.

### OpenMRS Wiki
[EMR Monitor Module](https://wiki.openmrs.org/display/projects/EMR+Monitor+Module)

### Technical overview
The module provides a REST web services API that allows the child servers to register with a parent server and to transmit system metrics data:

- [http://localhost:8080/openmrs/ws/rest/v1/emrmonitor/server](http://localhost:8080/openmrs/ws/rest/v1/emrmonitor/server)
    * GET: retrieve the list of servers already registered in the system
    * POST: creates a new EMRMonitorServer record in the system
        - e.g. posting an json like below adds a new server node to the list of servers to be monitored

```json
{
    "name": "Kamarando",
    "serverType": "CHILD",
    "serverUrl": "butaro.pih-emr.org",
    "serverUserName": "test4",
    "serverUserPassword": "password4",
    "uuid": "114F2A6E-A736-4473-B777-A08F76E818D9",
    "systemInformation": {
        "SystemInfo.title.openmrsInformation": {
            "SystemInfo.OpenMRSInstallation.openmrsVersion": "1.9.8 SNAPSHOT Build c3f109",
            "SystemInfo.hostname": "192.168.3.242"
        },
        "SystemInfo.title.javaRuntimeEnvironmentInformation": {
            "SystemInfo.JavaRuntimeEnv.operatingSystem": "Mac OS X",
            "SystemInfo.JavaRuntimeEnv.operatingSystemArch": "x86_64"
        },
        "SystemInfo.title.memoryInformation": {
            "SystemInfo.Memory.totalMemory": "509 MB"
        },
        "SystemInfo.title.dataBaseInformation": {
            "SystemInfo.Database.name": "butaronov6",
            "SystemInfo.Database.connectionURL": "jdbc:mysql://localhost:3306/butaronov6?autoReconnect=true&sessionVariables=storage_engine=InnoDB&useUnicode=true&characterEncoding=UTF-8",
        },
        "SystemInfo.title.moduleInformation": {
            "SystemInfo.Module.repositoryPath": "/Users/cosmin/pih/server-config/openmrs-rwanda-rwink/modules",
            "Rwanda Primary Care Module": "2.0.7-SNAPSHOT ",
            "Metadata Sharing": "1.1.8 ",
            "Reporting": "0.7.8 ",
            "OpenMRS EmrMonitor Module": "1.0-SNAPSHOT "
        }
    }
```

#### Data Model
Each server produces a list of reports similar to the systemInformation node above. Each report contains a list of system metrics.

Data is stored/represented in/by the following OpenMRS tables/objects:
* EmrMonitorServer: contains entries for all registered servers
```java
EmrMonitorServer extends BaseOpenmrsData implements Serializable{
    Integer id;
    String name;
    EmrMonitorServerType serverType; //LOCAL, PARENT, CHILD
    String serverUrl;
    String serverUserName;
    String serverUserPassword;
    Set<EmrMonitorReport> emrMonitorReports;
}
```
* EmrMonitorReport: contains a list of reports generated for each server.
```java
EmrMonitorReport implements Comparable<EmrMonitorReport>{
    public enum SubmissionStatus {
        WAITING_TO_SEND, SENT, LOCAL_ONLY
    }
    Integer id;
    EmrMonitorServer server;
    Set<EmrMonitorReportMetric> metrics;
    private SubmissionStatus status;
}
```
#### Service Layer

* EmrMonitorService: Methods to manipulate data stored in database. These should follow standard OpenMRS conventions:
  * EMR Monitor Server
    ```java
    EmrMonitorServer getEmrMonitorServer(Integer);
    EmrMonitorServer getEmrMonitorServerByUuid(String);
    List<EmrMonitorServer> getAllEmrMonitorServers();
    EmrMonitorServer saveEmrMonitorServer(EmrMonitorServer);
    EmrMonitorServer retireEmrMonitorServer(EmrMonitorServer);
    void purgeEmrMonitorServer(EmrMonitorServer);

    EmrMonitorServer getLocalServer();
    EmrMonitorServer getParentServer();
    List<EmrMonitorServer> getChildServers();
    ```
  * EMR Monitor Report
    ```java
    EmrMonitorReport getEmrMonitorReport(Integer);
    EmrMonitorReport getEmrMonitorReportByUuid(String);
    EmrMonitorReport saveEmrMonitorReport(EmrMonitorReport);
    void purgeEmrMonitorReport(EmrMonitorReport);

    EmrMonitorReport generateEmrMonitorReport();
    List<EmrMonitorReport> getEmrMonitorReports(EmrMonitorReportQuery);
    Map<EmrMonitorServer, EmrMonitorReport> getLatestEmrMonitorReports();
    EmrMonitorReport getLatestEmrMonitorReport(EmrMonitorServer);
    ```java

    ** Emr Monitor Report Metric
        ```java
        EmrMonitorReportMetric getEmrMonitorReportMetrics(EmrMonitorReportMetricQuery);
        ```
    ** Methods to communicate between servers (might want these to go in a different class)
        ```java
        ConnectionStatus testConnectionToParent();
        ConnectionStatus sendReportToParent();
        ```


#### Metric Generation Strategy:
```java
interface MetricProducer {
 boolean canProduceMetricsForCurrentSystem();
 List<EmrMonitorReportMetric> produceMetrics();
}
```
* Implementations of this class should be registered with Spring using @Component
* The service will iterate over all of these beans, call their produceMetrics method, and combine into a Report

#### Implementations:

* We should logically organize the different information we are currently collecting from
the methods available in the admnistrationService, and what we have already written in ExtraSystemInformation,
and group them together into implementations of MetricProducer

* For things like Sync Information, this should be in it's own class, and the "canProduceMetricsForCurrentSystem"
method will check whether or not sync is started

* For things that are O/S-specific, this should be similarly encapsulated, with a means for either automatically (or via GP),
determining which are appropriate given the running O/S.  This _should_ be possible to determine automatically.

* Ideally we would have one or more implementations that allow for more to be added in.  Eg.
```java
ConfigurableMetricProducer implements MetricProducer {
 // This would load xml files from somewhere in the .OpenMRS directory, parse these, and allow for executing things like:
 *  SQL queries
 *  Runtime commands on the underlying O/S
 *  Groovy scripts
}
```
#####Processing:
* We will need several scheduled tasks:
(I prefer Spring-managed tasks like we do in reporting, over use of the SchedulerService)

    * Task 1. Generate report for the Local Server on a periodic basis and save it
    * Task 2. If parent configured, check whether any reports have been generated but not transmitted, and send them
    * Task 3. Clean up history of reports (if desired, to save space)

##### Report Config
trigger_type
trigger_config
or
run_on_startup
run_on_context_refresh
run_on_event
run_on_cron

report_template (null indicates default)
destination_url
destination_username/password (can we use key-based authentication here instead?)


#####
EmrMonitorReport generateEmrMonitorReport();
List<EmrMonitorReport> getEmrMonitorReports(EmrMonitorReportQuery);
Map<EmrMonitorServer, EmrMonitorReport> getLatestEmrMonitorReports();
EmrMonitorReport getLatestEmrMonitorReport(EmrMonitorServer);

// Emr Monitor Report Metric

EmrMonitorReportMetric getEmrMonitorReportMetrics(EmrMonitorReportMetricQuery);

// Methods to communicate between servers (might want these to go in a different class)

ConnectionStatus testConnectionToParent();
ConnectionStatus sendReportToParent();
