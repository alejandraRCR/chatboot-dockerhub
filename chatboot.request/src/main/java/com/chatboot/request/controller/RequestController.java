package com.chatboot.request.controller;

import com.chatboot.request.dto.ResponseDTO;
import com.chatboot.request.service.RequestService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/virtual")
public class RequestController {
    private RequestService service;

    @GetMapping(value = "/ask", produces = "application/json")
    public ResponseDTO getRequest(@RequestParam(value = "question") String question) throws Exception {
        return service.getRequest(question);
    }


}
