<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("uicommons", "services/encounterService.js")

    ui.includeJavascript("emrmonitor", "configEmrMonitorServer.js")

%>
<h3>EMR Servers</h3>
<div id="config-emrmonitor-server-app" ng-controller="ConfigEmrMonitorServerCtrl">

    <div ng-show="serversFound">

        <table>
            <tbody>
            <thead>
            <tr>
                <td>
                    Id
                </td>
                <td>
                    Name
                </td>
                <td>
                    uuid
                </td>
                <td>
                    Type
                </td>
            </tr>
            </thead>
            <tr ng-repeat="server in serversFound">
                <td>{{ server.serverId }}</td>
                <td><a ng-click="selectPatient(patient)"> {{ server.serverName }} </a></td>
                <td>{{ server.uuid }}</td>
                <td>{{ server.serverType }}</td>
            </tr>
            </tbody>

        </table>
    </div>

</div>



<script type="text/javascript">
    angular.bootstrap('#config-emrmonitor-server-app', [ 'configEmrMonitorServer' ]);
</script>