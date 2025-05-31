package com.SAFE_Rescue.API_Ciudadano.repository;

import com.SAFE_Rescue.API_Ciudadano.modelo.Ciudadano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de credenciales
 * Maneja operaciones CRUD desde la base de datos usando Jakarta
 * Maneja validadores para encontrar el run y telefono
 */
@Repository
public interface CiudadanoRepository extends JpaRepository<Ciudadano, Long> {

    public boolean existsByRun(Long run);

    public boolean existsByTelefono(Long telefono);

}