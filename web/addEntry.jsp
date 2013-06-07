<%-- 
    Document   : addEntry
    Created on : Jan 15, 2013, 7:25:44 PM
    Author     : Jonathan
--%>

<%@page import="publicholidays.Database"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Add Entry</title>
    </head>
    <body>
        <% 
            Database database = new Database();
            database.add(request);
            response.sendRedirect("index.jsp");
            
        %>
        
    </body>
</html>
