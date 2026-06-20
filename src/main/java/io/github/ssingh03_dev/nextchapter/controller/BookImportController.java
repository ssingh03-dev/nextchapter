package io.github.ssingh03_dev.nextchapter.controller;

import io.github.ssingh03_dev.nextchapter.dto.response.ImportResponse;
import io.github.ssingh03_dev.nextchapter.service.BookImportService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// for bulk operations, calls new service that
@RestController
public class BookImportController {

    private final BookImportService bookImportService;

    public BookImportController(BookImportService bookImportService) {
        this.bookImportService = bookImportService;
    }

    public ImportResponse processPdf(@RequestParam("file")MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        return bookImportService.addFromMarkdown(content);
    }
}
