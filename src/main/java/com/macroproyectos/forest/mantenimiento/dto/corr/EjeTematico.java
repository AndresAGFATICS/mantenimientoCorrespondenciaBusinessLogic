package com.macroproyectos.forest.mantenimiento.dto.corr;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Clase asociada a la tabla CORR.EJE_TEMATICO
 * 
 * @author AGTFATICS
 *
 */
@ApiModel(value = "EjeTematico")
public class EjeTematico implements Serializable {

	private static final long serialVersionUID = -1529428674752772371L;

	private Integer id;
	private String descripcion;
	private int activo;
	private String codigo;

	/**
	 * Retorna el identificador del eje tematico
	 * 
	 * @return Identificador del eje tematico
	 */
	@ApiModelProperty(value = "Identificador del eje tem\u00e1tico")
	public Integer getId() {
		return id;
	}

	/**
	 * Instancia el identificador del eje tematico
	 * 
	 * @param id
	 *            Identificador del eje tematico
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Retorna la descripcion del eje tematico
	 * 
	 * @return Descripcion del eje tematico
	 */
	@ApiModelProperty(value = "Descripci\u00f3n del eje tem\u00e1tico")
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Instancia la descripcion del eje tematico
	 * 
	 * @param descripcion
	 *            Descripcion del tipo del eje tematico
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Retorna 1 si el eje tematicoo esta activo y 0 si esta inactivo
	 * 
	 * @return 1 si el eje tematico esta activo y 0 si esta inactivo
	 */
	@ApiModelProperty(value = "Activo: 1 y 0 seg\u00fan el caso")
	public int getActivo() {
		return activo;
	}

	/**
	 * Instancia 1 si el eje tematico esta activo y 0 si esta inactivo
	 * 
	 * @param activo
	 *            1 si el eje tematico esta activo y 0 si esta inactivo
	 */
	public void setActivo(int activo) {
		this.activo = activo;
	}

	
	/**
	 * Retorna el codigo del eje tematico
	 * 
	 * @return Codigo del eje tematico
	 */
	@ApiModelProperty(value = "Codigo del eje tem\u00e1tico")
	public String getCodigo() {
		return codigo;
	}

	/**
	 * Instancia el codigo del eje tematico
	 * 
	 * @param codigo
	 *            Codigo del tipo del eje tematico
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("descripcion", descripcion).append("activo", activo).append("codigo", codigo)
				.toString();
	}

}
