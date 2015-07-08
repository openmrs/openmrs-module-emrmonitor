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
<div id="config-emrmonitor-server-app" class="container" ng-controller="ConfigEmrMonitorServerCtrl">

    <div ng-show="showServers">

        <div class="alert alert-info">
            <p>Sort Type: {{ sortType }}</p>
            <p>Sort Reverse: {{ sortReverse }}</p>
            <p>Search Query: {{ searchServer }}</p>
        </div>

        <form>
            <div class="form-group">
                <div class="input-group">
                    <div class="input-group-addon"><i class="fa fa-search"></i></div>

                    <input type="text" class="form-control" placeholder="Search Servers" ng-model="searchServer">

                </div>
            </div>
        </form>

        <table class="table table-bordered table-striped">
            <tbody>
            <thead>
            <tr>
                <td>
                    <a href="#" ng-click="sortType = 'serverId'; sortReverse = !sortReverse">
                    Id
                    <span ng-show="sortType == 'serverId'" class="fa fa-caret-down"></span>
                    </a>
                </td>
                <td>
                    <a href="#" ng-click="sortType = 'serverName'; sortReverse = !sortReverse">
                    Name
                    </a>
                </td>
                <td>
                    <a href="#" ng-click="sortType = 'userName'; sortReverse = !sortReverse">
                        UserName
                    </a>
                </td>
                <td>
                    <a href="#" ng-click="sortType = 'uuid'; sortReverse = !sortReverse">
                    uuid
                    </a>
                </td>
                <td>
                    <a href="#" ng-click="sortType = 'serverType'; sortReverse = !sortReverse">
                    Type
                    </a>
                </td>
            </tr>
            </thead>
            <tr ng-repeat="server in serversFound | orderBy:sortType:sortReverse | filter:searchServer">
                <td>{{ server.serverId }}</td>
                <td><a ng-click="selectServer(server)"> {{ server.serverName }} </a></td>
                <td>{{ server.serverUserName }}</td>
                <td>{{ server.uuid }}</td>
                <td>{{ server.serverType }}</td>
            </tr>
            </tbody>

        </table>
    </div>

    <div ng-show="showSelectedServer">

        <h3>Edit server info:</h3>

        <form>
            <p>
                <label>name</label>
                <input ng-model="serverName"/>
            </p>

            <p>
                <label>url</label>
                <input ng-model="serverUrl"/>
            </p>
            <p>
                <label>username</label>
                <input ng-model="serverUserName"/>
            </p>
            <p>
                <label>password</label>
                <input ng-model="serverUserPassword"/>
            </p>


            <div class="error" ng-show="errorMessage">{{ errorMessage }}</div>


            <button class="button cancel" ng-click="cancelUpdate()">Cancel</button>
            <button class="button confirm right" ng-click="updateServerInfo()">Save</button>
        </form>


    </div>

</div>



<script type="text/javascript">
    angular.bootstrap('#config-emrmonitor-server-app', [ 'configEmrMonitorServer' ]);
</script>