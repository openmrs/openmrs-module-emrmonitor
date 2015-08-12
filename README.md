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

```
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

### Data Model
Data is store in the following OpenMRS tables:

* EmrMonitorServer: contains entries for all registered servers

`
+----------------------+--------------+------+-----+---------+----------------+
| Field                | Type         | Null | Key | Default | Extra          |
+----------------------+--------------+------+-----+---------+----------------+
| id                   | int(11)      | NO   | PRI | NULL    | auto_increment |
| name                 | varchar(255) | NO   |     | NULL    |                |
| server_type          | varchar(45)  | NO   |     | NULL    |                |
| server_url           | varchar(255) | YES  |     | NULL    |                |
| server_user_name     | varchar(45)  | YES  |     | NULL    |                |
| server_user_password | varchar(45)  | YES  |     | NULL    |                |
| uuid                 | char(38)     | NO   | UNI | NULL    |                |
| date_created         | datetime     | NO   |     | NULL    |                |
| changed_by           | int(11)      | YES  |     | NULL    |                |
| date_changed         | datetime     | YES  |     | NULL    |                |
| voided               | tinyint(4)   | NO   |     | 0       |                |
| voided_by            | int(11)      | YES  |     | NULL    |                |
| date_voided          | datetime     | YES  |     | NULL    |                |
| void_reason          | varchar(255) | YES  |     | NULL    |                |
+----------------------+--------------+------+-----+---------+----------------+
`

* Have this extend BaseOpenmrsMetdata?  Make serverId -> id, serverName -> name
* Remove systemInformation property from this class

EmrMonitorReport {
 EmrMonitorServer server;
 Date reportDate;
 SubmissionStatus status; // WAITING_TO_SEND, SENT, LOCAL_ONLY
}

EmrMonitorReportMetric {
 EmrMonitorReport report;
 String category;
 String metric;
 String value;  // Consider valueNumeric, valueBoolean, valueText...?
}
