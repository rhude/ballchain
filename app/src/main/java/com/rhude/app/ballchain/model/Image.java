package com.rhude.app.ballchain.model;


import android.graphics.Bitmap;

public class Image {
    private Bitmap imagecontent;
    private String status;

    public interface Status {
        String PUBLISH = "publish";
        String FUTURE = "future";
        String DRAFT = "draft";
        String PENDING = "pending";
        String PRIVATE = "private";
    }

    //<editor-fold desc="Constructor">
    public Image() {
        this.status = Status.PUBLISH;
    }

    public Image(Bitmap imagecontent) {
        this.status = Status.PUBLISH;
        this.imagecontent = imagecontent;
    }

    public Image(Bitmap imagecontent) {
        this.status = Status.PUBLISH;
        this.imagecontent = imagecontent;
    }
    //</editor-fold>

    //<editor-fold desc="Getters and Setters">
    public String getImageContent() {
        return imagecontent;
    }

    public void setImagecontent(String title) {
        this.imagecontent = imagecontent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    //</editor-fold>
}
