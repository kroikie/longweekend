<%@page import="com.foohyfooh.publicholidays.Database"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>View Public Holidays</title>
    </head>
    <body>

        <%
            String mode = request.getParameter("key");
            if (mode == null || !mode.equals("dev")) {
                response.sendRedirect("index.jsp");
            }
            Database database = new Database();
            database.print(out);
        %>

        <form>
            <input type="button" value ="Add to Database" onclick="window.location = 'addEntry.jsp';">
        </form>
    </body>
</html>
