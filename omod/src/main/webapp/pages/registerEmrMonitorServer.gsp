<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("uicommons", "services/encounterService.js")

    ui.includeJavascript("emrmonitor", "registerEmrMonitorServer.js")

%>
${ ui.includeFragment("emrmonitor", "menu") }

<div id="register-emrmonitor-server-app" class="container" ng-controller="RegisterEmrMonitorServerCtrl">
    <div ng-show="showRegisterServer">

        <h3>Parent Server</h3>

        <form>

            <p>
                <label>url</label>
                <input ng-model="parentServerUrl" placeholder="http://server_address:8080/openmrs"/>
            </p>
            <p>
                <label>username</label>
                <input ng-model="parentUserName" placeholder="enter user name to access parent server"/>
            </p>
            <p>
                <label>password</label>
                <input ng-model="parentUserPassword" type="password" placeholder="enter password"/>
            </p>
            <div class="field-error" ng-show="errorMessage">{{ errorMessage }}</div>
            <p>
                <div class="field-success" ng-show="successMessage">{{ successMessage }}</div>
            </p>
            <p>
                <button class="button" ng-click="testConnection()">Test Connection</button>
            </p>

            <button class="button cancel" ng-click="cancelUpdate()">Cancel</button>
            <button class="button confirm right" ng-click="registerServer()">Register</button>
        </form>

    </div>

</div>


<script type="text/javascript">
    angular.bootstrap('#register-emrmonitor-server-app', [ 'registerEmrMonitorServer' ]);
</script>