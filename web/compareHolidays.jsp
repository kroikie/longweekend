<%-- 
    Document   : compareHolidays
    Created on : Jan 18, 2013, 12:48:46 PM
    Author     : Jonathan
--%>

<%@page import="publicholidays.ModifyDatabase"%>
<%@page import="publicholidays.DatabaseEntry"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Compare Holidays</title>
    </head>
    <body>
        <%
            ModifyDatabase modifyDatabase = new ModifyDatabase();
            modifyDatabase.setHolidayListing(request);
            //modifyDatabase.printHolidaysListings(request, out);
            modifyDatabase.findLongWeekend(request, out);
         %>
         <script type="text/javascript">
            function gotoHome(){
                window.location.href = "index.jsp";
            }
        </script>
        <form>
            <input type="button" value ="Home Page" onclick="gotoHome();">
        </form>
    </body>
</html>
