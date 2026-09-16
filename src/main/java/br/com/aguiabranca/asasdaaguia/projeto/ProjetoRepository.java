package br.com.aguiabranca.asasdaaguia.projeto;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjetoRepository extends MongoRepository<ProjetoModel, String> {

    List<ProjetoModel> findByGestorResponsavelId(String gestorId);

    List<ProjetoModel> findByEstrategiaId(String estrategiaId);
}