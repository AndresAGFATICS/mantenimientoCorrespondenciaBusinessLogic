package com.macroproyectos.forest.mantenimiento.mapper.mantenimiento;

import java.util.List;
import java.util.Map;

import com.macroproyectos.forest.mantenimiento.dto.corr.RelacionEjeTematicoDependencia;
import com.macroproyectos.forest.mantenimiento.mapper.mantenimiento.MantenimientoMapperController;

/**
 * Interface que define el nombre de los metodos utilizados en la
 * automatizacion.
 * 
 * @author elizabeth.rangel
 *
 */
public interface ConfigurarEjeTematicoMapper extends MantenimientoMapperController {

	List<RelacionEjeTematicoDependencia> consultarEjeTematicoDependencia(Map<String, Object> parameters);

	List<RelacionEjeTematicoDependencia> consultarEjeTematicoDependenciaDuplicado(Map<String, Object> parameters);

	int consultarEjeTematicoDependenciaRelacion(Map<String, Object> parameter);

	int consultarEjeTematicoRadicadoRelacion(Map<String, Object> parameter);

	int consultarEjeTematicoPreguntaRelacion(Map<String, Object> parameter);

}
