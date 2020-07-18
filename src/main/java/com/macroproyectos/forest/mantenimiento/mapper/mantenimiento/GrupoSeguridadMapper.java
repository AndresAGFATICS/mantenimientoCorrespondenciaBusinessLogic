package com.macroproyectos.forest.mantenimiento.mapper.mantenimiento;

import java.util.List;
import java.util.Map;

import com.macroproyectos.forest.mantenimiento.dto.corr.GrupoSeguridad;

public interface GrupoSeguridadMapper extends MantenimientoMapperController {

	List<GrupoSeguridad> consultarGrupoSeguridad(Map<String, Object> parameters);
	
	void actualizaGrupoSeguridad(Map<String, Object> parameters);
	
}
