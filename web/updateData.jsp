<%-- 
    Document   : updateData
    Created on : Jan 16, 2013, 8:54:49 PM
    Author     : Jonathan
--%>

<%@page import="publicholidays.ModifyDatabase"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Update Data</title>
    </head>
    <body>
        <% 
            ModifyDatabase modifyDatabase = new ModifyDatabase();
            modifyDatabase.updateDatabaseEntry(request.getParameter("id"), request.getParameter("name"), request.getParameter("desc"),
                    request.getParameter("date"), request.getParameter("same_day"));
            response.sendRedirect("index.jsp");
        %>
    </body>
</html>
