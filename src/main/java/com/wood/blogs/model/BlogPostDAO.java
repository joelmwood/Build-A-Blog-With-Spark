package com.wood.blogs.model;

import java.util.List;

/**
 * Created by Wood on 10/23/2016.
 */
public interface BlogPostDAO {
    boolean add(BlogPost idea);

    List<BlogPost> findAll();

    BlogPost findBySlug(String slug);
}
