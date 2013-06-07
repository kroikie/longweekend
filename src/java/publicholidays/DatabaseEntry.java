package publicholidays;
//Need to make a better name for this that more accuratly describes it
//Such as HolidayEntry or DateEntry
//Consider changing the id and alwaysOnSameDay to short

import java.util.GregorianCalendar;

public class DatabaseEntry implements Comparable<DatabaseEntry> {
    
    private String desc, date, link;
    private int alwaysOnSameDay;

    
    public DatabaseEntry(int id, String name, String desc, String date, int aosd){
        this.desc = desc;
        this.date = date;
        alwaysOnSameDay = aosd;
        link = String.format("<a href=\"editDataPage.jsp?id=%d&name=%s&desc=%s&date=%s&same_day=%d\">%s</a>", 
        id, name, desc, date, aosd, name);
    }

    
    //Constuctor to take in user's dates
    public DatabaseEntry(String date){
        this(-1, "Your Date", "Your Date", date, 0);
    }
    
    
    
    private int getYear(){
        return Integer.parseInt(date.substring(0, 4));
    }
    
    private int getMonth(){
        return Integer.parseInt(date.substring(5, 7));
    }
    
    private int getDay(){
        return Integer.parseInt(date.substring(8));
    }
    
    public DatabaseEntry previousDatabaseEntry(){
        int year = getYear(), lastMonth = getMonth() - 1, day = getDay() - 1;
        boolean isLastMonth = false;
        
        if(lastMonth<=0){
            lastMonth = 12;
            year--;
        }
        
        if(day==0){//ie the last day of the previous month
            if(lastMonth == 1 || lastMonth == 3 || lastMonth == 5 || lastMonth == 7 || lastMonth == 10 || lastMonth == 12){
                isLastMonth = true;
                day = 31;
            }else if(lastMonth == 4 || lastMonth == 6 || lastMonth == 9 || lastMonth == 11){
                isLastMonth = true;
                day = 30;
            }else if( lastMonth == 2 ){
                isLastMonth = true;
                if(isLeapYear(year)){
                    day = 29;
                }else{
                    day = 28;
                }
            }
        }
        
        String previous = String.format("%d-%02d-%02d", year, isLastMonth?lastMonth:getMonth(), day);
        return new DatabaseEntry(previous);
    }
    
    public DatabaseEntry nextDatabaseEntry(){
        int year = getYear(), month = getMonth(), day = getDay() + 1;
        
        if((month == 1 || month == 3 || month == 5 || month == 7 || month == 10 || month == 12) && day > 31){
            month++;
            day = 01;
        }else if((month == 4 || month == 6 || month == 9 || month == 11) && day > 30){
            month++;
            day = 01;
        }else if( month == 2 ){
            if(isLeapYear(year) && day > 29){
                month++;
                day = 01;
            }else if(day > 28){
                month++;
                day = 01;  
            }
        }
        
        if(month>=13){
            year++;
            month = 01;
        }
        
        String next =  String.format("%d-%02d-%02d", year, month, day);
        return new DatabaseEntry(next);
    }
    
    private boolean isLeapYear(int y){
        if(y%400==0)
            return true;
        else if(y%100==0)
            return false;
        else if(y%4==0)
            return true;
        else
            return false;
    }
    
    
    public GregorianCalendar toGregorianCalendar(){
        //Subtract 1 from the Month to accommodate for the months starting from 0
        return new GregorianCalendar(getYear(), getMonth()-1, getDay());
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
