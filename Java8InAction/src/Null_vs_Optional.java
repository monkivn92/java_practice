import java.util.Optional;

public class Null_vs_Optional
{
    public static void main(String... args)
    {
        Thread this_thread = Thread.currentThread();

        Optional<String> gender = Optional.of("MALE");
        String answer1 = "Yes";
        String answer2 = null;

        System.out.println("Non-Empty Optional:" + gender);
        System.out.println("Non-Empty Optional: Gender value : " + gender.get());
        System.out.println("Empty Optional: " + Optional.empty());

        System.out.println("ofNullable on Non-Empty Optional: " + Optional.ofNullable(answer1));
        System.out.println("ofNullable on Empty Optional: " + Optional.ofNullable(answer2));

        //Optional.map and flatMap
        Optional<String> nonEmptyGender = Optional.of("male");
        Optional<String> emptyGender = Optional.empty();

        System.out.println("Non-Empty Optional:: " + nonEmptyGender.map(String::toUpperCase));
        System.out.println("Empty Optional    :: " + emptyGender.map(String::toUpperCase));

        Optional<Optional<String>> nonEmptyOtionalGender = Optional.of(Optional.of("male"));
        System.out.println("Optional value   :: " + nonEmptyOtionalGender);
        System.out.println("Optional.map     :: " + nonEmptyOtionalGender.map(g -> g.map(String::toUpperCase)));
        System.out.println("Optional.flatMap :: " + nonEmptyOtionalGender.flatMap(g -> g.map(String::toUpperCase)));

        // Optional.filter
        //Filter on Optional
        Optional<String> gender2 = Optional.of("MALE");
        Optional<String> emptyGender2 = Optional.empty();
        System.out.println(gender2.filter(g -> g.equals("male"))); //Optional.empty
        System.out.println(gender2.filter(g -> g.equalsIgnoreCase("MALE"))); //Optional[MALE]
        System.out.println(emptyGender2.filter(g -> g.equalsIgnoreCase("MALE"))); //Optional.empty

        if (gender.isPresent())
        {
            System.out.println("Value available.");
        }
        else {
            System.out.println("Value not available.");
        }

        gender.ifPresent(g -> System.out.println("In gender Option, value available."));

        //condition failed, no output print
        emptyGender.ifPresent(g -> System.out.println("In emptyGender Option, value available."));

        System.out.println(gender.orElse("<N/A>")); //MALE
        System.out.println(emptyGender.orElse("<N/A>")); //<N/A>

        System.out.println(gender.orElseGet(() -> "<N/A>")); //MALE
        System.out.println(emptyGender.orElseGet(() -> "<N/A>")); //<N/A>

    }
}
