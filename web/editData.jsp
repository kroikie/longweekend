<%-- 
    Document   : editData
    Created on : Jan 16, 2013, 5:23:29 PM
    Author     : Jonathan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Update Entry</title>
    </head>
    <body>
        <form action="updateData.jsp" method="GET">
            Holiday ID: <input type="text" name="id" value="<%out.print(request.getParameter("id"));%>"/>
            Holiday Name: <input type="text" name="name" value="<%out.print(request.getParameter("name"));%>"/>
            Holiday Description: <input type="text" name="desc" value="<%out.print(request.getParameter("desc"));%>"/>
            Holiday Date: <input type="text" name="date" value="<%out.print(request.getParameter("date"));%>"/>
            Holiday Happens On Same Day: <input type="text" name="same_day" value="<%out.print(request.getParameter("same_day"));%>"/>
            <br/>
            <input type="submit" value="Update">
        </form>
    </body>
</html>
