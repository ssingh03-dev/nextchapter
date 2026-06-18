package io.github.ssingh03_dev.nextchapter.service;

import org.springframework.stereotype.Service;

@Service
public class BookImportService {

    private final BookService bookService;
    private final ChapterService chapterService;

    public BookImportService(BookService bookService, ChapterService chapterService) {
        this.bookService = bookService;
        this.chapterService = chapterService;
    }

    // a private parse file method, should return book and chapters dto, just give parsed data
}
