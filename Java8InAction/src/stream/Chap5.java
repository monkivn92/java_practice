package stream;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Chap5
{
    public static void main(String... args)
    {
        Thread this_thread = Thread.currentThread();

        List<Dish> menu = Arrays.asList(
                new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH)
        );
        System.out.println("Original: " + menu.size() + " items");

        //5.1.1. Filtering with a predicate
        //a Predicate is a function returning a boolean
        long cnt = menu.stream().filter(Dish::isVegetarian).count();
        System.out.println("Near the line number: " + this_thread.getStackTrace()[1].getLineNumber());
        System.out.println("Filtered: " + cnt + " items");

        //5.1.2. Filtering unique elements
        List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
        System.out.println("Near the line number: " + this_thread.getStackTrace()[1].getLineNumber());
        numbers.stream()
                .filter(i -> i % 2 == 0)
                .distinct()
                .forEach(System.out::println);

        //5.1.3. Truncating a stream - Limit
        List<Dish> limitted = menu.stream()
                                .filter(d -> d.getCalories() > 300)
                                .limit(2)
                                .collect(toList());
        System.out.println("Near the line number: " + this_thread.getStackTrace()[1].getLineNumber());
        System.out.println("Limitted: " + limitted);

        //5.1.4. Skipping elements
        List<Dish> skipped = menu.stream()
                .filter(d -> d.getCalories() > 300)
                .skip(2)
                .collect(toList());
        System.out.println("Near the line number: " + this_thread.getStackTrace()[1].getLineNumber());
        System.out.println("skipped: " + skipped);

        //5.2. Mapping: like selecting a column in SQL
        //5.2.1. Applying a function to each element of a stream
        List<String> mapped1 = menu.stream()
                                    .map(Dish::getName)
                                    .collect(toList());
        System.out.println("Near the line number: " + this_thread.getStackTrace()[1].getLineNumber());
        System.out.println("mapped1: " + mapped1);

        List<String> words = Arrays.asList("Java8", "Lambdas", "In", "Action");
        List<Integer> wordLengths = words.stream()
                                        .map(String::length)
                                        .collect(toList());
        System.out.println("Near the line number: " + this_thread.getStackTrace()[1].getLineNumber());
        System.out.println("wordLengths: " + wordLengths);


        //5.2.2. Flattening streams
        System.out.println("Near the line number: " + this_thread.getStackTrace()[1].getLineNumber());
        words.stream().map(w -> w.split(""))
               .flatMap(Stream::of)//convert array to stream
               .distinct()
               .forEach(System.out::println);

        //A pair of arrays
        System.out.println("Near the line number: " + this_thread.getStackTrace()[1].getLineNumber());
        System.out.println("2 Array with stream " );
        List<Integer> numbers1 = Arrays.asList(1, 2, 3);
        List<Integer> numbers2 = Arrays.asList(3, 4);

        List<int[]> pair1 = numbers1.stream()
                                    .flatMap(i -> numbers2.stream().map( j -> new int[]{ i, j } ))
                                    .collect(toList());
        System.out.println("Pair1: " + pair1 );

        List<int[]> pair2 = numbers1.stream()
                .flatMap(i -> numbers2.stream().filter( j -> (i + j) % 3 == 0 )
                                                .map( j -> new int[]{ i, j } )
                )
                .collect(toList());
        System.out.println("Pair2: " + pair2 );

        //5.3.1. Checking to see if a predicate matches at least one element
        if(menu.stream().anyMatch(Dish::isVegetarian))
        {
            System.out.println("The menu is (somewhat) vegetarian friendly!!");
        }

        //5.3.2. Checking to see if a predicate matches all elements
        boolean isHealthy = menu.stream()
                .allMatch(d -> d.getCalories() < 1000);
        //5.3.3 no matches
        boolean isHealthy2 = menu.stream()
                .noneMatch(d -> d.getCalories() >= 1000);
        System.out.println("The menu is isHealthy2: " + isHealthy2);

        //5.3.3. Finding an element
        System.out.println("Find any");
        menu.stream()
            .filter(Dish::isVegetarian)
            .findAny()//return Optional<Dish>
            .ifPresent(d -> System.out.println(d.getName()));

        //5.3.4. Finding the first element
        List<Integer> someNumbers = Arrays.asList(1, 2, 3, 4, 5);
        Optional<Integer> firstSquareDivisibleByThree =
                someNumbers.stream()
                        .map(x -> x * x)
                        .filter(x -> x % 3 == 0)
                        .findFirst(); // 9

        //5.4: Reduce
        String all_dishes = menu.stream().map(d -> d.getName())
                                        .reduce(" -> ", (a, b) -> a+" "+b );
        System.out.println("All dishes: " + all_dishes);

        Optional<Integer> max_calori = menu.stream().map(d -> d.getCalories())
                                        .reduce(Integer::max);
        System.out.println("Max calori: " + max_calori.get());

        //5.6. Numeric streams
        int total_calories = menu.stream()
                        .mapToInt(Dish::getCalories)//return IntStream, not Stream<Integer>, prevent insidious auto boxing
                        .sum();
        IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
        //convert to Stream
        Stream<Integer> str_int = intStream.boxed();

        OptionalInt maxCalories = menu.stream()
                                        .mapToInt(Dish::getCalories)
                                        .max();
        int max_calo = maxCalories.orElse(1);

        //5.6.2. Numeric ranges
        IntStream evenNumbers = IntStream.rangeClosed(0,100).filter(n -> n%2 == 0);
        System.out.println("Even numbers quantity: " + evenNumbers.count());

        //5.7. Building streams #################3

        //5.7.1. Streams from values
        Stream<String> stream = Stream.of("Java 8 ", "Lambdas ", "In ", "Action");
        Stream<String> emptyStream = Stream.empty();

        //5.7.2. Streams from arrays
        int[] lumpers = {1,2 ,3,4 ,5};
        int lumpers_sum = Arrays.stream(lumpers).sum();

        //5.7.3. Streams from files
        Path file_path = Paths.get("data.txt");//reference to project root
        try(Stream<String> lines = Files.lines(file_path, Charset.defaultCharset()))
        {
            lines.map(String::toUpperCase).forEach(System.out::println);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //5.7.4. Streams from functions: creating infinite streams!
        Stream.iterate(0, n -> n + 2)
                .limit(10)
                .forEach(System.out::println);

        System.out.println("Stream generate random number");
        Stream.generate(Math::random)
                .limit(5)
                .forEach(System.out::println);

    }
}

class Dish
{
    private final String name;
    private final boolean vegetarian;
    private final int calories;
    private final Type type;

    public Dish(String name, boolean vegetarian, int calories, Type type)
    {
        this.name = name;
        this.vegetarian = vegetarian;
        this.calories = calories;
        this.type = type;
    }
    public String getName()
    {
        return name;
    }
    public boolean isVegetarian()
    {
        return vegetarian;
    }

    public int getCalories()
    {
        return calories;
    }
    public Type getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public enum Type { MEAT, FISH, OTHER }
}
