angular.module('registerEmrMonitorServer', [ 'encounterService', 'ui.bootstrap' ])

    .controller('RegisterEmrMonitorServerCtrl', [ '$scope', '$http',
        function($scope, $http) {

            $scope.showRegisterServer = true;

            $scope.parentServerUrl = null;
            $scope.parentUserName = null;
            $scope.parentUserPassword = null;

            $scope.testConnection = function() {
                console.log("test connection" );

                $scope.successMessage = null;
                $scope.errorMessage = null;

                var server = {
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
                        console.log("Failed to connect to remote server: " + error.error.message);
                        $scope.errorMessage = "Failed to connect to remote server: " + error.error.message;
                    });

            }

            $scope.registerServer = function() {
                console.log("register server" );

                $scope.successMessage = null;
                $scope.errorMessage = null;

                var server = {
                    serverType: "PARENT",
                    serverUrl: $scope.parentServerUrl,
                    serverUserName: $scope.parentUserName,
                    serverUserPassword: $scope.parentUserPassword
                };

                $http.post("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/register/" ,
                    server)
                    .success(function() {
                        console.log("Successfully connected to remote server!");
                        $scope.successMessage = "Successfully connected to remote server!";
                    })
                    .error(function(error) {
                        console.log("Failed to register with remote server: " + error.error.message);
                        $scope.errorMessage = "Failed to register with remote server: " + error.error.message;
                    });

            }

        }])
