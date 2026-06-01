package io.github.ssingh03_dev.nextchapter.controller;

import io.github.ssingh03_dev.nextchapter.dto.request.CreateChapterRequest;
import io.github.ssingh03_dev.nextchapter.dto.response.ChapterResponse;
import io.github.ssingh03_dev.nextchapter.dto.response.ChapterSummaryResponse;
import io.github.ssingh03_dev.nextchapter.dto.response.CreateChapterResponse;
import io.github.ssingh03_dev.nextchapter.service.ChapterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books/{bookId}/chapters")
public class ChapterController {

    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @PostMapping
    public ResponseEntity<CreateChapterResponse> addChapter(
            @PathVariable Long bookId,
            @RequestHeader("Authorization") String bearerToken,
            @Valid @RequestBody CreateChapterRequest createChapterRequest
            ) {
        String rawToken = bearerToken.replace("Bearer ", "");

        return chapterService
                .addChapter(bookId, createChapterRequest.title(), createChapterRequest.content(), rawToken)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{chapterId}")     // for internal ish use
    public ResponseEntity<ChapterResponse> getChapterById(
            @PathVariable Long bookId, @PathVariable Long chapterId
    ) {
        return chapterService.getChapterById(bookId, chapterId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{chapterNumber}")
    public ResponseEntity<ChapterResponse> getChapterByNumber(
            @PathVariable Long bookId, @PathVariable Integer chapterNumber
    ) {
        return chapterService.getChapterByNumber(bookId, chapterNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ChapterSummaryResponse> getChapters(@PathVariable Long bookId) {
        return chapterService.getChapters(bookId);
    }
}
