package datetime;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.Locale;

import static java.time.temporal.TemporalAdjusters.*;

public class Modify_Format_Parse
{
    public static void main(String ...args)
    {
        Thread this_thread = Thread.currentThread();

        //################## MODIFY #################################
        LocalDate date1 = LocalDate.of(2018,9,20);
        LocalDate date2 = date1.withYear(2017);
        LocalDate date3 = date1.withDayOfMonth(25);
        LocalDate date4 = date1.with(ChronoField.MONTH_OF_YEAR, 7);

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":1 "
                + date1
        );

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":2 "
                + date2
        );

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":3 "
                + date3
        );

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":4 "
                + date4
        );

        LocalDate date5 = date1.plusWeeks(6);
        LocalDate date6 = date1.minusYears(6);
        LocalDate date7 = date1.plus(6, ChronoUnit.MONTHS);

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":5 "
                + date5
        );

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":6 "
                + date6
        );

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":7 "
                + date7
        );

        LocalDate date8 = date1.with(nextOrSame(DayOfWeek.SUNDAY));
        LocalDate date9 = date1.with(lastDayOfMonth());

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":8 "
                + date8
        );

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":9 "
                + date9
        );

        LocalDate date10 = date1.with(new NextWorkingDay());
        //OR
        LocalDate date11 = date1.with( temporal -> {
            DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
            int dayToAdd = 1;
            if(dow == DayOfWeek.FRIDAY) dayToAdd = 3;
            else if(dow == DayOfWeek.SATURDAY) dayToAdd = 2;

            return temporal.plus(dayToAdd, ChronoUnit.DAYS);
        });

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":10 "
                + date10
                + " :11 "
                + date11
        );

        /*
        TemporalAdjuster nextWorkingDay = TemporalAdjusters.ofDateAdjuster(
        temporal -> {
            DayOfWeek dow =   DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
            int dayToAdd = 1;
            if (dow == DayOfWeek.FRIDAY) dayToAdd = 3;
            if (dow == DayOfWeek.SATURDAY) dayToAdd = 2;

            return temporal.plus(dayToAdd, ChronoUnit.DAYS);
        });
        date = date.with(nextWorkingDay);
        */


        //################ FORMAT & PARSE ##########################
        String f1 = date1.format(DateTimeFormatter.BASIC_ISO_DATE);
        String f2 = date1.format(DateTimeFormatter.ISO_LOCAL_DATE);

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":f1 "
                + f1
        );

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":f2 "
                + f2
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String f3 = date1.format(formatter);

        LocalDate f4 = LocalDate.parse(f3, formatter);

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":f3 "
                + f3
        );

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":f4 "
                + f4
        );

        DateTimeFormatter italianFormatter =
                DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);

        String f5 = date1.format(italianFormatter);

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":f5 "
                + f5
        );

        //12.12. Building a DateTimeFormatter
        DateTimeFormatter italyFormatter = new DateTimeFormatterBuilder()
                                                .appendText(ChronoField.DAY_OF_MONTH)
                                                .appendLiteral(". ")
                                                .appendText(ChronoField.MONTH_OF_YEAR)
                                                .appendLiteral(" ")
                                                .appendText(ChronoField.YEAR)
                                                .parseCaseInsensitive()
                                                .toFormatter(Locale.ITALIAN);
        String f6 = date1.format(italyFormatter);

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":f6 italy "
                + f6
        );

    }

}

class NextWorkingDay implements TemporalAdjuster
{

    @Override
    public Temporal adjustInto(Temporal temporal)
    {
        DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
        int dayToAdd = 1;
        if(dow == DayOfWeek.FRIDAY) dayToAdd = 3;
        else if(dow == DayOfWeek.SATURDAY) dayToAdd = 2;

        return temporal.plus(dayToAdd, ChronoUnit.DAYS);
    }
}
