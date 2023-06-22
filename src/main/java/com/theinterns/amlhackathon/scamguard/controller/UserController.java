package com.theinterns.amlhackathon.scamguard.controller;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.OpenAIServiceVersion;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.theinterns.amlhackathon.scamguard.dto.OpenAIRequestDto;
import com.theinterns.amlhackathon.scamguard.dto.OpenAIResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private RestTemplate restTemplate;
    protected final static String azureOpenaiKey = "b22e190a9bfc4ec88633651198635251";
    protected final static String endpoint = "https://aml-scamguard.openai.azure.com";
    protected final static String deploymentOrModelId = "aml-scamguard";

    @PostMapping("/chat-bot")
    public ResponseEntity<String> chatbot(@RequestBody OpenAIRequestDto request){
        OpenAIClient client = new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(azureOpenaiKey))
                .buildClient();

        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessage(ChatRole.SYSTEM).setContent(""));
        chatMessages.add(new ChatMessage(ChatRole.USER).setContent(request.getUserContent()));
        if(request.getPreviousResponse() != null )chatMessages.add(new ChatMessage(ChatRole.ASSISTANT).setContent(request.getPreviousResponse()));

        ChatCompletions chatCompletions = client.getChatCompletions(deploymentOrModelId, new ChatCompletionsOptions(chatMessages));
        String response = "";

        System.out.printf("Model ID=%s is created at %d.%n", chatCompletions.getId(), chatCompletions.getCreated());
        for (ChatChoice choice : chatCompletions.getChoices()) {
            ChatMessage message = choice.getMessage();
            System.out.printf("Index: %d, Chat Role: %s.%n", choice.getIndex(), message.getRole());
            System.out.println("Message:");
            response += message.getContent();
        }
        CompletionsUsage usage = chatCompletions.getUsage();
        System.out.printf("Usage: number of prompt token is %d, "
                        + "number of completion token is %d, and number of total tokens in request and response is %d.%n",
                usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/text-fraud-detection")
    public ResponseEntity<String> textFraudDetection(@RequestBody OpenAIRequestDto request){
        OpenAIClient client = new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(azureOpenaiKey))
                .buildClient();

        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessage(ChatRole.SYSTEM).setContent(""));
        chatMessages.add(new ChatMessage(ChatRole.USER).setContent(request.getUserContent()));
        if(request.getPreviousResponse() != null )chatMessages.add(new ChatMessage(ChatRole.ASSISTANT).setContent(request.getPreviousResponse()));

        ChatCompletions chatCompletions = client.getChatCompletions(deploymentOrModelId, new ChatCompletionsOptions(chatMessages));
        String response = "";

        System.out.printf("Model ID=%s is created at %d.%n", chatCompletions.getId(), chatCompletions.getCreated());
        for (ChatChoice choice : chatCompletions.getChoices()) {
            ChatMessage message = choice.getMessage();
            System.out.printf("Index: %d, Chat Role: %s.%n", choice.getIndex(), message.getRole());
            System.out.println("Message:");
            response += message.getContent();
        }
        CompletionsUsage usage = chatCompletions.getUsage();
        System.out.printf("Usage: number of prompt token is %d, "
                        + "number of completion token is %d, and number of total tokens in request and response is %d.%n",
                usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/speech-fraud-detection")
    public ResponseEntity<String> speechFraudDetection(@RequestBody OpenAIRequestDto request) throws Exception {

        return null;
    }

    @PostMapping("/url-fraud-detection")
    public ResponseEntity<String> urlFraudDetection(@RequestBody OpenAIRequestDto request){
        OpenAIClient client = new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(azureOpenaiKey))
                .buildClient();

        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessage(ChatRole.SYSTEM).setContent(
                "As an Azure OpenAI system role, you are a helpful fraud detector with knowledge about the context in Malaysia. Given a URL, you need to analyze its authenticity and provide the user with a percentage representing the likelihood of it being a fake or fraudulent website. Additionally, you should offer tips on how to handle such situations, including appropriate contact numbers to call and the recommended actions to take if any parties are involved."));
        chatMessages.add(new ChatMessage(ChatRole.USER).setContent(request.getUserContent()));
        if(request.getPreviousResponse() != null )chatMessages.add(new ChatMessage(ChatRole.ASSISTANT).setContent(request.getPreviousResponse()));

        ChatCompletions chatCompletions = client.getChatCompletions(deploymentOrModelId, new ChatCompletionsOptions(chatMessages));
        String response = "";

        System.out.printf("Model ID=%s is created at %d.%n", chatCompletions.getId(), chatCompletions.getCreated());
        for (ChatChoice choice : chatCompletions.getChoices()) {
            ChatMessage message = choice.getMessage();
            System.out.printf("Index: %d, Chat Role: %s.%n", choice.getIndex(), message.getRole());
            System.out.println("Message:");
            response += message.getContent();
        }
        CompletionsUsage usage = chatCompletions.getUsage();
        System.out.printf("Usage: number of prompt token is %d, "
                        + "number of completion token is %d, and number of total tokens in request and response is %d.%n",
                usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
