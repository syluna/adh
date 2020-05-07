/*
 * Copyright (c) 2012, Andreas Olofsson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.jayfella.jme.atmosphere;

/**
 * A basic calendar. Used to set sun direction and other things.
 *
 * @author Andreas
 * @author jayfella
 */
public final class Calendar {
    protected static float inv60 = 1 / 60f;

    protected byte[] DAYS = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    protected double minute;
    protected int hour;
    protected int day;
    protected int month;
    protected int year;

    protected int dayInYear;

    // Julian day number.
    protected long JDN;
    protected float tMult = 1f;

    protected String dateString = "";

    public Calendar(int year, int month, int day, int hour, int minute, float tMult) {
        reset(year, month, day, hour, minute, tMult);
    }

    public void reset(int year, int month, int day, int hour, int minute, float tMult) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.tMult = tMult;
        refresh();
    }

    public void refresh() {
        this.JDN = getJulianDayFromGregorian(year, month, day);
        updateDaysInYear();
        updateDateString();
    }

    public void update(float tpf) {

        minute += tpf * inv60 * tMult;

        if (minute >= 60) {
            minute = 0.0;
            updateHour();
        }
    }

    public void updateDaysInYear() {
        if (year % 4 == 0) {
            DAYS[1] = 29;
        } else {
            DAYS[1] = 28;
        }

        dayInYear = 0;
        for (int i = 0; i < month - 1; i++) {
            dayInYear += DAYS[i];
        }
        dayInYear += day;
    }

    public int getDayInYear() {
        return dayInYear;
    }

    protected void updateHour() {

        int oldDay = day;

        if (hour != 23) {
            hour++;
        } else {
            hour = 0;
            if (month == 12 && day == 31) {
                year++;
                month = 1;
                day = 1;
                updateDaysInYear();
            } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10
                    || month == 12) {
                if (day == 31) {
                    month++;
                    day = 1;
                } else {
                    day++;
                    hour = 0;
                }
                if (month == 4 || month == 6 || month == 9 || month == 11) {
                    if (day == 30) {
                        month++;
                        day = 1;
                    } else {
                        day++;
                    }
                }
                if (month == 2) {
                    if (day == 28 && year % 4 != 0) {
                        month++;
                        day = 1;
                    } else {
                        day++;
                    }
                    if (day == 29 && year % 4 == 0) {
                        month++;
                        day = 1;
                    }
                }
            }
            // Increment julian day and day in year.
            if (oldDay != day) {
                dayInYear += 1;
                JDN += 1;
            }
            updateDateString();
        }
    }

    public final long getJulianDayFromGregorian(int year, int month, int day) {

        int a = (14 - month) / 12;
        int m = month + 12 * a - 3;
        int y = year + 4800 - a;

        return day + (153 * m + 2) / 2 + 365 * y + y / 4 - y / 100 + y / 400 - 32045;
    }

    public float getTimeMult() {
        return tMult;
    }

    /**
     * Set the time multiplier. Passed time is calculated as 'time * tMult'. A tMult
     * value of 10 means every real-time second is equal to 10 app-time seconds.
     * 
     * @param tMult
     */
    public void setTimeMult(float tMult) {
        this.tMult = tMult;
    }

    // Returns date "yyyy/mm/dd"
    public String getDateString() {
        return dateString;
    }

    public void updateDateString() {
        // TODO use a proper formatter.
        StringBuilder s = new StringBuilder("Date: ");
        s.append(year).append("/");
        if (month < 10) {
            s.append("0");
        }
        s.append(month);
        s.append("/");
        if (day < 10) {
            s.append("0");
        }
        s.append(day);

        dateString = s.toString();
    }

    // Returns time "hh:mm"
    public String getTimeString() {
        StringBuilder s = new StringBuilder();
        if (hour < 10) {
            s.append("0");
        }
        s.append(hour).append(":");
        if (minute < 10) {
            s.append("0");
        }
        s.append((int) (minute));

        return s.toString();
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
        refresh();
    }

    public int getHour() {
        return hour;
    }

    public Calendar setHour(int hour) {
        this.hour = hour;
        refresh();

        return this;
    }

    public double getMinute() {
        return minute;
    }

    public double getSecond() {
        double frac = minute - Math.floor(minute);
        return 59.5d * frac;
    }

    public Calendar setMinute(double minute) {
        this.minute = minute;
        return this;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
        refresh();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
        refresh();
    }

    /**
     * Get the Julian day number.
     * 
     * @return
     */
    public long getJDN() {
        return JDN;
    }

    // http://blog.artofmemory.com/how-to-calculate-the-day-of-the-week-4203.html

    public int getDayInWeek() {
        return getDay(getDay(), getMonth(), getYear());
    }

    private int getDay(int day, int month, int year) {

        // take last 2 digits of year
        int yr = year % 100;
        int div = yr / 4;
        int add = yr + div;
        int yearCode = add % 7;

        int monthCodes[] = { 0, 3, 3, 6, 1, 4, 6, 2, 5, 0, 3, 5 };
        int monthCode = monthCodes[month - 1];

        // 1700's to 2300's
        int centuryCodes[] = { 4, 2, 0, 6, 4, 2, 0 };
        int yrE = Integer.parseInt(("" + year).substring(0, 2));
        int centuryCode = centuryCodes[yrE - 17];

        // If you can divide a Gregorian year by 4, it’s a leap year, unless it’s
        // divisible by 100.
        // But it is a leap year if it’s divisible by 400.
        boolean leapYear = (year % 4 == 0) && ((year % 100 != 0) | (year % 400 == 0));

        int calc = yearCode + monthCode + centuryCode + day;

        if (leapYear)
            calc -= 1;

        int res = calc % 7;
        return res;
    }

    public String getDayName() {
        return intToDayString(getDayInWeek());
    }

    private String intToDayString(int day) {

        switch (day) {
        case 0:
            return "Sunday";
        case 1:
            return "Monday";
        case 2:
            return "Tuesday";
        case 3:
            return "Wednesday";
        case 4:
            return "Thursday";
        case 5:
            return "Friday";
        case 6:
            return "Saturday";
        default:
            return "UNKNOWN DAY: " + day;
        }
    }

    public String getMonthName() {

        int month = getMonth();

        switch (month) {
        case 1:
            return "January";
        case 2:
            return "February";
        case 3:
            return "March";
        case 4:
            return "April";
        case 5:
            return "May";
        case 6:
            return "June";
        case 7:
            return "July";
        case 8:
            return "August";
        case 9:
            return "September";
        case 10:
            return "October";
        case 11:
            return "November";
        case 12:
            return "December";
        default:
            return "UNKNOWN MONTH: " + month;
        }

    }

    public int getDaysInCurrentMonth() {
        return DAYS[month - 1];
    }

    public int getDaysInMonth(int month) {
        return DAYS[month - 1];
    }

    public enum DayPart {
        Morning, // 05:00 - 11:59
        Afternoon, // 12:00 - 16:59
        Evening, // 17:00 - 20:59
        Night // 21:00 04:59
    }

    public DayPart getDayPart() {

        int hour = getHour();

        if (hour >= 5 && hour < 12) {
            return DayPart.Morning;
        } else if (hour >= 12 && hour < 17) {
            return DayPart.Afternoon;
        } else if (hour >= 17 && hour < 21) {
            return DayPart.Evening;
        }
        return DayPart.Night;
    }

    public void setDayPart(DayPart dayPart) {

        switch (dayPart) {
        case Morning:
            setHour(5).setMinute(0);
            break;
        case Afternoon:
            setHour(12).setMinute(0);
            break;
        case Evening:
            setHour(17).setMinute(0);
            break;
        case Night:
            setHour(21).setMinute(0);
            break;
        }

    }

}
