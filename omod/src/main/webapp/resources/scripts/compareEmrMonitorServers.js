angular.module('compareEmrMonitorServers', ['ngAnimate', 'ngTouch', 'ui.grid', 'ui.grid.selection', 'ui.grid.pinning', 'ui.grid.exporter', 'ui.grid.moveColumns', 'ui.grid.resizeColumns', 'ui.bootstrap'])

    .controller('CompareEmrMonitorServersCtrl', ['$scope', '$http', 'uiGridConstants',
        function ($scope, $http, uiGridConstants) {

            $scope.servers = [];
            $scope.metricNames = [];
            $scope.selectedMetrics = [];

            $scope.load = function () {
                $http.get("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/server?v=default")
                    .success(function (data) {
                        angular.forEach(data.results, function (server) {
                            $scope.servers.push(server);
                            angular.forEach(server.latestReport.metrics, function (metric) {
                                if ($scope.metricNames.indexOf(metric.metric) < 0) {
                                    $scope.metricNames.push(metric.metric);
                                }
                            });
                        });
                    });
            }
            $scope.load();

            $scope.gridOptions = {

                enableGridMenu: true,
                enableSelectAll: true,
                exporterCsvFilename: 'servers.csv',
                exporterPdfDefaultStyle: {
                    fontSize: 9
                },
                exporterPdfTableStyle: {
                    margin: [30, 30, 30, 30]
                },
                exporterPdfTableHeaderStyle: {
                    fontSize: 10,
                    bold: true,
                    italics: true,
                    color: 'red'
                },
                exporterPdfHeader: {
                    text: "Servers",
                    style: 'headerStyle'
                },
                exporterPdfFooter: function (currentPage, pageCount) {
                    return {
                        text: currentPage.toString() + ' of ' + pageCount.toString(),
                        style: 'footerStyle'
                    };
                },
                exporterPdfCustomFormatter: function (docDefinition) {
                    docDefinition.styles.headerStyle = {
                        fontSize: 22,
                        bold: true
                    };
                    docDefinition.styles.footerStyle = {
                        fontSize: 10,
                        bold: true
                    };
                    return docDefinition;
                },
                exporterPdfOrientation: 'landscape',
                exporterPdfPageSize: 'LETTER',
                exporterPdfMaxGridWidth: 500,
                exporterCsvLinkElement: angular.element(document.querySelectorAll(".custom-csv-link-location")),
                onRegisterApi: function (gridApi) {
                    $scope.gridApi = gridApi;
                }
            };

            $scope.changeMetric = function(m) {

                $scope.selectedMetrics.push(m);

                var metricTableColumns = [];
                metricTableColumns.push({ field: 'serverName', displayName: 'Server' });
                metricTableColumns.push({ field: 'reportDate', displayName: 'Report Date' });
                for (var idx in $scope.selectedMetrics) {
                    metricTableColumns.push({ field: 'col'+idx, displayName: $scope.selectedMetrics[idx] })
                }

                var metricTableData = [];
                angular.forEach($scope.servers, function (server) {
                    var serverRow = {};
                    serverRow['serverName'] = server.name;
                    serverRow['reportDate'] = moment(server.latestReport.dateCreated).format("YYYY-MM-DD HH:m:s");
                    for (var idx in $scope.selectedMetrics) {
                        var metric = $scope.selectedMetrics[idx];
                        var metricVal = $scope.getMetricValue(server, metric);
                        serverRow['col'+idx] = metricVal;
                    }
                    metricTableData.push(serverRow);
                });

                $scope.gridOptions.columnDefs = metricTableColumns;
                $scope.gridOptions.data = metricTableData;
                $scope.gridApi.core.notifyDataChange(uiGridConstants.dataChange.ALL);
            };

            $scope.getMetricValue = function (server, metric) {
                var ret = '';
                angular.forEach(server.latestReport.metrics, function (m) {
                    if (metric == m.metric) {
                        ret = m.value;
                    }
                });
                return ret;
            };
        }
    ])