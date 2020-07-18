package com.macroproyectos.forest.mantenimiento.mapper.mantenimiento;

import com.macroproyectos.forest.mantenimiento.dto.corr.EjeTematico;

import java.util.List;
import java.util.Map;

public interface EjeTematicoMapper extends MantenimientoMapperController {

	List<EjeTematico> consultarEjeTematico(Map<String, Object> parameters);

	List<EjeTematico> consultarEjeTematicoDuplicado(Map<String, Object> parameters);
	
	Long consultarSecuencia();

}
