package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.dto.request.UpdateBookRequest;
import io.github.ssingh03_dev.nextchapter.dto.response.BookResponse;
import io.github.ssingh03_dev.nextchapter.model.Book;
import io.github.ssingh03_dev.nextchapter.model.BookToken;
import io.github.ssingh03_dev.nextchapter.model.User;
import io.github.ssingh03_dev.nextchapter.repository.BookRepository;
import io.github.ssingh03_dev.nextchapter.repository.BookTokenRepository;
import io.github.ssingh03_dev.nextchapter.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {      // add/update books, for now
    private final BookTokenService bookTokenService;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private final JavaMailSender mailSender;
    private final BookTokenRepository bookTokenRepository;

    public BookService(BookTokenService bookTokenService, BookRepository bookRepository, UserRepository userRepository, JavaMailSender mailSender, BookTokenRepository bookTokenRepository) {
        this.bookTokenService = bookTokenService;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.bookTokenRepository = bookTokenRepository;
    }

    private BookResponse toBookResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCreatedAt()
        );
    }

    private void emailToken(String to, String token, Book book) {
        SimpleMailMessage message = new SimpleMailMessage();
        // below used it for testing, from testing environment or fake
        // also, the setup session once in constructor so it can time out
        // can configure hardcoded stuff into applications.properties through javamailer
        String from = "testing123@gmail.com";
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Token for " + book.getTitle() + " by " + book.getAuthor() + " (" + Instant.now() + ")");
        message.setText("Here is your token: " + token +
                "\nNew requests will only be processed 24 hours after this one.");

        mailSender.send(message);
    }

    // so added the user to books, now choose between sending email in booktokenservice once or what
    // TODO update method so if book + author exists, do not add maybe return null
    @Transactional  // so no partial data is added to database, rolls back once a fail happens
    public BookResponse addBook(String title, String author, String email) {
        // TODO for now, return null if already existing, then update dtos later on
        Optional<Book> bookOptional = bookRepository.findByTitleAndAuthor(title, author);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            return new BookResponse(
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getCreatedAt()
            );
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
        String rawToken = bookTokenService.getNewToken(book);

        // for now create similar email token method from authservice, this one has no expiry
        // then just have a new method that requests a new token if last one is forgotten somehow
        // this new method will call same method in booktokenservice which finds token for the book and overwrites its hash
        // the new token will be emailed with same method here

        emailToken(email, rawToken, book);

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCreatedAt()
        );
    }   // not tested if it works

    @Transactional
    public void requestLink(String email, Long bookId) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) return;

        Optional<Book> bookOptional = bookRepository.findByIdAndUser(bookId, user.get());

        if (bookOptional.isEmpty()) return;

        Book book = bookOptional.get();

        Optional<BookToken> bookTokenOptional = bookTokenRepository.findByBookId(bookId);

        if (bookTokenOptional.isEmpty()) return;

        BookToken bookToken = bookTokenOptional.get();

        if (bookToken.getCreatedAt().plus(24, ChronoUnit.HOURS).isBefore(Instant.now())) {
            String rawToken = bookTokenService.getNewToken(book);
            emailToken(email, rawToken, book);
        }
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
