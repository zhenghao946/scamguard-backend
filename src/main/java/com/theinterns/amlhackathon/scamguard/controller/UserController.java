package com.theinterns.amlhackathon.scamguard.controller;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.theinterns.amlhackathon.scamguard.dto.OpenAIRequestDto;
import com.theinterns.amlhackathon.scamguard.dto.UrlRequestDto;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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
    public ResponseEntity<List<ChatMessage>> chatbot(@RequestBody OpenAIRequestDto request){
        List<ChatMessage> systemRolePromptAndExample = new ArrayList<>();
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.SYSTEM).setContent("You are an AI assistant that helps people to detect fraud.\n" +
                "In whatever situation, please list options to users\n" +
                "1. National Scam Response Centre (NSRC): 997\n" +
                "2. CyberSecurity Malaysia's 24-hour Hotline: +03-89487322\n" +
                "3. Finantial Industry Mediation Centre (FIMC): 1-300-88-1911\n" +
                "If you want to explain more, please describe it in details....."));
        List<ChatMessage> response = azureOpenaiPrompt(request, systemRolePromptAndExample, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/text-fraud-detection")
    public ResponseEntity<List<ChatMessage>> textFraudDetection(@RequestBody OpenAIRequestDto request){
        String prompt = "";
        //        List<ChatMessage> response = azureOpenaiPrompt(request, prompt);
//        return new ResponseEntity<>(response, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/speech-fraud-detection")
    public ResponseEntity<String> speechFraudDetection(@RequestBody OpenAIRequestDto request) throws Exception {

        return null;
    }

    @PostMapping("/url-fraud-detection")
    public ResponseEntity<List<ChatMessage>> urlFraudDetection(@RequestBody OpenAIRequestDto requestDto){
        if(requestDto.getChatMessages().get(0).getRole() != ChatRole.USER) {
            List<ChatMessage> response = azureOpenaiPrompt(requestDto, null, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        String url = requestDto.getChatMessages().get(0).getContent();
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
//        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);
        try{
            driver.get("https://www.emailveritas.com/url-checker");
            Thread.sleep(5000);
            WebElement textBox = driver.findElement(By.className("email"));
            textBox.sendKeys(url);
            textBox.submit();
            Thread.sleep(5000);
            WebElement firstH3Element = driver.findElement(By.xpath("//h3[1]"));
            String result = firstH3Element.getText();
            driver.quit();
            List<ChatMessage> systemRolePromptAndExample = new ArrayList<>();
            systemRolePromptAndExample.add(new ChatMessage(ChatRole.SYSTEM).setContent("You are a fraud awareness and education chatbot. Your purpose is to assist users in detecting and preventing fraud, as well as providing appropriate advice based on the safety analysis of a given URL"));
            systemRolePromptAndExample.add(new ChatMessage(ChatRole.USER).setContent("URL: http://vufd.hyperphp.com/amzn\\nAnalysis: This site is unsafe"));
            systemRolePromptAndExample.add(new ChatMessage(ChatRole.ASSISTANT).setContent("Unfortunately, the provided URL is unsafe and likely a phishing attempt. It's important to handle such situations carefully to protect your personal information and online security. Here are some steps you can take to properly handle this phishing URL:\\n\\n1. Do not interact with the website: Avoid clicking any links, entering personal information, or downloading any files from the website. Interacting with phishing sites can put your sensitive data at risk.\\n\\n2. Report to the Malaysian Communications and Multimedia Commission (MCMC). They have a dedicated hotline for reporting such incidents. Please contact them at [insert hotline number here] or visit their official website for more information on how to report phishing attempts.\\n\\nStrengthen your online security: It's crucial to enhance your online security measures to protect yourself from future phishing attempts. Consider implementing strong and unique passwords for your online accounts, enabling two-factor authentication whenever possible, and being cautious while sharing personal information online.\\n\\nEducate yourself about phishing: Familiarize yourself with common signs of phishing attempts, such as suspicious URLs, grammatical errors, requests for sensitive information, and urgency tactics used to create a sense of urgency. Staying informed will help you recognize and avoid potential scams in the future.\\n\\nRemember, it's always better to err on the side of caution when it comes to online security. If you suspect any further malicious activity or have concerns about your personal information's safety, it's advisable to reach out to relevant authorities or consult with cybersecurity professionals for further guidance."));
            systemRolePromptAndExample.add(new ChatMessage(ChatRole.USER).setContent("URL: http://www.maybank2u.com.my\\nAnalysis: This site is safe    "));
            systemRolePromptAndExample.add(new ChatMessage(ChatRole.ASSISTANT).setContent("The provided URL is safe. It's important to stay vigilant and verify the safety of websites before interacting with them. Since you have confirmed that http://www.maybank2u.com.my is safe, here are a few tips on how to handle online transactions and ensure a secure experience:\\n\\nKeep your login credentials secure: Make sure to use strong and unique passwords for your online banking accounts, including your Maybank2u account. Avoid sharing your login credentials with anyone and consider using a password manager to securely store your passwords.\\n\\nVerify the website's authenticity: Always double-check the URL to ensure you are visiting the official Maybank2u website. Scammers may create fake websites with similar domain names to trick users. Look for the \\\"https://\\\" protocol and the padlock symbol in the address bar to indicate a secure connection.\\n\\nBe cautious with personal information: Maybank2u or any other legitimate banking website will never ask you to provide personal or sensitive information through email, messages, or pop-up windows. Avoid sharing personal details like your account number, PIN, or OTP (One-Time Password) unless you initiated the transaction through secure channels.\\n\\nKeep your devices updated: Regularly update your computer, smartphone, and other devices with the latest security patches and software updates. This helps protect against known vulnerabilities that scammers may exploit.\\n\\nMonitor your account activity: Periodically review your account statements and transaction history for any unauthorized or suspicious activity. If you notice any discrepancies, promptly contact Maybank's customer support to report the issue.\\n\\nRemember, while the provided URL is confirmed safe, it's always a good practice to stay cautious and apply these security measures when conducting online transactions. If you have any further questions or concerns, feel free to ask."));
            String urlDetectionResult = String.format("URL: %s\\nAnalysis: %s", url, result);
            List<ChatMessage> response = azureOpenaiPrompt(requestDto, systemRolePromptAndExample, urlDetectionResult);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            System.out.println("nub");
            driver.quit();
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    private List<ChatMessage> azureOpenaiPrompt(OpenAIRequestDto request, List<ChatMessage> system, String result){
        OpenAIClient client = new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(azureOpenaiKey))
                .buildClient();

        List<ChatMessage> chatMessages = new ArrayList<>();
        if(request.getChatMessages().get(0).getRole() != ChatRole.SYSTEM){
            for(ChatMessage chat: system){
                chatMessages.add(chat);
            }
        }
        chatMessages.addAll(request.getChatMessages());
        if(result != null) chatMessages.add(new ChatMessage(ChatRole.USER).setContent(result));
        ChatCompletions chatCompletions = client.getChatCompletions(deploymentOrModelId, new ChatCompletionsOptions(chatMessages));
        StringBuilder response = new StringBuilder();

        System.out.printf("Model ID=%s is created at %d.%n", chatCompletions.getId(), chatCompletions.getCreated());
        for (ChatChoice choice : chatCompletions.getChoices()) {
            ChatMessage message = choice.getMessage();
            System.out.printf("Index: %d, Chat Role: %s.%n", choice.getIndex(), message.getRole());
            response.append(message.getContent());
        }
        CompletionsUsage usage = chatCompletions.getUsage();
        System.out.printf("Usage: number of prompt token is %d, "
                        + "number of completion token is %d, and number of total tokens in request and response is %d.%n",
                usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
        chatMessages.add(new ChatMessage(ChatRole.ASSISTANT).setContent(response.toString()));
        if(result != null) chatMessages.remove(chatMessages.size()-2);
        return chatMessages;
    }
}
