package br.com.aguiabranca.asasdaaguia.ideia;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IdeiaRepository extends MongoRepository<IdeiaModel, String> {

    List<IdeiaModel> findByOperadorId(String operadorId);

    List<IdeiaModel> findByStatus(IdeiaModel.StatusIdeia status);
}