package com.septi.mkk;

import org.junit.Test;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TestLabKodingan {
    @Test
    public void kodinganTest() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy.HHmmss");
        System.out.println("Build " + sdf.format(System.currentTimeMillis()));
    }

    @Test
    public void timeMilis2ReadableFormat() {
        long yourmilliseconds = System.currentTimeMillis();
        System.out.println("Epoch Milliseconds : " + yourmilliseconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date resultdate = new Date(yourmilliseconds);
        System.out.println("Result Convert : " + sdf.format(resultdate));
    }

    @Test
    public void sizeOfMonth() {
        // Get the number of days in that month
        YearMonth yearMonthObject = YearMonth.of(2022, 5);
        int daysInMonth = yearMonthObject.lengthOfMonth();

        System.out.println(daysInMonth);
    }

    @Test
    public void getYear() {
        SimpleDateFormat formatingDate = new SimpleDateFormat("MM");
        String tahun = formatingDate.format(System.currentTimeMillis());

        System.out.println(tahun);
    }

    @Test
    public void getMonthInteger() {
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int month = localDate.getMonthValue();
        System.out.println(month);
    }

    @Test
    public void timesAgo() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date past = format.parse("17/06/2022");
            Date now = new Date();

            System.out.println(TimeUnit.MILLISECONDS.toMillis(now.getTime() - past.getTime()) + " milliseconds ago");
            System.out.println(TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + " minutes ago");
            System.out.println(TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + " hours ago");
            System.out.println(TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " days ago");
        }
        catch (Exception j){
            j.printStackTrace();
        }
    }

    @Test
    public void dateFormater() {
        /*
         * Menampilkan tanggal dengan formatter pattern
         * */
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Default : "+ now);
        System.out.println("Formater : " + dtf.format(now));
    }

    @Test
    public void timeMilis2Date() {
        long millis = System.currentTimeMillis();
        System.out.println("Today's date in millisecond : " + millis);

        Date date = new Date(millis);
        System.out.println("Convert to date by millis : " + date);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        System.out.println("Today's date by calendar : "+ sdf.format(cal.getTime()));

        Format format2DayString = new SimpleDateFormat("EEEE");
        Format format2MonthString = new SimpleDateFormat("dd MMMM yyyy");
        Format format2TimeString = new SimpleDateFormat("HH:mm:ss");

        String stringDay = format2DayString.format(new Date());
        String stringMonth = format2MonthString.format(new Date());
        String stringTimes = format2TimeString.format(new Date());

        System.out.println("Get Day : " + stringDay);
        System.out.println("Get Month : " + stringMonth);
        System.out.println("Get Times : " + stringTimes);
    }

    @Test
    public void convertDateString() throws ParseException {
        String stringDate = "19/06/2022 14:11:12";

        System.out.println("From String Date : " + stringDate);

        DateFormat assignPattern = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
        Date convertResultDateDefault = assignPattern.parse(stringDate);

        System.out.println("Convert by Default : " + convertResultDateDefault);

        SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        Date parsedDate = sdf.parse(String.valueOf(convertResultDateDefault));
        SimpleDateFormat print = new SimpleDateFormat("dd MMMM yyyy HH:mm");

        System.out.println("Final Result : " + print.format(parsedDate) + " WIB");
    }
}
