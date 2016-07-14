<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.13.0.js")
    ui.includeJavascript("uicommons", "angular-app.js")

    ui.includeJavascript("uicommons", "filters/serverDate.js")
    ui.includeJavascript("emrmonitor", "configEmrMonitorServer.js")
%>

<style>
    body {
        width: 99%;
        max-width: none;
    }
    #metric-table th {
        background-color: lightgray;
    }
</style>

${ ui.includeFragment("emrmonitor", "menu") }

<div id="config-emrmonitor-server-app" class="container" ng-controller="ConfigEmrMonitorServerCtrl">

    <div ng-show="showServerList">

        <h3>Monitored Servers</h3>

        <form>
            <div class="form-group" style="display:table-row">
                <i style="display:table-cell; padding-right:10px;" class="fa fa-search"></i>
                <span style="display:table-cell">
                    <input type="text" placeholder="Search Servers" ng-model="searchServer" size="50">
                </span>
            </div>
        </form>

        <br/>

        <table id="metric-table" class="table table-bordered table-striped">
            <tbody>
            <thead>
            <tr>
                <th>
                    <a href="#" ng-click="sortType = 'name'; sortReverse = !sortReverse">
                        Name
                    </a>
                </th>
                <th>
                    <a href="#" ng-click="sortType = 'uuid'; sortReverse = !sortReverse">
                        Identifier
                    </a>
                </th>
                <th>
                    <a href="#" ng-click="sortType = 'serverType'; sortReverse = !sortReverse">
                        Type
                    </a>
                </th>
                <th>
                    <a href="#" ng-click="sortType = 'latestReport.dateCreated'; sortReverse = !sortReverse">
                        Latest Report Date
                    </a>
                </th>
                <th>
                    Action
                </th>
            </tr>
            </thead>
            <tr ng-repeat="server in serversFound | orderBy:sortType:sortReverse | filter:searchServer">
                <td><a href="#" ng-click="displayServerMetrics(server)">{{ server.name }}</a></td>
                <td>{{ server.uuid }} </td>
                <td>{{ server.serverType }}</td>
                <td>{{ server.latestReport.dateCreated | serverDate: 'dd-MMM-yyyy h:mm a' }}</td>
                <td>
                    <a href="#" ng-click="editServer(server)">Edit</a> |
                    <a href="#" ng-click="displayServerMetrics(server)">View</a>
                </td>
            </tr>
            </tbody>

        </table>
    </div>

    <div ng-show="showEditServer">

        <h3>Edit server info:</h3>

        <form>
            <p>
                <label>name</label>
                <input ng-model="selectedServer.name"/>
            </p>
            <div class="error" ng-show="errorMessage">{{ errorMessage }}</div>

            <button class="button cancel" ng-click="listServers()">Cancel</button>
            <button class="button confirm right" ng-click="updateServerInfo()">Save</button>
        </form>

    </div>

    <div ng-show="showServerMetrics">

        <h3>Latest Server metrics for {{ selectedServer.name }}</h3>

        <div>
            <b style="vertical-align: middle;">Report Date:</b>  {{ selectedServer.latestReport.dateCreated | serverDate: 'dd-MMM-yyyy h:mm a' }}
            <span style="float:right; padding-bottom:10px;">
                <button class="button" ng-click="listServers()">Return to server list</button>
            </span>
        </span>
        </div>

        <table class="table table-bordered table-striped">
            <tbody>
                <thead>
                <tr>
                    <td>Metric</td>
                    <td>Value</td>
                </tr>
                </thead>
                <tr ng-repeat="metric in selectedServer.latestReport.metrics">
                    <td>{{ metric.metric }}</td>
                    <td>{{ metric.value }}</td>
                </tr>
            </tbody>
        </table>

    </div>

</div>

<script type="text/javascript">
    angular.bootstrap('#config-emrmonitor-server-app', [ 'configEmrMonitorServer' ]);
</script>