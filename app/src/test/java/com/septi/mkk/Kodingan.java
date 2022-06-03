package com.septi.mkk;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class Kodingan {
    @Test
    public void kodinganTest() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy.HHmmss");
        System.out.println("Build " + sdf.format(System.currentTimeMillis()));
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
    public void arr4Dimensi() {
    }
}
