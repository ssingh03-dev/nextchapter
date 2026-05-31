package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.dto.response.CreateChapterResponse;
import io.github.ssingh03_dev.nextchapter.model.Book;
import io.github.ssingh03_dev.nextchapter.model.Chapter;
import io.github.ssingh03_dev.nextchapter.repository.ChapterRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class ChapterService {
    private final BookTokenService bookTokenService;
    private final ChapterRepository chapterRepository;

    public ChapterService(BookTokenService bookTokenService, ChapterRepository chapterRepository) {
        this.bookTokenService = bookTokenService;
        this.chapterRepository = chapterRepository;
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
}
