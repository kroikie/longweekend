<%-- 
    Document   : compareHolidays
    Created on : Jan 18, 2013, 12:48:46 PM
    Author     : Jonathan
--%>

<%@page import="publicholidays.ModifyDatabase"%>
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
            
            modifyDatabase.getHolidayListing(request.getParameter("yourDate"));
            
            for(int i = 0; i < modifyDatabase.getPastHolidays().size();i++){
                out.print(modifyDatabase.getPastHolidays().get(i)+"<br/>");
            }
            %>
            
            <b>
            <%
            out.print(String.format("Your Date %s <br/>", request.getParameter("yourDate")));
            %>
            </b>
            <%
            for(int i = 0; i < modifyDatabase.getCommingHolidays().size();i++){
                out.print(modifyDatabase.getCommingHolidays().get(i)+"<br/>");
            }
        %>
    </body>
</html>
