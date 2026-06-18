package io.github.ssingh03_dev.nextchapter.controller;

import io.github.ssingh03_dev.nextchapter.dto.request.CreateBookRequest;
import io.github.ssingh03_dev.nextchapter.dto.request.UpdateBookRequest;
import io.github.ssingh03_dev.nextchapter.dto.response.BookResponse;
import io.github.ssingh03_dev.nextchapter.dto.response.CreateBookResponse;
import io.github.ssingh03_dev.nextchapter.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public CreateBookResponse addBook(@Valid @RequestBody CreateBookRequest createBookRequest) {
        // the return is a dto containing book info plus raw token
        return bookService.addBook(createBookRequest.title(), createBookRequest.author(), createBookRequest.email());
    }

    // get mapping by bookid, used for subscription and other stuff
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // get mapping for all books
    @GetMapping
    public List<BookResponse> getBooks() {
        return bookService.getBooks();
    }

    // anything after, the raw token is required
    // token defines what book is being affected, also authorizes it at the same time

    @PatchMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody UpdateBookRequest updateBookRequest
    ) {
        String rawToken = bearerToken.replace("Bearer ", "");
        return bookService.updateBook(id, rawToken, updateBookRequest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id,
            @RequestHeader("Authorization") String bearerToken
    ) {
        String rawToken = bearerToken.replace("Bearer ", "");
        if (bookService.deleteBook(id, rawToken)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
