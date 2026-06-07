package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.model.Chapter;
import io.github.ssingh03_dev.nextchapter.model.Subscription;
import io.github.ssingh03_dev.nextchapter.repository.ChapterRepository;
import io.github.ssingh03_dev.nextchapter.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionAsyncService {

    private final ChapterRepository chapterRepository;
    private final JavaMailSender mailSender;
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionAsyncService(ChapterRepository chapterRepository, JavaMailSender mailSender, SubscriptionRepository subscriptionRepository) {
        this.chapterRepository = chapterRepository;
        this.mailSender = mailSender;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Async("subscriptionTaskExecutor")
    @Transactional
    public void processSubscriptionAsync(Long subId) {
        Subscription subscription = subscriptionRepository.findById(subId).orElseThrow();

        List<Chapter> chapters = chapterRepository
                .findByBookIdAndChapterNumberGreaterThanOrderByChapterNumberAsc(
                        subscription.getBook().getId(),
                        subscription.getCurrentChapterNumber()
                );

        if (chapters.isEmpty()) return;

        SimpleMailMessage message = new SimpleMailMessage();
        // below used it for testing, from testing environment or fake
        // also, the setup session once in constructor so it can time out
        // can configure hardcoded stuff into applications.properties through javamailer
        String from = "subscribedChapters@gmail.com";
        String to = subscription.getUser().getEmail();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(String.format(
                "New Chapters for [%s] - Chapters {%d} to {%d}",
                subscription.getBook().getTitle(),
                chapters.getFirst().getChapterNumber(),
                chapters.getLast().getChapterNumber()
        ));

        StringBuilder body = new StringBuilder(String.format("""
                        Hello,
                        
                        New chapters are available for your subscription:
                        
                        Book Title: %s
                        Author: %s
                        
                        """,
                subscription.getBook().getTitle(),
                subscription.getBook().getAuthor()));
        for (Chapter chapter : chapters) {
            body.append(String.format("""
                            Chapter %d: %s
                            
                            %s
                            
                            """,
                    chapter.getChapterNumber(),
                    chapter.getTitle(),
                    chapter.getContent()));
        }

        message.setText(body.toString());

        mailSender.send(message);

        subscription.setCurrentChapterNumber(chapters.getLast().getChapterNumber());
    }
}
