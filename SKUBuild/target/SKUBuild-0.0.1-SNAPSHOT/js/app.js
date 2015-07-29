var serviceUrl="../SKUBuild/services/uploadService"
var pocApp = angular.module('pocApp', ["ngRoute"]);
  



pocApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/skuUpload', {
        templateUrl: 'html/skuUpload.html',
        controller: 'skuUploadController'
      }).
      otherwise({
        redirectTo: '/errorController'
      });
  }]);
  




pocApp.controller('skuUploadController', function($scope,$http) {
	
	
	
    $scope.placeholder1="Choose File..";
    $scope.placeholder2="Choose File..";
    $scope.placeholder3="Choose File..";
    $scope.percentage="0%";
    $scope.file_one_name="";
    $scope.file_two_name="";
    $scope.file_three_name="";
    $scope.file_one;
    $scope.file_two;
    $scope.file_three;
    $scope.fileArray=new FormData();
    $("#uploadBtn_2").prop("disabled", true);
    $("#uploadBtn_3").prop("disabled", true);
    $("#uploadFile").prop("disabled", true);
    $scope.showTable=false;
    $scope.file_changed_one = function(element) {
    	$scope.message="";	
        $scope.$apply(function(scope) {
            var photofile = element.files[0];
            $scope.placeholder1=photofile.name;
            var reader = new FileReader();
            reader.onload = function(e) {
            $scope.file_one_name=photofile.name;
            $scope.file_one=photofile;
            };
            reader.readAsDataURL(photofile);
            $(progressBar).attr("style","width:33%");
    		$scope.percentage="33%";
    		$("#firstButtonDiv").removeClass("btn-primary");
    		$("#firstButtonDiv").addClass("btn-success");
    		$("#uploadBtn_2").prop("disabled", false);
    		$("#secondButtonDiv").removeClass("disabled");
        });
    	};
    	
    	$scope.file_changed_two = function(element) {
        	
            $scope.$apply(function(scope) {
                var photofile = element.files[0];
                $scope.placeholder2=photofile.name;
                var reader = new FileReader();
                reader.onload = function(e) {
                	$scope.file_two_name=photofile.name;
                    $scope.file_two=photofile;
                };
                reader.readAsDataURL(photofile);
               	$(progressBar).attr("style","width:67%");
               	$scope.percentage="67%";
               	$("#secondButtonDiv").removeClass("btn-primary");
        		$("#secondButtonDiv").addClass("btn-success");
               	$("#uploadBtn_3").prop("disabled", false);
        		$("#thirdButtonDiv").removeClass("disabled");
            });
        	};
        	
        	$scope.file_changed_three = function(element) {
            	
                $scope.$apply(function(scope) {
                    var photofile = element.files[0];
                    $scope.placeholder3=photofile.name;
                    var reader = new FileReader();
                    reader.onload = function(e) {
                    	$scope.file_three_name=photofile.name;
                        $scope.file_three=photofile;
                    };
                    reader.readAsDataURL(photofile);
                    $(progressBar).attr("style","width:100%	");
                    $scope.percentage="100%";
                    $("#thirdButtonDiv").removeClass("btn-primary");
            		$("#thirdButtonDiv").addClass("btn-success");
            		$("#uploadFile").removeClass("disabled");
            		$("#uploadFile").removeClass("btn-primary");
            		$("#uploadFile").addClass("btn-success");
            		$("#uploadFile").prop("disabled", false);
            		$scope.showTable=true;
                });
            	};
            	$scope.uploadFile=function(){
            		if ($scope.showTable)
            		{
            			$scope.fileArray.append("ContentTemplateGenerator",$scope.file_one);
            			$scope.fileArray.append("AttributeReport",$scope.file_two);
            			$scope.fileArray.append("StaplesMasterStyleGuide",$scope.file_three);
            			//alert($scope.fileArray);
	            		/*$scope.message="Succesfully uploaded..";
	            		$scope.appTable={"show":true};
	            		$scope.files=[
	            	                  {
	            	                      "index":"1",
	            	                      "name": "File One",
	            	                      "status": "Successful"
	            	                  },
	            	                  {
	            	                	  "index":"2",
	            	                	  "name": "File Two",
	            	                      "status": "Successful"
	            	                  },
	            	                  {
	            	                	  "index":"3",
	            	                	  "name": "File Three",
	            	                      "status": "Successful"
	            	                  }]; */
            			$http.post(serviceUrl ,$scope.fileArray, {
            			    // this cancels AngularJS normal serialization of request
            			    transformRequest: angular.identity,
            			    // this lets browser set `Content-Type: multipart/form-data` 
            			    // header and proper data boundary
            			    headers: {'Content-Type': undefined}
            			})
            			.success(function(data, status, headers, config)
            			{
            				$scope.message=data[0];
            				$scope.appTable={"show":true};
            			})
            			.error(function(data, status, headers, config)
            			{
            				$scope.message=data[0];
            			});
            	}};
});

pocApp.controller('errorController', function($scope) {
     
    $scope.message = 'Error';
    
});