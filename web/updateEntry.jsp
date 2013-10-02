<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Update Entry</title>
        <script src="scripts/datetimepicker_css.js"></script>
    </head>
    <body>
        <%
            if(request.getQueryString() == null){
                response.sendRedirect("index.jsp");
            }
        %>
        <form action="_updateData.jsp" method="GET">
            Holiday ID: <input type="hidden" name="id" value="<%= request.getParameter("id") %>"/>
            Holiday Name: <input type="text" name="name" value="<%= request.getParameter("name") %>"/>
            Holiday Description: <input type="text" name="desc" value="<%= request.getParameter("desc") %>"/>
            Holiday Date: <input id="date" type="text" name="date" value="<%= request.getParameter("date") %>" readonly/>
            <img src="scripts/cal/cal.gif" 
                 onclick="NewCssCal('date', 'yyyyMMdd', 'dropdown');" 
                 style="cursor:pointer"/>
            Holiday Happens On Same Day: <input type="text" name="same_day" value="<%= request.getParameter("same_day") %>"/>
            <br/>
            <input type="submit" value="Update">
        </form>
    </body>
</html>
