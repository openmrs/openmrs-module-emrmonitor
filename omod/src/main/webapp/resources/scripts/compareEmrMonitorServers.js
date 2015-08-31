angular.module('compareEmrMonitorServers', ['ngAnimate', 'ngTouch', 'ui.grid', 'ui.grid.selection', 'ui.grid.pinning', 'ui.grid.exporter', 'ui.grid.moveColumns', 'ui.grid.resizeColumns', 'ui.bootstrap'])

.controller('CompareEmrMonitorServersCtrl', ['$scope', '$http',
  function($scope, $http) {

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

    $scope.getServers = function() {
      $http.get("/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/emrmonitor/server?v=default")
        .success(function(data) {
        	$scope.gridOptions.data = data.servers;
        });
    }

    $scope.getServers();
  }
])