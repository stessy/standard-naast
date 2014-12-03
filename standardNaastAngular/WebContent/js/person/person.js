var app = angular.module('standard',
		[ 'ngTable', 'ngResource', 'ui.bootstrap' ]);

app.controller('PersonsTable', function($scope, $http,
		ngTableParams) {
	$scope.tableParams = new ngTableParams({
		page : 1, // show first page
		count : 10, // count per page
		sorting : {memberNumber : 'asc'}
	}, {
		total : 0, // length of data
		getData : function($defer, params) {
			console.log('running defer');
			$http.get("persons.json").then(
					function(response) {
						$scope.tableParams.settings().$scope = $scope;
						console.log(response);
						$scope.users = response.data;
						params.total($scope.users.length);
						$defer.resolve($scope.users.slice((params.page() - 1)
								* params.count(), params.page()
								* params.count()));
					});

		}
//	,
//		$scope : {
//			$data : {}
//		}
	});
	$scope.tableParams.reload();
});

//app.factory('getPersonsService', function($http) {
//	var getPersonsService = {
//		async : function() {
//			var promise = $http.get("persons.json").then(function(response) {
//				console.log(response);
//				return response.data;
//			});
//			return promise;
//		}
//	};
//	return getPersonsService;
//});
