package com.chatboot.request.dto;

import com.chatboot.request.entity.Response;
import jakarta.persistence.Column;
import lombok.Getter;

import java.util.Date;

@Getter
public class ResponseDTO {
    private String id;
    private String answer;
    @Column(columnDefinition = "DATE")
    private Date createdAt;

    public ResponseDTO(Response response) {
        this.id = response.getId();
        this.answer = response.getAnswer();
        this.createdAt = response.getCreatedAt();
    }

    public ResponseDTO(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "ResponseKey(answer=" + this.answer + ")";
    }

}
