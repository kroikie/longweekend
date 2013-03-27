package publicholidays;
//Need to make a better name for this that more accuratly describes it
//Such as HolidayEntry or DateEntry
public class DatabaseEntry implements Comparable<DatabaseEntry> {
    
    private String name, desc, date, link;
    private int id, alwaysOnSameDay;

    
    public DatabaseEntry(int id, String name, String desc, String date, int aosd){
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.date = date;
        alwaysOnSameDay = aosd;
        link = String.format(" <a href=\"editDataPage.jsp?id=%d&name=%s&desc=%s&date=%s&same_day=%d\">%s</a> ", 
        id, name, desc, date, aosd, name);
    }

    
    //Constuctor to take in user's dates
    public DatabaseEntry(String date){
        this(-1, "Your Date", "Your Date", date, 0);
    }
    
    
    
    public int getID(){
        return id;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDate(){
        return date;
    }
    
    public String getDesc(){
        return desc;
    }
    
    public int alwaysOnSameDate(){
        return alwaysOnSameDay;
    }
    
    
    public int getYear(){
        return Integer.parseInt(date.substring(0, 4));
    }
    
    public int getMonth(){
        return Integer.parseInt(date.substring(5, 7));
    }
    
    public int getDay(){
        return Integer.parseInt(date.substring(8));
    }
    
    
    public DatabaseEntry nextDatabaseEntry(){
        int year = getYear();
        int month = getMonth();
        int day = getDay() + 1;//Get the next value of the date
        
        
        if((month == 1 || month == 3 || month == 5 || month == 7 || month == 10 || month == 12) && day>31){
            month++;
            day = 01;
        }else if((month == 4 || month == 6 || month == 9 || month == 11) && day>30){
            month++;
            day = 01;
        }else if( month == 2 ){
            if(isLeapYear(year)){
                if(day>29){
                    month++;
                    day = 01;
                }
            }else{
                if(day>28){
                    month++;
                    day = 01;
                }
            }
        }
        
        if(month>13){
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
