package Application.dtos.Chatbot;

import java.util.List;

public record ChatConversation(List<ChatMessageResponse> messages) {
}
