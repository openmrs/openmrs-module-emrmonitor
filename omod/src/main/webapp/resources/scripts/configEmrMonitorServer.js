angular.module('configEmrMonitorServer', [ 'ui.bootstrap' ])

    .controller('ConfigEmrMonitorServerCtrl', [ '$scope', '$http',
        function($scope, $http) {

            $scope.sortType     = 'serverType'; // set the default sort type
            $scope.sortReverse  = true;  // set the default sort order
            $scope.searchServer   = '';     // set the default search/filter term
            $scope.showServers = false;
            $scope.showSelectedServer = false;
            $scope.showServerMetrics = false;
            $scope.serverMetrics = {};
            
            $scope.getServers =  function(){
                $http.get("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/server?v=default")
                    .success(function(data) {
                        $scope.serversFound = data.results;
                        $scope.showServers = true;
                    });
            }

            $scope.getServers();

            $scope.selectServer = function(server) {
                $scope.showServers = false;
                $scope.showServerMetrics = false;
                $scope.showSelectedServer = true;

                $scope.selectedServer = server;
                $scope.name = server.name;
            }

            $scope.deleteServer = function(server) {

                $http.delete("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/server/" + server.uuid)
                    .success(function() {
                        console.log("Server: " + server.name + " has been deleted");
                        $scope.getServers();
                        $scope.showSelectedServer = false;
                        $scope.showServerMetrics = false;
                    })
                    .error(function(error) {
                        console.log("Failed to delete server: " + error.error.message);
                        window.alert("Failed to delete server: " + error.error.message);
                    });
            }

            $scope.displayServerMetrics = function(server) {

                $scope.showServers = false;
                $scope.showSelectedServer = false;
                $scope.showServerMetrics = true;

                $scope.serverMetrics = server;

            }

            $scope.cancelUpdate = function() {
                $scope.showSelectedServer = false;
                $scope.showServerMetrics = false;
                $scope.getServers();
            }

            $scope.updateServerInfo = function() {
                var server = {
                    uuid: $scope.selectedServer.uuid,
                    name: $scope.name,
                };

                $http.post("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/server/" + server.uuid,
                    server)
                    .success(function() {
                        console.log("Server info updated!");
                        $scope.getServers();
                        $scope.showSelectedServer = false;
                        $scope.showServerMetrics = false;
                    })
                    .error(function(error) {
                        console.log("Failed to update server info: " + error.error.message);
                        window.alert("Error");
                    });

            }

        }])
