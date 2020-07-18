package com.macroproyectos.forest.proyecto.dto.esquema;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Clase asociada a la tabla [esquema.nombre_tabla] o a la entidad [nombre_entidad]
 * 
 * Representa... xxx por ejemplo: xxxxxx.
 * 
 * @author paola.moreno
 *
 */
@ApiModel(value = "Clase de ejemplo para un tr\u00e1mite")
public class Clase implements Serializable {

	// Generar SIEMPRE esta variable
	private static final long serialVersionUID = 4698524734576468261L;
	private Integer id;
	private String codigo;
    private String nombre;
    private String descripcion;
    private String codigoNombre;
    

	/**
     * Documentar
     * 
     * @return
     */
    @ApiModelProperty(value = "Identificador")
	public int getId() {
		return id;
	}
    
    /**
     * Documentar
     * 
     * @param id
     */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
     * Documentar
     * 
     * @return
     */
    @ApiModelProperty(value = "Codigo")	
	public String getCodigo() {
		return codigo;
	}

    /**
     * Documentar
     * 
     * @param codigo
     */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/**
     * Documentar
     * 
     * @return
     */
    @ApiModelProperty(value = "Nombre")
	public String getNombre() {
		return nombre;
	}
    
    /**
     * Documentar
     * 
     * @param nombre
     */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	/**
     * Documentar
     * 
     * @return
     */
    @ApiModelProperty(value = "Descripci\u00f3n")
	public String getDescripcion() {
		return descripcion;
	}
    
    /**
     * Documentar
     * 
     * @param descripcion
     */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	

	/**
     * Documentar
     * 
     * @return
     */
    @ApiModelProperty(value = "Codigo y nombre")
	public String getCodigoNombre() {
		return codigoNombre;
	}

    
    /**
     * Documentar
     * 
     * @param codigoNombre
     */
	public void setCodigoNombre(String codigoNombre) {
		this.codigoNombre = codigoNombre;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof Clase)) {
			return false;
		}
		Clase castOther = (Clase) other;
		return new EqualsBuilder().append(id, castOther.id).append(codigo, castOther.codigo)
				.append(nombre, castOther.nombre).append(descripcion, castOther.descripcion)
				.append(codigoNombre, castOther.codigoNombre).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(codigo).append(nombre).append(descripcion).append(codigoNombre)
				.toHashCode();
	}
    

}
