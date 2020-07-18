package com.macroproyectos.forest.proyecto.exception;

import java.util.Map;

import com.macroproyectos.api.automatization.dto.Type;
import com.macroproyectos.api.automatization.util.ValidationUtil;
import com.macroproyectos.forest.mantenimiento.business.util.Parameters;
import com.macroproyectos.forest.sistema.log.MPLogger;
import com.mp.errors.MPException;

/**
 * Clase que maneja las excepciones del proceso y envia al log la informacion
 * correspondiente del error
 * 
 * @author kenneth.sanchez
 *
 */
public class ProcessException extends MPException {
    private static final long serialVersionUID = 8159309372304862520L;

    /**
     * Constructor que llama al constructor de la clase padre con el nombre de la
     * aplicacion y el nombre del proceso
     * 
     * @param number Numero de la excepcion
     * @param e Excepcion generada
     * @param parameters Parametros
     */
    public ProcessException(int number, Exception e, String... parameters) {
        super(Parameters.APPLICATION_NAME, Parameters.PROCESS_NAME, number, e, parameters);

        MPLogger.log.error("", e);
    }

    /**
     * Constructor que llama al constructor de la clase padre sin enviar la
     * excepcion, el nombre de la aplicacion ni el nombre del proceso
     * 
     * @param number Numero de la excepcion
     * @param parameters Parametros
     */
    public ProcessException(int number, String... parameters) {
        this(number, null, parameters);
    }

    /**
     * Lanza la excepcion si los parametros no coinciden con el tipo de dato requerido
     * 
     * @param parameters Parametros a validar
     * @param valueTypes Tipos de datos definidos para cada parametro
     * @throws ProcessException Excepcion del proceso
     */
    public static void validateTypeException(Map<String, Object> parameters, Map<String, Type> valueTypes)
            throws ProcessException {
        if (ValidationUtil.validateType(parameters, valueTypes) != null)
            throw new ProcessException(105, ValidationUtil.validateType(parameters, valueTypes));
    }

    /**
     * Lanza la excepcion si los parametros requeridos no se encuentran en el mapa de parametros
     * 
     * @param functionName Nombre de la funcion que requiere el parametro la cual se utilia en los logsde excepcion
     * @param parameters Parametros a validar
     * @param parameterNames Nombre de los parametros a validar como requeridos
     * @throws ProcessException Excecpcion del proceso
     */
    public static void validateRequiredParametersException(String functionName, Map<String, Object> parameters,
            String... parameterNames) throws ProcessException {
        if (ValidationUtil.validateRequiredParameters(parameters, parameterNames) != null)
            throw new ProcessException(104, ValidationUtil.validateRequiredParameters(parameters, parameterNames),
                    functionName);
    }
}