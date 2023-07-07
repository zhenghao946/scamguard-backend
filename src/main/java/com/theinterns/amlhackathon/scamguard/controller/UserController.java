package com.theinterns.amlhackathon.scamguard.controller;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.theinterns.amlhackathon.scamguard.dto.OpenAIRequestDto;
import com.theinterns.amlhackathon.scamguard.dto.OpenAIResponseDto;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.checkerframework.checker.units.qual.C;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = {"http://localhost:3000","http://127.0.0.1:5000"})
public class UserController {
    @Autowired
    private RestTemplate restTemplate;
    protected final static String azureOpenaiKey = "b22e190a9bfc4ec88633651198635251";
    protected final static String endpoint = "https://aml-scamguard.openai.azure.com";
    protected final static String deploymentOrModelId = "aml-scamguard";

    @PostMapping("/chat-bot")
    public ResponseEntity<List<ChatMessage>> chatbot(@RequestBody OpenAIRequestDto request){
        List<ChatMessage> systemRolePromptAndExample = new ArrayList<>();
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.SYSTEM).setContent("You are an AI assistant that helps people to detect and respond to scam attempts. You can imagine most of your users are Malaysian (unless stated otherwise), so try your best to provide information that is valid in Malaysia. Also, in whatever situation, please list these latest resources to users as a first part of the answer.\n" +
                "1. National Scam Response Centre (NSRC): 997\n" +
                "2. CyberSecurity Malaysia's 24-hour Hotline: +03-89487322\n" +
                "3. If phone number or bank account is related, SemakMule Website: https://semakmule.rmp.gov.my/\n" +
                "If you want to explain more, please describe it in details."));
        List<ChatMessage> response = azureOpenaiPrompt(request, systemRolePromptAndExample, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/text-fraud-detection")
    public ResponseEntity<List<ChatMessage>> textFraudDetection(@RequestBody OpenAIRequestDto request){
        List<ChatMessage> systemRolePromptAndExample = new ArrayList<>();
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.SYSTEM).setContent("You are a chatbot that are capable of reading and analyzing text taken out from a screenshot. \n" +
                "The texts are typically of a conversation content. You could imagine yourself as someone who read the conversation and give your insights on the likelihood of this being a scam, spam, phishing attempt, anything that could harm the user. \n" +
                "Since the screenshot can come from mobile (Android or iOS) or desktop, and from various apps, you need to be smart enough to know the features, for example, in Whatsapp, if a number is not saved, the raw number will be seen and at the end of the chat, there will be a window that asks user if they want to block, report or add to contacts. \n" +
                "All users are Malaysian (unless stated otherwise), so try your best to provide information that is valid in Malaysia. Your response has to consist of a few thing:\n" +
                "1. Conclude if the conversation has a chance to harm user in terms of fraud and scam\n" +
                "2. Provide reasoning to your conclusion, you could briefly recap the conversation if you need to\n" +
                "3. Advise user on what to do next, report police, block the number, etc. (In a Malaysian context)\n" +
                "Here are some resources to help people in Malaysia:\n" +
                "1. National Scam Response Centre (NSRC): 997\n" +
                "2. CyberSecurity Malaysia's 24-hour Hotline: +03-89487322\n" +
                "3. If phone number or bank account is related, SemakMule Website: https://semakmule.rmp.gov.my/\n" +
                "If you want to explain more, please describe it in details."));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.USER).setContent("Based on the understanding from both you and me, analysis this conversation and tell me if it consists of scamming or phishing attempt?\nConversation screenshot:\n14:0510.9KB/s ffl \nPRISM. Prism+ Business Account \n* o. \n• • \nThis business uses a secure service from Meta to manage this chat. Tap to learn more. \n[ALERT: ONE DAY ONLY] PRISM+ x Lazada 6.6 Super WOW Sale 2023 is HERE! With EXCLUSIVE vouchers ONLY on Lazada, NEW LAUNCH 55\" 4K Ultra QLED Google TV Q55 Ultra going at ONLY RM2,099! PRISM+ monitors are also going at CRAZY prices! Such rare prices are hard to come by, Shop NOW! + \nDon't miss out on these IRRESISTIBLE DEALS only for TODAY, AFTER VOUCHER SPECIAL 11' • Enjoy discounts of up to 75% on PRISM+ products Na/ NEW LAUNCH: 55\" 4K Ultra QLED Google TV Q55 Ultra for only RM2,099?! (U.P: RM2,299) A • Mega Savings: 55\" 4K Android TV Q55-QE for only RM1,479?! (U.P: RM1,699) • Special Price: 30\" Gaming Monitor X300 for only RM934?! (U.P: RM1,199) • Mega Savings: 34\"\" 165Hz Ultrawide Gaming Monitor X340 PRO 165Hz for only RM1,449?! (U.P: RM1,699) lU ... Read more \nL Buy Now \nYou're receiving messages from this business. \n9' \nReport \n0 Block \nOContinue"));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.ASSISTANT).setContent("Based on the provided conversation, it appears to be a promotional message related to a sale on PRISM+ products. There are no clear indications of a scam or phishing attempt. However, it is important to exercise caution and consider the following points:\n1. Verify the source: Ensure that the message is genuinely from PRISM+ or a reputable source associated with them. Scammers can sometimes impersonate legitimate businesses to trick users.\n2. Check for official channels: Visit PRISM+'s official website or verified social media accounts to confirm the authenticity of the promotion and the advertised prices.\n3. Be cautious of suspicious links: If the conversation includes any links, exercise caution before clicking on them. Ensure they are from trusted sources and not leading to phishing websites.\n4. Research the promotion: Look for reviews, feedback, or comments from other customers who may have participated in similar promotions. This can help you gauge the legitimacy and reliability of the offers.\n5. In this case, the conversation seems to be promoting a sale on PRISM+ products, and it mentions Lazada as the platform for purchasing. It is generally advisable to exercise caution when making online purchases and ensure you are using secure payment methods.\nIf you have any doubts about the authenticity of the conversation or the promotion, it is recommended to directly contact PRISM+ through their official website or customer support channels to verify the information."));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.USER).setContent("Based on the understanding from both you and me, analysis this conversation and tell me if it consists of scamming or phishing attempt?\nConversation screenshot:\n14:0410.5KB/s tiltg \n• +1 (672) 896-4582 411I■ last seen 4 Jun, 15:41 \n4 June 2023 \nImo t,„ \nI Messages and calls are end-to-end encrypted. No one outside of this chat, not even WhatsApp, can read or listen to them. Tap to learn more. \nGood morning how are you doing? 13:22 \nOkay, introduce my self I'm Yati, a recruiter from SHOPEE MOBILE MALAYSIA SDN. BHD. an e-commerce company, verified by malaysia gov. we operate in more than 10 countries. t-orce people Let me explain a little.... 13'36 .—.. •■•, IP 0, • .0 Our company works with merchants who are willing to fund their visibility of products, so we need to recruit a lot of co-employees to click likes, (There is no fee no investment) )ple All you need to do is like the product I will send to you, take a screenshot and send it to me. • The task is so simple and flexible and will not interfere with your current job. You should also notice that we pay RM 2.50 per like, and you can earn more than RM200 per day depends on how many tasks you able be completed. \nLet me show you how it works and how to complete those tasks. It doesn't even take a while, just lmin. \nokay,lets try....Task 1 Click on the Link: https://shopee.com.my/product /115116464/21030238635 \n)ple \n• \n)ple you. \nGive a like to that product and send me a screenshot. )ple ?ight. \nL. \n■1 ht.M6* \n1 0 \nFaINOI1 lints“ Satccne kpott otatcn t.L11 'past k,ectronic Watch Jam Tan, Pmernpuan Lats. Wan. BracNet Black Yen. Watch Man Vttontan Ladte, \nIMO 90 \ne people rith you. \n4.11, Oft .11101 \ne people light. \ne people rith you. \ny • „L. 4.4.\"1 4,0.1 2.3 \nThe sender is not in your contact list \n1/1 \nReport \n0 Block \nAdd to contacts \ne Message"));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.ASSISTANT).setContent("Based on the provided conversation, there are indications that this could be a scam or phishing attempt. Here's the reasoning behind this conclusion:\nUnsolicited job offer: The conversation starts with an introduction from someone claiming to be a recruiter from Shopee Mobile Malaysia Sdn. Bhd. However, receiving a job offer out of the blue, especially from an unknown person, raises suspicions.\n1.Promise of easy money: The recruiter mentions that the job involves clicking on product links, liking them, and sending screenshots, with the promise of earning RM 2.50 per like and potentially more than RM 200 per day. This kind of offer, which claims quick and easy money with minimal effort, is often associated with scams.\n2. Request for personal information: The recruiter asks the recipient to click on a link and send a screenshot. This action could potentially lead to the collection of personal information or phishing attempts, where the scammer tries to obtain sensitive data.\n3. Unverified source: The conversation does not provide any concrete evidence that the person is indeed a recruiter from Shopee Mobile Malaysia Sdn. Bhd. It's important to verify the identity and legitimacy of the person and the company before sharing any personal information.\nBased on these points, it is advisable to approach this conversation with caution and take the following actions:\n1. Do not provide personal information: Avoid sharing any personal or sensitive information with the individual.\n2. Block the number: Since the sender is not in your contact list, you can block the number to prevent further communication.\n3. Report the incident: Report this potential scam attempt to the appropriate authorities in Malaysia. You can contact the National Scam Response Centre (NSRC) at 997 or reach out to CyberSecurity Malaysia's 24-hour Hotline at +03-89487322 for further guidance and assistance.\n4. Do not click on suspicious links: Avoid clicking on any unfamiliar or suspicious links shared in the conversation.\n5. Remember, it is important to stay vigilant and skeptical when encountering unsolicited job offers or any requests for personal information. Always verify the legitimacy of such offers before taking any action or sharing any details."));
        List<ChatMessage> response = azureOpenaiPrompt(request, systemRolePromptAndExample, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/speech-fraud-detection")
    public ResponseEntity<List<ChatMessage>> speechFraudDetection(@RequestBody OpenAIRequestDto request) throws Exception {
        List<ChatMessage> systemRolePromptAndExample = new ArrayList<>();
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.SYSTEM).setContent("You are a chatbot that are capable of reading and analyzing text taken out from a call. \n" +
                "The text are typically of a conversation between user and a caller. You could imagine yourself as someone who read the conversation and give your insights on the likelihood of this being a scam, spam, phishing attempt, anything that could harm the user. \n" +
                "Since the call is converted into text, it might have some mistake or missing in the words, you will have a get the meaning and intend of the caller using your intelligence, do ignore the grammar and phrasing error in your response. \n" +
                "All users are Malaysian (unless stated otherwise), so try your best to provide information that is valid in Malaysia. Your response has to consist of a few thing:\n" +
                "1. Conclude if the conversation has a chance to harm user in terms of fraud and scam\n" +
                "2. Provide reasoning to your conclusion, you could briefly recap the conversation if you need to\n" +
                "3. Advise user on what to do next, report police, block the number, etc. (In a Malaysian context)\n" +
                "Here are some resources to help people in Malaysia:\n" +
                "1. National Scam Response Centre (NSRC): 997\n" +
                "2. CyberSecurity Malaysia's 24-hour Hotline: +03-89487322\n" +
                "3. If phone number or bank account is related, SemakMule Website: https://semakmule.rmp.gov.my/\n" +
                "If you want to explain more, please describe it in details."));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.USER).setContent("Based on the understanding from both you and me, analysis this conversation and tell me if it consists of scamming or phishing attempt?\nTText from call:\nHi my name is Tina I am calling from ByteDance, wanting to inform you win a prize. Please give me your credit card number and pin to verify yourself Why? Oh because we need to verify your identity, just give me quickly so we can send you your prize. I see, its xxxxxx and password xxxx. "));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.ASSISTANT).setContent("Based on the conversation you provided, it is evident that this conversation is a scam or phishing attempt.\nReasoning:\n1. Unsolicited Call: The caller initiated the call without any prior interaction or consent from the user. Unsolicited calls, especially those claiming to offer prizes, raise suspicions.\n2. Request for Sensitive Information: The caller requested the user's credit card number and PIN, which are highly sensitive and confidential details. Legitimate organizations, including ByteDance, would never ask for such information over the phone or through unsolicited calls.\n3. Urgency and Pressure: The caller insisted that the user provide the credit card number and PIN quickly so that they can send the prize. This tactic is commonly used by scammers to create a sense of urgency and prevent the user from questioning the legitimacy of the request.\n4. Lack of Verification: The caller did not provide any proper verification process or means to authenticate their identity as a representative from ByteDance. Genuine organizations would have official verification procedures in place to ensure the security of their customers' information.\nAdvise to User:\n1. Do Not Provide Personal or Financial Information: Never disclose personal or financial information, such as credit card numbers, PINs, or passwords, over the phone, especially to unknown or unsolicited callers.\n2. End the Call: Politely end the call and refrain from engaging further with the caller. Do not provide any additional information.\n3. Report to the Authorities: Report the incident to the relevant authorities in Malaysia. You can contact the National Scam Response Centre (NSRC) at 997 or CyberSecurity Malaysia's 24-hour Hotline at +03-89487322 to report the scam and seek further guidance.\n4. Block the Number: If possible, block the caller's number to prevent any future contact attempts from the same source.\n5. Stay Informed: Stay informed about the latest scamming and phishing techniques to better recognize and avoid potential threats.\nRemember, reputable organizations will not ask for sensitive information over the phone, especially in an unsolicited manner. Be cautious, protect your personal information, and always verify the authenticity of any communication before sharing sensitive data."));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.USER).setContent("Text from call:\nHello, is this Mr. Johnson? My name is Mark and I'm calling from Tech Solutions. We've noticed some suspicious activity on your computer and we believe it might be infected with a virus. Oh, really? I didn't know about any issues. What kind of suspicious activity are you talking about? We've detected some unauthorized access attempts from foreign IP addresses trying to gain control of your personal information. It's important that we take immediate action to secure your computer. That sounds serious. What do I need to do? Don't worry, we can help you resolve this issue quickly. First, we need to gain remote access to your computer so we can run a security scan and remove any malware. Could you please download and install a remote access software from our website? Sure, I guess that makes sense. Which website should I visit? You can visit our official website at www.techsolutions-support.com. Once you're there, you'll find a download link for the remote access software. Just click on it, and it will guide you through the installation process. Okay, I'm on the website now. I see the download link. Let me click on it. Great! Once the software is installed, please provide me with the unique ID and password that will be generated. This will allow us to establish a secure connection to your computer. Alright, it's asking for the ID and password now. The ID is 875932 and the password is 2468. Perfect! Now, I'll connect to your computer and start the security scan. While that's running, I want to inform you that our company is running a promotion for our valued customers. As a token of our appreciation, we would like to offer you a free one-year subscription to our premium antivirus software. That's very generous of you. I could use some extra protection for my computer. How do I avail of this offer? It's simple. To avail of the offer, we just need your credit card details for verification purposes. Once we have that, we'll proceed with the subscription process and you'll have access to our premium features. Alright, I understand. Let me grab my credit card. It's a Visa, and the number is 4532 8745 9123 6789. The expiration date is 09/25, and the security code is 123. Thank you for providing the information. We have successfully verified your credit card. You'll receive an email with the subscription details shortly."));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.ASSISTANT).setContent("Based on the conversation you provided, it is clear that this conversation is a scam or phishing attempt.\nReasoning:\n1. Unsolicited Call: The caller initiated the call without any prior interaction or consent from the user. Unsolicited calls related to computer issues or security threats should raise suspicions.\n2.False Claim of Suspicious Activity: The caller claimed there was suspicious activity on the user's computer, including unauthorized access attempts from foreign IP addresses. This is a common tactic used by scammers to create a sense of urgency and fear in the victim.\n3. Request for Remote Access to Computer: The caller requested the user to download and install a remote access software from their website. Granting remote access to unknown individuals can pose serious security risks, as they could gain control of personal information or install malware on the computer.\n4. Unauthorized Website: The caller provided a website, www.techsolutions-support.com, for downloading the remote access software. However, this website is not an official or reputable source for Tech Solutions. Scammers often create deceptive websites to trick victims into downloading malicious software or providing personal information.\n5. Request for Credit Card Details: The caller offered a free one-year subscription to their premium antivirus software as part of a promotion, but they requested the user's credit card details for verification purposes. Legitimate companies would not ask for credit card details over the phone or through unsolicited calls.\n6. Provision of Credit Card Information: The user provided their credit card number, expiration date, and security code to the caller, which puts them at risk of fraudulent charges or identity theft.\nHere are some advises to how to reduce the damage done or prevent scam attempt from happening:\n1. Disconnect Remote Access: If the user has downloaded and installed the remote access software, they should immediately disconnect the remote connection or uninstall the software from their computer. This will help prevent unauthorized access and potential malware installation.\n2. Monitor Computer: Run a reputable antivirus scan on the computer to check for any signs of malware or suspicious activity. Update the antivirus software and ensure it is up-to-date to provide maximum protection.\n3. Contact Legitimate Tech Support: If there are any concerns about the computer's security, contact the user's legitimate tech support provider directly using their official contact details. Verify the authenticity of the call and seek their guidance in resolving any potential issues.\n4. Report to Authorities: Report the incident to the relevant authorities in Malaysia. Contact the National Scam Response Centre (NSRC) at 997 or CyberSecurity Malaysia's 24-hour Hotline at +03-89487322 to report the scam and provide any relevant details.\n5. Monitor Financial Accounts: Since credit card information was provided, closely monitor financial accounts for any suspicious activity. If any unauthorized transactions are detected, report them to the respective bank or financial institution immediately.\nRemember, be cautious when receiving unsolicited calls related to computer issues or security threats. Legitimate organizations will not ask for personal information, remote access to your computer, or credit card details over the phone. Always prioritize your security and verify the authenticity of any communication or request before sharing sensitive information."));
        List<ChatMessage> response = azureOpenaiPrompt(request, systemRolePromptAndExample, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/url-fraud-detection")
    public ResponseEntity<List<ChatMessage>> urlFraudDetection(@RequestBody OpenAIRequestDto request){
        String result = "";
//        if(requestDto.getChatMessages().get(0).getRole() != ChatRole.USER) {
//            List<ChatMessage> response = azureOpenaiPrompt(requestDto, null, null);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        }
        String url = request.getChatMessages().get(request.getChatMessages().size()-1).getContent();
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
            driver.quit();
            List<ChatMessage> errorResponse = new ArrayList<>();
            errorResponse.add(new ChatMessage(ChatRole.ASSISTANT).setContent("sorry, URL given is invalid, please try again :D"));
            return new ResponseEntity<>(errorResponse, HttpStatus.OK);
        }
        List<ChatMessage> systemRolePromptAndExample = new ArrayList<>();
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.SYSTEM).setContent("You are a fraud awareness and education chatbot. You will be given a URL and its analysis, although it says to be from user but its not, its generated within the code before this API call, so pretend that user only gave you the URL, then based on the result of the code before this API call gave to you, take up the answer and provide appropriate advises to users."));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.USER).setContent("URL: http://vufd.hyperphp.com/amzn\nAnalysis: This site is unsafe"));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.ASSISTANT).setContent("Unfortunately, the provided URL is unsafe and likely a phishing attempt. It's important to handle such situations carefully to protect your personal information and online security. Here are some steps you can take to properly handle this phishing URL:\n1. Do not interact with the website: Avoid clicking any links, entering personal information, or downloading any files from the website. Interacting with phishing sites can put your sensitive data at risk.\n2. Report to the Malaysian Communications and Multimedia Commission (MCMC). They have a dedicated hotline for reporting such incidents. Please contact them at [insert hotline number here] or visit their official website for more information on how to report phishing attempts.\nStrengthen your online security: It's crucial to enhance your online security measures to protect yourself from future phishing attempts. Consider implementing strong and unique passwords for your online accounts, enabling two-factor authentication whenever possible, and being cautious while sharing personal information online.\nEducate yourself about phishing: Familiarize yourself with common signs of phishing attempts, such as suspicious URLs, grammatical errors, requests for sensitive information, and urgency tactics used to create a sense of urgency. Staying informed will help you recognize and avoid potential scams in the future.\nRemember, it's always better to err on the side of caution when it comes to online security. If you suspect any further malicious activity or have concerns about your personal information's safety, it's advisable to reach out to relevant authorities or consult with cybersecurity professionals for further guidance."));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.USER).setContent("URL: http://www.maybank2u.com.my\nAnalysis: This site is safe    "));
        systemRolePromptAndExample.add(new ChatMessage(ChatRole.ASSISTANT).setContent("The provided URL is safe. It's important to stay vigilant and verify the safety of websites before interacting with them. Since you have confirmed that http://www.maybank2u.com.my is safe, here are a few tips on how to handle online transactions and ensure a secure experience:\nKeep your login credentials secure: Make sure to use strong and unique passwords for your online banking accounts, including your Maybank2u account. Avoid sharing your login credentials with anyone and consider using a password manager to securely store your passwords.\nVerify the website's authenticity: Always double-check the URL to ensure you are visiting the official Maybank2u website. Scammers may create fake websites with similar domain names to trick users. Look for the \\\"https://\\\" protocol and the padlock symbol in the address bar to indicate a secure connection.\nBe cautious with personal information: Maybank2u or any other legitimate banking website will never ask you to provide personal or sensitive information through email, messages, or pop-up windows. Avoid sharing personal details like your account number, PIN, or OTP (One-Time Password) unless you initiated the transaction through secure channels.\nKeep your devices updated: Regularly update your computer, smartphone, and other devices with the latest security patches and software updates. This helps protect against known vulnerabilities that scammers may exploit.\nMonitor your account activity: Periodically review your account statements and transaction history for any unauthorized or suspicious activity. If you notice any discrepancies, promptly contact Maybank's customer support to report the issue.\nRemember, while the provided URL is confirmed safe, it's always a good practice to stay cautious and apply these security measures when conducting online transactions. If you have any further questions or concerns, feel free to ask."));
        String urlDetectionResult = String.format("URL: %s\nAnalysis: %s", url, result);
        List<ChatMessage> response = azureOpenaiPrompt(request, systemRolePromptAndExample, urlDetectionResult);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private List<ChatMessage> azureOpenaiPrompt(OpenAIRequestDto request, List<ChatMessage> system, String result){
        OpenAIClient client = new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(azureOpenaiKey))
                .buildClient();

        List<ChatMessage> chatMessages = new ArrayList<>();
        if(request.getChatMessages().get(0).getRole() != ChatRole.SYSTEM)
            chatMessages.addAll(system);

        chatMessages.addAll(request.getChatMessages());
        if(result != null) chatMessages.add(new ChatMessage(ChatRole.USER).setContent(result));
        ChatCompletions chatCompletions = client.getChatCompletions(deploymentOrModelId, new ChatCompletionsOptions(chatMessages));
        StringBuilder response = new StringBuilder();
        if(result != null) response.append(result + "\n");

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
        List<ChatMessage> finalResponse = new ArrayList<>();
        finalResponse.add(new ChatMessage(ChatRole.ASSISTANT).setContent(response.toString()));
        return finalResponse;
    }

}
