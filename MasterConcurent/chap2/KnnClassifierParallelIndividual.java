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

        CountDownLatch endController = new  CountDownLatch(dataSet.size());

        int index=0;

        for (Sample localExample : dataSet) 
        {
            IndividualDistanceTask task = new IndividualDistanceTask(distances, index, localExample, example, endController);
            executor.execute(task);
            index++;
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


public class IndividualDistanceTask implements Runnable 
{
    private Distance[] distances;
    private int index;
    private Sample localExample;
    private Sample example;
    private CountDownLatch endController;

    public IndividualDistanceTask(Distance[] distances, int index, Sample localExample,Sample example, CountDownLatch endController) 
    {
        this.distances=distances;
        this.index=index;
        this.localExample=localExample;
        this.example=example;
        this.endController=endController;
    }

    //Note that although all the tasks share the array of distances, 
    //we don't need to use any synchronization mechanism 
    //because each task will modify a different position of the array.
    public void run() 
    {
        distances[index] = new Distance();

        distances[index].setIndex(index);

        distances[index].setDistance(EuclideanDistanceCalculator.calculate(localExample,    example));

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