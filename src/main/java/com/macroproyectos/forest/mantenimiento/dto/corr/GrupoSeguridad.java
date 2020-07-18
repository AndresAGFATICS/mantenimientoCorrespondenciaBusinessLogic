package com.macroproyectos.forest.mantenimiento.dto.corr;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Clase asociada a la tabla CORR.GRUPO_SEGURIDAD
 * 
 * @author AGTFATICS
 *
 */
@ApiModel(value = "GrupoSeguridad")
public class GrupoSeguridad implements Serializable {
	
	private static final long serialVersionUID = -1529428674752772371L;

	private Integer id;
	private String codigo;
	private String descripcion;
	private Integer id_grupo;
	private String observacion;
	private int activo;
	private int editable;
	
	
	/**
	 * Retorna el identificador del Grupo de Seguridad
	 * 
	 * @return Identificador del Grupo de Seguridad
	 */
	@ApiModelProperty(value = "Identificador del Grupo de Seguridad")
	public Integer getId() {
		return id;
	}
	/**
	 * Instancia el identificador del Grupo de Seguridad
	 * 
	 * @param id
	 *            Identificador del Grupo de Seguridad
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	/**
	 * Retorna el c\u00f3digo del Grupo de Seguridad
	 * 
	 * @return C\u00f3digo del Grupo de Seguridad
	 */
	@ApiModelProperty(value = "C\u00f3digo del Grupo de Seguridad")
	public String getCodigo() {
		return codigo;
	}
	/**
	 * Instancia el c\u00f3digo del Grupo de Seguridad
	 * 
	 * @param codigo
	 *            c\u00f3digo del Grupo de Seguridad
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	
	/**
	 * Retorna el descripci\u00f3n del Grupo de Seguridad
	 * 
	 * @return Descripci\u00f3n del Grupo de Seguridad
	 */
	@ApiModelProperty(value = "Descripci\u00f3n del Grupo de Seguridad")
	public String getDescripcion() {
		return descripcion;
	}
	/**
	 * Instancia el descripci\u00f3n del Grupo de Seguridad
	 * 
	 * @param descripcion
	 *            descripci\u00f3n del Grupo de Seguridad
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
	/**
	 * Retorna el identificador del grupo del Grupo de Seguridad
	 * 
	 * @return Identificador del grupo del Grupo de Seguridad
	 */
	@ApiModelProperty(value = "Identificador del grupo del Grupo de Seguridad")
	public Integer getId_grupo() {
		return id_grupo;
	}
	/**
	 * Instancia identificador del grupo del Grupo de Seguridad
	 * 
	 * @param id_grupo
	 *            Identificador del grupo del Grupo de Seguridad
	 */
	public void setId_grupo(Integer id_grupo) {
		this.id_grupo = id_grupo;
	}
	
	
	/**
	 * Retorna la observaci\u00f3n del grupo del Grupo de Seguridad
	 * 
	 * @return Observaci\u00f3n del grupo del Grupo de Seguridad
	 */
	@ApiModelProperty(value = "Observaci\u00f3n del grupo del Grupo de Seguridad")
	public String getObservacion() {
		return observacion;
	}
	/**
	 * Instancia observaci\u00f3n del grupo del Grupo de Seguridad
	 * 
	 * @param observacion
	 *            observaci\u00f3n del grupo del Grupo de Seguridad
	 */
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	
	
	/**
	 * Retorna el activo del grupo del Grupo de Seguridad
	 * 
	 * @return Activo del grupo del Grupo de Seguridad
	 */
	@ApiModelProperty(value = "Activo del grupo del Grupo de Seguridad")
	public int getActivo() {
		return activo;
	}
	/**
	 * Instancia activo del grupo del Grupo de Seguridad
	 * 
	 * @param activo
	 *            activo del grupo del Grupo de Seguridad
	 */
	public void setActivo(int activo) {
		this.activo = activo;
	}
	
	
	/**
	 * Retorna el editable del grupo del Grupo de Seguridad
	 * 
	 * @return Editable del grupo del Grupo de Seguridad
	 */
	@ApiModelProperty(value = "Editable del grupo del Grupo de Seguridad")
	public int getEditable() {
		return editable;
	}
	/**
	 * Instancia editable del grupo del Grupo de Seguridad
	 * 
	 * @param editable
	 *            editable del grupo del Grupo de Seguridad
	 */
	public void setEditable(int editable) {
		this.editable = editable;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("codigo", codigo).append("descripcion", descripcion).append("id_grupo", id_grupo).append("observacion", observacion).append("activo", activo).append("editable", editable)
				.toString();
	}
	

}
