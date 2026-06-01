package io.github.ssingh03_dev.nextchapter.repository;

import io.github.ssingh03_dev.nextchapter.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    @Query("SELECT COALESCE(MAX(c.chapterNumber), 0) FROM Chapter c WHERE c.book.id = :bookId")
    Integer findMaxChapterNumberByBookId(Long bookId);

    Optional<Chapter> findByBookIdAndChapterNumber(Long bookId, Integer chapterNumber);

    List<Chapter> findByBookIdOrderByChapterNumberAsc(Long bookId);

    // below is for aftermath of deleting a chapter; hence chapter number is the deleted one
    List<Chapter> findByBookIdAndChapterNumberGreaterThanOrderByChapterNumberAsc(
            Long bookId,
            Integer chapterNumber
    );
}
