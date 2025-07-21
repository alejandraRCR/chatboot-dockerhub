package com.chatboot.request.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KeywordAnswerData {
    private String id;
    private String keyword;
    private String answer;
}
