package com.SAFE_Rescue.API_Ciudadano.service;

import com.SAFE_Rescue.API_Ciudadano.repository.CredencialRepository;
import com.SAFE_Rescue.API_Ciudadano.modelo.Credencial;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.NoSuchElementException;

/**
 * Servicio para la gestión integral de credencial
 * Maneja operaciones CRUD, asignación de credeniales
 * y validación de datos para credencial
 */
@Service
@Transactional
public class CredencialService {

    // REPOSITORIOS INYECTADOS
    @Autowired
    private CredencialRepository credencialRepository;

    // MÉTODOS CRUD PRINCIPALES

    /**
     * Obtiene todas las credenciales registradas en el sistema.
     * @return Lista completa de credenciales
     */
    public List<Credencial> findAll(){
        return credencialRepository.findAll();
    }

    /**
     * Busca una credencial por su ID único.
     * @param id Identificador del credencial
     * @return credencial encontrado
     * @throws NoSuchElementException Si no se encuentra el credencial
     */
    public Credencial findByID(long id){
        return credencialRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró Credencial con ID: " + id));
    }

    /**
     * Guarda un nuevo credencial en el sistema.
     * Realiza validaciones y guarda relaciones con otros componentes.
     * @param credencial Datos del credencial a guardar
     * @return credencial guardado con ID generado
     * @throws RuntimeException Si ocurre algún error durante el proceso
     * @throws DataIntegrityViolationException Si ocurre algún error durante el proceso
     */
    public Credencial save(Credencial credencial) {
        try {
            validarCredencial(credencial);
            return credencialRepository.save(credencial);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("El correo ya está en uso. Por favor, use otro.");
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Error al guardar la credencial: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Actualiza los datos de un credencial existente.
     * @param credencial Datos actualizados del credencial
     * @param id Identificador del credencial a actualizar
     * @return credencial actualizado
     * @throws IllegalArgumentException Si el credencial proporcionado es nulo
     * @throws NoSuchElementException Si no se encuentra el credencial a actualizar
     * @throws RuntimeException Si ocurre algún error durante la actualización
     */
    public Credencial update(Credencial credencial ,long id) {
        try {
            if (credencial == null) {
                throw new IllegalArgumentException("El Credencial no puede ser nulo");
            }

            Credencial antiguaCredencial = credencialRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Credencial no encontrada"));

            //Control de errores
            if (credencial.getContrasenia() != null) {
                if (credencial.getContrasenia().length() > 16) {
                    throw new RuntimeException("El valor contrasenia excede máximo de caracteres (16)");
                }else {
                    antiguaCredencial.setContrasenia(credencial.getContrasenia());
                }
            }

            if (credencial.getCorreo() != null) {
                if (credencialRepository.existsByCorreo(credencial.getCorreo())) {
                    throw new RuntimeException("El Correo ya existe");
                }else{
                    if (credencial.getCorreo().length() > 80) {
                        throw new RuntimeException("El valor correo excede máximo de caracteres (80)");
                    }else {
                        antiguaCredencial.setCorreo(credencial.getCorreo());
                    }
                }
            }

            antiguaCredencial.setActivo(credencial.isActivo());
            return credencialRepository.save(antiguaCredencial);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el Credencial: " + e.getMessage());
        }
    }

    /**
     * Elimina un credencial del sistema.
     * @param id Identificador del credencial a eliminar
     * @throws NoSuchElementException Si no se encuentra el credencial
     */
    public void delete(long id){

        if (!credencialRepository.existsById(id)) {
            throw new NoSuchElementException("Credencial no encontrada");
        }
        credencialRepository.deleteById(id);
    }

    /**
     * Valida la credencial
     * @param credencial credencial
     * @throws IllegalArgumentException Si la credencial no cumple con las reglas de validación
     */
    public void validarCredencial(Credencial credencial) {

        if (credencial.getIntentosFallidos() < 0) {
            throw new IllegalArgumentException("La Cantidad debe ser un número positivo");
        }

        if (credencial.getContrasenia() != null) {
            if (credencial.getContrasenia().length() > 16) {
                throw new RuntimeException("El valor Contrasenia excede máximo de caracteres (16)");
            }
        } else {
            throw new IllegalArgumentException("La Contrasenia del ciudadano es requerido");
        }

        if (credencial.getCorreo() != null) {
            if (credencial.getCorreo().length() > 80) {
                throw new RuntimeException("El valor de Correo excede máximo de caracteres (80)");
            }
        } else {
            throw new IllegalArgumentException("El Correo es requerido");
        }
    }

    // MÉTODOS DE VERIFICACION DE CREDENCIALES PARA LOGIN

    /**
     * Verifica la contraseña al iniciar sesión
     * @param correo correo del ciudadano
     * @param contrasenia contrasenia del ciudadano
     */
    public boolean verificarCredenciales(String correo, String contrasenia) {
        Credencial credencial = credencialRepository.findByCorreo(correo);
        if (credencial != null) {
            boolean sonCorrectas = contrasenia.equals(credencial.getContrasenia());
            if (!sonCorrectas) {
                credencial.setIntentosFallidos(credencial.getIntentosFallidos() + 1);
                credencialRepository.save(credencial);
            }
            return sonCorrectas;
        }
        return false;
    }


}
