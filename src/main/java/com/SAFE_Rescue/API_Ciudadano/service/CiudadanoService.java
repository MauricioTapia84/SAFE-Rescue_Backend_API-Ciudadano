package com.SAFE_Rescue.API_Ciudadano.service;

import com.SAFE_Rescue.API_Ciudadano.modelo.Credencial;
import com.SAFE_Rescue.API_Ciudadano.repository.CiudadanoRepository;
import com.SAFE_Rescue.API_Ciudadano.modelo.Ciudadano;
import com.SAFE_Rescue.API_Ciudadano.repository.CredencialRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Servicio para la gestión integral de ciudadano
 * Maneja operaciones CRUD, asignación de credeniales
 * y validación de datos para ciudadano
 */
@Service
@Transactional
public class CiudadanoService {

    // REPOSITORIOS INYECTADOS
    @Autowired private CiudadanoRepository ciudadanoRepository;
    @Autowired private CredencialRepository credencialRepository;

    // SERVICIOS INYECTADOS
    @Autowired private CredencialService credencialService;


    // MÉTODOS CRUD PRINCIPALES

    /**
     * Obtiene todos los ciudadanos registrados en el sistema.
     * @return Lista completa de ciudadanos
     */
    public List<Ciudadano> findAll(){
        return ciudadanoRepository.findAll();
    }

    /**
     * Busca un Ciudadano por su ID único.
     * @param id Identificador del Ciudadano
     * @return Ciudadano encontrado
     * @throws NoSuchElementException Si no se encuentra el Ciudadano
     */
    public Ciudadano findByID(long id){
        return ciudadanoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró Ciudadano con ID: " + id));
    }

    /**
     * Guarda un nuevo ciudadano en el sistema.
     * Realiza validaciones y guarda relaciones con otros componentes.
     * @param ciudadano Datos del ciudadano a guardar
     * @return ciudadano guardado con ID generado
     * @throws RuntimeException Si ocurre algún error durante el proceso
     * @throws DataIntegrityViolationException Si ocurre algún error durante el proceso
     */
    public Ciudadano save(Ciudadano ciudadano) {
        try {

            Credencial guardadaCredencial = credencialService.save(ciudadano.getCredencial());

            ciudadano.setCredencial(guardadaCredencial);

            validarCiudadano(ciudadano);

            return ciudadanoRepository.save(ciudadano);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Error: el correo de la credencial ya está en uso.");
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Error al guardar el Ciudadano: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Actualiza los datos de un ciudadano existente.
     * @param ciudadano Datos actualizados del ciudadano
     * @param id Identificador del ciudadano a actualizar
     * @return ciudadano actualizado
     * @throws IllegalArgumentException Si el ciudadano proporcionado es nulo
     * @throws NoSuchElementException Si no se encuentra el ciudadano a actualizar
     * @throws RuntimeException Si ocurre algún error durante la actualización
     */
    public Ciudadano update(Ciudadano ciudadano, long id) {
        try {
            if (ciudadano == null) {
                throw new IllegalArgumentException("El ciudadano no puede ser nulo");
            }

            Ciudadano antiguoCiudadano = ciudadanoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Ciudadano no encontrado"));

            //Control de errores
            if (ciudadano.getNombre() != null) {
                if (ciudadano.getNombre().length() > 50) {
                    throw new RuntimeException("El valor nombre excede máximo de caracteres (50)");
                }else{
                    antiguoCiudadano.setNombre(ciudadano.getNombre());
                }
            }

            if (ciudadano.getTelefono() != null) {
                if (ciudadanoRepository.existsByTelefono(ciudadano.getTelefono())) {
                    throw new RuntimeException("El Telefono ya existe");
                }else{
                    if (String.valueOf(ciudadano.getTelefono()).length()> 9) {
                        throw new RuntimeException("El valor telefono excede máximo de caracteres (9)");
                    }else {
                        antiguoCiudadano.setTelefono(ciudadano.getTelefono());
                    }
                }
            }

            if (ciudadano.getRun() != null) {
                if (ciudadanoRepository.existsByRun(ciudadano.getRun())) {
                    throw new RuntimeException("El RUN ya existe");
                }else{
                    if (String.valueOf(ciudadano.getRun()).length() > 8) {
                        throw new RuntimeException("El valor RUN excede máximo de caracteres (8)");
                    }else{
                        antiguoCiudadano.setRun(ciudadano.getRun());
                    }
                }
            }

            if (ciudadano.getDv() != null) {
                if (ciudadano.getDv().length() > 1) {
                    throw new RuntimeException("El valor DV excede máximo de caracteres (1)");
                }else{
                    antiguoCiudadano.setDv(ciudadano.getDv());
                }
            }

            if (ciudadano.getAPaterno() != null) {
                if (ciudadano.getAPaterno().length() > 50) {
                    throw new RuntimeException("El valor a_paterno excede máximo de caracteres (50)");
                }else{
                    antiguoCiudadano.setAPaterno(ciudadano.getAPaterno());
                }
            }

            if (ciudadano.getAMaterno() != null) {
                if (ciudadano.getAMaterno().length() > 50) {
                    throw new RuntimeException("El valor a_materno excede máximo de caracteres (50)");
                }else{
                    antiguoCiudadano.setAMaterno(ciudadano.getAMaterno());
                }
            }

            if (ciudadano.getFechaRegistro() != null) {
                antiguoCiudadano.setFechaRegistro(ciudadano.getFechaRegistro());
            }

            return ciudadanoRepository.save(antiguoCiudadano);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el Ciudadano: " + e.getMessage());
        }
    }

    /**
     * Elimina un ciudadano del sistema.
     * @param id Identificador del ciudadano a eliminar
     * @throws NoSuchElementException Si no se encuentra el ciudadano
     */
    public void delete(long id){
        if (!ciudadanoRepository.existsById(id)) {
            throw new NoSuchElementException("Ciudadano no encontrado");
        }
        ciudadanoRepository.deleteById(id);
    }

    // MÉTODOS PRIVADOS DE VALIDACIÓN Y UTILIDADES

    /**
     * Valida el ciudadano
     * @param ciudadano Ciudadano
     * @throws IllegalArgumentException Si el ciudadano no cumple con las reglas de validación
     */
    public void validarCiudadano(Ciudadano ciudadano) {


        if (ciudadano.getRun() >= 0) {
            throw new IllegalArgumentException("La Cantidad debe ser un número positivo");
        } else {
            if (String.valueOf(ciudadano.getRun()).length() > 8) {
                throw new RuntimeException("El valor RUN excede máximo de caracteres (8)");
            }else{
                if (ciudadanoRepository.existsByRun(ciudadano.getRun())) {
                    throw new RuntimeException("El RUN ya existe");
                }
            }
        }

        if (ciudadano.getDv() != null) {
            if (ciudadano.getDv().length() > 1) {
                throw new RuntimeException("El valor DV excede máximo de caracteres (1)");
            }
        } else {
            throw new IllegalArgumentException("El DV del ciudadano es requerido");
        }

        if (ciudadano.getNombre() != null) {
            if (ciudadano.getNombre().length() > 50) {
                throw new RuntimeException("El valor nombre del ciudadano excede máximo de caracteres (50)");
            }
        } else {
            throw new IllegalArgumentException("El nombre del ciudadano es requerido");
        }

        if (ciudadano.getAPaterno() != null) {
            if (ciudadano.getAPaterno().length() > 50) {
                throw new RuntimeException("El valor a_paterno del ciudadano excede máximo de caracteres (50)");
            }
        } else {
            throw new IllegalArgumentException("El a_paterno  del ciudadano es requerido");
        }

        if (ciudadano.getAMaterno() != null) {
            if (ciudadano.getAMaterno().length() > 50) {
                throw new RuntimeException("El valor a_materno excede máximo de caracteres (50)");
            }
        } else {
            throw new IllegalArgumentException("El a_paterno  del ciudadano es requerido");
        }

        if (ciudadano.getTelefono() >= 0) {
            throw new IllegalArgumentException("La Cantidad debe ser un número positivo");
        } else {
            if (String.valueOf(ciudadano.getTelefono()).length()> 9) {
                throw new RuntimeException("El valor telefono excede máximo de caracteres (9)");
            }else{
                if (ciudadanoRepository.existsByTelefono(ciudadano.getTelefono())) {
                    throw new RuntimeException("El Telefono ya existe");
                }
            }
        }

    }

    // MÉTODOS DE ASIGNACIÓN DE RELACIONES

    /**
     * Asigna un Credencial a un ciudadano
     * @param ciudadanoId ID del ciudadano
     * @param credencialId ID del credencial
     */
    public void asignarCredencial(long ciudadanoId, long credencialId) {
        Ciudadano ciudadano = ciudadanoRepository.findById(ciudadanoId)
                .orElseThrow(() -> new RuntimeException("Ciudadano no encontrado"));

        Credencial credencial = credencialRepository.findById(credencialId)
                .orElseThrow(() -> new RuntimeException("Credencial no encontrada"));

        ciudadano.setCredencial(credencial);
        ciudadanoRepository.save(ciudadano);
    }

}