package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.dto.request.UpdateChapterRequest;
import io.github.ssingh03_dev.nextchapter.dto.response.ChapterResponse;
import io.github.ssingh03_dev.nextchapter.dto.response.ChapterSummaryResponse;
import io.github.ssingh03_dev.nextchapter.dto.response.CreateChapterResponse;
import io.github.ssingh03_dev.nextchapter.model.Book;
import io.github.ssingh03_dev.nextchapter.model.Chapter;
import io.github.ssingh03_dev.nextchapter.repository.ChapterRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ChapterService {
    private final BookTokenService bookTokenService;
    private final ChapterRepository chapterRepository;

    public ChapterService(BookTokenService bookTokenService, ChapterRepository chapterRepository) {
        this.bookTokenService = bookTokenService;
        this.chapterRepository = chapterRepository;
    }

    private ChapterResponse toChapterResponse(Chapter chapter) {
        return new ChapterResponse(
                chapter.getId(),
                chapter.getChapterNumber(),
                chapter.getTitle(),
                chapter.getContent(),
                chapter.getCreatedAt()
        );
    }

    private ChapterSummaryResponse toChapterSummaryResponse(Chapter chapter) {
        return new ChapterSummaryResponse(
                chapter.getId(),
                chapter.getChapterNumber(),
                chapter.getTitle()
        );
    }

    public Optional<CreateChapterResponse> addChapter(Long bookId, String title, String content, String rawToken) {
        Book book = bookTokenService.findBookByToken(rawToken).orElse(null);

        if (book == null || !book.getId().equals(bookId)) {
            return Optional.empty();
        }

        Chapter chapter = new Chapter();
        chapter.setBook(book);
        chapter.setTitle(title);
        chapter.setContent(content);
        chapter.setChapterNumber(chapterRepository.findMaxChapterNumberByBookId(bookId) + 1);
        chapter.setCreatedAt(Instant.now());

        chapter = chapterRepository.save(chapter);

        return Optional.of(new CreateChapterResponse(
                chapter.getId(),
                chapter.getBook().getId(),
                chapter.getChapterNumber(),
                chapter.getTitle(),
                chapter.getContent()
        ));
    }

    public Optional<ChapterResponse> getChapterById(Long bookId, Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);

        if (chapter == null || !chapter.getBook().getId().equals(bookId)) {
            return Optional.empty();
        }

        return Optional.of(toChapterResponse(chapter));
    }

    public Optional<ChapterResponse> getChapterByNumber(Long bookId, Integer chapterNumber) {
        return chapterRepository.findByBookIdAndChapterNumber(bookId, chapterNumber)
                .map(this::toChapterResponse);
    }

    public List<ChapterSummaryResponse> getChapters(Long bookId) {
        return chapterRepository.findByBookIdOrderByChapterNumberAsc(bookId)
                .stream()
                .map(this::toChapterSummaryResponse)
                .toList();
    }

    public Optional<ChapterResponse> updateChapter(
            Long bookId, Long chapterId, String rawToken, UpdateChapterRequest updateChapterRequest
    ) {
        Book book = bookTokenService.findBookByToken(rawToken).orElse(null);
        if (book == null || !book.getId().equals(bookId)) {
            return Optional.empty();
        }

        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
        if (chapter == null || !chapter.getBook().getId().equals(bookId)) {
            return Optional.empty();
        }

        if (updateChapterRequest.title() != null) {
            chapter.setTitle(updateChapterRequest.title());
        }
        if (updateChapterRequest.content() != null) {
            chapter.setContent(updateChapterRequest.content());
        }

        chapter = chapterRepository.save(chapter);

        return Optional.of(toChapterResponse(chapter));
    }

    public boolean deleteChapter(Long bookId, Long chapterId, String rawToken) {
        Book book = bookTokenService.findBookByToken(rawToken).orElse(null);
        if (book == null || !book.getId().equals(bookId)) {
            return false;
        }

        Chapter chapter = chapterRepository.findById(chapterId).orElse(null);
        if (chapter == null || !chapter.getBook().getId().equals(bookId)) {
            return false;
        }

        int chapterNumber = chapter.getChapterNumber();

        chapterRepository.delete(chapter);

        List<Chapter> affectedChapters = chapterRepository
                .findByBookIdAndChapterNumberGreaterThanOrderByChapterNumberAsc(bookId, chapterNumber);
        affectedChapters.forEach(aChapter -> aChapter.setChapterNumber(aChapter.getChapterNumber()-1));
        chapterRepository.saveAll(affectedChapters);

        return true;
    }
}
