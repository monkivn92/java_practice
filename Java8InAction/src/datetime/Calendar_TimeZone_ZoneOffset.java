package datetime;

import java.time.*;
import java.util.TimeZone;

public class Calendar_TimeZone_ZoneOffset
{
    public static void main(String ...args)
    {
        Thread this_thread = Thread.currentThread();

        ZoneId romeZone = ZoneId.of("Europe/Rome");
        ZoneId zoneId_default = TimeZone.getDefault().toZoneId();

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":Europe/Rome : "
                + romeZone
        );

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":default : "
                + zoneId_default
        );

        LocalDate date = LocalDate.of(2014, Month.MARCH, 18);
        ZonedDateTime zdt1 = date.atStartOfDay(romeZone);

        LocalDateTime dateTime = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45);
        ZonedDateTime zdt2 = dateTime.atZone(romeZone);

        Instant instant = Instant.now();
        ZonedDateTime zdt3 = instant.atZone(romeZone);

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":zdt1 : "
                + zdt1
        );

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":zdt2 : "
                + zdt2
        );

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":zdt3 : "
                + zdt3
        );

        //ZoneOffset
        ZoneOffset newYorkOffset = ZoneOffset.of("-05:00");
        LocalDateTime dt_now = LocalDateTime.now();
        OffsetDateTime dt_now_newyork = OffsetDateTime.of(dt_now, newYorkOffset);

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":now at NewYork: "
                + dt_now_newyork
        );





    }
}
