package com.chatboot.request.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "RESPONSE", schema = "ADMIN")
public class Response {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String question;
    private String answer;
    private String keywords;
    @Column(columnDefinition = "DATE")
    private Date createdAt;
    private Integer status;


    public Response(String question, String keywords) {
        this.question = question;
        this.keywords = keywords;
        this.status = 0;
    }

}
