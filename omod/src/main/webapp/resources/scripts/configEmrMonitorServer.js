angular.module('configEmrMonitorServer', [ "uicommons.filters", "ui.bootstrap", 'ngGrid' ])

    .controller('ConfigEmrMonitorServerCtrl', [ '$scope', '$http',
        function($scope, $http) {

            $scope.sortType     = 'serverType'; // set the default sort type
            $scope.sortReverse  = true;  // set the default sort order
            $scope.searchServer   = '';     // set the default search/filter term
            $scope.showServerList = false;
            $scope.showEditServer = false;
            $scope.showServerMetrics = false;
            $scope.reportHistoryData = [];
            $scope.selectedServer = null;
            $scope.selectedReport = null;
            $scope.pagingInformation = '';
            
            $scope.listServers =  function(){
                $http.get("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/server?v=default")
                    .success(function(data) {
                        $scope.serversFound = data.results;
                        $scope.showServerList = true;
                        $scope.showEditServer = false;
                        $scope.showServerMetrics = false;
                        $scope.selectedServer = null;
                        $scope.serverMetrics = null;
                    });
            }

            $scope.listServers();

            $scope.editServer = function(server) {
                $scope.showServerList = false;
                $scope.showEditServer = true;
                $scope.showServerMetrics = false;
                $scope.selectedServer = server;
            }

            $scope.displayServerMetrics = function(server, report) {
                $scope.showServerList = false;
                $scope.showEditServer = false;
                $scope.showServerMetrics = true;;
                $scope.selectedServer = server;
                $scope.selectedReport = report || server.latestReport;
                $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
            }

            $scope.updateServerInfo = function() {
                var server = {
                    uuid: $scope.selectedServer.uuid,
                    name: $scope.selectedServer.name,
                };

                $http.post("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/server/" + server.uuid,
                    server)
                    .success(function() {
                        console.log("Server info updated!");
                        $scope.listServers();
                    })
                    .error(function(error) {
                        console.log("Failed to update server info: " + error.error.message);
                        window.alert("Error");
                    });

            }

            $scope.deleteServer = function(server) {

                $http.delete("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/server/" + server.uuid)
                    .success(function() {
                        console.log("Server: " + server.name + " has been deleted");
                        $scope.listServers();
                    })
                    .error(function(error) {
                        console.log("Failed to delete server: " + error.error.message);
                        window.alert("Failed to delete server: " + error.error.message);
                    });
            }

            $scope.totalReportHistoryItems = 0;

            $scope.filterOptions = {
                filterText: "",
                useExternalFilter: true
            };

            $scope.pagingOptions = {
                pageSize: 4,
                currentPage: 1
            };

            $scope.updatePagingInformation = function() {
                var pg = $scope.pagingOptions.currentPage;
                var sz = $scope.pagingOptions.pageSize;
                var tot = $scope.totalReportHistoryItems;
                var lastOnPage = pg * sz;
                var firstOnPage = lastOnPage - sz + 1;
                if (lastOnPage > tot) {
                    lastOnPage = tot;
                }
                $scope.pagingInformation = firstOnPage + " to " + lastOnPage + " of " + tot;
            }

            $scope.setPagingData = function(data, page, pageSize){
                var pagedData = data.slice((page - 1) * pageSize, page * pageSize);
                $scope.reportHistoryData = pagedData;
                $scope.totalReportHistoryItems = data.length;
                if (!$scope.$$phase) {
                    $scope.$apply();
                }
                $scope.updatePagingInformation();
            };

            $scope.getPagedDataAsync = function (pageSize, page, searchText) {
                setTimeout(function () {
                    var data;
                    if ($scope.selectedServer) {
                        var getAllUrl = "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/report?server=" + $scope.selectedServer.uuid;
                        if (searchText) {
                            var ft = searchText.toLowerCase();
                            $http.get(getAllUrl).success(function (fullData) {
                                data = fullData.results.filter(function (item) {
                                    return JSON.stringify(item).toLowerCase().indexOf(ft) != -1;
                                });
                                $scope.setPagingData(data, page, pageSize);
                            });
                        } else {
                            $http.get(getAllUrl).success(function (fullData) {
                                $scope.setPagingData(fullData.results, page, pageSize);
                            });
                        }
                    }
                }, 100);
            };

            $scope.$watch('pagingOptions', function (newVal, oldVal) {
                if (newVal !== oldVal && newVal.currentPage !== oldVal.currentPage) {
                    $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
                }
            }, true);

            $scope.$watch('filterOptions', function (newVal, oldVal) {
                if (newVal !== oldVal) {
                    $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
                }
            }, true);

            $scope.loadReport = function(row) {
                $http.get("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/report/" + row.getProperty('uuid'), {"v": "default"})
                    .success(function(data) {
                        $scope.displayServerMetrics($scope.selectedServer, data);
                    });
            }

            $scope.isSelected = function(row) {
                var rowUuid = row.getProperty('uuid');
                if ($scope.selectedReport) {
                    return (rowUuid == $scope.selectedReport.uuid);
                }
                return false;
            }

            $scope.reportHistoryGrid = {
                data: 'reportHistoryData',
                enablePaging: true,
                showFooter: true,
                enableRowSelection: false,
                totalServerItems: 'totalReportHistoryItems',
                pagingOptions: $scope.pagingOptions,
                filterOptions: $scope.filterOptions,
                columnDefs: [
                    {
                        field: 'dateCreated',
                        displayName: 'Report Date',
                        cellTemplate: '<div ng-class="{selected: isSelected(row)}"><span ng-click="loadReport(row)">{{COL_FIELD | serverDate: "dd-MMM-yyyy h:mm a"}}</span></div>'}
                ]
            };

        }])
