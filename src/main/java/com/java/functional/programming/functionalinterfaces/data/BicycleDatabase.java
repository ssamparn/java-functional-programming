package com.java.functional.programming.functionalinterfaces.data;

import java.util.Arrays;
import java.util.List;

public class BicycleDatabase {

    public static List<Bicycle> getAllBicycles() {
        Bicycle bicycle1 = new Bicycle("Giant", 1);
        Bicycle bicycle2 = new Bicycle("Scott", 2);
        Bicycle bicycle3 = new Bicycle("Trek", 3);
        Bicycle bicycle4 = new Bicycle("GT", 4);
        return Arrays.asList(bicycle1, bicycle2, bicycle3, bicycle4);
    }
}
