package com.macroproyectos.forest.proyecto.log;

import org.slf4j.LoggerFactory;

import com.macroproyectos.forest.mantenimiento.business.util.Parameters;

/**
 * Clase que define los logger de la automatizacion
 * 
 * @author kenneth.sanchez
 *
 */
public final class MPLogger {
    public static final org.slf4j.Logger log = LoggerFactory.getLogger(Parameters.PROCESS_NAME);

    /**
     * Constructor privado requerido por Sonar
     */
    private MPLogger() {
    }
}