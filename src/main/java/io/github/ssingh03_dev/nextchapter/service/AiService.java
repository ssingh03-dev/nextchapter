package io.github.ssingh03_dev.nextchapter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AiService {

    private final ChatClient chatClient;
    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    private static final int MAX_CONTENT_WORDS = 800;

    public AiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    private String truncate(String text) {
        String[] words = text.split("\\s+");
        if (words.length <= MAX_CONTENT_WORDS) return text;
        String firstHalf = String.join(" ", Arrays.copyOfRange(words, 0, MAX_CONTENT_WORDS / 2));
        String lastHalf = String.join(" ", Arrays.copyOfRange(words, words.length - MAX_CONTENT_WORDS / 2, words.length));
        return firstHalf + " [...] " + lastHalf;
    }   // should work, if too larger, takes portion of first part and last part based on max word count

    public String getRecap(String bookTitle, String chapterTitle, String content) {

        String prompt = """
            You are a reading assistant. Write a recap of the chapter below.
            
            Rules (strictly follow):
            - Exactly 1-2 sentences. No more.
            - No options, no formatting, no bullet points, no headers.
            - No introductory phrases like "In this chapter..." or "You left off...".
            - Output ONLY the recap text. Nothing else before or after it.
            
            Book: %s
            Chapter: %s
            
            Chapter Content:
            %s
            """.formatted(bookTitle, chapterTitle, truncate(content));

        try {
            return chatClient.prompt()
                    .user(prompt)
                    .call().content();
        } catch (RuntimeException e) {
            log.warn("Gemini recap failed for {} / {}", bookTitle, chapterTitle, e);
            return "AI recap unavailable right now because Gemini is busy.";
        }
    }
}
