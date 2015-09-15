angular.module('compareEmrMonitorServers', ['ngAnimate', 'ngTouch', 'ui.grid', 'ui.grid.selection', 'ui.grid.pinning', 'ui.grid.exporter', 'ui.grid.moveColumns', 'ui.grid.resizeColumns', 'ui.bootstrap'])

.controller('CompareEmrMonitorServersCtrl', ['$scope', '$http', 'uiGridConstants',
  function($scope, $http, uiGridConstants) {

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
        text: "IMB Servers",
        style: 'headerStyle'
      },
      exporterPdfFooter: function(currentPage, pageCount) {
        return {
          text: currentPage.toString() + ' of ' + pageCount.toString(),
          style: 'footerStyle'
        };
      },
      exporterPdfCustomFormatter: function(docDefinition) {
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
      onRegisterApi: function(gridApi) {
        $scope.gridApi = gridApi;
      }
    };

    /*
     * Reads all the keys in the "systemInformation" node of the Json file and returns them in an array.
     * They will be used as checkboxes where a user can select which data point to display.
     */
    $scope.getSelections = function() {
      var selections = [];
      angular.forEach($scope.servers, function(server, value) {
        angular.forEach(server.systemInformation, function(key, value) {
          if (selections.indexOf(value) < 0) {
            selections.push(value);
          }
        });


      });
      return selections;
    };

    /*
     * Reads all the keys in the "systemInformation" node of the Json file and returns them in an array.
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

    $scope.getServers = function() {
      $http.get("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/server?v=default")
        .success(function(data) {
          $scope.servers = data.servers;
        });
    }

    $scope.getServers();


    /**
     * Creates a new Json object from 
     * the selected key
     */
    $scope.click = function(clicked) {

      $scope.getSelections();
      var columnDefs = [];
      var keys = $scope.getKeys(clicked);
      var newJson = [];
      angular.forEach($scope.servers, function(server, event) {

        var jsonObject = {};
        jsonObject['serverName'] = server.serverName;

        for (var i in keys) {

          var nestedKey = keys[i];
          var newKey = nestedKey.substring(nestedKey.lastIndexOf(".") + 1);
          var nestedValue = $scope.getNestedValue(server.serverName, clicked, nestedKey);

          jsonObject[newKey] = nestedValue;
        }

        newJson.push(jsonObject);

      });


      $scope.gridOptions.columnDefs.length = 0;
      $scope.gridOptions.data = newJson;

      $scope.gridApi.core.notifyDataChange(uiGridConstants.dataChange.ALL);
    };

    /**
     * Uses the server name and the selected key to retrieve the 
     * value of corresponding to the nestedKey parameter.
     */

    $scope.getNestedValue = function(name, clicked, nestedKey) {

      var value = null;
      angular.forEach($scope.servers, function(server, event) {

        if (server.serverName == name) {

          angular.forEach(server.systemInformation, function(obj, event) {

            if (event == clicked) {

              value = obj[nestedKey];
            }

          });

        }

      });
      return value;
    };
  }
])