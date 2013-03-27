package publicholidays;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import javax.swing.JOptionPane;

//TODO: Make modifications to deal with Entries that are TBA
//TODO: Work on findLongWeekend Method
//TODO: Work the implementations of findLongWeekend and printHolidayListings

public class ModifyDatabase extends HttpServlet {
    
    //i am working well
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private String query;
    private ArrayList<DatabaseEntry> holidays, pastHolidays, comingHolidays, TBAHolidays;
    
    private static String pass = "";
    private String getPassword(){
        if(pass.isEmpty()){
            pass = JOptionPane.showInputDialog(null, "Enter your password");
        }
        return pass;   
    }
    
    
    public ModifyDatabase(){
        openConnection();
        getDatabaseEntries();
        Collections.sort(holidays);
    }
    
    
    private void openConnection(){
        
        try{
            Class.forName("java.sql.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/longweekend", "root", getPassword());
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            query = "SELECT * FROM holidays;";
            resultSet = statement.executeQuery(query);
        }catch(ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        
    }//end openConnection
    
    
    private void getDatabaseEntries(){
        
        holidays = new ArrayList<DatabaseEntry>();
        try{
            while(resultSet.next()){
                holidays.add(new DatabaseEntry(resultSet.getInt("id"), resultSet.getString("holidayName"), resultSet.getString("holidayDesc"), 
                        resultSet.getString("holidayDate"), resultSet.getInt("alwaysOnSameDay")));
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }//end getDatabaseEntries
    
    
    public void addDatabaseEntry(HttpServletRequest request){
               
        try{
            resultSet.last();
            resultSet.moveToInsertRow();
            int id = resultSet.getRow()+1;
            resultSet.updateInt("id", id);
            resultSet.updateString("holidayName", request.getParameter("name"));
            resultSet.updateString("holidayDesc", request.getParameter("desc"));
            resultSet.updateString("holidayDate", request.getParameter("date"));
            resultSet.updateInt("alwaysOnSameDay", Integer.parseInt(request.getParameter("same_day")));
            resultSet.insertRow();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }finally{
            closeDatabase();
        }
        
    }//end addDatabaseEntry
    
    
    public void printDatabase(JspWriter out){
        try{
            for(int i= 0; i< holidays.size(); i++)
                out.print(holidays.get(i)+"<br/>");
        }catch(IOException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        
    }//end printDatabse

    
    public void updateDatabaseEntry(HttpServletRequest request){
        
        try{
            resultSet.close();
            resultSet = statement.executeQuery("SELECT * FROM test.holidays");
            while(resultSet.next()){
                if(resultSet.getInt("id") == Integer.parseInt(request.getParameter("id"))){
                    query = String.format("UPDATE test.holidays SET holidayName=\"%s\", holidayDesc=\"%s\", holidayDate=\"%s\", alwaysOnSameDay=%d WHERE id=%d;",
                            request.getParameter("name"), request.getParameter("desc"), request.getParameter("date"), Integer.parseInt(request.getParameter("same_day")), Integer.parseInt(request.getParameter("id"))
                    );
                    statement.executeUpdate(query);
                    break;
                }
            
            }
        }catch(SQLException e){
            //JOptionPane.showMessageDialog(null, e.getMessage());
        }finally{
            closeDatabase();
        }
        
    }//end updateDatabaseEntry
    
    
    public void setHolidayListing(HttpServletRequest request){
        
        DatabaseEntry yourDateEntry = new DatabaseEntry(request.getParameter("yourDate"));
        boolean sameDay = false;
        pastHolidays = new ArrayList<DatabaseEntry>();
        comingHolidays = new ArrayList<DatabaseEntry>();
        TBAHolidays = new ArrayList<DatabaseEntry>();
        //Put a condition if the year of the holiday is less then the current year that it is TBA
        
        for(DatabaseEntry d : holidays){
            if(d.compareTo(yourDateEntry) < 0){
                pastHolidays.add(d);
            }else if(d.compareTo(yourDateEntry) == 0){
                //Need to work on if the date is the same as a holiday
                sameDay = true;
            }else if(d.compareTo(yourDateEntry) >0){
                comingHolidays.add(d);
            }
            
        }
        
    }//end setHolidayListings
    
    
    public void printHolidaysListings(HttpServletRequest request, JspWriter out){
        try{
            out.print("<b>");
            for(int i = 0; i < pastHolidays.size();i++){
                out.print(pastHolidays.get(i)+"<br/>");
            }
            out.print(String.format("Your Date %s <br/>", request.getParameter("yourDate")));
            for(int i = 0; i < comingHolidays.size();i++){
                out.print(comingHolidays.get(i)+"<br/>");
            }
            out.print("</b>");
        }catch(IOException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        
    }//end printDatabase
    
    
    /*Pseudocode for findLongWeekend
     * while there still more holidays in comingHlidays
     * if the next day is a public holiday or saturday or sunday
     * mark date or probably print it highlighted
     * end if
     * end while
    */
    public void findLongWeekend(HttpServletRequest request, JspWriter out){
        try{
            DatabaseEntry current = new DatabaseEntry(request.getParameter("yourDate"));
            //Need to work on this printing the correct name of the holiday if it is an actual holiday
            do{
                out.print("*"+current+"*<br/>");
                current = current.nextDatabaseEntry();
            }while(isHoliday(current) || isWeekendDay(current));
            
        }catch(IOException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }//end findLongWeekend

    
    private boolean isHoliday(DatabaseEntry d){
        for(DatabaseEntry i: comingHolidays){
            if(i.compareTo(d)==0){
                return true;
            }
        }
        return false;
    }
    
    private boolean isWeekendDay(DatabaseEntry d){
        //Subtract 1 from the Month to accomodate for the months starting from 0
        Calendar x = new GregorianCalendar(d.getYear(), d.getMonth()-1, d.getDay());
        if(x.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY|| x.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
            return true; 
        }
        return false;
    }
    
    
    private void closeDatabase() {
        try {
            connection.close();
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
}
