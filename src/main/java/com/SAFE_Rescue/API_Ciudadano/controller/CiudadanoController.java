package com.SAFE_Rescue.API_Ciudadano.controller;

import com.SAFE_Rescue.API_Ciudadano.service.CiudadanoService;
import com.SAFE_Rescue.API_Ciudadano.modelo.Ciudadano;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controlador REST para la gestión de ciudadanos
 * Proporciona endpoints para operaciones CRUD y gestión de relaciones de ciudadanos
 */
@RestController
@RequestMapping("/api-ciudadano/v1/ciudadanos")
public class CiudadanoController {

    // SERVICIOS INYECTADOS

    @Autowired
    private CiudadanoService ciudadanoService;

    // OPERACIONES CRUD BÁSICAS

    /**
     * Obtiene todos los ciudadanos registrados en el sistema.
     * @return ResponseEntity con lista de ciudadanos o estado NO_CONTENT si no hay registros
     */
    @GetMapping
    public ResponseEntity<List<Ciudadano>> listar(){

        List<Ciudadano> ciudadanos = ciudadanoService.findAll();
        if(ciudadanos.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(ciudadanos);
    }

    /**
     * Busca un ciudadano por su ID.
     * @param id ID del ciudadano a buscar
     * @return ResponseEntity con el ciudadano encontrado o mensaje de error
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarCiudadano(@PathVariable long id) {
        Ciudadano ciudadano;

        try {
            ciudadano = ciudadanoService.findByID(id);
        }catch(NoSuchElementException e){
            return new ResponseEntity<String>("Ciudadano no encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(ciudadano);
    }

    /**
     * Crea un nuevo Ciudadano
     * @param ciudadano Datos del Ciudadano a crear
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping
    public ResponseEntity<String> agregarCiudadano(@RequestBody Ciudadano ciudadano) {
        try {
            Ciudadano nuevoCiudadano = ciudadanoService.save(ciudadano);
            return ResponseEntity.status(HttpStatus.CREATED).body("Ciudadano creado con éxito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor.");
        }
    }

    /**
     * Actualiza un Ciudadano existente.
     * @param id ID del Ciudadano a actualizar
     * @param ciudadano Datos actualizados del Ciudadanoo
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarCiudadano(@PathVariable long id, @RequestBody Ciudadano ciudadano) {
        try {
            Ciudadano nuevoCiudadano = ciudadanoService.update(ciudadano, id);
            return ResponseEntity.ok("Actualizado con éxito");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ciudadano no encontrado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor.");
        }
    }

    /**
     * Elimina un Ciudadano del sistema.
     * @param id ID del Ciudadano a eliminar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCiudadano(@PathVariable long id) {

        try {
            ciudadanoService.delete(id);
            return ResponseEntity.ok("Ciudadano eliminado con éxito.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ciudadano no encontrado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor.");
        }
    }

    // GESTIÓN DE RELACIONES

    /**
     * Asigna una credencial a un ciudadano
     * @param ciudadanoId ID del ciudadano
     * @param credencialId ID de la credencial a asignar
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping("/{ciudadanoId}/asignar-credencial/{credencialId}")
    public ResponseEntity<String> asignarCredencial(@PathVariable int ciudadanoId, @PathVariable int credencialId) {
        try {
            ciudadanoService.asignarCredencial(ciudadanoId, credencialId);
            return ResponseEntity.ok("Credencial asignada al Ciudadano exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
