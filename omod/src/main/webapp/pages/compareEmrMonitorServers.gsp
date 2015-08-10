<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
     ui.includeJavascript("uicommons", "services/encounterService.js")
    ui.includeJavascript("emrmonitor", "compareEmrMonitorServers.js")

%>

${ ui.includeFragment("emrmonitor", "menu") }

<h3>EMR Servers</h3>
<div id="compare-emrmonitor-servers-app" class="container" ng-controller="CompareEmrMonitorServersCtrl">

    <div ng-show="showCompareServers">
    
     <ul ng-repeat="server in servers |limitTo:1">
      <ul ng-repeat="(key, val) in server.systemInformation">
        <li>
          {{key}} <input id="{{key}}" type="checkbox" ng-click="click(key)" /></li>

      </ul>
    </ul>

        <table id="compareServers" class="table table-bordered table-striped">

        </table>
    </div>

</div>

<script type="text/javascript">
    angular.bootstrap('#compare-emrmonitor-servers-app', [ 'compareEmrMonitorServers' ]);
</script>