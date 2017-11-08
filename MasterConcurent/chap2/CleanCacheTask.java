public class CleanCacheTask implements Runnable 
{
    private ParallelCache cache;

    public CleanCacheTask(ParallelCache cache) 
    {
        this.cache = cache;
    }

    @Override
    public void run() 
    {
        try {
            while (!Thread.currentThread().interrupted()) 
            {
                TimeUnit.SECONDS.sleep(10);
                cache.cleanCache();
            }
        } 
        catch (InterruptedException e) 
        {

        }
    }
}