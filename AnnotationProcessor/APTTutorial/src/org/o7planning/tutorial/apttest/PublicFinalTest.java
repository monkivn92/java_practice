package org.o7planning.tutorial.apttest;

import org.o7planning.ann.PublicFinal;

public class PublicFinalTest {

    @PublicFinal
    public final static int ABC = 100;

    @PublicFinal
    private static String MODULE_NAME = "APT";

    public static void main(String... args)
    {
        System.out.println("aaaaa");
    }


}
