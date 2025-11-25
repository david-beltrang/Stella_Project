package Application.dtos.chatbot;

import java.time.LocalDateTime;

public record ChatMessageResponse(String content, LocalDateTime timestamp, String role) {
}
