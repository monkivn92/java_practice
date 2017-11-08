public class KnnClassifier 
{
    private List <? extends Sample> dataSet;
    private int k;

    public KnnClassifier(List <? extends Sample> dataSet, int k) 
    {
        this.dataSet=dataSet;
        this.k=k;
    }
    public String classify (Sample example) 
    {
        Distance[] distances = new Distance[dataSet.size()];

        int index=0;

        for (Sample localExample : dataSet) 
        {
            distances[index] = new Distance();
            distances[index].setIndex(index);

            distances[index].setDistance(EuclideanDistanceCalculator.calculate(localExample, example));

            index++;
        }

        Arrays.sort(distances);

        Map<String, Integer> results = new HashMap<>();
        for (int i = 0; i < k; i++) 
        {
            Sample localExample = dataSet.get(distances[i].getIndex());
            String tag = localExample.getTag();
            results.merge(tag, 1, (a, b) -> a+b);
        }
        return Collections.max(results.entrySet(),   Map.Entry.comparingByValue()).getKey();
    }


}//end class class KnnClassifier 



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