package com.foohyfooh.publicholidays.entity;

import java.io.Serializable;
import java.util.GregorianCalendar;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "holidays")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DateEntry.findAll", query = "SELECT d FROM DateEntry d"),
    @NamedQuery(name = "DateEntry.findById", query = "SELECT d FROM DateEntry d WHERE d.id = :id"),
    @NamedQuery(name = "DateEntry.findByHolidayName", query = "SELECT d FROM DateEntry d WHERE d.holidayName = :holidayName"),
    @NamedQuery(name = "DateEntry.findByHolidayDate", query = "SELECT d FROM DateEntry d WHERE d.holidayDate = :holidayDate"),
    @NamedQuery(name = "DateEntry.findByAlwaysOnSameDay", query = "SELECT d FROM DateEntry d WHERE d.alwaysOnSameDay = :alwaysOnSameDay")})
public class DateEntry implements Serializable, Comparable<DateEntry> {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "holidayName")
    private String holidayName;
    @Lob
    @Column(name = "holidayDesc")
    private String holidayDesc;
    @Column(name = "holidayDate")
    private String holidayDate;
    @Column(name = "alwaysOnSameDay")
    private Integer alwaysOnSameDay;

    public transient static final int ALWAYS_ON_SAME_DAY = 1;
    
    public DateEntry() {
        id = -1;
        holidayName = holidayDate = holidayDesc = "";
        alwaysOnSameDay = 0;
    }

    public DateEntry(int id, String name, String desc, String date, int aosd) {
        this.id = id;
        holidayName = name;
        holidayDesc = desc;
        holidayDate = date;
        alwaysOnSameDay = aosd;
    }

    //Constuctor to take in user's dates
    public DateEntry(String date) {
        this(-1, "Your Date", "Your Date", date, 0);
    }

    //<editor-fold defaultstate="collapsed" desc="setter and getter methods">
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHolidayName() {
        return holidayName;
    }

    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    public String getHolidayDesc() {
        return holidayDesc;
    }

    public void setHolidayDesc(String holidayDesc) {
        this.holidayDesc = holidayDesc;
    }

    public String getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(String holidayDate) {
        this.holidayDate = holidayDate;
    }

    public Integer getAlwaysOnSameDay() {
        return alwaysOnSameDay;
    }

    public void setAlwaysOnSameDay(Integer alwaysOnSameDay) {
        this.alwaysOnSameDay = alwaysOnSameDay;
    }
    //</editor-fold>

    public DateEntry previousDate() {
        int year = getYear(), lastMonth = getMonth() - 1, day = getDay() - 1;
        boolean isLastMonth = false;

        if (day == 0) {//ie the last day of the previous month
            isLastMonth = true;
            if (lastMonth == 0) {//ie 12 of last year
                lastMonth = 12;
                day = 31;
                year--;
            } else if (lastMonth == 1 || lastMonth == 3 || lastMonth == 5 || lastMonth == 7 || lastMonth == 8 || lastMonth == 10) {
                day = 31;
            } else if (lastMonth == 4 || lastMonth == 6 || lastMonth == 9 || lastMonth == 11) {
                isLastMonth = true;
                day = 30;
            } else if (lastMonth == 2) {
                if (isLeapYear(year)) {
                    day = 29;
                } else {
                    day = 28;
                }
            }
        }

        String previous = String.format("%d-%02d-%02d", year, isLastMonth ? lastMonth : getMonth(), day);
        return new DateEntry(previous);
    }

    public DateEntry nextDate() {
        int year = getYear(), month = getMonth(), day = getDay() + 1;

        if ((month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) && day > 31) {
            month++;
            day = 1;
            if (month == 13) {
                year++;
                month = 1;
            }
        } else if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30) {
            month++;
            day = 1;
        } else if (month == 2) {
            if (isLeapYear(year) && day > 29) {
                month++;
                day = 1;
            } else if (day > 28) {
                month++;
                day = 1;
            }
        }

        String next = String.format("%d-%02d-%02d", year, month, day);
        return new DateEntry(next);
    }
    
    public DateEntry nextYear() {
        String date = String.format("%d-%02d-%02d", getYear()+1, getMonth(), getDay());
        return new DateEntry(-1, holidayName, holidayDesc, date, alwaysOnSameDay);
    }

    public GregorianCalendar toGregorianCalendar() {
        //Subtract 1 from the Month to accommodate for the GregorianCalender months starting from 0
        return new GregorianCalendar(getYear(), getMonth() - 1, getDay());
    }

    //Private Methods
    private int getYear() {
        return Integer.parseInt(holidayDate.split("-")[0]);
    }

    private int getMonth() {
        return Integer.parseInt(holidayDate.split("-")[1]);
    }

    private int getDay() {
        return Integer.parseInt(holidayDate.split("-")[2]);
    }

    private boolean isLeapYear(int y) {
        if (y % 400 == 0) {
            return true;
        } else if (y % 100 == 0) {
            return false;
        } else if (y % 4 == 0) {
            return true;
        }
        return false;
    }

    //Overriden Methods
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DateEntry)) {
            return false;
        }
        DateEntry other = (DateEntry) object;
        if ((this.holidayDate == null && other.holidayDate != null) || (this.holidayDate != null && !this.holidayDate.equals(other.holidayDate))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String link;
        if(id != -1){
            link = String.format("<a href=\"updateEntry.jsp?id=%d&name=%s&desc=%s&date=%s&same_day=%d\">%s</a>",
                id, holidayName, holidayDesc, holidayDate, alwaysOnSameDay, holidayName);
        }else{
            link = holidayName;
        }
        return String.format("Holiday Name: %s Holiday Desc: %s Holiday Date: %s AlwaysOnSameDay: %d",
                link, holidayDesc, holidayDate, alwaysOnSameDay);
    }

    @Override
    public int compareTo(DateEntry d) {
        return holidayDate.compareTo(d.holidayDate);
    }
}
