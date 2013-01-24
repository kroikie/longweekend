/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publicholidays;

import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;


public class ModifyDatabase {
    
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private String query;
    private ArrayList<DatabaseEntry> database, pastHolidays, commingHolidays;
    
    
    public ModifyDatabase(){
        openDatabase();
        getData();
    }
    
    private void openDatabase(){
        
        try{
            Class.forName("java.sql.Driver");
            String pass = JOptionPane.showInputDialog(null, "Enter your password");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", pass);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            query = "SELECT * FROM test.holidays;";
            resultSet = statement.executeQuery(query);
        }catch(ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        
    }//end openDatabase
    
    private void getData(){
        
        database = new ArrayList<DatabaseEntry>();
        try{
            while(resultSet.next()){
                database.add(new DatabaseEntry(resultSet.getInt("id"), resultSet.getString("holidayName"), resultSet.getString("holidayDesc"), 
                        resultSet.getString("holidayDate"), resultSet.getInt("alwaysOnSameDay")));
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }//end getData
    
    public void addDatabaseEntry(String holidayName, String holidayDesc, String holidayDate, String alwaysOnSameDay){
        
        try{
            resultSet.last();
            resultSet.moveToInsertRow();
            int id = resultSet.getRow()+1;
            resultSet.updateInt("id", id);
            resultSet.updateString("holidayName", holidayName);
            resultSet.updateString("holidayDesc", holidayDesc);
            resultSet.updateString("holidayDate", holidayDate);
            resultSet.updateInt("alwaysOnSameDay", Integer.parseInt(alwaysOnSameDay));
            resultSet.insertRow();
            database.add(new DatabaseEntry(id, holidayName, holidayDesc, holidayDate, Integer.parseInt(alwaysOnSameDay)));
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }finally{
            closeDatabase();
        }
        
        
    }//end addToDatabase
    
    public ArrayList<DatabaseEntry> getDatabase(){
        return database;
    }
    
    public ArrayList<DatabaseEntry> getPastHolidays(){
        return  pastHolidays;
    }
    
    public ArrayList<DatabaseEntry> getCommingHolidays(){
        return commingHolidays;
    }
    
    public void updateDatabaseEntry(String id, String holidayName, String holidayDesc, String holidayDate, String alwaysOnSameDay){
        
        
        try{
            resultSet.close();
            resultSet = statement.executeQuery("SELECT * FROM test.holidays");
            while(resultSet.next()){
                if(resultSet.getInt("id") == Integer.parseInt(id)){
                    query = String.format("UPDATE test.holidays SET holidayName=\"%s\", holidayDesc=\"%s\", holidayDate=\"%s\", alwaysOnSameDay=%d WHERE id=%d;",
                            holidayName, holidayDesc, holidayDate, Integer.parseInt(alwaysOnSameDay), Integer.parseInt(id)
                    );
                    statement.executeUpdate(query);
                }
            
            }
        }catch(SQLException e){
            //JOptionPane.showMessageDialog(null, e.getMessage());
        }finally{
            database.get(Integer.parseInt(id)-1).update(Integer.parseInt(id), holidayName, holidayDesc, holidayDate, Integer.parseInt(alwaysOnSameDay));
            closeDatabase();
        }
        
    }//end updateDatabaseEntry
    
    public void getHolidayListing(String holidayDate){
        
        boolean sameDay = false;
        pastHolidays = new ArrayList<DatabaseEntry>();
        commingHolidays = new ArrayList<DatabaseEntry>();
        
        for(DatabaseEntry d : database){
            if(d.date.compareTo(holidayDate) < 0){
                pastHolidays.add(d);
            }else if(d.date.compareTo(holidayDate) == 0){
                sameDay = true;
            }else if(d.date.compareTo(holidayDate) >0){
                commingHolidays.add(d);
            }
        }
        
    }//end getHolidayListings
    

    private void closeDatabase() {
        try {
            connection.close();
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
    private class DatabaseEntry implements Comparable<DatabaseEntry> {
        private String desc, date, link;
        private int alwaysOnSameDay;
        
        public DatabaseEntry(int id, String name, String desc, String date, int aosd){
            update(id, name, desc, date, aosd);
        }
        
        private void update(int id, String name, String desc, String date, int aosd) {
            this.desc = desc;
            this.date = date;
            alwaysOnSameDay = aosd;
            link = String.format(" <a href=\"editDataPage.jsp?id=%d&name=%s&desc=%s&date=%s&same_day=%d\">%s</a> ", 
            id, name, desc, date, aosd, name);
        }
        
        
        @Override
        public String toString(){
            return String.format("Holiday Name: %s Holiday Desc: %s Holiday Date: %s AlwaysOnSameDay: %d", 
                    link, desc, date, alwaysOnSameDay);
        }

        @Override
        public int compareTo(DatabaseEntry d) {
            return date.compareTo(d.date);
        }
            

    }
}
