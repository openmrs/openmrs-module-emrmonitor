<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("emrmonitor", "configEmrMonitorServer.js")
%>

${ ui.includeFragment("emrmonitor", "menu") }

<h3>Monitored Servers</h3>
<div id="config-emrmonitor-server-app" class="container" ng-controller="ConfigEmrMonitorServerCtrl">

    <div ng-show="showServers">

        <form>
            <div class="form-group" style="display:table-row">
                <i style="display:table-cell; padding-right:10px;" class="fa fa-search"></i>
                <span style="display:table-cell">
                    <input type="text" placeholder="Search Servers" ng-model="searchServer" size="50">
                </span>
            </div>
        </form>

        <br/>

        <table class="table table-bordered table-striped">
            <tbody>
            <thead>
            <tr>
                <td>
                    <a href="#" ng-click="sortType = 'id'; sortReverse = !sortReverse">
                        Id
                    </a>
                </td>
                <td>
                    <a href="#" ng-click="sortType = 'name'; sortReverse = !sortReverse">
                        Name
                    </a>
                </td>
                <td>
                    <a href="#" ng-click="sortType = 'serverType'; sortReverse = !sortReverse">
                        Type
                    </a>
                </td>
                <td>
                    Action
                </td>
            </tr>
            </thead>
            <tr ng-repeat="server in serversFound | orderBy:sortType:sortReverse | filter:searchServer">
                <td>{{ server.id }}</td>
                <td>{{ server.name }} </td>
                <td>{{ server.serverType }}</td>
                <td>
                    <a href="#" ng-click="selectServer(server)">Edit</a> |
                    <a href="#" ng-click="displayServerMetrics(server)">View</a>
                </td>
            </tr>
            </tbody>

        </table>
    </div>

    <div ng-show="showSelectedServer">

        <h3>Edit server info:</h3>

        <form>
            <p>
                <label>name</label>
                <input ng-model="name"/>
            </p>
            <div class="error" ng-show="errorMessage">{{ errorMessage }}</div>

            <button class="button cancel" ng-click="cancelUpdate()">Cancel</button>
            <button class="button confirm right" ng-click="updateServerInfo()">Save</button>
        </form>

    </div>

    <div ng-show="showServerMetrics">

        <div ng-repeat="(key,val) in selectedServer.systemInformation">
            <h4>{{ key }}</h4>
            <table class="table table-bordered table-striped">
                <tbody>
                    <thead>
                    <tr>
                        <td>Metric</td>
                        <td>Value</td>
                    </tr>
                    </thead>
                    <tr ng-repeat="(key, val) in val">
                        <td>{{ key }}</td>
                        <td>{{ val }}</td>
                    </tr>
                </tbody>
            </table>
        </div>

    </div>

</div>

<script type="text/javascript">
    angular.bootstrap('#config-emrmonitor-server-app', [ 'configEmrMonitorServer' ]);
</script>