package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.dto.request.UpdateBookRequest;
import io.github.ssingh03_dev.nextchapter.dto.response.BookResponse;
import io.github.ssingh03_dev.nextchapter.dto.response.CreateBookResponse;
import io.github.ssingh03_dev.nextchapter.model.Book;
import io.github.ssingh03_dev.nextchapter.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {      // add/update books, for now
    private final BookTokenService bookTokenService;
    private final BookRepository bookRepository;

    public BookService(BookTokenService bookTokenService, BookRepository bookRepository) {
        this.bookTokenService = bookTokenService;
        this.bookRepository = bookRepository;
    }

    private BookResponse toBookResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCreatedAt()
        );
    }

    @Transactional  // so no partial data is added to database, rolls back once a fail happens
    public CreateBookResponse addBook(String title, String author) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setCreatedAt(Instant.now());

        book = bookRepository.save(book);
        String rawToken = bookTokenService.getNewToken(book.getId());

        return new CreateBookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                rawToken
        );
    }

    // a method to find book by its id and return that book object
    public Optional<BookResponse> getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::toBookResponse);
    }

    public List<BookResponse> getBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::toBookResponse)
                .toList();
    }

    public Optional<BookResponse> updateBook(Long id, String rawToken, UpdateBookRequest updateBookRequest) {
        Book book = bookTokenService.findBookByToken(rawToken).orElse(null);
        if (book == null || !book.getId().equals(id)) {
            return Optional.empty();
        }

        if (updateBookRequest.title() != null) {
            book.setTitle(updateBookRequest.title());
        }
        if (updateBookRequest.author() != null) {
            book.setAuthor(updateBookRequest.author());
        }

        book = bookRepository.save(book);

        return Optional.of(toBookResponse(book));
    }

    public boolean deleteBook(Long id, String rawToken) {
        Book book = bookTokenService.findBookByToken(rawToken).orElse(null);

        if (book == null || !book.getId().equals(id)) {
            return false;
        }

        bookRepository.deleteById(id);
        return true;
    }
}
