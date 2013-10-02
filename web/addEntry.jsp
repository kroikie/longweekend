<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Add to Database</title>
        <script src="scripts/datetimepicker_css.js"></script>
    </head>
    <body>
        <form action="_addEntry.jsp" method="GET">
            Holiday Name: <input type="text" name="name" required/>
            Holiday Description: <input type="text" name="desc"/>
            Holiday Date: <input id="date" type="text" name="date" readonly required/>
            <img src="scripts/cal/cal.gif" 
                 onclick="NewCssCal('date', 'yyyyMMdd', 'dropdown');" 
                 style="cursor:pointer"/>
            Holiday Happens On Same Day: <input type="text" name="same_day" required/>
            <br/>
            <input type="submit" value="Submit">
        </form>
    </body>
</html>
