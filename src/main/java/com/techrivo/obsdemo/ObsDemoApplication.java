package com.techrivo.obsdemo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SpringBootApplication
public class ObsDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObsDemoApplication.class, args);
    }

    @Bean
    public ChatClient openAiChatClient(OpenAiChatModel model) {
        return ChatClient.builder(model).build();
    }

}

@RestController
@ResponseBody
class JokeController {

    private final ChatClient chatClient;

    JokeController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/{topic}")
    public Map<String, String> joke(@PathVariable String topic) {
        String reply = chatClient
                .prompt()
                .user(
                        """
                                Tell me a joke about %s. Be concise. Don't send anything except the joke.
                                """.formatted(topic)
                )
                .call()
                .content();
        assert reply != null;
        return Map.of("joke", reply);
    }

}