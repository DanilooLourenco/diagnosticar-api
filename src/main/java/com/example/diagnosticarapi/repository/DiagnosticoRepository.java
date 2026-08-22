package com.example.diagnosticarapi.repository;

import com.example.diagnosticarapi.model.Diagnostico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository


public interface DiagnosticoRepository extends JpaRepository<Diagnostico, Long> {
    List<Diagnostico> findByCarroPlaca(String placa);
}

