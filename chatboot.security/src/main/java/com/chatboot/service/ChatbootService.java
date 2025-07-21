package com.chatboot.service;

import com.chatboot.client.AspectsClientResponse;
import com.chatboot.dto.ResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatbootService {
    private AspectsClientResponse aspectsClientResponse;

    public ResponseDTO getVirtualAsk(String question) {
        return aspectsClientResponse.getResponseDTO(question);
    }


}
