package com.example.diagnosticarapi.repository;

import com.example.diagnosticarapi.model.Carro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarroRepository extends JpaRepository<Carro, Long> {
    // Comando mágico do Spring Data: só de escrever esse nome, ele cria o SQL de busca por Placa sozinho!
    Optional<Carro> findByPlaca(String placa);
}
