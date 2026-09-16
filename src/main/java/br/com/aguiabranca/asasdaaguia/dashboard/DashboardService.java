package br.com.aguiabranca.asasdaaguia.dashboard;

import br.com.aguiabranca.asasdaaguia.projeto.ProjetoModel;
import br.com.aguiabranca.asasdaaguia.projeto.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProjetoRepository projetoRepository;
    private final GeminiClient geminiClient;

    public ResumoGeralDTO resumoGeral() {
        List<ProjetoModel> projetos = projetoRepository.findAll();

        List<ResumoProjetoDTO> resumosIndividuais = projetos.stream()
                .map(this::montarResumo)
                .toList();

        BigDecimal investimentoTotal = somar(projetos, ProjetoModel::getInvestimento);
        BigDecimal retornoTotal = somar(projetos, p -> nvl(p.getRetornoFinanceiro()));
        BigDecimal lucroTotal = retornoTotal.subtract(investimentoTotal);

        long concluidos = projetos.stream().filter(p -> p.getEtapa() == ProjetoModel.EtapaProjeto.CONCLUIDO).count();
        long emAndamento = projetos.stream().filter(p -> p.getEtapa() == ProjetoModel.EtapaProjeto.EM_ANDAMENTO).count();

        BigDecimal roiMedio = calcularRoi(investimentoTotal, retornoTotal);

        return new ResumoGeralDTO(
                projetos.size(),
                concluidos,
                emAndamento,
                investimentoTotal,
                retornoTotal,
                lucroTotal,
                roiMedio,
                resumosIndividuais
        );
    }

    public List<ResumoProjetoDTO> resumoPorEstrategia(String estrategiaId) {
        return projetoRepository.findByEstrategiaId(estrategiaId).stream()
                .map(this::montarResumo)
                .toList();
    }

    private ResumoProjetoDTO montarResumo(ProjetoModel projeto) {
        BigDecimal investimento = nvl(projeto.getInvestimento());
        BigDecimal retorno = nvl(projeto.getRetornoFinanceiro());
        BigDecimal lucro = retorno.subtract(investimento);
        BigDecimal roi = calcularRoi(investimento, retorno);

        return new ResumoProjetoDTO(
                projeto.getId(),
                projeto.getNome(),
                projeto.getEstrategiaId(),
                projeto.getEtapa(),
                investimento,
                retorno,
                lucro,
                roi,
                projeto.getProdutividadeGanhaPercentual(),
                projeto.getPrazo()
        );
    }

    private BigDecimal calcularRoi(BigDecimal investimento, BigDecimal retorno) {
        if (investimento == null || investimento.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return retorno.subtract(investimento)
                .divide(investimento, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal somar(List<ProjetoModel> projetos, java.util.function.Function<ProjetoModel, BigDecimal> extrator) {
        return projetos.stream()
                .map(extrator)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal nvl(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private String montarPrompt(ResumoGeralDTO resumo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Voce e um analista de negocios da empresa Aguia Branca (transporte rodoviario). ");
        sb.append("Analise os dados abaixo sobre os projetos de inovacao da empresa e gere de 3 a 5 insights ");
        sb.append("objetivos e acionaveis para a lideranca, em portugues, formato de lista com bullet points. ");
        sb.append("Foque em oportunidades de melhoria, riscos e destaques positivos.\n\n");

        sb.append("Total de projetos: ").append(resumo.totalProjetos()).append("\n");
        sb.append("Projetos concluidos: ").append(resumo.projetosConcluidos()).append("\n");
        sb.append("Projetos em andamento: ").append(resumo.projetosEmAndamento()).append("\n");
        sb.append("Investimento total: R$ ").append(resumo.investimentoTotal()).append("\n");
        sb.append("Retorno total: R$ ").append(resumo.retornoTotal()).append("\n");
        sb.append("Lucro total: R$ ").append(resumo.lucroTotal()).append("\n");
        sb.append("ROI medio: ").append(resumo.roiMedio()).append("%\n\n");

        sb.append("Detalhe por projeto:\n");
        resumo.projetos().forEach(p -> sb.append("- ")
                .append(p.nome())
                .append(" | etapa: ").append(p.etapa())
                .append(" | investimento: R$ ").append(p.investimento())
                .append(" | retorno: R$ ").append(p.retornoFinanceiro())
                .append(" | ROI: ").append(p.roi()).append("%")
                .append(" | produtividade: ").append(p.produtividadeGanhaPercentual()).append("%\n")
        );

        return sb.toString();
    }

    public InsightsDTO gerarInsights() {
        ResumoGeralDTO resumo = resumoGeral();

        String prompt = montarPrompt(resumo);
        String textoGerado = geminiClient.gerarInsights(prompt);

        return new InsightsDTO(textoGerado);
    }
}