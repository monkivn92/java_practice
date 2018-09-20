package datetime;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;

public class DurationAndPeriod
{
    public static void main(String ...args)
    {
        //The class java.time.Period is limited to calendar date precision.
        //The class java.time.Duration only handles second (and nanosecond) precision but treats days always as equivalent to 24 hours = 86400 seconds.
        Period tenDays = Period.between(LocalDate.of(2014, 3, 8),
                LocalDate.of(2014, 3, 18));

        Instant instant_1 = Instant.ofEpochSecond(3);
        Instant instant_2 = Instant.ofEpochSecond(3, 1_000_000_000);//one billion nanosec

        Duration a_sec = Duration.between(instant_1, instant_2);

        System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber()
                + ": "
                + tenDays.getDays()
        );

        System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber()
                + ": "
                + a_sec.getSeconds()
        );

    }
}
