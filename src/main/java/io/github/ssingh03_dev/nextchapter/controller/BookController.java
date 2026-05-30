package io.github.ssingh03_dev.nextchapter.controller;

import io.github.ssingh03_dev.nextchapter.dto.request.CreateBookRequest;
import io.github.ssingh03_dev.nextchapter.service.BookService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/books")
    public String addBook(@RequestBody CreateBookRequest createBookRequest) {
        return bookService.addBook(createBookRequest.getTitle(), createBookRequest.getAuthor());
    }
}
