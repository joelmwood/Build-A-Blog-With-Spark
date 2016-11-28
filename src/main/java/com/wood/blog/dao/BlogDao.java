package com.wood.blog.dao;

/**
 * Created by Wood on 11/28/2016.
 */
import java.util.List;

public interface BlogDao {
    boolean addEntry(BlogEntryDAO blogEntry);
    List<BlogEntryDAO> findAllEntries();
    BlogEntryDAO findEntryBySlug(String slug);
    List<BlogEntryDAO> entriesByAuthor(String author);
    void deleteEntry(BlogEntryDAO entry);
    List<BlogEntryDAO> entriesByTag(String tag);
}