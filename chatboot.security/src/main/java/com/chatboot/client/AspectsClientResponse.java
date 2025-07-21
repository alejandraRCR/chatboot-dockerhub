package com.chatboot.client;

import com.chatboot.dto.ResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "${feign.client.chatboot.request}",
        url = "${feign.client.chatboot.request.url}"
)
public interface AspectsClientResponse {
    @GetMapping(
            value = "${feign.client.chatboot.path}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseDTO getResponseDTO(@RequestParam("question") String question);
}
