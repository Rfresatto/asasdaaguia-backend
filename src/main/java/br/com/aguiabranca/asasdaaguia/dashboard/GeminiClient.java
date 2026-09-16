package br.com.aguiabranca.asasdaaguia.dashboard;

import br.com.aguiabranca.asasdaaguia.common.exception.NegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class GeminiClient {

    private final RestClient restClient;
    private final String apiKey;

    public GeminiClient(
            @Value("${gemini.api.url}") String apiUrl,
            @Value("${gemini.api.key}") String apiKey
    ) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder().baseUrl(apiUrl).build();
    }

    @SuppressWarnings("unchecked")
    public String gerarInsights(String prompt) {
        Map<String, Object> corpo = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        try {
            Map<String, Object> resposta = restClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(corpo)
                    .retrieve()
                    .body(Map.class);

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) resposta.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            throw new NegocioException("Erro ao gerar insights com IA: " + e.getMessage());
        }
    }
}