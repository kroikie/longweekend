<%@page import="publicholidays.Database"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>View Public Holidays</title>
    </head>
    <body>
        
        <% 
            Database database = new Database();
            database.print(out);
        %>
        
        <form>
            <input type="button" value ="Add to Database" onclick="window.location = 'addEntry.jsp';">
        </form>
    </body>
</html>
