<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("uicommons", "services/encounterService.js")
    ui.includeJavascript("uicommons", "moment.js")
    ui.includeJavascript("emrmonitor", "compareEmrMonitorServers.js")

    ui.includeJavascript("emrmonitor", "angular-touch.js")
    ui.includeJavascript("emrmonitor", "angular-animate.js")
    ui.includeJavascript("emrmonitor", "csv.js")
    ui.includeJavascript("emrmonitor", "pdfmake.js")
    ui.includeJavascript("emrmonitor", "vfs_fonts.js")
    ui.includeJavascript("emrmonitor", "ui-grid.js")
    ui.includeCss("emrmonitor", "ui-grid.css")

%>

<style>
    body {
        width: 99%;
        max-width: none;
    }
</style>

${ ui.includeFragment("emrmonitor", "menu") }

<h3>EMR Servers</h3>
<div id="compare-emrmonitor-servers-app" class="container" ng-controller="CompareEmrMonitorServersCtrl">

    Select data point:

    <select ng-model="selectedMetric" ng-change="changeMetric(selectedMetric)">
        <option value=""></option>
        <option ng-repeat="metric in metricNames" ng-selected="{{ selectedMetrics.indexOf(metric) > 0 }}"  value="{{ metric }}">
            {{ metric }}
        </option>
    </select>

    <br/>
    <br/>
    <div ui-grid="gridOptions" ui-grid-selection ui-grid-exporter ui-grid-move-columns ui-grid-pinning ui-grid-resize-columns class="grid"></div>
 </div>

<script type="text/javascript">
    angular.bootstrap('#compare-emrmonitor-servers-app', [ 'compareEmrMonitorServers' ]);
</script>