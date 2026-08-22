package com.example.diagnosticarapi.repository;

import com.example.diagnosticarapi.model.DadosOficina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DadosOficinaRepository extends JpaRepository<DadosOficina, Integer> {
}
