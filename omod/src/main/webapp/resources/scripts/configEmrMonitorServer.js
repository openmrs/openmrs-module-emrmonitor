angular.module('configEmrMonitorServer', [ 'encounterService', 'ui.bootstrap' ])

    .controller('ConfigEmrMonitorServerCtrl', [ '$scope', '$http',
        function($scope, $http) {


            $http.get("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/server?v=default")
                .success(function(data) {
                    $scope.serversFound = data.servers;
                });


        }])
