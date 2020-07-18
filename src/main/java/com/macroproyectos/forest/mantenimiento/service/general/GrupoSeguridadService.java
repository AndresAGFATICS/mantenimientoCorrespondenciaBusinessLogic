package com.macroproyectos.forest.mantenimiento.service.general;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.macroproyectos.api.automatization.dto.Response;
import com.macroproyectos.api.automatization.dto.Transaction;
import com.macroproyectos.api.automatization.service.APISaveGridService;
import com.macroproyectos.api.automatization.util.MessageUtil;
import com.macroproyectos.forest.mantenimiento.business.util.Parameters;
import com.macroproyectos.forest.mantenimiento.dto.corr.GrupoSeguridad;
import com.macroproyectos.forest.mantenimiento.mapper.mantenimiento.EjeTematicoMapper;
import com.macroproyectos.forest.mantenimiento.mapper.mantenimiento.GrupoSeguridadMapper;
import com.macroproyectos.forest.seguridad.UserInfo;
import com.macroproyectos.forest.sistema.exception.ProcessException;
import com.macroproyectos.forest.sistema.log.MPLogger;


/**
 * Clase para consultar Los ejes tematicos
 * 
 * @author AGTFATICS
 *
 */
@Service
@SuppressWarnings({ "rawtypes", "unchecked" })
public class GrupoSeguridadService {
	

	 @Autowired
	 public transient GrupoSeguridadMapper mapperQ;
	 @Autowired
	 public transient GrupoSeguridadMapper mapperTx;

	 private static final String SAVE_OK = "save_ok";
	 private static final String STATE = "state";
	 private static final String ATTRIBUTES = "attributes";
	 private static final String FEATURES = "features";
	 private static final String DELETE = "delete";

	 private String labelId = "id";
	 private String labelCodigo = "codigo";
	 private String labelDescripcion = "descripcion";
	 private String labelId_gurpo = "id_grupo";
	 private String labelObservacion = "observacion";
	 private String labelActivo = "activo";
	 private String labelEditable = "editable";
	 
	 private String mensageDescripcionRepetida = "error_grupo_seguridad_descripcion";
	 
	 private String gridGrupoSeguridad = "grd_grupoSeguridad";
	 
	 
	 /**
	     * Retorna la lista de Grupos de seguridad existentes de acuerdo a los parametros
	     * establecidos y ordenados por codigo y descripcion.
	     * 
	     * @param parameters
	     *            Mapa de parametros a utilizar en la consulta: id Identificador del
	     *            registro, activo: 1 activo, 0 inactivo, descripcion: Descripci\u00f3n
	     *            del grupo de seguridad
	     * 
	     * @param pageSize
	     *            N\u00famero de registros a consultar por pagina
	     * @param pageNumber
	     *            N\u00famero de la pagina a consultar
	     * 
	     * @return Lista de grupos de seguridad
	     * @throws ProcessException
	     *             Excepci\u00f3n del proceso
	     */
	    public List<GrupoSeguridad> consultarGrupoSeguridad(Map<String, Object> parameters, Integer pageSize, Integer pageNumber)
	            throws ProcessException {

	        try {

	            return mapperQ.getPaginatedResult("consultarGrupoSeguridad", parameters, pageSize, pageNumber);

	        } catch (Exception e) {
	            throw new ProcessException(101, e, "Grupos de Seguridad");
	        }
	    }
	    
	    
	    public Response actualizarGrupoSeguridad(Map<String, Object> parameters)
	            throws ProcessException, IOException {

	    	try (Transaction tx = new Transaction(Parameters.getEsquemas(), Parameters.MYBATIS_CONFIGURATION)) {
	            mapperTx = tx.startTransaction(TransactionDefinition.PROPAGATION_REQUIRES_NEW,
	            		GrupoSeguridadMapper.class);

	            Map<String, Object> parametros = new HashMap<>();
	            
	            parametros.put("id", parameters.get("id"));
	            parametros.put("codigo", parameters.get("codigo"));
	            parametros.put("activo", parameters.get("activo"));
	            parametros.put("descripcion", parameters.get("descripcion"));
	            MPLogger.log.error("........///////////////////.............,{}", parametros);
	            mapperTx.actualizaGrupoSeguridad(parametros);
	           
	            tx.commit();

	            return new Response(true, MessageUtil.getMessage(SAVE_OK, Parameters.MESSAGES, "Actualizar Grupo Seguridad"), null);
	        } catch (SQLException e) {
	            MPLogger.log.error("Actualizar Grupo Seguridad", e);
	            return new Response(false, MessageUtil.getMessage("save_error", Parameters.MESSAGES, "Actualizar Grupo Seguridad"), null);
	        }
	    	
	    }
	    
	    
	    /**
	     * Guarda la configuración del Grupo de Seguridad
	     *
	     * @param jsonGrid
	     *            JSON con la informacion enviada por el front-end
	     * @return
	     * @throws ProcessException
	     *             Excepcion de proceso
	     */
	    public Response guardaGrupoSeguridadBD(String objectForm, UserInfo infoUsuario) throws ProcessException {

	        Gson gson = new Gson();
	        Map<String, Object> formData = gson.fromJson(objectForm, new TypeToken<Map<String, Object>>() {}.getType());

	        Map<String, Object> formDataGrid = gson.fromJson(formData.get(gridGrupoSeguridad).toString(),
	                new TypeToken<Map<String, Object>>() {}.getType());

	        List<Map<String, Object>> grupoSeguridadGrid = (ArrayList<Map<String, Object>>) formDataGrid.get(FEATURES);

	        if (grupoSeguridadGrid != null && !grupoSeguridadGrid.isEmpty()) {

	            String dataGrid = gson.toJson(formDataGrid);
	            return guardarGrid(dataGrid, "C.C.GSEG.M");

	        }

	        return new Response(true, MessageUtil.getMessage(SAVE_OK, Parameters.MESSAGES), null);
	    }
	    
	    
	    /**
	     * Guarda el grid enviado
	     *
	     * @param jsonGrid
	     *            JSON con la informacion enviada por el front-end
	     *
	     * @return Objecto con el resultado de la operacion
	     * @throws ProcessException
	     *             Excepcion del proceso
	     */
	    private Response guardarGrid(String jsonGrid, String jasonTabla) throws ProcessException {

	        try (Transaction tx = new Transaction(Parameters.getEsquemas(), Parameters.MYBATIS_CONFIGURATION)) {
	            mapperTx = tx.startTransaction(TransactionDefinition.PROPAGATION_REQUIRES_NEW, GrupoSeguridadMapper.class);

	            APISaveGridService apiSaveGridService = new APISaveGridService(tx.getSession());

	            if (!apiSaveGridService.saveGrid(jsonGrid, jasonTabla, tx)) {
	                tx.rollback();
	                MPLogger.log.error("______________________________Aqui con trol ok");
	                return new Response(false,
	                        MessageUtil.getMessage("save_error", Parameters.MESSAGES, "Configurar Grupo Seguridad"), null);
	            }
	            tx.commit();

	            MPLogger.log.error("______________________________Aqui con trol error");

	            return new Response(true, MessageUtil.getMessage(SAVE_OK, Parameters.MESSAGES), null);

	        } catch (Exception e) {
	            MPLogger.log.error("", e);
	            throw new ProcessException(106, e, "Configurar Grupo Seguridad");
	        }
	    }

}
