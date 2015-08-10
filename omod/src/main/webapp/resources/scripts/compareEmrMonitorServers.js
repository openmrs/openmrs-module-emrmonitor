angular.module('compareEmrMonitorServers', ['encounterService', 'ui.bootstrap'])

.controller('CompareEmrMonitorServersCtrl', ['$scope', '$http',
  function($scope, $http) {

    $scope.showCompareServers = false;


    $scope.getServers = function() {
      $http.get("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/server?v=default")
        .success(function(data) {
          $scope.servers = data.servers;
          $scope.showCompareServers = true;
        });
    }

    $scope.getServers();

    /*
     * Reads all the keys in the "systemInformation" node of the Json file and returns them in an array.
     * They will be used as checkboxes where a user can select which data point to display.
     */
    $scope.getKeys = function(event) {
      var str = event;
      var keys = [];
      angular.forEach($scope.servers, function(server, event) {
        angular.forEach(server.systemInformation, function(key, value) {
          if (value === str) {
            angular.forEach(key, function(key, value) {
              if (keys.indexOf(value) < 0) {
                keys.push(value);
              }
            });
          }
        });


      });
      return keys;
    };

    /*
     * Creates checkboxes from the keys using getKeys() method and displays all 
     * the values in a table format.
     */
    $scope.click = function(event) {
      var keys = $scope.getKeys(event);
      document.getElementById("compareServers").innerHTML = "";
      var tr = document.createElement('tr');
      var td1 = document.createElement('td');
      td1.innerHTML = '<h2>Servers</h2>';
      tr.appendChild(td1);
      for (var i in keys) {
        var td = document.createElement('td');
        var msg = keys[i];
        td.innerHTML += '<h2>' + msg + '</h2>';
        tr.appendChild(td);
      }

      document.getElementById("compareServers").appendChild(tr);
      var str = event;
      angular.forEach($scope.servers, function(server, event) {
        angular.forEach(server.systemInformation, function(key, value) {

          if (value === str) {
            var tr = document.createElement('tr');
            var t = document.createElement('td');
            t.innerHTML = server.serverName;
            tr.appendChild(t);

            for (var i in keys) {
              var td = document.createElement('td');
              td.innerHTML += key[keys[i]];
              tr.appendChild(td);
            }
            document.getElementById("compareServers").appendChild(tr);

          }

        });

      });

    };

  }
])