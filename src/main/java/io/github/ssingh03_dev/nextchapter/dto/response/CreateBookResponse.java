package io.github.ssingh03_dev.nextchapter.dto.response;

public class CreateBookResponse {
    private Long bookId;
    private String title;
    private String author;
    private String rawToken;

    public CreateBookResponse() {}

    public Long getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getRawToken() {
        return rawToken;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setRawToken(String rawToken) {
        this.rawToken = rawToken;
    }
}
