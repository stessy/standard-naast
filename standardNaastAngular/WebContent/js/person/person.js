/**
 * 
 */

var app = angular.module('standard', ['ngTable', 'ngRoute', 'ui.bootstrap']);
app.controller('PersonsTable', function(getPersonsService, $scope) {
	getPersonsService.async().then(function(data){
		$scope.users = data;
	});
});

app.factory('getPersonsService', function($http){
	var getPersonsService = {
			async: function(){
				var promise = $http.get("http://localhost:8080/standardnaastwebservices/rest/members/false").then(function (response){
					console.log(response);
					return response.data;
				});
				return promise;
			}
	};
	return getPersonsService;
});

//app.controller('TabsDemoCtrl',
//		function ($scope) {
//			  $scope.tabs = [
//			    { title:'Dynamic Title 1', content:'Dynamic content 1' },
//			    { title:'Dynamic Title 2', content:'Dynamic content 2', disabled: true }
//			  ];
//
//			  $scope.alertMe = function() {
//			    setTimeout(function() {
//			      alert('You\'ve selected the alert tab!');
//			    });
//			  };
//			});
