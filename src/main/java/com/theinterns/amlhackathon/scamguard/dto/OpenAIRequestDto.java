package com.theinterns.amlhackathon.scamguard.dto;

import com.azure.ai.openai.models.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenAIRequestDto {

    private List<ChatMessage> chatMessages;
}
