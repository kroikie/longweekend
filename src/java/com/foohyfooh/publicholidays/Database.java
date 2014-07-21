package com.foohyfooh.publicholidays;

import com.foohyfooh.publicholidays.entity.DateEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import org.json.JSONArray;
import org.json.JSONObject;

public class Database {

    public static final int LONG_WEEKEND_BEFORE = 0;
    public static final int LONG_WEEKEND_AFTER = 1;
    
    private final EntityManager entityManger;
    private final List<DateEntry> holidays;
    private final int nextId;

    public Database() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("holidays");
        entityManger = factory.createEntityManager();
        Query query = entityManger.createNamedQuery("DateEntry.findAll", DateEntry.class);
        holidays = query.getResultList();
        nextId = holidays.get(holidays.size() - 1).getId() + 1;
        
        //Modifing the list to add the hoidays that are on the same day each year
        List<DateEntry> nextYearHolidays = new ArrayList<>(), 
                yearAfterNextHolidays = new ArrayList<>();
        for(DateEntry d: holidays){
            if(d.getAlwaysOnSameDay() == DateEntry.ALWAYS_ON_SAME_DAY){
                DateEntry nextYearVersion = d.nextYear();
                nextYearHolidays.add(nextYearVersion);
                yearAfterNextHolidays.add(nextYearVersion.nextYear());
            }
        }
        holidays.addAll(nextYearHolidays);
        holidays.addAll(yearAfterNextHolidays);
        Collections.sort(holidays);
    }

    public List<DateEntry> getHolidays() {
        return holidays;
    }

    public void add(HttpServletRequest request) {
        String  name = request.getParameter("name"), 
                desc = request.getParameter("desc"),
                date = request.getParameter("date"), 
                same_day = request.getParameter("same_day");
        
        if(name == null || desc== null || same_day == null) return;
        
        DateEntry toAdd = new DateEntry(nextId, name, desc, date, Integer.parseInt(same_day));
        persist(toAdd);
    }

    public void update(HttpServletRequest request) {
        String  id = request.getParameter("id"), 
                name = request.getParameter("name"), 
                desc = request.getParameter("desc"),
                date = request.getParameter("date"), 
                same_day = request.getParameter("same_day");
        
        if(id == null || name == null || desc== null || same_day == null) return;
        
        DateEntry update = new DateEntry(Integer.parseInt(id),
                 name ,desc, date,
                 Integer.parseInt(same_day));
        persist(update);
    }

    public void print(JspWriter out) {
        try {
            for (DateEntry d : holidays) {
                if(d.getId() != -1)
                    out.print(d + "<br/>");
            }
        } catch (IOException e) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public List findLongWeekend(HttpServletRequest request) {
        List<List<DateEntry>> allLongWeekends = new ArrayList<>();
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        getUserDates(request);
        
        //Set up a list of possible long weekend candidates
        List<DateEntry> toTraverse = new ArrayList<>();
	for(DateEntry d: holidays){
            if(startDate.compareTo(d.getHolidayDate()) <= 0 
                            && endDate.compareTo(d.getHolidayDate()) >= 0 ){
                //Get the first possible day in a long weekend
                toTraverse.add(getStartDate(d));
            }
	}        
        
        for(DateEntry current: toTraverse){
            List<DateEntry> longweekend = new ArrayList<>();
            while (isHoliday(current) || isWeekend(current) || isMondayAfterHoliday(current)) {
                ArrayList<Object> collision = isHolidayColliding(current);
                if ((Boolean) collision.get(0)) {
                    longweekend.add((DateEntry) collision.get(1));//Add the first colliding date
                    longweekend.add((DateEntry) collision.get(2));//Add the second colliding date
                    DateEntry next = (DateEntry) collision.get(2);
                    current = next.nextDate();//Get the nextId date to modify
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
            allLongWeekends.add(longweekend);
        }
        removeIntersecting(allLongWeekends);
        
        String s = request.getParameter("selector");
        int selector =  s != null ? Integer.parseInt(s) : LONG_WEEKEND_AFTER;
        switch(selector){
            case LONG_WEEKEND_BEFORE:
                return allLongWeekends.get(allLongWeekends.size()-1);
            case LONG_WEEKEND_AFTER:
                return allLongWeekends.get(0);
            default:
                return allLongWeekends.get(0);
        }
    }

    //Priavte Methods
    private void persist(DateEntry d) {
        entityManger.getTransaction().begin();
        entityManger.persist(d);
        entityManger.getTransaction().commit();
        entityManger.close();
    }

    private boolean isHoliday(DateEntry d) {
        return holidays.contains(d);
    }

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
    }

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
    }

    private ArrayList<Object> isHolidayColliding(DateEntry d) {
        /*Info on data returned by this method
         * The first return value is a Boolean to know if the holidays collided
         * The second return value is the first date the same day
         * The third is the second date that had the same day
         * It returns true if the date passed in is colliding
         */
        
        //Reducing the amount of holidays to check for collision
        ArrayList<DateEntry> holidaySet = new ArrayList<>();
        for(DateEntry c: holidays){
            if(d.compareTo(d) >= 0){
                holidaySet.add(c);
            }
        }
        ArrayList<Object> collision = new ArrayList<>();
        DateEntry current, next;
        for (int outerCounter = 0; outerCounter < holidaySet.size(); outerCounter++) {
            current = holidaySet.get(outerCounter);
            for (int innerCounter = 1; innerCounter < holidaySet.size(); innerCounter++) {
                next = holidaySet.get(innerCounter);

                //If the date is the samebut different names you get the nextId day
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
    }
    
    private DateEntry getStartDate(DateEntry d){
        GregorianCalendar date = d.toGregorianCalendar();
        if(date.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY)
            return d.previousDate();//Set the date to the saturday
        else if(date.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.MONDAY)
            return d.previousDate().previousDate();//Set the date to the saturday
        return d;//Tuesday-Saturday
    }
    
    public void removeIntersecting(List<List<DateEntry>> list) {
        //Assumes that the first contains all the elements 
        for(int i = 1; i < list.size(); i++){
            List<DateEntry> list1 = list.get(i-1), list2 = list.get(i);
            if(listIntersect(list1, list2)){
                list.remove(i);
                i = 1;
            }
        }
    }
    
    public boolean listIntersect(List<DateEntry> list1, List<DateEntry> list2) {
        for(DateEntry d: list2){
            if(list1.contains(d)){
                return true;
            }
        }
        return false;
    }

    private void getUserDates(HttpServletRequest request){
        String userDatesJSON = request.getParameter("userDates");
        if(userDatesJSON == null || userDatesJSON.isEmpty())
            return;
        
        JSONArray userDates = new JSONArray(userDatesJSON);
        for(int i = 0;i < userDates.length(); i++){
            JSONObject jsonObject = userDates.getJSONObject(i);
            DateEntry add = new DateEntry(jsonObject.getString("date"));
            add.setHolidayName(jsonObject.getString("name"));
            add.setHolidayDesc(jsonObject.getString("desc"));
            holidays.add(add);
        }
        
        Collections.sort(holidays);
    }
}
