package com.theinterns.amlhackathon.scamguard.controller;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.theinterns.amlhackathon.scamguard.dto.OpenAIRequestDto;
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
        List<ChatMessage> systemRolePromptAndExample = new ArrayList<>();
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.SYSTEM).setContent("You are a chatbot that are capable of reading and analyzing text taken out from a screenshot. The texts are typically of a conversation content. You could imagine yourself as someone who read the conversation and give your insights on the likelihood of this being a scam, spam, phishing attempt, anything that could harm the user. Since the screenshot can come from mobile (Android or iOS) or desktop, and from various apps, you need to be smart enough to know the features, for example, in Whatsapp, if a number is not saved, the raw number will be seen and at the end of the chat, there will be a window that asks user if they want to block, report or add to contacts. Your response has to consist of a few thing:\\n1. Conclude if the conversation has a chance to harm user in terms of fraud and scam\\n2. Provide reasoning to your conclusion, you could briefly recap the conversation if you need to\\n3. Advise user on what to do next, report police, block the number, etc. (In a Malaysian context)"));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.USER).setContent("Based on the understanding from both you and me, analysis this conversation and tell me if it consists of scamming or phishing attempt?\\n\\nConversation screenshot:\\n14:0510.9KB/s ffl \\nPRISM. Prism+ Business Account \\n* o. \\n• • \\nThis business uses a secure service from Meta to manage this chat. Tap to learn more. \\n[ALERT: ONE DAY ONLY] PRISM+ x Lazada 6.6 Super WOW Sale 2023 is HERE! With EXCLUSIVE vouchers ONLY on Lazada, NEW LAUNCH 55\\\" 4K Ultra QLED Google TV Q55 Ultra going at ONLY RM2,099! PRISM+ monitors are also going at CRAZY prices! Such rare prices are hard to come by, Shop NOW! + \\nDon't miss out on these IRRESISTIBLE DEALS only for TODAY, AFTER VOUCHER SPECIAL 11' • Enjoy discounts of up to 75% on PRISM+ products Na/ NEW LAUNCH: 55\\\" 4K Ultra QLED Google TV Q55 Ultra for only RM2,099?! (U.P: RM2,299) A • Mega Savings: 55\\\" 4K Android TV Q55-QE for only RM1,479?! (U.P: RM1,699) • Special Price: 30\\\" Gaming Monitor X300 for only RM934?! (U.P: RM1,199) • Mega Savings: 34\\\"\\\" 165Hz Ultrawide Gaming Monitor X340 PRO 165Hz for only RM1,449?! (U.P: RM1,699) lU ... Read more \\n\\nL Buy Now \\n\\nYou're receiving messages from this business. \\n9' \\nReport \\n0 Block \\nOContinue"));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.ASSISTANT).setContent("Based on the provided conversation, it does not appear to contain any explicit signs of scamming or phishing attempts. The conversation seems to be a promotional message from PRISM+, a business advertising their products and exclusive deals on Lazada.\\n\\nHowever, it's important to exercise caution when receiving unsolicited messages, especially from unknown contacts or businesses. While this conversation does not overtly indicate any malicious intent, it's always advisable to verify the legitimacy of the offers and the credibility of the business before making any purchases or sharing personal information.\\n\\nIf you have any doubts or suspicions about the message or the sender, it would be wise to research the business independently, check for customer reviews, and make purchases through trusted platforms or official channels to ensure a secure and legitimate transaction."));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.USER).setContent("Based on the understanding from both you and me, analysis this conversation and tell me if it consists of scamming or phishing attempt?\\n\\nConversation screenshot:\\n14:0410.5KB/s tiltg \\n• +1 (672) 896-4582 411I■ last seen 4 Jun, 15:41 \\n4 June 2023 \\nImo t,„ \\nI Messages and calls are end-to-end encrypted. No one outside of this chat, not even WhatsApp, can read or listen to them. Tap to learn more. \\nGood morning how are you doing? 13:22 \\nOkay, introduce my self I'm Yati, a recruiter from SHOPEE MOBILE MALAYSIA SDN. BHD. an e-commerce company, verified by malaysia gov. we operate in more than 10 countries. t-orce people Let me explain a little.... 13'36 .—.. •■•, IP 0, • .0 Our company works with merchants who are willing to fund their visibility of products, so we need to recruit a lot of co-employees to click likes, (There is no fee no investment) )ple All you need to do is like the product I will send to you, take a screenshot and send it to me. • The task is so simple and flexible and will not interfere with your current job. You should also notice that we pay RM 2.50 per like, and you can earn more than RM200 per day depends on how many tasks you able be completed. \\nLet me show you how it works and how to complete those tasks. It doesn't even take a while, just lmin. \\nokay,lets try....Task 1 Click on the Link: https://shopee.com.my/product /115116464/21030238635 \\n)ple \\n• \\n)ple you. \\nGive a like to that product and send me a screenshot. )ple ?ight. \\nL. \\n\\n■1 ht.M6* \\n1 0 \\nFaINOI1 lints“ Satccne kpott otatcn t.L11 'past k,ectronic Watch Jam Tan, Pmernpuan Lats. Wan. BracNet Black Yen. Watch Man Vttontan Ladte, \\nIMO 90 \\ne people rith you. \\n4.11, Oft .11101 \\ne people light. \\ne people rith you. \\ny • „L. 4.4.\\\"1 4,0.1 2.3 \\nThe sender is not in your contact list \\n1/1 \\nReport \\n0 Block \\nAdd to contacts \\ne Message "));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.ASSISTANT).setContent("Based on the provided conversation, there are indications that it could potentially be a scam or phishing attempt. Here's an analysis of the conversation:\\n\\nThe conversation starts with a greeting and an introduction from someone named Yati, claiming to be a recruiter from \\\"SHOPEE MOBILE MALAYSIA SDN. BHD.,\\\" an e-commerce company verified by the Malaysian government.\\n\\nYati explains that their company works with merchants who want to increase the visibility of their products. They claim to be recruiting individuals to click on product likes without any fee or investment. They mention that all the recipient needs to do is like the product, take a screenshot, and send it to them. They promise payment of RM 2.50 per like and the potential to earn more than RM200 per day based on completed tasks.\\n\\nThey provide a link to a specific product on Shopee and instruct the recipient to click on it, like the product, and send a screenshot. The product in question is not visible in the conversation text.\\n\\nHowever, it's important to note several red flags in this conversation. The sender claims to be a recruiter from a specific company but provides no official identification or contact information. They offer a seemingly easy task for earning money without any investment or fees, which can be suspicious. Additionally, they ask the recipient to click on a link, which can potentially lead to phishing websites or malware.\\n\\nConsidering these factors, it is advisable to exercise caution. It is recommended to verify the legitimacy of the recruiter and the company independently through official channels or by contacting Shopee directly. Avoid clicking on any unfamiliar links or sharing personal information without proper verification."));
        List<ChatMessage> response = azureOpenaiPrompt(request, systemRolePromptAndExample, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/speech-fraud-detection")
    public ResponseEntity<String> speechFraudDetection(@RequestBody OpenAIRequestDto request) throws Exception {

        return null;
    }

    @PostMapping("/url-fraud-detection")
    public ResponseEntity<List<ChatMessage>> urlFraudDetection(@RequestBody OpenAIRequestDto requestDto){
        String result = "";
        if(requestDto.getChatMessages().get(0).getRole() != ChatRole.USER) {
            List<ChatMessage> response = azureOpenaiPrompt(requestDto, null, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        String url = requestDto.getChatMessages().get(0).getContent();
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);
        try{
            driver.get("https://www.emailveritas.com/url-checker");
            Thread.sleep(5000);
            WebElement textBox = driver.findElement(By.className("email"));
            textBox.sendKeys(url);
            textBox.submit();
            Thread.sleep(5000);
            WebElement firstH3Element = driver.findElement(By.xpath("//h3[1]"));
            result = firstH3Element.getText();
            driver.quit();
        }catch (Exception e){
            System.out.println("nub");
            driver.quit();
        }
        List<ChatMessage> systemRolePromptAndExample = new ArrayList<>();
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.SYSTEM).setContent("You are a fraud awareness and education chatbot. You will be given a URL and its analysis, although it says to be from user but its not, its generated within the code before this API call, so pretend that user only gave you the URL, then based on the result of the code before this API call gave to you, take up the answer and provide appropriate advises to users."));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.USER).setContent("URL: http://vufd.hyperphp.com/amzn\\nAnalysis: This site is unsafe"));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.ASSISTANT).setContent("Unfortunately, the provided URL is unsafe and likely a phishing attempt. It's important to handle such situations carefully to protect your personal information and online security. Here are some steps you can take to properly handle this phishing URL:\\n\\n1. Do not interact with the website: Avoid clicking any links, entering personal information, or downloading any files from the website. Interacting with phishing sites can put your sensitive data at risk.\\n\\n2. Report to the Malaysian Communications and Multimedia Commission (MCMC). They have a dedicated hotline for reporting such incidents. Please contact them at [insert hotline number here] or visit their official website for more information on how to report phishing attempts.\\n\\nStrengthen your online security: It's crucial to enhance your online security measures to protect yourself from future phishing attempts. Consider implementing strong and unique passwords for your online accounts, enabling two-factor authentication whenever possible, and being cautious while sharing personal information online.\\n\\nEducate yourself about phishing: Familiarize yourself with common signs of phishing attempts, such as suspicious URLs, grammatical errors, requests for sensitive information, and urgency tactics used to create a sense of urgency. Staying informed will help you recognize and avoid potential scams in the future.\\n\\nRemember, it's always better to err on the side of caution when it comes to online security. If you suspect any further malicious activity or have concerns about your personal information's safety, it's advisable to reach out to relevant authorities or consult with cybersecurity professionals for further guidance."));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.USER).setContent("URL: http://www.maybank2u.com.my\\nAnalysis: This site is safe    "));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.ASSISTANT).setContent("The provided URL is safe. It's important to stay vigilant and verify the safety of websites before interacting with them. Since you have confirmed that http://www.maybank2u.com.my is safe, here are a few tips on how to handle online transactions and ensure a secure experience:\\n\\nKeep your login credentials secure: Make sure to use strong and unique passwords for your online banking accounts, including your Maybank2u account. Avoid sharing your login credentials with anyone and consider using a password manager to securely store your passwords.\\n\\nVerify the website's authenticity: Always double-check the URL to ensure you are visiting the official Maybank2u website. Scammers may create fake websites with similar domain names to trick users. Look for the \\\"https://\\\" protocol and the padlock symbol in the address bar to indicate a secure connection.\\n\\nBe cautious with personal information: Maybank2u or any other legitimate banking website will never ask you to provide personal or sensitive information through email, messages, or pop-up windows. Avoid sharing personal details like your account number, PIN, or OTP (One-Time Password) unless you initiated the transaction through secure channels.\\n\\nKeep your devices updated: Regularly update your computer, smartphone, and other devices with the latest security patches and software updates. This helps protect against known vulnerabilities that scammers may exploit.\\n\\nMonitor your account activity: Periodically review your account statements and transaction history for any unauthorized or suspicious activity. If you notice any discrepancies, promptly contact Maybank's customer support to report the issue.\\n\\nRemember, while the provided URL is confirmed safe, it's always a good practice to stay cautious and apply these security measures when conducting online transactions. If you have any further questions or concerns, feel free to ask."));
        String urlDetectionResult = String.format("URL: %s\\nAnalysis: %s", url, result);
        List<ChatMessage> response = azureOpenaiPrompt(requestDto, systemRolePromptAndExample, urlDetectionResult);
        return new ResponseEntity<>(response, HttpStatus.OK);
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
