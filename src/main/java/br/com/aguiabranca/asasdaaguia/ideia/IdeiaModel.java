package br.com.aguiabranca.asasdaaguia.ideia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ideias")
public class IdeiaModel {

    @Id
    private String id;

    private String titulo;

    private String descricao;

    private String operadorId;

    private String estrategiaId;

    private StatusIdeia status;

    private Integer prioridade;

    private LocalDate dataCriacao;

    public enum StatusIdeia {
        PENDENTE,
        APROVADA,
        REPROVADA
    }
}