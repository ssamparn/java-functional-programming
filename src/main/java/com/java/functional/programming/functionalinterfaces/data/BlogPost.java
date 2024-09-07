package com.java.functional.programming.functionalinterfaces.data;

import com.java.functional.programming.functionalinterfaces.util.Util;

public record BlogPost(
        String title,
        String author,
        BlogPostType type,
        int likes) {

    public static BlogPost createBlogPost() {
        return new BlogPost(
                Util.faker().book().title(),
                Util.faker().book().author(),
                BlogPostType.blogPost(),
                Util.faker().random().nextInt(1, 100)
        );
    }

}
