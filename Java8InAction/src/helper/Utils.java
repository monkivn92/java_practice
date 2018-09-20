package helper;

public class Utils
{
    public static int getCurrentLineNumber()
    {
        return Thread.currentThread().getStackTrace()[1].getLineNumber();
    }

    public static String LNStr()
    {
        return Utils.getCurrentLineNumber() + ": ";
    }
}
