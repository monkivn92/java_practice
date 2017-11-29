
import annotation.builder.processor.*;

public class Person
{

    private int age;

    private String name;

    @BuilderProperty
    public int getAge()
    {
        return age;
    }

    @BuilderProperty
    public void setAge(int age)
    {
        this.age = age;
    }

    public String getName()
    {
        return name;
    }

    @BuilderProperty
    public void setName(String name)
    {
        this.name = name;
    }

    public static void main(String... args)
    {
        Person p = new PersonBuilder().setAge(25).setName("2345").build();
        System.out.println(p.getName());
    }

}
