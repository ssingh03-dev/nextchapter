package io.github.ssingh03_dev.nextchapter.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import io.github.ssingh03_dev.nextchapter.model.Book;
import io.github.ssingh03_dev.nextchapter.model.Chapter;
import io.github.ssingh03_dev.nextchapter.model.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class SubscriptionPdfService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionPdfService.class);

    public byte[] generateChaptersPdf(Subscription subscription, List<Chapter> chapters) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Book book = subscription.getBook();

        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLACK);
            Font authorFont = FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.DARK_GRAY);
            Font dividerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);

            Paragraph bookTitle = new Paragraph(book.getTitle(), titleFont);
            bookTitle.setAlignment(Element.ALIGN_CENTER);
            bookTitle.setSpacingBefore(60f);
            document.add(bookTitle);

            Paragraph author = new Paragraph("by " + book.getAuthor(), authorFont);
            author.setAlignment(Element.ALIGN_CENTER);
            author.setSpacingBefore(8f);
            document.add(author);

            Paragraph meta = new Paragraph("Latest Chapters | Delivered by NextChapter", dividerFont);
            meta.setAlignment(Element.ALIGN_CENTER);
            meta.setSpacingBefore(4f);
            document.add(meta);

            document.add(new Paragraph("\n\n"));

            Font chapterTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

            for (Chapter chapter : chapters) {
                Paragraph chapterHeading = new Paragraph(
                        "Chapter " + chapter.getChapterNumber() + ": " + chapter.getTitle(),
                        chapterTitleFont
                );
                chapterHeading.setSpacingBefore(20f);
                chapterHeading.setSpacingAfter(8f);
                document.add(chapterHeading);

                Paragraph body = new Paragraph(chapter.getContent(), bodyFont);
                body.setLeading(18f);
                document.add(body);

                document.add(new Paragraph("\n"));
            }

            document.close();
        } catch (DocumentException e) {
            log.error("Failed to generate PDF for book id={}", book.getId(), e);
            return new byte[0];
        }

        return baos.toByteArray();
    }
}
