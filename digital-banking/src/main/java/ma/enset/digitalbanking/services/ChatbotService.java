package ma.enset.digitalbanking.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ChatbotService {

    private static final String SYSTEM_PROMPT = """
            Tu es l'assistant virtuel de l'application "Digital Banking".
            Réponds en français, de manière concise et professionnelle, en te basant UNIQUEMENT
            sur le contexte fourni. Si l'information ne figure pas dans le contexte, indique poliment
            que tu ne disposes pas de cette information et invite l'utilisateur à contacter le support.
            """;

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final ResourceLoader resourceLoader;
    private final boolean enabled;

    private volatile SimpleVectorStore vectorStore;

    public ChatbotService(ChatModel chatModel,
                          EmbeddingModel embeddingModel,
                          ResourceLoader resourceLoader,
                          @Value("${spring.ai.openai.api-key:}") String apiKey) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.embeddingModel = embeddingModel;
        this.resourceLoader = resourceLoader;
        this.enabled = apiKey != null && !apiKey.isBlank();
    }

    public boolean isEnabled() {
        return enabled;
    }

    /** Construit (une seule fois) le vector store RAG à partir de la base de connaissances. */
    private SimpleVectorStore vectorStore() {
        if (vectorStore == null) {
            synchronized (this) {
                if (vectorStore == null) {
                    log.info("Initialisation du vector store RAG...");
                    SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();
                    TextReader reader = new TextReader(
                            resourceLoader.getResource("classpath:rag/banking-knowledge.md"));
                    reader.setCharset(java.nio.charset.StandardCharsets.UTF_8);
                    List<Document> documents = new TokenTextSplitter().apply(reader.get());
                    store.add(documents);
                    vectorStore = store;
                    log.info("Vector store RAG prêt ({} segments).", documents.size());
                }
            }
        }
        return vectorStore;
    }

    public String ask(String question) {
        if (!enabled) {
            return "Le chatbot n'est pas disponible : la clé OpenAI n'est pas configurée sur le serveur.";
        }
        try {
            return chatClient.prompt()
                    .advisors(QuestionAnswerAdvisor.builder(vectorStore()).build())
                    .system(SYSTEM_PROMPT)
                    .user(question)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("Erreur du chatbot", e);
            String msg = e.getMessage() == null ? "" : e.getMessage();
            if (msg.contains("401") || msg.contains("invalid_api_key")) {
                return "Le service d'IA a refusé la clé OpenAI (401). "
                        + "Vérifiez la clé configurée dans secret.properties (OPENAI_API_KEY).";
            }
            return "Désolé, une erreur est survenue lors du traitement de votre question. Réessayez plus tard.";
        }
    }
}
