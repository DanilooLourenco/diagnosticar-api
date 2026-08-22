package com.example.diagnosticarapi.repository;

import com.example.diagnosticarapi.model.ClienteDevedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteDevedorRepository extends JpaRepository<ClienteDevedor, Long> {

    // Busca o devedor garantindo que ele pertença à oficina logada
    @Query("SELECT c FROM ClienteDevedor c WHERE c.usuario.id = :usuarioId AND (c.nome LIKE %:termo% OR c.apelido LIKE %:termo% OR c.documento = :termo)")
    Optional<ClienteDevedor> findByTermoEUsuario(@Param("termo") String termo, @Param("usuarioId") Long usuarioId);

    // 🚀 Colamos essa busca inteligente aqui dentro:
    @Query("SELECT c FROM ClienteDevedor c WHERE " +
            "LOWER(c.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "LOWER(c.apelido) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "c.documento = :busca")
    Optional<ClienteDevedor> findByNomeOrApelidoOrDocumento(@Param("busca") String busca);
}