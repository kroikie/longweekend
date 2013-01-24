<%-- 
    Document   : addToDatabase
    Created on : Jan 16, 2013, 10:23:53 AM
    Author     : Jonathan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Add to Database</title>
    </head>
    <body>
        <form action="SendData.jsp" method="GET">
            Holiday Name: <input type="text" name="name"/>
            Holiday Description: <input type="text" name="desc"/>
            Holiday Date: <input type="text" name="date"/>
            Holiday Happens On Same Day: <input type="text" name="same_day"/>
            <br/>
            <input type="submit" value="Submit">
        </form>
    </body>
</html>
