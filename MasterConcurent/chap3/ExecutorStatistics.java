public class ExecutorStatistics 
{
    private AtomicLong executionTime = new AtomicLong(0L);
    private AtomicInteger numTasks = new AtomicInteger(0);

    public void addExecutionTime(long time) 
    {
        executionTime.addAndGet(time);
    }

    public void addTask() 
    {
        numTasks.incrementAndGet();
    }

    @Override
    public String toString() 
    {
        return "Executed Tasks: "+getNumTasks()+". Execution Time: "+getExecutionTime();
    }

}