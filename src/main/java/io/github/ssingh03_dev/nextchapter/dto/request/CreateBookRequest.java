package io.github.ssingh03_dev.nextchapter.dto.request;

public class CreateBookRequest {
    private String title;
    private String author;

    public CreateBookRequest() {
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
}
