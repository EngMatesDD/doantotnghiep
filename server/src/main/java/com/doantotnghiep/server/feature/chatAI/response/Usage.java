package com.doantotnghiep.server.feature.chatAI.response;

import lombok.Data;

@Data
public class Usage {
    private int prompt_tokens;
    private int completion_tokens;
    private int total_tokens;
}
