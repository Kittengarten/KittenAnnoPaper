package tk.iluyf.mc;

import static org.bukkit.ChatColor.*;

public class AnnoCompute {
    final static short commonYearMonthCount = 27;
    final static short commonMonthDayCount = 20;
    final static short cycleLeapYearCount = 10;
    final static short cycleGreaterMonthCount = 3;
    final static short yearCycle = 29;
    final static short monthCycle = 10;
    final static short yearCycleMonthCount = yearCycle * commonYearMonthCount + cycleLeapYearCount;
    final static short monthCycleDayCount = monthCycle * commonMonthDayCount + cycleGreaterMonthCount;
    short monthCycle_FirstdayDay[] = new short[monthCycle + 1];
    short yearCycle_FirstmonthMonth[] = new short[yearCycle + 1];

    public String main(long annoDayNew) {
        return annoToString(annoDayNew);
    }

    public AnnoCompute() {
        monthCycle_FirstdayDayCompute();
        yearCycle_FirstmonthMonthCompute();
    }

    private final void monthCycle_FirstdayDayCompute() {
        monthCycle_FirstdayDay[1] = 1;
        for (short monthCycle_Month_Circulate = 2; monthCycle_Month_Circulate <= monthCycle; ++monthCycle_Month_Circulate) {
            if (isCommonMonth(monthCycle_Month_Circulate - 1)) {
                monthCycle_FirstdayDay[monthCycle_Month_Circulate] = (short) (monthCycle_FirstdayDay[monthCycle_Month_Circulate
                        - 1] + commonMonthDayCount);
            } else {
                monthCycle_FirstdayDay[monthCycle_Month_Circulate] = (short) (monthCycle_FirstdayDay[monthCycle_Month_Circulate
                        - 1] + commonMonthDayCount + 1);
            }
        }
    }

    private final void yearCycle_FirstmonthMonthCompute() {
        yearCycle_FirstmonthMonth[1] = 1;
        for (short yearCycle_Year_Circulate = 2; yearCycle_Year_Circulate <= yearCycle; ++yearCycle_Year_Circulate) {
            if (isCommonYear(yearCycle_Year_Circulate - 1)) {
                yearCycle_FirstmonthMonth[yearCycle_Year_Circulate] = (short) (yearCycle_FirstmonthMonth[yearCycle_Year_Circulate
                        - 1] + commonYearMonthCount);
            } else {
                yearCycle_FirstmonthMonth[yearCycle_Year_Circulate] = (short) (yearCycle_FirstmonthMonth[yearCycle_Year_Circulate
                        - 1] + commonYearMonthCount + 1);
            }
        }
    }

    private final boolean isCommonYear(long year) {
        short netYear = (short) (year % yearCycle);
        if (netYear == 2 || netYear == 5 || netYear == 8 || netYear == 11 || netYear == 14 || netYear == 16
                || netYear == 19 || netYear == 22 || netYear == 25 || netYear == 28) {
            return false;
        } else {
            return true;
        }
    }

    private final boolean isCommonMonth(long month) {
        short netMonth = (short) (month % monthCycle);
        if (netMonth == 2 || netMonth == 5 || netMonth == 9) {
            return false;
        } else {
            return true;
        }
    }

    final long[] dayToMonth(long day) {
        long monthDayNumber[] = new long[2];
        long monthCycleCount = day / monthCycleDayCount;
        short netDay = (short) (day % monthCycleDayCount);
        short monthCycle_MonthCount_Circulate = 1;
        while (netDay >= monthCycle_FirstdayDay[monthCycle_MonthCount_Circulate + 1]) {
            ++monthCycle_MonthCount_Circulate;
            if (monthCycle_MonthCount_Circulate >= monthCycle) {
                break;
            }
        }
        monthDayNumber[1] = netDay - monthCycle_FirstdayDay[monthCycle_MonthCount_Circulate] + 1;
        monthDayNumber[0] = monthCycleCount * monthCycle + monthCycle_MonthCount_Circulate;
        return monthDayNumber;
    }

    private final long[] monthToYear(long month) {
        long yearMonthNumber[] = new long[2];
        long yearCycleCount = month / yearCycleMonthCount;
        short netMonth = (short) (month % yearCycleMonthCount);
        short yearCycle_YearCount_Circulate = 1;
        while (netMonth >= yearCycle_FirstmonthMonth[yearCycle_YearCount_Circulate + 1]) {
            ++yearCycle_YearCount_Circulate;
            if (yearCycle_YearCount_Circulate >= yearCycle) {
                break;
            }
        }
        yearMonthNumber[1] = netMonth - yearCycle_FirstmonthMonth[yearCycle_YearCount_Circulate] + 1;
        yearMonthNumber[0] = yearCycleCount * yearCycle + yearCycle_YearCount_Circulate;
        if (!isCommonYear(yearMonthNumber[0])) {
            yearMonthNumber[1] -= 1;
        }
        return yearMonthNumber;
    }

    private String annoToString(long annoDayNew) {
        long monthDay[] = new long[2];
        monthDay = dayToMonth(annoDayNew);
        short dayNumber = (short) monthDay[1];
        long yearMonth[] = new long[2];
        yearMonth = monthToYear(monthDay[0]);
        short monthNumber = (short) yearMonth[1];
        long yearNumber = yearMonth[0];
        if (yearNumber > 0 && monthNumber >= 0 && dayNumber > 0) {
            return BOLD + yearConvert(yearNumber) + "年" + RESET + monthConvert(monthNumber) + RESET
                    + dayConvert((short) dayNumber);
        } else {
            return "";
        }
    }

    public static void main(String[] args) {
        AnnoCompute meow = new AnnoCompute();
        System.out.println(meow.annoToString(170425/* 可以是任意天数 */));
    }// 测试用

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
            return "世界树纪元元";
        }
        return "世界树纪元" + returnValue;
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