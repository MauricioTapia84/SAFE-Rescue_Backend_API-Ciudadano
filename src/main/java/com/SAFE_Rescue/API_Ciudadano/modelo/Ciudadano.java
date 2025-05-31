package com.SAFE_Rescue.API_Ciudadano.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Entidad que representa un Ciudadano en el sistema.
 * Contiene información sobre la composición y estado del Ciudadano
 */
@Entity
@Table(name = "ciudadano")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Ciudadano {

    /**
     * Identificador único del ciudadano
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Run del ciudadano
     * Debe ser un valor no nulo, único y con una longitud máxima recomendada de 8 caracteres
     */
    @Column(unique = true,length = 8,nullable = false)
    private Long run;

    /**
     * Digito verificador del ciudadano
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 1 caracteres
     */
    @Column(length = 1,nullable = false)
    private String dv;

    /**
     * Nombre descriptivo del ciudadano
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 50 caracteres
     */
    @Column(length = 50,nullable = false)
    private String nombre;

    /**
     * Apellido paterno descriptivo del ciudadano
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 50 caracteres
     */
    @Column(name= "a_paterno",length = 50,nullable = false)
    private String aPaterno;

    /**
     * Apellido materno descriptivo del ciudadano
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 50 caracteres
     */
    @Column(name= "a_materno",length = 50,nullable = false)
    private String aMaterno;

    /**
     * Fecha Registro del ciudadano
     * Debe ser un valor no nulo
     */
    @Column(name= "fecha_registro",nullable = false)
    private Date fechaRegistro;

    /**
     * Telefono disponible del ciudadano
     * Valor entero no negativo (>= 0)
     * Representa unidades disponibles en inventario
     */
    @Column(unique = true,length = 9,nullable = false)
    private Long telefono;

    /**
     * Credenciales
     * Relación uno-a-muchos
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "credenciales_id", referencedColumnName = "id")
    private Credencial credencial;

}
