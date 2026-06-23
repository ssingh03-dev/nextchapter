package io.github.ssingh03_dev.nextchapter.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AiService {

    private final ChatClient chatClient;

    private static final int MAX_CONTENT_WORDS = 800;

    public AiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    private String truncate(String text) {
        String[] words = text.split("\\s+");
        if (words.length <= MAX_CONTENT_WORDS) return text;
        return String.join(" ", Arrays.copyOfRange(words, 0, MAX_CONTENT_WORDS)) + "...";
    }

    public String getRecap(String bookTitle, String chapterTitle, String content) {

        String prompt = """
            You are a reading assistant helping someone continue a book they've been reading.
            
            Given the chapter below, write a 2-3 sentence recap that:
            - Reminds the reader what happened without spoiling anything ahead
            - Is engaging and makes them excited to continue reading
            - Reads naturally, like a friend catching you up
            
            Book: %s
            Chapter: %s
            
            Chapter Content:
            %s
            
            Recap:
            """.formatted(bookTitle, chapterTitle, truncate(content));

        return chatClient.prompt()
                .user(prompt)
                .call().content();
    }
}
