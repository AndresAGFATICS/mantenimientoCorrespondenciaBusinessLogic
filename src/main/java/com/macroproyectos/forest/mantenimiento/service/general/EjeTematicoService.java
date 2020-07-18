package com.macroproyectos.forest.mantenimiento.service.general;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.macroproyectos.api.automatization.dto.Response;
import com.macroproyectos.api.automatization.dto.Transaction;
import com.macroproyectos.api.automatization.dto.Type;
import com.macroproyectos.api.automatization.service.APISaveGridService;
import com.macroproyectos.api.automatization.util.GridUtil;
import com.macroproyectos.api.automatization.util.MessageUtil;
import com.macroproyectos.forest.mantenimiento.business.util.Parameters;
import com.macroproyectos.forest.mantenimiento.dto.corr.EjeTematico;
import com.macroproyectos.forest.mantenimiento.mapper.mantenimiento.EjeTematicoMapper;
import com.macroproyectos.forest.seguridad.UserInfo;
import com.macroproyectos.forest.sistema.exception.ProcessException;
import com.macroproyectos.forest.sistema.log.MPLogger;

/**
 * Clase para consultar Los ejes tematicos
 *
 * @author AGFATICS
 *
 */
@Service
@SuppressWarnings({ "rawtypes", "unchecked" })
public class EjeTematicoService {

    @Autowired
    public EjeTematicoMapper mapper;

    @Autowired
    public EjeTematicoMapper mapperTx;

    private static final String SAVE_OK = "save_ok";
    private static final String STATE = "state";
    private static final String ATTRIBUTES = "attributes";
    private static final String FEATURES = "features";
    private static final String DELETE = "delete";

    private String labelId = "id";
    private String labelDescripcion = "descripcion";
    private String labelCodigo = "codigo";
    private String mensageDescripcionRepetida = "error_eje_tematico_descripcion";

    private String gridEjeTematico = "grd_ejeTematico";

    /**
     * Retorna la lista de Ejes tematicos existentes de acuerdo a los parametros
     * establecidos y ordenados por codigo y descripcion.
     *
     * @param parameters
     *            Mapa de parametros a utilizar en la consulta: id Identificador del
     *            subtipo, activo: 1 activo, 0 inactivo, descripcion: Descripcion
     *            del eje tematico
     *
     * @param pageSize
     *            Numero de registros a consultar por pagina
     * @param pageNumber
     *            Numero de la pagina a consultar
     *
     * @return Lista de ejes tematicos
     * @throws ProcessException
     *             Excepcion del proceso
     */
    public List<EjeTematico> consultarEjeTematico(Map<String, Object> parameters, Integer pageSize, Integer pageNumber)
            throws ProcessException {

        try {

            return mapper.getPaginatedResult("consultarEjeTematico", parameters, pageSize, pageNumber);

        } catch (Exception e) {
            throw new ProcessException(101, e, "Ejes Tem\u00e1ticos");
        }
    }

    /**
     * Retorna la lista de Ejes tematicos con la descripcion especificada. Esta
     * consulta se utilza para detectar duplicados.
     *
     * @param parameters
     *            Parametros a enviar a la consulta: id: Identificador tipo de
     *            radicado, descripcion : descripcion del tipo de radicado
     *
     * @return Lista de ejes tematicos
     * @throws ProcessException
     *             Excepcion del proceso
     */

    public List<EjeTematico> consultarEjeTematicoDuplicado(Map<String, Object> parameters) throws ProcessException {

        ProcessException.validateRequiredParametersException("Consulta de ejes tem\u00e1ticos repetidos", parameters,
                labelDescripcion);

        Map<String, Type> valueTypes = new HashMap<>();
        valueTypes.put(labelDescripcion, Type.ENTERO);

       // ProcessException.validateTypeException(parameters, valueTypes);

        try {
            return mapper.getPaginatedResult("consultarEjeTematicoDuplicado", parameters, null, null);

        } catch (Exception e) {
            throw new ProcessException(101, e, "Ejes Tem\u00e1ticos duplicados");
        }
    }

    /**
     * Valida si en el grid de configuracion de relaciones tramites y tipos de
     * radicados la columna descripcion se encuentra repetida
     *
     * @param datosGrid
     *            Lista de objetos que contiene el mapa de la data del grid
     * @return Response Respuesta de la validacion status Retorna true o false (true
     *         significa que a validacion es exitosa y el grid se puede guardar)
     *         message Mensaje retornado de la validacion
     * @throws ProcessException
     */
    public Response validarEjeTematicoRepetido(List<Map<String, Object>> datosGrid) throws ProcessException {
        try {

            if (datosGrid == null || datosGrid.isEmpty())
                return new Response(true, MessageUtil.getMessage("save_ok", Parameters.MESSAGES), null);

            List<String> idColumns = new ArrayList<>();
            idColumns.add(labelDescripcion);

            Response respuestaValidacion = GridUtil.registroDuplicado(datosGrid, idColumns);

            if (respuestaValidacion != null && respuestaValidacion.getStatus()) {
                List<List<String>> lista = (List<List<String>>) respuestaValidacion.getData();
                return new Response(false,
                        MessageUtil.getMessage("error_descripcion_repetida", Parameters.MESSAGES, lista.get(0).get(0)),
                        null);
            }

            // ---------------------------------------------------------------------------------------------//
            // El registro es nuevo
            // ---------------------------------------------------------------------------------------------//

            Predicate filterState = row -> "new".equals(((Map) row).get(STATE));

            BiPredicate filterFieldsMain = (grd, reg) -> ((Map) grd).get(labelDescripcion)
                    .equals(((Map) ((Map) reg).get(ATTRIBUTES)).get(labelDescripcion));

            Map<String, Map<String, Type>> fieldsDB = new HashMap<>();

            Map<String, Type> valuesDB1 = new HashMap<>();
            valuesDB1.put(labelDescripcion, Type.VARCHAR);
            fieldsDB.put(labelDescripcion, valuesDB1);

            Function<Map, List> functionValidate = (Map parametersSearch) -> {
                try {
                    return this.consultarEjeTematicoDuplicado(parametersSearch);
                } catch (Exception e) {
                    MPLogger.log.error("", e);
                    return null;
                }
            };

            respuestaValidacion = GridUtil.registroDuplicadoBD(datosGrid, filterState, filterFieldsMain, fieldsDB,
                    functionValidate);

            if (respuestaValidacion != null && respuestaValidacion.getStatus()) {
                List<List<String>> lista = (List<List<String>>) respuestaValidacion.getData();
                return new Response(false,
                        MessageUtil.getMessage(mensageDescripcionRepetida, Parameters.MESSAGES, lista.get(0).get(0)),
                        null);
            }

            // -----------------------------------------------------------------------------//
            // El registro existrente
            // -----------------------------------------------------------------------------//
            filterState = row -> !"new".equals(((Map) row).get(STATE));
            Predicate filterFields = row -> ((Map) row).get(labelId) != null
                    && !((Map) row).get(labelId).toString().isEmpty();

            Map<String, Type> valuesDB2 = new HashMap<>();
            valuesDB2.put(labelId, Type.ENTERO);
            fieldsDB.put(labelId, valuesDB2);

            respuestaValidacion = GridUtil.registroDuplicadoBD(datosGrid, filterState, filterFieldsMain, filterFields,
                    fieldsDB, functionValidate);

            if (respuestaValidacion != null && respuestaValidacion.getStatus()) {
                List<List<String>> lista = (List<List<String>>) respuestaValidacion.getData();
                return new Response(false,
                        MessageUtil.getMessage(mensageDescripcionRepetida, Parameters.MESSAGES, lista.get(0).get(1)),
                        null);
            }

            return new Response(true, MessageUtil.getMessage("save_ok", Parameters.MESSAGES), null);

        } catch (Exception e) {
            throw new ProcessException(106, e, "validar ejes tem\u00e1ticos duplicados BD");
        }
    }

    /**
     * Guarda la configuración del eje tematico
     *
     * @param jsonGrid
     *            JSON con la informacion enviada por el front-end
     * @return
     * @throws ProcessException
     *             Excepcion de proceso
     */
    public Response guardaEjeTematicoBD(String objectForm, UserInfo infoUsuario) throws ProcessException {

        Gson gson = new Gson();
        Map<String, Object> formData = gson.fromJson(objectForm, new TypeToken<Map<String, Object>>() {}.getType());

        Map<String, Object> formDataGrid = gson.fromJson(formData.get(gridEjeTematico).toString(),
                new TypeToken<Map<String, Object>>() {}.getType());

        List<Map<String, Object>> ejeTematicoGrid = (ArrayList<Map<String, Object>>) formDataGrid.get(FEATURES);

        if (ejeTematicoGrid != null && !ejeTematicoGrid.isEmpty()) {

            Response resultado = validarEjeTematicoRepetido(ejeTematicoGrid);
            if (!resultado.getStatus())
                return resultado;

            ejeTematicoGrid.stream().filter(row -> "new".equals(row.get(STATE))).map(row -> (Map) row.get(ATTRIBUTES))
                    .forEach(tipo -> tipo.put(labelCodigo, buscarCodigoEjeTematico()));

            if (ejeTematicoGrid.stream().filter(row -> !row.get(STATE).equals(DELETE))
                    .map(row -> (Map) row.get(ATTRIBUTES)).filter(row -> row.get(labelCodigo) == null).count() > 1)
                return new Response(false, "Error al generar el c\u00f3digo del tipo de radicado", null);

            String dataGrid = gson.toJson(formDataGrid);
            return guardarGrid(dataGrid, "C.C.ETEM.M");

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
            mapperTx = tx.startTransaction(TransactionDefinition.PROPAGATION_REQUIRES_NEW, EjeTematicoMapper.class);

            APISaveGridService apiSaveGridService = new APISaveGridService(tx.getSession());

            if (!apiSaveGridService.saveGrid(jsonGrid, jasonTabla, tx)) {
                tx.rollback();
                MPLogger.log.error("______________________________Aqui con trol ok");
                return new Response(false,
                        MessageUtil.getMessage("save_error", Parameters.MESSAGES, "Configurar Tipos Radicado"), null);
            }
            tx.commit();

            MPLogger.log.error("______________________________Aqui con trol error");

            return new Response(true, MessageUtil.getMessage(SAVE_OK, Parameters.MESSAGES), null);

        } catch (Exception e) {
            MPLogger.log.error("", e);
            throw new ProcessException(106, e, "Configurar Tipos Radicado");
        }
    }

    /**
     * Devuelve el codigo con la longitud enviada en la que el principio de la
     * cadena actual se rellena con ceros(0).
     *
     * @param cadena
     *            Cadena a procesar
     * @param longitud
     *            Longitud de la cadena a retornar
     *
     * @return Cadena con la longitud enviada
     * @throws SQLException
     */
    private String buscarCodigoEjeTematico() {

        // mapperTx = tx.startTransaction(TransactionDefinition.PROPAGATION_REQUIRED, EjeTematicoMapper.class);

        Long secuencia = mapperTx.consultarSecuencia();
        // Long secuencia = radicacionService.incrementarSecuencia("CORR.TIPO_RADICADO_CODIGO_SEQ");

        MPLogger.log.info("burcarCodigoTipoRadicado: {}", secuencia);

        if (secuencia == null)
            return null;

        if (secuencia >= 3)
            secuencia.toString();

        return StringUtils.leftPad(secuencia.toString(), 3, "0");
    }

}
