package br.com.aguiabranca.asasdaaguia.projeto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "projetos")
public class ProjetoModel {

    @Id
    private String id;

    private String nome;

    private String descricao;

    private String estrategiaId;

    private String ideiaOrigemId;

    private EtapaProjeto etapa;

    private BigDecimal investimento;

    private BigDecimal retornoFinanceiro;

    private LocalDate prazo;

    private String gestorResponsavelId;

    private BigDecimal produtividadeGanhaPercentual;

    public enum EtapaProjeto {
        PLANEJAMENTO,
        EM_ANDAMENTO,
        CONCLUIDO,
        CANCELADO
    }
}