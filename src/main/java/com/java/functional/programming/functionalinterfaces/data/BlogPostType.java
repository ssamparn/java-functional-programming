package com.java.functional.programming.functionalinterfaces.data;

import java.util.Random;

public enum BlogPostType {
    NEWS,
    REVIEW,
    GUIDE;

    private static final Random PRNG = new Random();

    public static BlogPostType blogPost()  {
        BlogPostType[] blogPostTypes = values();
        return blogPostTypes[PRNG.nextInt(blogPostTypes.length)];
    }
}
