<!DOCTYPE html>
<html ng-app="longWeekend">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Long Weekend</title>
        <link rel="stylesheet" href="styles/index.css"/>
    </head>
    <body ng-controller="longweekendController">
        <div id="nav">
            <ul id="list">
                <li ng-repeat="page in pages">
                    <a href="{{page.url}}">{{ page.title }}</a>
                </li>
            </ul>
        </div>
        <div id="display">
            <ng-view></ng-view>
            <div id="results"></div>
        </div>
        
        
        <script src="scripts/angular.min.js"></script>
        <script src="scripts/datetimepicker_css.js"></script>
        <script src="scripts/manager.js"></script>
    </body>
</html>