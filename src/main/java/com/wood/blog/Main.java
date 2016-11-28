package com.wood.blog;

import com.github.slugify.Slugify;
import com.wood.blog.dao.BlogDao;
import com.wood.blog.dao.BlogEntryDAO;
import com.wood.blog.model.Comment;
import com.wood.blog.model.NotFoundException;
import com.wood.blog.model.SimpleBlogDAO;
import com.wood.blog.model.SimpleBlogEntryDAO;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.io.IOException;
import java.util.*;

import static spark.Spark.*;

/**
 * Created by Wood on 10/24/2016.
 */
public class Main {private static final String flashMessage = "";

    private static void setFlashMessage(Request req, String message) {
        req.session().attribute(flashMessage, message);
    }

    private static String getFlashMessage(Request req) {
        if (req.session(false) == null) {
            return null;
        }
        if (!req.session().attributes().contains(flashMessage)) {
            return null;
        }
        return (String) req.session().attribute(flashMessage);
    }

    private static String captureFlashMessage(Request req) {
        String message = getFlashMessage(req);
        if (message != null) {
            req.session().removeAttribute(flashMessage);
        }
        return message;
    }

    public static void main(String[] args) {
        staticFileLocation("/public");
        BlogDao dao = new SimpleBlogDAO();
        Map<String, Object> model = new HashMap<>();

        BlogEntryDAO entry1 = new SimpleBlogEntryDAO(
                "Software",
                "Joel Wood",
                "This dao entry is about software. This dao entry is about software. " +
                "This dao entry is about software. This dao entry is about software. " +
                "This dao entry is about software. This dao entry is about software. " +
                "This dao entry is about software. This dao entry is about software. " +
                "This dao entry is about software. This dao entry is about software. " +
                "This dao entry is about software. This dao entry is about software. " +
                "This dao entry is about software. This dao entry is about software. ");

        BlogEntryDAO entry2 = new SimpleBlogEntryDAO(
                "Hardware",
                "Joel Wood",
                "This dao entry is about hardware. This dao entry is about hardware. " +
                "This dao entry is about hardware. This dao entry is about hardware. " +
                "This dao entry is about hardware. This dao entry is about hardware. " +
                "This dao entry is about hardware. This dao entry is about hardware. " +
                "This dao entry is about hardware. This dao entry is about hardware.");

        BlogEntryDAO entry3 = new SimpleBlogEntryDAO(
                "Peripherals",
                "Joel Wood",
                "This dao entry is about Peripherals. This dao entry is about Peripherals."  +
                "This dao entry is about Peripherals. This dao entry is about Peripherals."  +
                "This dao entry is about Peripherals. This dao entry is about Peripherals."  +
                "This dao entry is about Peripherals. This dao entry is about Peripherals."  +
                "This dao entry is about Peripherals. This dao entry is about Peripherals."  +
                "This dao entry is about Peripherals. This dao entry is about Peripherals.");

        entry1.addTag("code");
        entry1.addTag("software");
        entry2.addTag("hardware");
        entry2.addTag("not-code");
        entry3.addTag("mouse");
        entry3.addTag("keyboard");

        dao.addEntry(entry1);
        dao.addEntry(entry2);
        dao.addEntry(entry3);

        before((req, res) -> {
            if (req.cookie("username") != null) {
                req.attribute("username", req.cookie("username"));
            }
            model.put("username", req.attribute("username"));
        });

        get("/", (req, res) -> {
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "/hbs/index.hbs");
        }, new HandlebarsTemplateEngine());

        get("/index", (req, res) -> {
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "/hbs/index.hbs");
        }, new HandlebarsTemplateEngine());

        get("/entries", (req, res) -> {
            model.remove("tag");
            model.remove("author");
            model.put("flashMessage", captureFlashMessage(req));
            model.put("entries", dao.findAllEntries());
            return new ModelAndView(model, "/hbs/entries.hbs");
        }, new HandlebarsTemplateEngine());

        get("/new", (req, res) -> {
            if(req.attribute("username") != null) {
                model.put("flashMessage", captureFlashMessage(req));
                return new ModelAndView(model, "/hbs/new.hbs");
            } else {
                setFlashMessage(req, "Please log in first");
                res.redirect("/log-in");
                return null;
            }
        }, new HandlebarsTemplateEngine());

        post("/entries", (req, res) -> {
            BlogEntryDAO entry = new SimpleBlogEntryDAO(req.queryParams("title"), req.attribute("username"), req.queryParams("content"));
            Set<String> tags = new TreeSet<>();
            Collections.addAll(tags, req.queryParams("tags").toLowerCase().split("[\\W\\s_]+"));
            tags.forEach(entry::addTag);
            dao.addEntry(entry);
            setFlashMessage(req,"\"" + entry.getTitle() + "\""+" created successfully!");
            res.redirect("/entries");
            return null;
        });

        get("/entries/:slug", (req, res) -> {
            model.put("flashMessage", captureFlashMessage(req));
            model.put("entry", dao.findEntryBySlug(req.params("slug")));
            return new ModelAndView(model, "/hbs/entry.hbs");
        }, new HandlebarsTemplateEngine());

        post("/entries/:slug/comment", (req, res) -> {
            BlogEntryDAO entry = dao.findEntryBySlug(req.params("slug"));
            String author;
            if (req.attribute("username") != null) {
                author = req.attribute("username");
            } else {
                author = "anonymous";
            }
            entry.addComment(new Comment(req.queryParams("title"), author, req.queryParams("comment")));
            setFlashMessage(req, "Comment posted!");
            res.redirect("/entries/" + entry.getSlug());
            return null;
        });

        get("/log-in", (req, res) -> {
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "/hbs/log-in.hbs");
        }, new HandlebarsTemplateEngine());

        post("/log-in", (req, res) -> {
            String username = req.queryParams("username");
            res.cookie("username", username);
            res.redirect("/entries");
            return null;
        });

        get("/:author", (req, res) -> {
            String author = req.params("author");
            model.remove("tag");
            model.put("flashMessage", captureFlashMessage(req));
            model.put("entries", dao.entriesByAuthor(author));
            model.put("author", author);
            return new ModelAndView(model, "/hbs/entries.hbs");
        }, new HandlebarsTemplateEngine());

        get("/entries/:slug/edit", (req, res) -> {
            BlogEntryDAO entry = dao.findEntryBySlug(req.params("slug"));
            if ((entry.getAuthor()).equals(req.attribute("username")) || (req.attribute("username")).equals("admin")) {
                model.put("entry", entry);
                model.put("flashMessage", captureFlashMessage(req));
                return new ModelAndView(model, "/hbs/edit.hbs");
            } else {
                setFlashMessage(req, "You do not have permission to edit this post.");
                res.redirect("/entries/" + entry.getSlug());
                return null;
            }
        }, new HandlebarsTemplateEngine());

        post("/entries/:slug/edit", (req, res) -> {
            BlogEntryDAO entry = dao.findEntryBySlug(req.params("slug"));
            entry.setTitle(req.queryParams("title"));
            entry.setContent(req.queryParams("content"));
            entry.setDate();
            entry.setSlug();
            Set<String> tags = new TreeSet<>();
            Collections.addAll(tags, req.queryParams("tags").toLowerCase().split("[\\W\\s_]+"));
            tags.forEach(entry::addTag);
            setFlashMessage(req, "Post updated successfully!");
            res.redirect("/entries/" + entry.getSlug());
            return null;
        });

        post("/entries/:slug/delete", (req, res) -> {
            BlogEntryDAO entry = dao.findEntryBySlug(req.params("slug"));
            if ((entry.getAuthor()).equals(req.attribute("username")) || (req.attribute("username")).equals("admin")) {
                dao.deleteEntry(entry);
                setFlashMessage(req, "\"" + entry.getTitle() + "\" deleted successfully.");
                res.redirect("/entries");
            } else {
                setFlashMessage(req, "You do not have permission to delete this post!");
                res.redirect("/entries/" + entry.getSlug());
            }
            return null;
        });

        get("/tags/:tag", (req, res) -> {
            String tag = req.params("tag");
            model.remove("author");
            model.put("flashMessage", captureFlashMessage(req));
            model.put("tag", tag);
            model.put("entries", dao.entriesByTag(tag));
            return new ModelAndView(model, "/hbs/entries.hbs");
        }, new HandlebarsTemplateEngine());

        exception(NotFoundException.class, (exc, req, res) -> {
            res.status(404);
            HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();
            String html = engine.render(new ModelAndView(null, "/hbs/not-found.hbs"));
            res.body(html);
        });
    }
}
