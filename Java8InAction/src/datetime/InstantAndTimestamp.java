package datetime;

import java.time.Instant;
import java.time.ZoneOffset;

public class InstantAndTimestamp
{
    public static void main(String ...args)
    {
        Instant instant_1 = Instant.ofEpochSecond(3);
        Instant instant_2 = Instant.ofEpochSecond(3, 1_000_000_000);//one billion nanosec
        Instant instant_3 = Instant.ofEpochSecond(3, -1_000_000_000);//one billion nanosec

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
