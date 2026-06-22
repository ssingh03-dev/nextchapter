package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.dto.internal.ParsedChapter;
import io.github.ssingh03_dev.nextchapter.dto.internal.ParsedMarkdown;
import io.github.ssingh03_dev.nextchapter.dto.response.BookResponse;
import io.github.ssingh03_dev.nextchapter.dto.response.ChapterSummaryResponse;
import io.github.ssingh03_dev.nextchapter.dto.response.ImportResponse;
import io.github.ssingh03_dev.nextchapter.model.Book;
import io.github.ssingh03_dev.nextchapter.model.Chapter;
import io.github.ssingh03_dev.nextchapter.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookImportService {

    private final ChapterService chapterService;
    private final BookRepository bookRepository;
    private final BookTokenService bookTokenService;
    private final BookService bookService;

    public BookImportService(ChapterService chapterService, BookRepository bookRepository, BookTokenService bookTokenService, BookService bookService) {
        this.chapterService = chapterService;
        this.bookRepository = bookRepository;
        this.bookTokenService = bookTokenService;
        this.bookService = bookService;
    }

    // a private parse file method, should return book and chapters dto, just give parsed data
    // parse based on YAML front matter
    // plus chapters in ascending format, so deny if next chapter number is less than before
    // example: (so should triger when ## hit, then comes chpater with number, then title after :+space)
    /*
        ---
        title: My Book Title
        author: John Doe
        ---

        ## Chapter 1: The Beginning

        Content of chapter one goes here.

        ## Chapter 2: The Middle

        Content of chapter two goes here.

        ## Chapter 3: The End

        Content of chapter three goes here.
    */
    // mainly generated with claude ai
    // later add illegalargument exceptions for each step (make controller catch it)
    private ParsedMarkdown parse(String raw) {
        String[] parts = raw.split("---", 3);
        // parts[1] = YAML block
        // parts[2] = markdown body

        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid format: missing YAML front matter block (expected ---).");
        }

        // Parse YAML front matter
        Yaml yaml = new Yaml();
        Map<String, Object> frontMatter = yaml.load(parts[1]);

        if (frontMatter == null) {
            throw new IllegalArgumentException("Invalid format: front matter block is empty.");
        }

        String title  = (String) frontMatter.get("title");
        String author = (String) frontMatter.get("author");
        String email = (String) frontMatter.get("email");
        String token = (String) frontMatter.get("token");

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Invalid format: missing 'title' in front matter.");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Invalid format: missing 'author' in front matter.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Invalid format: missing 'email' in front matter.");
        }

        // token is kinda optional, only needed when book exists and that exception is handled in calling method

        // Parse chapters
        String[] sections = parts[2].split("(?m)(?=^## )");
        List<ParsedChapter> chapters = new ArrayList<>();

        for (String section : sections) {
            if (section.startsWith("## ")) {
                String firstLine = section.lines().findFirst().orElse("");
                String chapterTitle = firstLine.replace("## ", "").trim();
                String content = section.substring(firstLine.length()).trim();
                chapters.add(new ParsedChapter(chapterTitle, content));
            }
        }

        if (chapters.isEmpty()) {
            throw new IllegalArgumentException("Invalid format: no chapters found. Chapters must start with '## '.");
        }

        return new ParsedMarkdown(title, author, email, token, chapters);
    }

    @Transactional
    public ImportResponse addFromMarkdown(String content) {
        ParsedMarkdown pM = parse(content);

        Optional<Book> bookOptional = bookRepository.findByTitleIgnoreCaseAndAuthorIgnoreCase(pM.title(), pM.author());
        boolean created = bookOptional.isEmpty();
        Book book;

        if (!created) {
            if (pM.rawToken() == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token required");
            book = bookTokenService.findBookByToken(pM.rawToken()).orElse(null);
            if (!bookOptional.get().equals(book)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token for this book.");
            }
        } else {
            BookResponse bookResponse = bookService.addBook(pM.title(), pM.author(), pM.email());
            // addBook either found existing book OR created new one + emailed token
            book = bookRepository.findById(bookResponse.id()).orElseThrow();
        }

        List<Chapter> chapters = new ArrayList<>();

        for (ParsedChapter chapter : pM.chapters()) {
            chapters.add(chapterService.addChapter(book, chapter.title(), chapter.content()));
        }

        return new ImportResponse(
                new BookResponse(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getCreatedAt()
                ),
                chapters.stream().map((chapter) -> new ChapterSummaryResponse(
                        chapter.getId(),
                        chapter.getChapterNumber(),
                        chapter.getTitle()
                )).toList(),
                created
        );
    }
}
