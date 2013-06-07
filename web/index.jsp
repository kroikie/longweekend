<%-- 
    Document   : index
    Created on : Jan 15, 2013, 6:56:15 PM
    Author     : Jonathan
--%>

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
        <script type="text/javascript">
            function addTo(){
                window.location.href = "addToDatabase.jsp";
            }
            
        </script>
        <form>
            <input type="button" value ="Add to Database" onclick="addTo();">
        </form>
        
        <form action="compareHolidays.jsp" method="GET">
            <br/>Enter the information in the following format: YYYY-MM-DD
            <br/><input type="text" name="yourDate"/>
            <input type="submit" value="Check for closest holidays"/>
        </form>
    </body>
</html>
