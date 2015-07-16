angular.module('registerEmrMonitorServer', [ 'encounterService', 'ui.bootstrap' ])

    .controller('RegisterEmrMonitorServerCtrl', [ '$scope', '$http',
        function($scope, $http) {

            $scope.showRegisterServer = true;

            $scope.parentServerName = "HarmonyServer";
            $scope.parentServerUrl = "http://197.243.32.206:8081/openmrs";
            $scope.parentUserName = "[Enter User Name]";
            $scope.parentUserPassword = "[Enter Password]";

            $scope.testConnection = function() {
                console.log("test connection" );

                $scope.successMessage = null;
                $scope.errorMessage = null;

                var server = {
                    serverName: $scope.parentServerName,
                    serverType: "PARENT",
                    serverUrl: $scope.parentServerUrl,
                    serverUserName: $scope.parentUserName,
                    serverUserPassword: $scope.parentUserPassword
                };

                $http.post("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/connect/" ,
                    server)
                    .success(function() {
                        console.log("Successfully connected to remote server!");
                        $scope.successMessage = "Successfully connected to remote server!";
                    })
                    .error(function(error) {
                        console.log("Failed to update server info: " + error);
                        $scope.errorMessage = "Failed to update server info: " + error;
                    });

            }

        }])
