package ma.enset.digitalbanking.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import ma.enset.digitalbanking.services.ChatbotService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
@Tag(name = "Chatbot", description = "Assistant virtuel RAG (Spring AI + OpenAI)")
public class ChatbotRestController {

    private final ChatbotService chatbotService;

    @GetMapping("/status")
    @Operation(summary = "Indique si le chatbot est configuré")
    public Map<String, Boolean> status() {
        return Map.of("enabled", chatbotService.isEnabled());
    }

    @PostMapping
    @Operation(summary = "Poser une question à l'assistant")
    public Map<String, String> ask(@RequestBody @Valid ChatRequest request) {
        return Map.of("answer", chatbotService.ask(request.getMessage()));
    }

    @Data
    public static class ChatRequest {
        @NotBlank(message = "Le message est obligatoire")
        private String message;
    }
}
