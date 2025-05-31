package com.SAFE_Rescue.API_Ciudadano.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un credencial en el sistema.
 * Contiene información sobre la composición y estado del credencial
 */
@Entity
@Table(name = "credencial_ciudadana")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Credencial {

    /**
     * Identificador único del credencial
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Correo de la credencial
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 80 caracteres
     */
    @Column(unique = true,length = 80,nullable = false)
    private String correo;

    /**
     * Contrasenia de la credencial
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 16 caracteres
     */
    @Column(length = 16, nullable = false)
    private String contrasenia;

    /**
     * Intentos Fallidos del ciudadano al iniciar sesion
     * Valor entero no negativo (>= 0)
     */
    @Column(name ="intentos_fallidos",length = 1, nullable = true)
    private int intentosFallidos = 0;

    /**
     * Estadode la credencial
     * Debe ser un valor no nulo
     */
    @Column(nullable = false)
    private boolean activo;


}
