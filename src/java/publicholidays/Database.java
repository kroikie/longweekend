package publicholidays;

import entity.DateEntry;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.swing.JOptionPane;

public class Database {
    
    private EntityManagerFactory factory; 
    private EntityManager entityManger;
    private Query query;
    private List<DateEntry> holidays;
    private ArrayList<DateEntry> past, coming;
    
    
    public Database(){
        factory = Persistence.createEntityManagerFactory("holidays");
        entityManger = factory.createEntityManager();
        getHolidays();
    }
    
    public List<DateEntry> getList(){
        return holidays;
    }//end getList
    
    public void add(HttpServletRequest request){
        entityManger.getTransaction().begin();
        //The next id will be the size as the List is zero indexed
        DateEntry toAdd = new DateEntry(holidays.size(), request.getParameter("name"), 
                request.getParameter("desc"), request.getParameter("date")
                , Integer.parseInt(request.getParameter("same_day"))
        );
        entityManger.persist(toAdd);
        entityManger.getTransaction().commit();
        entityManger.close();
    }//end add
    
    public void update(HttpServletRequest request){
        entityManger.getTransaction().begin();
        DateEntry update = holidays.get(Integer.parseInt(request.getParameter("id"))-1);
        update.setHolidayName(request.getParameter("name"));
        update.setHolidayDesc(request.getParameter("desc"));
        update.setHolidayDate(request.getParameter("date"));
        update.setAlwaysOnSameDay(Integer.parseInt(request.getParameter("same_day")));
        entityManger.persist(update);
        entityManger.getTransaction().commit();
        entityManger.close();
    }//end update
    
    public void print(JspWriter out){
        try{
            for(DateEntry d: holidays)
                out.print(d+"<br/>");
        }catch(IOException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        
    }//end print
    
    public void print(PrintWriter out){
        for(DateEntry d: holidays)
            out.print(d+"<br/>");
    }//end print
    
    public void setListings(HttpServletRequest request){
        DateEntry yourDate = new DateEntry(request.getParameter("yourDate"));
        past = new ArrayList<DateEntry>();
        coming = new ArrayList<DateEntry>();
        for(DateEntry current: holidays){
            if(yourDate.compareTo(current) < 0){
                past.add(current);
            }else{//ie these are on or after the date specified
                coming.add(current);
            }
        }
        
    }
    
    public void findLongWeekend(HttpServletRequest request, JspWriter out){
        
        try{
            DateEntry current = new DateEntry(request.getParameter("yourDate"));
            do{
                ArrayList<Object> collision = isHolidayColliding(current);
                if((Boolean)collision.get(0)){
                    current = (DateEntry) collision.get(1);
                    out.print(current+"<br/>");//Print first clashing day
                    current = (DateEntry) collision.get(2);
                    out.print(current+"<br/>");//Print secondd clashing day
                    current = current.nextDate();
                    current.setHolidayName("Honorary Date");
                    out.print(current+"<br/>");//Print the day after
                    current = current.nextDate();
                    continue;
                }
                for(DateEntry d: coming){
                   if(d.compareTo(current)==0){
                       current = d;
                       break;
                   } 
                }
                out.print(current+"<br/>");
                current = current.nextDate();
            }while(isHoliday(current) || isWeekend(current) || isMondayAfterHoliday(current));
        }catch(IOException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        
    }
    
    public List findLongWeekend(HttpServletRequest request){
        
        List<DateEntry> longweekend = new ArrayList<DateEntry>();
        DateEntry current = new DateEntry(request.getParameter("yourDate"));
        do{
            ArrayList<Object> collision = isHolidayColliding(current);
            if((Boolean)collision.get(0)){
                longweekend.add((DateEntry) collision.get(1));
                longweekend.add((DateEntry) collision.get(2));
                DateEntry next = (DateEntry)collision.get(2);
                current = next.nextDate();
                current.setHolidayName("Honorary Date");
                longweekend.add(current);
                current = current.nextDate();
                continue;
            }
            for(DateEntry d: coming){
               if(d.compareTo(current)==0){
                   current = d;
                   break;
               } 
            }
            longweekend.add(current);
            current = current.nextDate();
        }while(isHoliday(current) || isWeekend(current) || isMondayAfterHoliday(current));
        
        
        
        return longweekend;
    }
    
    
    //Priavte Methods
    private void getHolidays(){
        query = entityManger.createNamedQuery("DateEntry.findAll", DateEntry.class);
        holidays = query.getResultList();
    }//getHolidays
    
    private boolean isHoliday(DateEntry d){
        return holidays.contains(d);
    }//end isHoliday
    
    private boolean isWeekend(DateEntry d){
         //Subtract 1 from the Month to accommodate for the months starting from 0
        GregorianCalendar date = d.toGregorianCalendar();
        if(date.get(GregorianCalendar.DAY_OF_WEEK)==GregorianCalendar.SATURDAY ||
           date.get(GregorianCalendar.DAY_OF_WEEK)==GregorianCalendar.SUNDAY){
            return true; 
        }
        return false;
    }//end isWeekend
    
    private boolean isMondayAfterHoliday(DateEntry d){
        DateEntry previous = d.previousDate();
        if(!isHoliday(previous))
            return false;
        
        GregorianCalendar previousDay = previous.toGregorianCalendar();
        GregorianCalendar day = d.toGregorianCalendar();
        if(previousDay.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY 
                && day.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.MONDAY){
            return true;
        }
        return false;
    }//end isMondayAfterHoliday
    
    private ArrayList<Object> isHolidayColliding(DateEntry d){
        //Need a way to start make this start searching for holidays after the one passed in
        ArrayList<Object> collision = new ArrayList<Object>();
        if(coming.size() < 2){
            collision.add(false);
            return collision;
        }
        DateEntry current = coming.get(0);
        DateEntry next = coming.get(1); 
        for(int outerCounter = 1; outerCounter < coming.size(); outerCounter++){
            for(int innerCounter = 2; innerCounter < coming.size(); innerCounter++ ){
                
                //If the date and the link are different then the holidays collide
                //You get the next day
                if( current.getHolidayDate().equals(next.getHolidayDate()) 
                        && !current.toString().equals(next.toString()) ){
                    if(d.getHolidayDate().equals(current.getHolidayDate())){
                        collision.add(true);
                        collision.add(current);
			collision.add(next);
                        return collision;//The date passed is a collision
                    }
                }
                next = coming.get(innerCounter);
            }
            current = coming.get(outerCounter);
        }
        
        collision.add(false);
        return collision;
    }//end isHolidayColliding
    
}
