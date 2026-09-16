package br.com.aguiabranca.asasdaaguia.estrategia;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EstrategiaRepository extends MongoRepository<EstrategiaModel, String> {

    Optional<EstrategiaModel> findByVigenteTrue();
}