/* 
First, we create a task for every distance we need to calculate and send them to the
executor. Then, the main thread has to wait for the end of the execution of those
tasks. 
To control that finalization, we have used a synchronization mechanism
provided by the Java concurrency API: the CountDownLatch class. This class allows
a thread to wait until other threads have arrived at a determined point of their code.
It's initialized with the number of threads you want to wait for. It implements
two methods:
• getDown(): This method decreases the number of threads you have
to wait for
• await(): This method suspends the thread that calls it until the counter
reaches zero

 */

public class KnnClassifierParallelIndividual 
{
    private List<? extends Sample> dataSet;
    private int k;
    private ThreadPoolExecutor executor;
    private int numThreads;
    private boolean parallelSort;

    public KnnClassifierParallelIndividual(List<? extends Sample>    dataSet, int k, int factor, boolean parallelSort) 
    {
        this.dataSet=dataSet;
        this.k=k;

        numThreads=factor*  (Runtime.getRuntime().availableProcessors());
        executor=(ThreadPoolExecutor)Executors.newFixedThreadPool(numThreads);

        this.parallelSort=parallelSort;

    }

    public String classify (Sample example) throws Exception 
    {
        Distance[] distances = new Distance[dataSet.size()];

        CountDownLatch endController = new  CountDownLatch(numThreads);

        int length = dataSet.size() / numThreads;

       int startIndex = 0, endIndex = length;

        for (int i = 0; i < numThreads; i++) 
        {
            
            GroupDistanceTask task = new GroupDistanceTask(distances, startIndex, 
                                                                    endIndex, dataSet, example, endController);
            
            startIndex = endIndex;
            //For allthe threads except the last one, we add the length value to the start index to calculate
            //the end index. For the last one, the last index is the size of the dataset.
            if (i < numThreads - 2) 
            {
                endIndex = endIndex + length;
            } 
            else 
            {
                endIndex = dataSet.size();
            }

            executor.execute(task);
        }

        endController.await();

        if (parallelSort) 
        {
            Arrays.parallelSort(distances);
        } 
        else 
        {
            Arrays.sort(distances);
        }

        return Collections.max(results.entrySet(),   Map.Entry.comparingByValue()).getKey();

    }



    public void destroy() 
    {
        executor.shutdown();
    }


}//end class KnnClassifierParallelIndividual


public class GroupDistanceTask implements Runnable 
{
    private Distance[] distances;

    private int startIndex, endIndex;
    private Sample example;

    private List<? extends Sample> dataSet;
    private CountDownLatch endController;

    public GroupDistanceTask(Distance[] distances, int startIndex,int endIndex, List<? extends Sample> dataSet, 
                                                                            Sampleexample, CountDownLatch endController) 
    {
        this.distances = distances;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.example = example;
        this.dataSet = dataSet;
        this.endController = endController;
    }

    public void run() 
    {
        for (int index = startIndex; index < endIndex; index++) 
        {
            Sample localExample=dataSet.get(index);
            distances[index] = new Distance();
            distances[index].setIndex(index);
            distances[index].setDistance(EuclideanDistanceCalculator.calculate(localExample, example));
        }
        endController.countDown();
    }


}

public class EuclideanDistanceCalculator 
{
    public static double calculate (Sample example1, Sampleexample2) 
    {
        double ret=0.0d;

        double[] data1=example1.getExample();

        double[] data2=example2.getExample();

        if (data1.length!=data2.length) 
        {
            throw new IllegalArgumentException ("Vector doesn't have the same length");
        }

        for (int i=0; i<data1.length; i++) 
        {
            ret += Math.pow(data1[i]-data2[i], 2);
        }

        return Math.sqrt(ret);

    }
}