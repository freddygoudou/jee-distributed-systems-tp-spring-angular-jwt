package ma.enset.digitalbanking.telegram;

import lombok.extern.slf4j.Slf4j;
import ma.enset.digitalbanking.services.ChatbotService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Active et enregistre le bot Telegram uniquement si telegram.bot.enabled=true.
 * Par défaut (false), aucun bot n'est démarré : l'application fonctionne sans token.
 */
@Configuration
@ConditionalOnProperty(prefix = "telegram.bot", name = "enabled", havingValue = "true")
@Slf4j
public class TelegramConfig {

    @Bean
    public BankingTelegramBot bankingTelegramBot(@Value("${telegram.bot.token}") String token,
                                                 @Value("${telegram.bot.username}") String username,
                                                 ChatbotService chatbotService) {
        return new BankingTelegramBot(token, username, chatbotService);
    }

    @Bean
    public BotSession telegramBotSession(BankingTelegramBot bot) throws Exception {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        BotSession session = api.registerBot(bot);
        log.info(">>> Bot Telegram enregistré : @{}", bot.getBotUsername());
        return session;
    }
}
