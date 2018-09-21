package datetime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.TimeZone;

public class InstantAndTimestamp
{
    public static void main(String ...args)
    {
        Thread this_thread = Thread.currentThread();

        Instant instant_1 = Instant.ofEpochSecond(3);
        Instant instant_2 = Instant.ofEpochSecond(3, 1_000_000_000);//one billion nanosec
        Instant instant_3 = Instant.ofEpochSecond(3, -1_000_000_000);//one billion nanosec

        //From DateTime to Instant/TimeStamp
        LocalDateTime dateTime = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45);
        Instant fromDateTime = dateTime.toInstant(ZoneOffset.UTC);
        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":FromDateTimeToInstant: "
                + fromDateTime.toEpochMilli()
                + " ms"
        );

        //From Timestamp/Instant to DateTime
        Instant instant_now = Instant.ofEpochSecond(1395150300);
        LocalDateTime timeFromInstant = LocalDateTime.ofInstant(instant_now, TimeZone.getDefault().toZoneId());

        System.out.println(this_thread.getStackTrace()[1].getLineNumber()
                + ":from instant to datetime: "
                + timeFromInstant
        );

        System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber()
                + ": "
                + instant_1
        );

        System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber()
                + ": "
                + instant_2
        );

        System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber()
                + ": "
                + instant_3
        );

        System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber()
                + ": current timestamp : "
                + Instant.now().getEpochSecond()
        );

    }
}
