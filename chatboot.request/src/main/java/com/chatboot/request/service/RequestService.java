package com.chatboot.request.service;

import com.chatboot.request.dto.ResponseDTO;
import com.chatboot.request.entity.KeywordAnswerData;
import com.chatboot.request.entity.Response;
import com.chatboot.request.entity.ResponseKey;
import com.chatboot.request.repository.ResponseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestService {
    private ResponseRepository responseRepository;

    public ResponseDTO getRequest(String question) {
        long startTime = System.nanoTime();
        String timeSuffix = "";

        String processedQuestion = question.toLowerCase();
        Optional<Response> response = responseRepository.findByQuestionIgnoreCase(question);
        if (!response.isPresent()) {
            String test = findAnswerByKeywords(processedQuestion);
            return new ResponseDTO(test);
        }
        long endTime = System.nanoTime();
        timeSuffix = ", (Tiempo de respuesta: " + formatDuration(startTime, endTime) + ")";
        return new ResponseDTO((response.get().getStatus() == 1) ? response.get().getAnswer() + timeSuffix : "Esta registrada la pregunta en espera de ser respondida");
    }

    private List<String> tokenizeQuestion(String question) {
        List<String> tokens = Arrays.stream(question.trim().split("\\s+"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        return tokens;
    }

    private String tokenizeQuestionAsString(String question) {
        return Arrays.stream(question.trim().split("\\s+"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));
    }

    private List<KeywordAnswerData> generateKeywordAnswerDataList() {
        List<Response> allResponses = responseRepository.findByStatusTrue();
        List<KeywordAnswerData> resultList = new ArrayList<>();

        for (Response response : allResponses) {
            if (response.getKeywords() != null && !response.getKeywords().trim().isEmpty()
                    && response.getAnswer() != null && !response.getAnswer().trim().isEmpty()) {
                Arrays.stream(response.getKeywords().toLowerCase().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .forEach(individualKeyword -> {
                            resultList.add(new KeywordAnswerData(
                                    response.getId(),
                                    individualKeyword,
                                    response.getAnswer()
                            ));
                        });
            }
        }
        return resultList;
    }


    private String formatDuration(long startTime, long endTime) {
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        return String.format("%.3f", durationInSeconds) + " segundos";
    }

    private String normalizeString(String text) {
        if (text == null) {
            return null;
        }
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }

    private String findAnswerByKeywords(String question) {
        long startTime = System.nanoTime();

        String normalizedQuestion = normalizeString(question);

        List<String> inputTokens = tokenizeQuestion(normalizedQuestion);

        String finalAnswer;
        String timeSuffix = "";

        if (inputTokens.isEmpty()) {
            finalAnswer = "No fue encontrada la respuesta, me estoy entrenado voy a guardarla para responderla en futuro";
            String keywordsToSave = tokenizeQuestionAsString(question);
            saveResponse(question, keywordsToSave);
        } else {
            List<KeywordAnswerData> allKeywordAnswerData = generateKeywordAnswerDataList();

            Map<ResponseKey, Integer> matchCountsPerResponse = new HashMap<>();
            for (KeywordAnswerData data : allKeywordAnswerData) {
                if (inputTokens.contains(data.getKeyword())) {
                    ResponseKey key = new ResponseKey(data.getId(), data.getAnswer());
                    matchCountsPerResponse.merge(key, 1, Integer::sum);
                }
            }

            int currentHighestMatchCount = 0;
            for (Integer count : matchCountsPerResponse.values()) {
                if (count > currentHighestMatchCount) {
                    currentHighestMatchCount = count;
                }
            }

            Set<String> uniqueBestAnswers = new LinkedHashSet<>();
            if (currentHighestMatchCount > 0) {
                for (Map.Entry<ResponseKey, Integer> entry : matchCountsPerResponse.entrySet()) {
                    if (entry.getValue() == currentHighestMatchCount) {
                        uniqueBestAnswers.add(entry.getKey().getAnswer());
                    }
                }
            }

            if (uniqueBestAnswers.isEmpty()) {
                finalAnswer = "No hay una respuesta para su consulta, me estoy entrenado la voy a guardar para buscarla";
                String keywordsToSave = tokenizeQuestionAsString(question);
                saveResponse(question, keywordsToSave);
            } else if (uniqueBestAnswers.size() == 1) {
                finalAnswer = uniqueBestAnswers.iterator().next();
                long endTime = System.nanoTime();
                timeSuffix = ", (Tiempo de respuesta: " + formatDuration(startTime, endTime) + ")";
            } else {
                StringBuilder sbMultipleAnswers = new StringBuilder();
                int counter = 0;

                for (String answer : uniqueBestAnswers) {
                    if (counter > 0) {
                        sbMultipleAnswers.append(", ");
                    }
                    sbMultipleAnswers.append(answer);
                    counter++;
                }

                finalAnswer = "Tengo varias respuestas posibles a tu pregunta: " + sbMultipleAnswers.toString();

                long endTime = System.nanoTime();
                timeSuffix = ", (Tiempo de respuesta: " + formatDuration(startTime, endTime) + ")";
            }
        }

        return finalAnswer + timeSuffix;
    }

    private void saveResponse(String question, String keywords) {
        Response response = this.responseRepository.save(new Response(question, normalizeString(keywords)));
    }
}
