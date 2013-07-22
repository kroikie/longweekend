var xmlHttp = createXMLHTTPObject();
var display = document.getElementById("results");

function createXMLHTTPObject() {
    if (window.XMLHttpRequest)
        return new XMLHttpRequest();
    return new ActiveXObject("Microsoft.XMLHTTP");
}

function process(url) {
    if (xmlHttp) {
        xmlHttp.open("GET", url, true);
        xmlHttp.onreadystatechange = displayInfo;
        xmlHttp.send(null);
    }
}

function displayInfo() {
    if (xmlHttp.readyState === 4) {
        if (xmlHttp.status === 200) {
            var json = JSON.parse(xmlHttp.responseText);
            display.innerHTML = "";
            json.forEach(function(holiday) {
                display.innerHTML += "Holiday Name: " + holiday.holidayName +
                        " Date: " + holiday.holidayDate +
                        " Desc: " + holiday.holidayDesc + "<br/>";
            });
        }
    }
}

function validate(date){
  return date.match('\\d{4}-\\d{2}-\\d{2}');  
}

var app = angular.module("longWeekend", []);
app.config(function ($routeProvider) {
    $routeProvider
            .when('/longWeekend', {
        controller: 'longweekendController',
        templateUrl: 'longWeekend.html'
    })
            .when('/holidaysFrom', {
        controller: 'longweekendController',
        templateUrl: 'holidaysFrom.html'
    })
            .when('/holidaysBetween', {
        controller: 'longweekendController',
        templateUrl: 'holidaysBetween.html'
    })
            .otherwise({redirectTo: '/longWeekend'});
});


app.controller("longweekendController", function($scope) {
    $scope.view = '';
    $scope.pages = [
        {title: 'Long Weekend', url: '#/longWeekend'},
        {title: 'Holidays From', url: '#/holidaysFrom'},
        {title: 'Holidays Between', url: '#/holidaysBetween'}
    ];

    $scope.longWeekend = {
        date: "",
        selector: "0",
        process: function() {
            if(!validate($scope.longWeekend.date)){
                display.innerHTML = "Enter a valid start date\n\
                Format: YYYY-MM-DD";
                return;
            }
            if ($scope.longWeekend.selector === "0") {
                process('LongWeekend?startDate=' + new Date().getFullYear() +
                        '-01-01&endDate=' + $scope.longWeekend.date + '&selector=0');
            } else {
                process('LongWeekend?startDate=' + $scope.longWeekend.date +
                        '&endDate=' + new Date().getFullYear() + '-12-31&selector=1');
            }
        }
    };

    $scope.holidaysFrom = {
        startDate: "",
        process: function() {
            if(!validate($scope.holidaysFrom.startDate)){
                display.innerHTML = "Enter a valid start date\n\
                Format: YYYY-MM-DD";
               return; 
            }
            process('HolidaysFrom?startDate=' + $scope.holidaysFrom.startDate);
        }
    };

    $scope.holidaysBetween = {
        startDate: "",
        endDate: "",
        process: function() {
            if(!validate($scope.holidaysBetween.startDate) || !validate($scope.holidaysBetween.endDate)){
                display.innerHTML = "";
                if(!validate($scope.holidaysBetween.startDate))
                    display.innerHTML += "Enter a valid start date \n\
                    Format: YYYY-MM-DD<br/>";
                if(!validate($scope.holidaysBetween.endDate))
                    display.innerHTML += "Enter a valid end date \n\
                    Format: YYYY-MM-DD";
                return;
            }
            process('HolidaysBetween?startDate=' + $scope.holidaysBetween.startDate
                    + '&endDate=' + $scope.holidaysBetween.endDate);
        }
    };

});



