<%@page import="com.foohyfooh.publicholidays.Database"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Update Data</title>
    </head>
    <body>
        <% 
            Database database = new Database();
            database.update(request);
            response.sendRedirect("dev.jsp");
        %>
    </body>
</html>
