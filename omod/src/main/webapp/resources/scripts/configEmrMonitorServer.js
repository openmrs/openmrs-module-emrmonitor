angular.module('configEmrMonitorServer', [ "uicommons.filters", "ui.bootstrap" ])

    .controller('ConfigEmrMonitorServerCtrl', [ '$scope', '$http',
        function($scope, $http) {

            $scope.sortType     = 'serverType'; // set the default sort type
            $scope.sortReverse  = true;  // set the default sort order
            $scope.searchServer   = '';     // set the default search/filter term
            $scope.showServerList = false;
            $scope.showEditServer = false;
            $scope.showServerMetrics = false;
            $scope.selectedServer = null;
            
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

            $scope.displayServerMetrics = function(server) {
                $scope.showServerList = false;
                $scope.showEditServer = false;
                $scope.showServerMetrics = true;
                $scope.selectedServer = server;
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

        }])
