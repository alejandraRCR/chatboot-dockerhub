package com.chatboot.controller;

import com.chatboot.dto.ResponseDTO;
import com.chatboot.service.ChatbootService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/virtual")
@AllArgsConstructor
public class ChatbootController {
    ChatbootService chatbootService;

    @GetMapping("/ask")
    public ResponseDTO getResponse(@RequestParam(value = "question") String question) throws Exception {
        return chatbootService.getVirtualAsk(question);
    }


}
