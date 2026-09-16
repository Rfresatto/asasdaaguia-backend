package br.com.aguiabranca.asasdaaguia.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record ResumoGeralDTO(
        long totalProjetos,
        long projetosConcluidos,
        long projetosEmAndamento,
        BigDecimal investimentoTotal,
        BigDecimal retornoTotal,
        BigDecimal lucroTotal,
        BigDecimal roiMedio,
        List<ResumoProjetoDTO> projetos
) {
}