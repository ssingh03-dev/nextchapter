package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.dto.response.CreateBookResponse;
import io.github.ssingh03_dev.nextchapter.model.Book;
import io.github.ssingh03_dev.nextchapter.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BookService {      // add/update books, for now
    private final BookTokenService bookTokenService;
    private final BookRepository bookRepository;

    public BookService(BookTokenService bookTokenService, BookRepository bookRepository) {
        this.bookTokenService = bookTokenService;
        this.bookRepository = bookRepository;
    }

    @Transactional  // so no partial data is added to database, rolls back once a fail happens
    public CreateBookResponse addBook(String title, String author) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setCreatedAt(Instant.now());

        book = bookRepository.save(book);
        String rawToken = bookTokenService.getNewToken(book.getId());

        CreateBookResponse rep = new CreateBookResponse();
        rep.setBookId(book.getId());
        rep.setTitle(book.getTitle());
        rep.setAuthor(book.getAuthor());
        rep.setRawToken(rawToken);

        return rep;
    }
}
