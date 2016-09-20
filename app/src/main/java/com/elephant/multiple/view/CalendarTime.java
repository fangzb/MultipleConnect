package com.elephant.multiple.view;

import java.util.Calendar;

public class CalendarTime {

    public int dateId;

    public int year, month, day;

    public CalendarTime(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        generateId();
    }

    public static CalendarTime create(int year, int month, int day) {
        return new CalendarTime(year, month, day);
    }

    public static CalendarTime create(int dayId) {
        int year = dayId / 10000;
        dayId = dayId % 10000;
        int month = dayId / 100;
        int day = dayId % 100;
        return new CalendarTime(year, month, day);
    }

    public int compareToDay(CalendarTime other) {
        return dateId - other.dateId;
    }

    public boolean isSameMonth(CalendarTime calendarTime) {
        return year == calendarTime.year && month == calendarTime.month;
    }

    public void generateId() {
        dateId = generateId(year, month, day);
    }

    public static int generateId(int year, int month, int day) {
        return day + 100 * month + 10000 * year;
    }

    public static CalendarTime createFromString(String time) {
        int dayId = Integer.parseInt(time.replaceAll("-", "")) - 100;
        return create(dayId);
    }

    @Override
    public String toString() {
        return "CalendarTime{" + dateId + '}';
    }

    /**
     * 20160103 转换成 2016-02-03
     * @return
     */
    public String parseToString() {
        return String.format("%d-%02d-%02d", year, month + 1, day);
    }

    /**
     * 转换成 Calendar对象
     * @return
     */
    public Calendar parseToCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar;
    }
}
