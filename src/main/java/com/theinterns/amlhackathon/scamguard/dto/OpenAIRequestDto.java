package com.theinterns.amlhackathon.scamguard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenAIRequestDto {

    private String previousResponse;
    private String userContent;


}
