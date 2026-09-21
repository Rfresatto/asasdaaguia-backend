package br.com.aguiabranca.asasdaaguia.estrategia;

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
@Document(collection = "estrategias")
public class EstrategiaModel {

    @Id
    private String id;

    private String titulo;

    private String descricao;

    private String categoria;

    private String campanha;

    private LocalDate dataCriacao;

    private boolean vigente;

    private String criadoPorId;

    private String peso;
}