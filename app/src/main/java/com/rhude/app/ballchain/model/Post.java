package com.rhude.app.ballchain.model;

/**
 * Created by sean on 2018-02-14.
 */

public class Post {
    private String title;
    private String content;
    private Integer featured_media;
    private String status;

    public interface Status {
        String PUBLISH = "publish";
        String FUTURE = "future";
        String DRAFT = "draft";
        String PENDING = "pending";
        String PRIVATE = "private";
    }

    //<editor-fold desc="Constructor">
    public Post() {
        this.status = Status.PUBLISH;
    }

    public Post(String title, String content) {
        this.status = Status.PUBLISH;
        this.title = title;
        this.content = content;
    }

    public Post(String title, String content, Integer featured_media) {
        this.status = Status.PUBLISH;
        this.title = title;
        this.content = content;
        this.featured_media = featured_media;
    }
    //</editor-fold>

    //<editor-fold desc="Getters and Setters">
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getFeatured_media() {
        return featured_media;
    }

    public void setFeatured_media(Integer featured_media) {
        this.featured_media = featured_media;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    //</editor-fold>
}
