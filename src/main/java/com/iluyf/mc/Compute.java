package com.iluyf.mc;

import static org.bukkit.ChatColor.*;

public class Compute {
    final static short commonYearMonthCount = 27; // 平年的月数
    final static short commonMonthDayCount = 20; // 小月的天数
    final static short yearCycle = 29; // 闰年周期的年数
    final static short monthCycle = 10; // 每周期的闰年数
    final static short cycleLeapYearCount = 10; // 每周期的闰年数
    final static short cycleGreaterMonthCount = 3; // 每周期的大月数
    final static short yearCycleMonthCount = yearCycle * commonYearMonthCount + cycleLeapYearCount; // 闰年周期的月数
    final static short monthCycleDayCount = monthCycle * commonMonthDayCount + cycleGreaterMonthCount; // 大月周期的天数
    short yearCycleFirstmonthMonth[] = new short[yearCycle]; // 闰年周期中，每年的首月所处的月数戳
    short monthCycleFirstdayDay[] = new short[monthCycle]; // 大月周期中，每月的首日所处的天数戳

    class YearMonth {
        long year, month;
    }

    class MonthDay {
        long month, day;
    }

    public String output(long annoDay) {
        return annoToString(annoDay);
    }

    // 计算出闰年和大月
    public Compute() {
        yearCycleFirstmonthMonthCompute();
        monthCycleFirstdayDayCompute();
    }

    // 计算闰年
    private final void yearCycleFirstmonthMonthCompute() {
        yearCycleFirstmonthMonth[0] = 0;
        for (short i = 1; i < yearCycle; ++i) {
            if (isCommonYear(i - 1))
                yearCycleFirstmonthMonth[i] = (short) (yearCycleFirstmonthMonth[i - 1] + commonYearMonthCount);
            else
                yearCycleFirstmonthMonth[i] = (short) (yearCycleFirstmonthMonth[i - 1] + commonYearMonthCount + 1);
        }
    }

    // 判断是否平年
    private final boolean isCommonYear(long year) {
        short netYear = (short) (year % yearCycle);
        final short years[] = { 1, 4, 7, 10, 13, 15, 18, 21, 24, 27 };
        for (short v : years) {
            if (netYear == v)
                return false;
        }
        return true;
    }

    // 计算大月
    private final void monthCycleFirstdayDayCompute() {
        monthCycleFirstdayDay[0] = 0;
        for (short i = 1; i < monthCycle; ++i) {
            if (isCommonMonth(i - 1))
                monthCycleFirstdayDay[i] = (short) (monthCycleFirstdayDay[i - 1] + commonMonthDayCount);
            else
                monthCycleFirstdayDay[i] = (short) (monthCycleFirstdayDay[i - 1] + commonMonthDayCount + 1);
        }
    }

    // 判断是否小月
    private final boolean isCommonMonth(long month) {
        short netMonth = (short) (month % monthCycle);
        final short months[] = { 1, 4, 8 };
        for (short v : months) {
            if (netMonth == v)
                return false;
        }
        return true;
    }

    // 输出月数戳对应的年数戳、月份
    private final YearMonth toYearMonth(long month) {
        YearMonth yearMonthNumber = new YearMonth();
        long yearCycleCount = month / yearCycleMonthCount;
        short netMonth = (short) (month % yearCycleMonthCount);
        short i = 0;
        while (netMonth >= yearCycleFirstmonthMonth[i] && i < yearCycle)
            ++i;
        yearMonthNumber.year = yearCycleCount * yearCycle + i - 1;
        yearMonthNumber.month = netMonth - yearCycleFirstmonthMonth[i - 1] + 1;
        // 如果是闰年，月份序号整体减少 1
        if (!isCommonYear(yearMonthNumber.year)) {
            yearMonthNumber.month--;
        }
        return yearMonthNumber;
    }

    // 输出天数戳对应的月数戳、日期
    final MonthDay toMonthDay(long day) {
        MonthDay monthDayNumber = new MonthDay();
        long monthCycleCount = day / monthCycleDayCount;
        short netDay = (short) (day % monthCycleDayCount);
        short i = 1;
        while (netDay >= monthCycleFirstdayDay[i] && i < monthCycle)
            ++i;
        monthDayNumber.month = monthCycleCount * monthCycle + i - 1;
        monthDayNumber.day = netDay - monthCycleFirstdayDay[i - 1] + 1;
        return monthDayNumber;
    }

    private String annoToString(long annoDay) {
        MonthDay monthDay = new MonthDay();
        monthDay = toMonthDay(annoDay);
        short dayNumber = (short) monthDay.day;
        YearMonth yearMonth = new YearMonth();
        yearMonth = toYearMonth(monthDay.month);
        short monthNumber = (short) yearMonth.month;
        long yearNumber = yearMonth.year;
        if (yearNumber > 0 && monthNumber >= 0 && dayNumber > 0) {
            return BOLD + yearConvert(yearNumber) + RESET + monthConvert(monthNumber) + RESET
                    + dayConvert((short) dayNumber);
        } else {
            return "";
        }
    }

    public long[] annoToValue(long annoDay) {
        MonthDay monthDay = new MonthDay();
        monthDay = toMonthDay(annoDay);
        short dayNumber = (short) monthDay.day;
        YearMonth yearMonth = new YearMonth();
        yearMonth = toYearMonth(monthDay.month);
        short monthNumber = (short) yearMonth.month;
        long yearNumber = yearMonth.year;
        if (yearNumber > 0 && monthNumber >= 0 && dayNumber > 0) {
            long[] returnValue = { yearNumber, monthNumber, dayNumber };
            return returnValue;
        } else {
            long[] returnValue = { -1L, -1L, -1L };
            return returnValue;
        }
    }

    private final String numberConvert(long number) {
        switch ((short) number) {
            case 0:
                return "〇";
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
            case 7:
                return "七";
            case 8:
                return "八";
            case 9:
                return "九";
            default:
                return "";
        }
    }

    private final String yearConvert(long yearNumber) {
        short yearLength = (short) Long.toString(yearNumber).length();
        String returnValue = "";
        String yearConvertMemory[][] = new String[yearLength][2];
        for (long yearNumber_ = yearNumber; yearNumber_ > 0; yearNumber_ /= 10) {
            short Circulate = 0;// 0表示个位，1表示十位，以此类推
            yearConvertMemory[Circulate][0] = String.valueOf(yearNumber_ % 10);
            yearConvertMemory[Circulate][1] = numberConvert(Long.valueOf(yearConvertMemory[Circulate][0]));
            returnValue = yearConvertMemory[Circulate][1] + returnValue;
        }
        if (yearNumber == 1L) {
            return "世界树纪元元年";
        }
        return "世界树纪元" + returnValue + "年";
    }

    private final String monthConvert(short monthNumber) {
        switch (monthNumber) {
            case 0:
                return AQUA + "寂月";
            case 1:
                return AQUA + "雪月";
            case 2:
                return AQUA + "海月";
            case 3:
                return AQUA + "夜月";
            case 4:
                return AQUA + "彗月";
            case 5:
                return AQUA + "凉月";
            case 6:
                return AQUA + "芷月";
            case 7:
                return GREEN + "茸月";
            case 8:
                return GREEN + "雨月";
            case 9:
                return GREEN + "花月";
            case 10:
                return GREEN + "梦月";
            case 11:
                return GREEN + "音月";
            case 12:
                return GREEN + "晴月";
            case 13:
                return GREEN + "岚月";
            case 14:
                return RED + "萝月";
            case 15:
                return RED + "苏月";
            case 16:
                return RED + "茜月";
            case 17:
                return RED + "梨月";
            case 18:
                return RED + "荷月";
            case 19:
                return RED + "茶月";
            case 20:
                return RED + "茉月";
            case 21:
                return GOLD + "铃月";
            case 22:
                return GOLD + "信月";
            case 23:
                return GOLD + "瑶月";
            case 24:
                return GOLD + "风月";
            case 25:
                return GOLD + "叶月";
            case 26:
                return GOLD + "霜月";
            case 27:
                return GOLD + "奈月";
            default:
                return "";
        }
    }

    private final String dayConvert(short dayNumber) {
        String dayConvertMemory[][] = new String[2][2];
        dayConvertMemory[1][0] = String.valueOf(dayNumber / 10);
        dayConvertMemory[0][0] = String.valueOf(dayNumber % 10);
        switch (dayConvertMemory[1][0]) {
            case "0":
                dayConvertMemory[1][1] = "初";
                break;
            case "1":
                dayConvertMemory[1][1] = "十";
                break;
            case "2":
                dayConvertMemory[1][1] = "廿";
                break;
            default:
                dayConvertMemory[1][1] = "";
        }
        dayConvertMemory[0][1] = numberConvert(Integer.parseInt(dayConvertMemory[0][0].toString()));
        switch (dayNumber) {
            case 10:
                return "初十";
            case 20:
                return "二十";
            default:
                return dayConvertMemory[1][1] + dayConvertMemory[0][1];
        }
    }
}