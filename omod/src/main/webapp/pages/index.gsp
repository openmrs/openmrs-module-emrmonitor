<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.13.0.js")
    ui.includeJavascript("uicommons", "angular-ui/ng-grid-2.0.7.min.js")
    ui.includeJavascript("uicommons", "angular-app.js")

    ui.includeJavascript("uicommons", "filters/serverDate.js")
    ui.includeJavascript("emrmonitor", "configEmrMonitorServer.js")

    ui.includeCss("uicommons", "angular-ui/ng-grid.min.css")
%>

<style>
    body {
        width: 99%;
        max-width: none;
    }
    #metric-table th {
        background-color: lightgray;
    }
    .gridStyle {
        border: 1px solid rgb(212,212,212);
        width: 200px;
        height: 600px;
    }
    #report-history-table {
        width: 100%;
    }
    #report-history-table td {
        vertical-align: top;
    }
    .selected {
        background-color: darkslategray;
        color: white;
    }
    .ngRowCountPicker {
        display: none;
    }
    .ngFooterTotalItems {
        display:none;
    }
    .ngPagerButton {
        width: 15px;
        padding: 2px 2px 2px 6px;
        text-align: center;
        vertical-align: middle;
    }
    .ngPagerCurrent {
        width:20px;
        padding: 2px;
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
                        Latest Report
                    </a>
                </th>
                <th>
                    Action
                </th>
            </tr>
            </thead>
            <tr ng-repeat="server in serversFound | orderBy:sortType:sortReverse | filter:searchServer">
                <td>{{ server.name }}</td>
                <td>{{ server.uuid }} </td>
                <td>{{ server.serverType }}</td>
                <td><a href="#" ng-click="displayServerMetrics(server)">{{ server.latestReport.dateCreated | serverDate: 'dd-MMM-yyyy h:mm a' }}</a></td>
                <td>
                    <a href="#" ng-click="editServer(server)">Edit</a> |
                    <a href="#" ng-click="displayServerMetrics(server)">History</a>
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

    <div id="report-history-section" ng-show="showServerMetrics">

        <div>
            <h3>Server metrics for {{ selectedServer.name }}</h3>
        </div>

        <table id="report-history-table">
            <tr>
                <td>
                    <div class="gridStyle" ng-grid="reportHistoryGrid" id="reportHistoryGrid"></div>
                    <div>
                        {{ pagingInformation }}
                    </div>
                </td>
                <td>
                    <div id="report-details">

                        <div style="padding-bottom:5px;">
                            <b>Report Date:</b>  {{ selectedReport.dateCreated | serverDate: 'dd-MMM-yyyy h:mm a' }}
                        </div>

                        <table class="table table-bordered table-striped">
                            <tbody>
                                <thead>
                                    <tr style="background-color: lightgray;">
                                        <td>Metric</td>
                                        <td>Value</td>
                                    </tr>
                                </thead>
                                <tr ng-repeat="metric in selectedReport.metrics">
                                    <td>{{ metric.metric }}</td>
                                    <td>{{ metric.value }}</td>
                                </tr>
                            </tbody>
                        </table>

                    </div>
                </td>
            </tr>

        </table>

    </div>

</div>

<script type="text/javascript">
    angular.bootstrap('#config-emrmonitor-server-app', [ 'configEmrMonitorServer' ]);
</script>