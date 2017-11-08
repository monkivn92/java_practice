public class LogTask implements Runnable 
{
    @Override
    public void run() 
    {
        try {

            while (Thread.currentThread().interrupted()) 
            {
                TimeUnit.SECONDS.sleep(10);
                Logger.writeLogs();
            }
        } 
        catch (InterruptedException e)         
        {

        }

        ///??? Logger.writeLogs();
    }
}