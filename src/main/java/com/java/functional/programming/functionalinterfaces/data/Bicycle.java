package com.java.functional.programming.functionalinterfaces.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bicycle {

    private String brand;
    private Integer frameSize = 0;

    public Bicycle(String brand) {
        this.brand = brand;
        this.frameSize++;
    }
}
