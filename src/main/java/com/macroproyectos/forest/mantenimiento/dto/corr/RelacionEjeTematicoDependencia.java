package com.macroproyectos.forest.mantenimiento.dto.corr;

import java.io.Serializable;

import com.macroproyectos.forest.sistema.dto.forest.Dependencia;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Clase que representa la tabla CORR.REL_TRAMITE_TIPO_RADICADO.
 * 
 * Asocia el relacion de la relacion tramite tipo radicado con una dependencia
 * 
 * @author elizabeth.rangel
 *
 */
@ApiModel(value = "Relaci\u00f3n Eje Tem\u00e1tico - Dependencia")
public class RelacionEjeTematicoDependencia implements Serializable {

	private static final long serialVersionUID = 7383592428687412896L;

	private Integer id;
	private EjeTematico ejeTematico;
	private Dependencia dependencia;

	/**
	 * Retorna el identificador de la relacion de la relacion de ejes tematicos y
	 * dependencias
	 * 
	 * @return Identificador de la relacion
	 */
	@ApiModelProperty(value = "Identificador de la relaci\u00f3n de Eje Tem\u00e1tico  - Dependencia")
	public Integer getId() {
		return id;
	}

	/**
	 * Instancia el identificador de la relacion de ejes tematicos y dependencias
	 * 
	 * @param id
	 *            Identificador
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Retorna el eje tematico relacionada
	 * 
	 * @return ejeTematico
	 */
	@ApiModelProperty(value = "Eje Tem\u00e1tico relacionado")
	public EjeTematico getEjeTematico() {
		return ejeTematico;
	}

	/**
	 * Instancia el eje tematico relacionado
	 * 
	 * @param ejeTematico
	 *            Eje tematico relacionado
	 * 
	 */
	public void setEjeTematico(EjeTematico ejeTematico) {
		this.ejeTematico = ejeTematico;
	}

	/**
	 * Retorna la dependencia relacionada
	 * 
	 * @return Dependencia
	 */
	@ApiModelProperty(value = "Dependencia relacionada")
	public Dependencia getDependencia() {
		return dependencia;
	}

	/**
	 * Instancia dependencia relacionada
	 * 
	 * @param dependencia
	 *            Dependencia
	 */
	public void setDependencia(Dependencia dependencia) {
		this.dependencia = dependencia;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("ejeTematico", ejeTematico)
				.append("dependencia", dependencia).toString();
	}
	
}
