package publicholidays;

public class DatabaseEntry implements Comparable<DatabaseEntry> {
        private String desc, date, link;
        private int alwaysOnSameDay;
        
        public DatabaseEntry(int id, String name, String desc, String date, int aosd){
            update(id, name, desc, date, aosd);
        }
        
        public final void update(int id, String name, String desc, String date, int aosd) {
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
        
        //Overloaded Method to compare the dates passed by String
        public int compareTo(String d) {
            return date.compareTo(d);
        }

}
