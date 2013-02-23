package publicholidays;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JOptionPane;

//TODO: Make a method to deal with holidays that are TBA
//TODO: Work on findLongWeekend Method

public class ModifyDatabase {
    
    //i am working well
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private String query;
    private ArrayList<DatabaseEntry> database, pastHolidays, comingHolidays;
    
    
    public ModifyDatabase(){
        openDatabase();
        getData();
        //Not implemented so that as some holidays in 2013 have TBA
        //sortData();
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
    
    //Using Selection Sort to sort the database
    private void sortData(){
        
        int indexOfEarliest;
        for(int i = 0;i < database.size()-1;i++){
            indexOfEarliest = i;
            for(int j = i+1; j < database.size(); j++){
                if(database.get(j).compareTo(database.get(indexOfEarliest)) < 0){
                    indexOfEarliest = j;
                }
            }
            
            if( indexOfEarliest != i){
                Collections.swap(database, i, indexOfEarliest);
            }
        }
        
    }//end sortData
    
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
        final ArrayList<DatabaseEntry> copy = database;
        return copy;
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
        comingHolidays = new ArrayList<DatabaseEntry>();
        
        for(DatabaseEntry d : database){
            if(d.compareTo(holidayDate) < 0){
                pastHolidays.add(d);
            }else if(d.compareTo(holidayDate) == 0){
                sameDay = true;
            }else if(d.compareTo(holidayDate) >0){
                comingHolidays.add(d);
            }
        }
        
    }//end getHolidayListings
    
    
    private void findLongWeekend(){
        
    }//end findLongWeekend
    
    //These methods return a final copy of the database so that the original data can't be tampered with by the JSP
    public ArrayList<DatabaseEntry> getPastHolidays(){
        final ArrayList<DatabaseEntry> copy = pastHolidays;
        return  copy;
    }
    
    public ArrayList<DatabaseEntry> getComingHolidays(){
        final ArrayList<DatabaseEntry> copy = comingHolidays;
        return copy;
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
