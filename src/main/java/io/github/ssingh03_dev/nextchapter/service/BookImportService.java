package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.dto.response.ImportResponse;
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

    public ImportResponse addFromMarkdown(String content) {
        return null;        // placeholder
    }
}
