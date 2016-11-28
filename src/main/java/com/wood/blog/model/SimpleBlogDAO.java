package com.wood.blog.model;

/**
 * Created by Wood on 11/28/2016.
 */
import com.wood.blog.dao.BlogDao;
import com.wood.blog.dao.BlogEntryDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleBlogDAO implements BlogDao {
    private List<BlogEntryDAO> entries;

    public SimpleBlogDAO() {
        entries = new ArrayList<>();
    }

    @Override
    public boolean addEntry(BlogEntryDAO blogEntry) {
        return entries.add(blogEntry);
    }

    @Override
    public List<BlogEntryDAO> findAllEntries() {
        return new ArrayList<>(entries);
    }

    @Override
    public BlogEntryDAO findEntryBySlug(String slug) {
        return entries.stream()
                .filter(entry -> entry.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<BlogEntryDAO> entriesByAuthor(String author) {
        return entries.stream()
                .filter(entry -> entry.getAuthor().equals(author))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEntry(BlogEntryDAO entry) {
        entries.remove(entry);
    }

    @Override
    public List<BlogEntryDAO> entriesByTag(String tag) {
        return entries.stream()
                .filter(entry -> entry.getTags().contains(tag))
                .collect(Collectors.toList());
    }
}
