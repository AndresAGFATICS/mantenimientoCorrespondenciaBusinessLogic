package com.macroproyectos.forest.mantenimiento.mapper.mantenimiento;

import java.util.List;
import java.util.Map;

import com.macroproyectos.api.automatization.mapper.MapperController;
import com.macroproyectos.forest.mantenimiento.service.ConfigProyectoApp;
import com.macroproyectos.forest.sistema.exception.ProcessException;
import com.macroproyectos.forest.sistema.service.ConfigApp;

/**
 * Interfaz Mapper controladora que contiene el metodo generico de paginacion
 * para cualquier tipo de consulta
 * 
 * @author Kenneth Sanchez
 *
 */
@SuppressWarnings("rawtypes")
public interface MantenimientoMapperController extends MapperController {
	/**
	 * Retorna la lista paginada con la informacion retornada por la consulta
	 * suministrada en queryName, la cual debe estar registrada en la interfaz
	 * mapper especificada
	 * 
	 * @param queryName
	 *            Consulta a ejecutar
	 * @param parameters
	 *            Parametros de la consulta
	 * @param pageSize
	 *            Numero maximo de registros por pagina
	 * @param pageNumber
	 *            Numero de pagina a consulta
	 * 
	 * @return Lista de objetos con el resultado de la consulta paginada
	 * 
	 * @throws ProcessException
	 *             Excepcion del proceso
	 */
	public default List getPaginatedResult(String queryName, Map<String, Object> parameters, Integer pageSize,
			Integer pageNumber) throws ProcessException {

		if (pageSize == null && pageNumber == null)
			return getPaginatedResult(queryName, parameters, pageSize, pageNumber, false, false);
		else
			return getPaginatedResult(queryName, parameters, pageSize, pageNumber, true, true);

	}

	/**
	 * Retorna la lista paginada con la informacion retornada por la consulta
	 * suministrada en queryName, la cual debe estar registrada en la interfaz
	 * mapper especificada
	 * 
	 * @param queryName
	 *            Consulta a ejecutar
	 * @param parameters
	 *            Parametros de la consulta
	 * @param pageSize
	 *            Numero maximo de registros por pagina
	 * @param pageNumber
	 *            Numero de pagina a consulta
	 * @param count
	 *            Indica si el componente efectua conteo en la paginacion (Ejemplo
	 *            un autocompletar no requiere saber el total de paginas)
	 * @param paginate
	 *            Indica si el componente efectua los parametros con paginacion
	 *            activa
	 * 
	 * @return Lista de objetos con el resultado de la consulta paginada
	 * 
	 * @throws ProcessException
	 *             Excepcion del proceso
	 */
	public default List getPaginatedResult(String queryName, Map<String, Object> parameters, Integer pageSize,
			Integer pageNumber, boolean count, boolean paginate) throws ProcessException {
		try {
			ConfigApp configSistema = new ConfigApp();
			ConfigProyectoApp configApp = new ConfigProyectoApp();

			return getPaginatedResult(queryName, parameters, pageSize, pageNumber, configApp.sqlSessionFactory(),
					configSistema.getDataSource(), count, paginate);

		} catch (Exception e) {
			throw new ProcessException(106, e, "Sistema Mapper Get paginated");
		}
	}
}