package com.example.diagnosticarapi.repository;

import com.example.diagnosticarapi.model.ServicoRealizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicoRealizadoRepository extends JpaRepository<ServicoRealizado, Long> {
    List<ServicoRealizado> findByVeiculoPlaca(String veiculoPlaca);
    List<ServicoRealizado> findByUsuarioId(Long usuarioId);
}