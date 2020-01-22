package com.example.project_final;


public class NewsItem {
    // NEWS OBJECT
    public NewsItem(String title, String author,  String image) {
        this.title = title;
        this.author = author;
        this.image = image;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    private String title;
    private String author;
    private String image;

}