package publicholidays;

import entity.DateEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.swing.JOptionPane;

public class Database {

    public final static int LONG_WEEKEND_BEFORE = 0;
    public final static int LONG_WEEKEND_AFTER = 1;
    
    private EntityManager entityManger;
    private List<DateEntry> holidays;

    public Database() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("holidays");
        entityManger = factory.createEntityManager();
        Query query = entityManger.createNamedQuery("DateEntry.findAll", DateEntry.class);
        holidays = query.getResultList();
    }

    public List<DateEntry> getHolidays() {
        return holidays;
    }//end getHolidays

    public void add(HttpServletRequest request) {
        //The next id will be the size as the List is zero indexed
        DateEntry toAdd = new DateEntry(holidays.size(), request.getParameter("name"),
                request.getParameter("desc"), request.getParameter("date"), Integer.parseInt(request.getParameter("same_day")));
        persist(toAdd);
    }//end add

    public void update(HttpServletRequest request) {
        DateEntry update = holidays.get(Integer.parseInt(request.getParameter("id")) - 1);
        update.setHolidayName(request.getParameter("name"));
        update.setHolidayDesc(request.getParameter("desc"));
        update.setHolidayDate(request.getParameter("date"));
        //update.setAlwaysOnSameDay(Integer.parseInt(request.getParameter("same_day")));
        persist(update);
    }//end update

    public void print(JspWriter out) {
        try {
            for (DateEntry d : holidays) {
                out.print(d + "<br/>");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }//end print

    public void findLongWeekend(HttpServletRequest request, JspWriter out) {
        /*Pseudocode for findLongWeekend
         * while there still more holidays in comingHolidays
         * if the next day is a public holiday or saturday or Sunday or Monday after Sunday holiday
         * print it
         * else if the holidays collides skip the next day as it will be a holiday
         * end if
         * end while
         */        
        try {
            DateEntry current = new DateEntry(request.getParameter("startDate"));
            do {
                ArrayList<Object> collision = isHolidayColliding(current);
                if ((Boolean) collision.get(0)) {
                    //Print the colliding dates
                    out.print((DateEntry) collision.get(1) + "</br>");
                    out.print((DateEntry) collision.get(2) + "</br>");
                    DateEntry next = (DateEntry) collision.get(2);
                    current = next.nextDate();//Get the next date to modify 
                    current.setHolidayName("Honorary Date");
                    current.setHolidayDesc("Honorary Date");
                    out.print(current + "</br>");
                    current = current.nextDate();
                    continue;
                }
                for (DateEntry d : holidays) {
                    if (d.getHolidayDate().equals(current.getHolidayDate())) {
                        current = d;
                        break;
                    }
                }
                    out.print(current + "</br>");
                    current = current.nextDate();
            } while (isHoliday(current) || isWeekend(current) || isMondayAfterHoliday(current));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }//end findLongWeekend

    public List findLongWeekend(HttpServletRequest request, int selector) {
        List<List<DateEntry>> allLongWeekends = new ArrayList<List<DateEntry>>();
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        
        //Set up a list of possible long weekend candidates
        List<DateEntry> toTraverse = new ArrayList<DateEntry>();
	for(DateEntry d: holidays){
            if(startDate.compareTo(d.getHolidayDate()) <= 0 
                            && endDate.compareTo(d.getHolidayDate()) >= 0 ){
                //Get the first possible day in a long weekend
                toTraverse.add(getStartDate(d));
            }
	}
        //Remove duplicates due to the proximity of some holidays to each other
        toTraverse = removeDuplicates(toTraverse);
        
        for(int i = 0; i < toTraverse.size(); i++){
            List<DateEntry> longweekend = new ArrayList<DateEntry>();
            DateEntry current = toTraverse.get(i);
            while (isHoliday(current) || isWeekend(current) || isMondayAfterHoliday(current)) {
                ArrayList<Object> collision = isHolidayColliding(current);
                if ((Boolean) collision.get(0)) {
                    longweekend.add((DateEntry) collision.get(1));//Add the first colliding date
                    longweekend.add((DateEntry) collision.get(2));//Add the second colliding date
                    DateEntry next = (DateEntry) collision.get(2);
                    current = next.nextDate();//Get the next date to modify
                    current.setHolidayName("Honorary Date");
                    current.setHolidayDesc("Honorary Date");
                    longweekend.add(current);
                    current = current.nextDate();
                    continue;
                }
                for (DateEntry d : holidays) {
                    if (d.getHolidayDate().equals(current.getHolidayDate())) {
                        current = d;
                        break;
                    }
                }
                longweekend.add(current);
                current = current.nextDate();
            }
            if (!longweekend.isEmpty()) {
                allLongWeekends.add(longweekend);
            }
        }
        
        if(true)
            return allLongWeekends;
        
        switch(selector){
            case LONG_WEEKEND_BEFORE:
                return allLongWeekends.get(0);
            case LONG_WEEKEND_AFTER:
                return allLongWeekends.get(allLongWeekends.size()-1);
             default:
                 return Collections.EMPTY_LIST;
        }
    }//end findLongWeekend

    //Priavte Methods
    private void persist(DateEntry d) {
        entityManger.getTransaction().begin();
        entityManger.persist(d);
        entityManger.getTransaction().commit();
        entityManger.close();
    }//end persist

    private boolean isHoliday(DateEntry d) {
        return holidays.contains(d);
    }//end isHoliday

    private boolean isWeekend(DateEntry d) {
        GregorianCalendar date = d.toGregorianCalendar();
        if (date.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY) {
            d.setHolidayName("Saturday");
            d.setHolidayDesc("Saturday");
            return true;
        } else if (date.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY) {
            d.setHolidayName("Sunday");
            d.setHolidayDesc("Sunday");
            return true;
        }
        return false;
    }//end isWeekend

    private boolean isMondayAfterHoliday(DateEntry d) {
        DateEntry previous = d.previousDate();
        if (!isHoliday(previous)) {
            return false;
        }

        GregorianCalendar previousDay = previous.toGregorianCalendar();
        GregorianCalendar day = d.toGregorianCalendar();
        if (previousDay.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY
                && day.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.MONDAY) {
            d.setHolidayName("Honorary Date");
            d.setHolidayDesc("Honorary Date");
            return true;
        }
        return false;
    }//end isMondayAfterHoliday

    private ArrayList<Object> isHolidayColliding(DateEntry d) {
        /*Info on data returned by this method
         * The first return value is a Boolean to know if the holidays collided
         * The second return value is the first date the same day
         * The third is the second date that had the same day
         * It returns true if the date passed in is colliding
         */
        
        //Reducing the amount of holidays to check for collision
        ArrayList<DateEntry> holidaySet = new ArrayList<DateEntry>();
        for(DateEntry c: holidays){
            if(d.compareTo(d) >= 0){
                holidaySet.add(c);
            }
        }
        ArrayList<Object> collision = new ArrayList<Object>();
        DateEntry current, next;
        for (int outerCounter = 0; outerCounter < holidaySet.size(); outerCounter++) {
            current = holidaySet.get(outerCounter);
            for (int innerCounter = 1; innerCounter < holidaySet.size(); innerCounter++) {
                next = holidaySet.get(innerCounter);

                //If the date is the samebut different names you get the next day
                if (current.getHolidayDate().equals(next.getHolidayDate())
                        && !current.getHolidayName().equals(next.getHolidayName())) {
                    if (d.getHolidayDate().equals(current.getHolidayDate())) {
                        collision.add(true);
                        collision.add(current);
                        collision.add(next);
                        return collision;//The date passed is a collision
                    }
                }
            }
        }
        collision.add(false);
        return collision;
    }//end isHolidayColliding
    
    private DateEntry getStartDate(DateEntry d){
        GregorianCalendar date = d.toGregorianCalendar();
        if(date.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY)
            return d.previousDate();//Set the date to the saturday
        else if(date.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.MONDAY)
            return d.previousDate();//Set the date to the saturdya
        return d;//Tuesday-Saturday
    }//end getStartDate
    
    private List<DateEntry> removeDuplicates(List<DateEntry> list){
        //Conver the list to a map which can only have one copy of a key
        //holidayDate is used since it the property that indicates a duplicate
        HashMap<String, DateEntry> map = new HashMap<String, DateEntry>();
        for(DateEntry d: list){
            map.put(d.getHolidayDate(), d);
        }
        //Convert the map back to a list to be used
        List<DateEntry> newList = new ArrayList<DateEntry>(map.values());
        Collections.sort(newList);
        return newList;
    }//end removeDuplicates
}
