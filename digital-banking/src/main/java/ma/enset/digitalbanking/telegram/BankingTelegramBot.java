package ma.enset.digitalbanking.telegram;

import lombok.extern.slf4j.Slf4j;
import ma.enset.digitalbanking.services.ChatbotService;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Bot Telegram relayant les messages vers le chatbot RAG.
 * Instancié uniquement lorsque telegram.bot.enabled=true (voir TelegramConfig).
 */
@Slf4j
public class BankingTelegramBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final ChatbotService chatbotService;

    public BankingTelegramBot(@Value("${telegram.bot.token}") String token,
                              @Value("${telegram.bot.username}") String botUsername,
                              ChatbotService chatbotService) {
        super(token);
        this.botUsername = botUsername;
        this.chatbotService = chatbotService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String question = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            String answer;
            if ("/start".equals(question)) {
                answer = "Bonjour 👋 Je suis l'assistant Digital Banking. Posez-moi votre question.";
            } else {
                answer = chatbotService.ask(question);
            }

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(answer);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Erreur d'envoi Telegram", e);
            }
        }
    }
}
