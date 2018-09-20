package datetime;

import java.time.*;


public class DateTimeLocal
{
    public static void main(String ...args)
    {
        LocalDate local_now = LocalDate.now();

        System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber()
                + ": "
                +local_now.toString()
        );

        System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber()
                + ": isLeap "
                +local_now.isLeapYear()
        );

        LocalDateTime local_time_now = LocalDateTime.now();

        int local_time_now_year = local_time_now.getYear();

        DayOfWeek dow = local_time_now.getDayOfWeek();

        System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber()
                + ": timestamp : "
                + local_time_now.toEpochSecond(ZoneOffset.UTC)
        );

        System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber()
                + ": day of week : "
                + dow
        );

        LocalTime local_time_of = LocalTime.of(16,20,30);

        System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber()
                + ": local time : "
                + local_time_of
        );

        LocalDateTime local_date_time_parse = LocalDateTime.parse("2014-03-18T13:45:20");

        System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber()
                + ": timestamp after parse : "
                + local_date_time_parse.toEpochSecond(ZoneOffset.UTC)
        );

        // 2014-03-18T13:45:20
        LocalDateTime dt1 = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45, 20);
        //LocalDateTime dt2 = LocalDateTime.of(date, time);
        //LocalDateTime dt3 = date.atTime(13, 45, 20);
        //LocalDateTime dt4 = date.atTime(time);
        //LocalDateTime dt5 = time.atDate(date);

    }
}
