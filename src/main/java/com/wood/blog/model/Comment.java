package com.wood.blog.model;

/**
 * Created by Wood on 11/28/2016.
 */
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Comment {
    private String title;
    private String author;
    private String comment;
    private String date;

    public Comment(String title, String author, String comment){
        this.title = title;
        this.author = author;
        this.comment = comment;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");
        date = ZonedDateTime.now().format(formatter);
    }

    public String getAuthor() {
        return author;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public String getTitle(){
        return title;
    }
}