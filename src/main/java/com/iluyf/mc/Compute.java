package com.iluyf.mc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public final class Compute {

    public static final byte COMMON_YEAR_MONTH_N = 27; // 平年的月数
    private static final byte COMMON_MONTH_DAY_N = 20; // 小月的天数
    private static final byte YEAR_CYCLE = 29; // 闰年周期的年数
    private static final byte MONTH_CYCLE = 10; // 每周期的闰年数
    private static final byte CYCLE_LEAP_YEAR_N = 10; // 每周期的闰年数
    private static final byte CYCLE_GREATER_MONTH_N = 3; // 每周期的大月数
    private static final short YEAR_CYCLE_MONTH_N = YEAR_CYCLE * COMMON_YEAR_MONTH_N + CYCLE_LEAP_YEAR_N; // 闰年周期的月数
    private static final short MONTH_CYCLE_DAY_N = MONTH_CYCLE * COMMON_MONTH_DAY_N + CYCLE_GREATER_MONTH_N; // 大月周期的天数
    private static final Byte[] LEAP_YEARS = {1, 4, 7, 10, 13, 15, 18, 21, 24, 27}; // 每个周期的闰年
    private static final Set<Byte> LEAP_YEARS_SET = new HashSet<>(Arrays.asList(LEAP_YEARS));// 每个周期的闰年
    private static final Byte[] GREATER_MONTHS = {1, 4, 8}; // 每个周期的大月
    private static final Set<Byte> GREATER_MONTHS_SET = new HashSet<>(Arrays.asList(GREATER_MONTHS)); // 每个周期的大月
    private static final short[] YEAR_CYCLE_FIRST_MONTH_I = new short[YEAR_CYCLE]; // 闰年周期中，每年的首月所处的月数戳
    private static final short[] MONTH_CYCLE_FIRST_DAY_I = new short[MONTH_CYCLE]; // 大月周期中，每月的首日所处的天数戳
    private static final String NUM_STR = "〇一二三四五六七八九";
    private static final String MONTH_STR = "寂雪海夜彗凉芷茸雨花梦音晴岚萝苏茜梨荷茶茉铃信瑶风叶霜奈";
    private static final String DAY_STR = "初十廿";

    static final class YearMonth {

        long year;
        byte month;
    }

    static final class MonthDay {

        long month;
        byte day;
    }

    static final Component output(long annoDay) {
        return annoToString(annoDay);
    }

    static {
        // 计算闰年
        YEAR_CYCLE_FIRST_MONTH_I[0] = 0;
        for (byte i = 0; YEAR_CYCLE - 1 > i; ++i) {
            YEAR_CYCLE_FIRST_MONTH_I[i + 1] = (short) (YEAR_CYCLE_FIRST_MONTH_I[i] + COMMON_YEAR_MONTH_N);
            if (!isCommonYear(i)) {
                ++YEAR_CYCLE_FIRST_MONTH_I[i + 1];
            }
        }

        // 计算大月
        MONTH_CYCLE_FIRST_DAY_I[0] = 0;
        for (byte i = 0; MONTH_CYCLE - 1 > i; ++i) {
            MONTH_CYCLE_FIRST_DAY_I[i + 1] = (short) (MONTH_CYCLE_FIRST_DAY_I[i] + COMMON_MONTH_DAY_N);
            if (!isCommonMonth(i)) {
                ++MONTH_CYCLE_FIRST_DAY_I[i + 1];
            }
        }
    }

    // 判断是否平年
    static final boolean isCommonYear(long year) {
        return !LEAP_YEARS_SET.contains((byte) (year % YEAR_CYCLE));
    }

    // 判断是否小月
    static final boolean isCommonMonth(long month) {
        return !GREATER_MONTHS_SET.contains((byte) (month % MONTH_CYCLE));
    }

    // 返回月数戳对应的年数戳、月份
    static final YearMonth toYearMonth(long month) {
        YearMonth yearMonthNumber = new YearMonth();
        short netMonth = (short) (month % YEAR_CYCLE_MONTH_N);
        int i = (byte) Arrays.binarySearch(YEAR_CYCLE_FIRST_MONTH_I, netMonth);
        if (i < 0) {
            i = -i - 2;
        }
        yearMonthNumber.year = month / YEAR_CYCLE_MONTH_N * YEAR_CYCLE + i;
        yearMonthNumber.month = (byte) (netMonth - YEAR_CYCLE_FIRST_MONTH_I[i]);
        // 如果是平年，月份序号整体增加 1
        if (isCommonYear(yearMonthNumber.year)) {
            yearMonthNumber.month++;
        }
        return yearMonthNumber;
    }

    // 返回天数戳对应的月数戳、日期
    static final MonthDay toMonthDay(long day) {
        MonthDay monthDayNumber = new MonthDay();
        short netDay = (short) (day % MONTH_CYCLE_DAY_N);
        int i = (byte) Arrays.binarySearch(MONTH_CYCLE_FIRST_DAY_I, netDay);
        if (i < 0) {
            i = -i - 2;
        }
        monthDayNumber.month = day / MONTH_CYCLE_DAY_N * MONTH_CYCLE + i;
        monthDayNumber.day = (byte) (netDay - MONTH_CYCLE_FIRST_DAY_I[i] + 1);
        return monthDayNumber;
    }

    // 时间转换为字符串
    static final Component annoToString(long annoDay) {
        MonthDay monthDay = toMonthDay(annoDay);
        byte dayNumber = monthDay.day;
        YearMonth yearMonth = toYearMonth(monthDay.month);
        byte monthNumber = yearMonth.month;
        long yearNumber = yearMonth.year + 1;
        if (0 < yearNumber && 0 <= monthNumber && 0 < dayNumber) {
            return Component.join(
                    Anno.JOIN_CONF,
                    Component.text(yearConvert(yearNumber), null, TextDecoration.BOLD),
                    monthConvert(monthNumber),
                    Component.text(dayConvert(dayNumber)));
        } else {
            return Component.text("");
        }
    }

    // 时间转换为值
    static final long[] annoToValue(long annoDay) {
        MonthDay monthDay = toMonthDay(annoDay);
        byte dayNumber = monthDay.day;
        YearMonth yearMonth = toYearMonth(monthDay.month);
        byte monthNumber = yearMonth.month;
        long yearNumber = yearMonth.year;
        if (0 < yearNumber && 0 <= monthNumber && 0 < dayNumber) {
            long[] returnValue = {yearNumber, monthNumber, dayNumber};
            return returnValue;
        } else {
            long[] returnValue = {-1L, -1L, -1L};
            return returnValue;
        }
    }

    // 只允许传入 0～9 的整数
    static final String numberConvert(Short number) {
        return String.valueOf(NUM_STR.charAt(number));
    }

    static final String yearConvert(long yearNumber) {
        String returnValue = "";
        String[] yearConvertMemory = new String[2];
        for (long yearNumber_ = yearNumber; yearNumber_ > 0; yearNumber_ /= 10) {
            yearConvertMemory[0] = String.valueOf(yearNumber_ % 10);
            try {
                yearConvertMemory[1] = numberConvert(
                        (short) Integer.parseInt(yearConvertMemory[0]));
            } catch (NumberFormatException e) {
            }
            returnValue = yearConvertMemory[1] + returnValue;
        }
        if (yearNumber == 1L) {
            return Anno.WTA + "元年";
        }
        return Anno.WTA + returnValue + "年";
    }

    static final Component monthConvert(short monthNumber) {
        String str = String.valueOf(MONTH_STR.charAt(monthNumber)) + "月";
        if (7 > monthNumber) {
            return Component.text(str, NamedTextColor.AQUA);
        }
        if (14 > monthNumber) {
            return Component.text(str, NamedTextColor.GREEN);
        }
        if (21 > monthNumber) {
            return Component.text(str, NamedTextColor.RED);
        }
        return Component.text(str, NamedTextColor.GOLD);
    }

    static final String dayConvert(short dayNumber) {
        String dayConvertMemory[][] = new String[2][2];
        dayConvertMemory[1][0] = String.valueOf(dayNumber / 10);
        dayConvertMemory[0][0] = String.valueOf(dayNumber % 10);
        dayConvertMemory[1][1] = String.valueOf(DAY_STR.charAt(Integer.parseInt(dayConvertMemory[1][0])));
        dayConvertMemory[0][1] = numberConvert((short) Integer.parseInt(dayConvertMemory[0][0]));
        return switch (dayNumber) {
            case 10 ->
                "初十";
            case 20 ->
                "二十";
            default ->
                dayConvertMemory[1][1] + dayConvertMemory[0][1];
        };
    }
}
