package Application.dtos.chatbot;

import java.util.List;

public record ChatConversation(List<ChatMessageResponse> messages) {
}
