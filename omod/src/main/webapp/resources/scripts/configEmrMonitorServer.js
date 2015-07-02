angular.module('configEmrMonitorServer', [ 'encounterService', 'ui.bootstrap' ])

    .controller('ConfigEmrMonitorServerCtrl', [ '$scope', '$http',
        function($scope, $http) {

            $scope.sortType     = 'serverType'; // set the default sort type
            $scope.sortReverse  = false;  // set the default sort order
            $scope.searchServer   = '';     // set the default search/filter term

            $http.get("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/server?v=default")
                .success(function(data) {
                    $scope.serversFound = data.servers;
                });


        }])
