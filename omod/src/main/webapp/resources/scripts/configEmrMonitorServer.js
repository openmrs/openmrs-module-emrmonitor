angular.module('configEmrMonitorServer', [ 'encounterService', 'ui.bootstrap' ])

    .controller('ConfigEmrMonitorServerCtrl', [ '$scope', '$http',
        function($scope, $http) {

            $scope.sortType     = 'serverType'; // set the default sort type
            $scope.sortReverse  = false;  // set the default sort order
            $scope.searchServer   = '';     // set the default search/filter term
            $scope.showServers = false;
            $scope.showSelectedServer = false;
            $scope.showServerMetrics = false;


            $scope.getServers =  function(){
                $http.get("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/server?v=default")
                    .success(function(data) {
                        $scope.serversFound = data.servers;
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
                $scope.serverUrl = server.serverUrl;
                $scope.serverUserName = server.serverUserName;
                $scope.serverUserPassword = server.serverUserPassword;
            }

            $scope.displayServerMetrics = function(server) {

                $scope.showServers = false;
                $scope.showSelectedServer = false;
                $scope.showServerMetrics = true;

                $scope.selectedServer = server;

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
                    serverUrl: $scope.serverUrl,
                    serverUserName: $scope.serverUserName,
                    serverUserPassword: $scope.serverUserPassword
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
                        console.log("Failed to update server info: " + error);
                        window.alert("Error");
                    });

            }

        }])
