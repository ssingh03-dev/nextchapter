package io.github.ssingh03_dev.nextchapter.service;

import io.github.ssingh03_dev.nextchapter.model.Chapter;
import io.github.ssingh03_dev.nextchapter.model.Subscription;
import io.github.ssingh03_dev.nextchapter.repository.ChapterRepository;
import io.github.ssingh03_dev.nextchapter.repository.SubscriptionRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionAsyncService {

    private final ChapterRepository chapterRepository;
    private final JavaMailSender mailSender;
    private final SubscriptionRepository subscriptionRepository;
    private static final Logger log = LoggerFactory.getLogger(SubscriptionAsyncService.class);
    private final SubscriptionPdfService subscriptionPdfService;

    public SubscriptionAsyncService(ChapterRepository chapterRepository, JavaMailSender mailSender, SubscriptionRepository subscriptionRepository, SubscriptionPdfService subscriptionPdfService) {
        this.chapterRepository = chapterRepository;
        this.mailSender = mailSender;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPdfService = subscriptionPdfService;
    }

    private @NonNull MimeMessageHelper getMimeMessageHelper(MimeMessage message, Subscription subscription, List<Chapter> chapters) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // below used it for testing, from testing environment or fake
        // also, the setup session once in constructor so it can time out
        // can configure hardcoded stuff into applications.properties through javamailer

        String from = "subscribedChapters@gmail.com";
        String to = subscription.getUser().getEmail();

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(String.format(
                "New Chapters for [%s] - Chapters {%d} to {%d}",
                subscription.getBook().getTitle(),
                chapters.getFirst().getChapterNumber(),
                chapters.getLast().getChapterNumber()
        ));
        helper.addAttachment("latestChapters.pdf",
                new ByteArrayResource(subscriptionPdfService.generateChaptersPdf(subscription, chapters)));
        return helper;
    }

    private static @NonNull StringBuilder getBody(Subscription subscription, List<Chapter> chapters) {
        StringBuilder body = new StringBuilder(String.format("""
                        <p>Hello,</p>
                        
                        <p>New chapters are available for your subscription:</p>
                        
                        <h2>%s</h2>
                        <p><strong>Author:</strong> %s</p>
                        
                        """,
                subscription.getBook().getTitle(),
                subscription.getBook().getAuthor()));
        for (Chapter chapter : chapters) {
            body.append(String.format("""
                            <p><strong>Chapter %d: %s</strong></p>
                            <p>%s</p>
                            <hr>
                            """,
                    chapter.getChapterNumber(),
                    chapter.getTitle(),
                    chapter.getContent()));
        }
        return body;
    }

    @Async("subscriptionTaskExecutor")
    @Transactional
    public void processSubscriptionAsync(Long subId) {      // should not stop other emails
        try {
            Subscription subscription = subscriptionRepository.findById(subId).orElseThrow();

            Integer lastChapterNumber = subscription.getLastSentChapter() == null
                    ? 0
                    : subscription.getLastSentChapter().getChapterNumber();

            List<Chapter> chapters = chapterRepository
                    .findByBookIdAndChapterNumberGreaterThanOrderByChapterNumberAsc(
                            subscription.getBook().getId(),
                            lastChapterNumber
                    );

            if (chapters.isEmpty()) return;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = getMimeMessageHelper(message, subscription, chapters);

            StringBuilder body = getBody(subscription, chapters);
            helper.setText(body.toString(), true);

            mailSender.send(message);

            subscription.setLastSentChapter(chapters.getLast());

            subscriptionRepository.save(subscription);
        } catch (MessagingException e) {
            log.error("Mime message error: ", e);
        }
    }
}
