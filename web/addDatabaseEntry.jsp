<%-- 
    Document   : addDatabaseEntry
    Created on : Jan 15, 2013, 7:25:44 PM
    Author     : Jonathan
--%>

<%@page import="publicholidays.ModifyDatabase"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Add Database Entry</title>
    </head>
    <body>
        <% 
            ModifyDatabase modifyDatabase = new ModifyDatabase();
            modifyDatabase.addDatabaseEntry(request);
            response.sendRedirect("index.jsp");
        %>
        
    </body>
</html>
