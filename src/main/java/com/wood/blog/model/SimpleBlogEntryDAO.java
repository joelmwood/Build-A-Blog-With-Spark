package com.wood.blog.model;

/**
 * Created by Wood on 11/28/2016.
 */
import com.github.slugify.Slugify;
import com.wood.blog.dao.BlogEntryDAO;
import spark.Request;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SimpleBlogEntryDAO implements BlogEntryDAO {
    private String slug;
    private String title;
    private String author;
    private String date;
    private String content;
    private List<Comment> comments;
    private Set<String> tags;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");

    public SimpleBlogEntryDAO(String title, String author, String content){
        this.title=title;
        this.author=author;
        this.content=content;
        date = ZonedDateTime.now().format(formatter);
        comments = new ArrayList<>();
        tags = new HashSet<>();
        try {
            Slugify slugy = new Slugify();
            slug=slugy.slugify(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setDate() {
        date = ZonedDateTime.now().format(formatter);
    }

    @Override
    public List<String> getTags() {
        return new ArrayList<>(tags);
    }

    @Override
    public boolean addTag(String tag) {
        return tags.add(tag);
    }

    @Override
    public void setSlug() {
        try {
            Slugify slugy = new Slugify();
            slug=slugy.slugify(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getSlug(){
        return slug;
    }

    @Override
    public String getContent(){
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean addComment(Comment comment) {
        return comments.add(comment);
    }

    @Override
    public ArrayList<Comment> getComments(){
        return new ArrayList<Comment>(comments);
    }

    @Override
    public boolean isAuthor(Request req) {
        return req.attribute("username").equals(author) || req.attribute("username").equals("admin");
    }
}