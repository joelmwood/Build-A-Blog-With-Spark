package com.wood.blogs.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wood on 10/23/2016.
 */
public class SimpleBlogPostDAO implements BlogPostDAO {

    private List<BlogPost> ideas;

    public SimpleBlogPostDAO() {
        ideas = new ArrayList<>();
    }

    @Override
    public boolean add(BlogPost idea) {
        return ideas.add(idea);
    }

    @Override
    public List<BlogPost> findAll() {
        return new ArrayList<>(ideas);
    }

    @Override
    public BlogPost findBySlug(String slug) {
        return ideas.stream()
                .filter(idea -> idea.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

}
