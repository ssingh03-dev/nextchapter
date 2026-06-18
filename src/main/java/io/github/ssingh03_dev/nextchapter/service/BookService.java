package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.dto.request.UpdateBookRequest;
import io.github.ssingh03_dev.nextchapter.dto.response.BookResponse;
import io.github.ssingh03_dev.nextchapter.dto.response.CreateBookResponse;
import io.github.ssingh03_dev.nextchapter.model.Book;
import io.github.ssingh03_dev.nextchapter.model.User;
import io.github.ssingh03_dev.nextchapter.repository.BookRepository;
import io.github.ssingh03_dev.nextchapter.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {      // add/update books, for now
    private final BookTokenService bookTokenService;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private final JavaMailSender mailSender;

    public BookService(BookTokenService bookTokenService, BookRepository bookRepository, UserRepository userRepository, JavaMailSender mailSender) {
        this.bookTokenService = bookTokenService;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    private BookResponse toBookResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCreatedAt()
        );
    }

    private void emailToken(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        // below used it for testing, from testing environment or fake
        // also, the setup session once in constructor so it can time out
        // can configure hardcoded stuff into applications.properties through javamailer
        String from = "testing123@gmail.com";
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Current Token as of " + Instant.now());
        message.setText("Here is your token: " + token);

        mailSender.send(message);
    }

    // so added the user to books, now choose between sending email in booktokenservice once or what
    // TODO update method so if book + author exists, do not add maybe return null
    @Transactional  // so no partial data is added to database, rolls back once a fail happens
    public CreateBookResponse addBook(String title, String author, String email) {
        // TODO for now, return null if already existing, then update dtos later on
        if (bookRepository.findByTitleAndAuthor(title, author).isPresent()) {
            return null;
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setCreatedAt(Instant.now());
                    return userRepository.save(newUser);
                });

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setCreatedAt(Instant.now());
        book.setUser(user);

        book = bookRepository.save(book);
        String rawToken = bookTokenService.getNewToken(book.getId());

        // for now create similar email token method from authservice, this one has no expiry
        // then just have a new method that requests a new token if last one is forgotten somehow
        // this new method will call same method in booktokenservice which finds token for the book and overwrites its hash
        // the new token will be emailed with same method here

        emailToken(email, rawToken);

        return new CreateBookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor()
        );
    }   // not tested if it works

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
