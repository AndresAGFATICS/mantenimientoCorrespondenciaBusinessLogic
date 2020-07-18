package com.macroproyectos.forest.mantenimiento.business.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.macroproyectos.forest.properties.ForestProperties;

/**
 * Clase que define los parametros requeridos en la automatizacion
 * 
 * @author kenneth.sanchez
 *
 */
public final class Parameters {

	// Base de datos del cliente: oracle, postgresql, mysql, mssql
	public static final String FOREST_DATABASE_ID = "forest.baseId";

	// Archivo de propiedades con los mensajes a utilizar
	public static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages.messages_proceso", new Locale("es"));

	// Nombre de la aplicacion
	public static final String APPLICATION_NAME = "base";
	
	private static Map<String, String> esquemas = new HashMap<>();

	// Nombre del proceso automatizado, el cual es utilizado para generar el archivo
	// de log. Debe coincidir con el
	// nombre del archivo xml que contiene los SQL utilizado por MyBatis.
	public static final String PROCESS_NAME = "proyecto"; // ACTUALIZAR CON NOMBRE PROCESO

	// Nombre del archivo de configuracion de mybatis
	public static final String MYBATIS_CONFIGURATION = "proyectoMybatisConfiguration.xml"; // ACTUALIZAR 'proyecto' CON NOMBRE PROCESO

	// Nombre de las propiedades del producto para los esquemas
    public static final String ESQUEMA_FOREST_BD = "nombre_esquema_Forest";
	public static final String ESQUEMA_GRL_BD = "nombre_esquema_Grl";
	public static final String ESQUEMA_BPMN_BD = "nombre_esquema_Bpmn";
	public static final String ESQUEMA_ARCH_BD = "nombre_esquema_Archivo";
	public static final String ESQUEMA_CORR_BD = "nombre_esquema_Corr";
	
	// Nombre de las key requeridos por mybatis para reeemplazar los esquemas
	public static final String LABEL_ESQUEMA_FOREST = "esquemaForest";
	public static final String LABEL_ESQUEMA_GRL = "esquemaGrl";
	public static final String LABEL_ESQUEMA_BPMN = "esquemaBpmn";
	public static final String LABEL_ESQUEMA_ARCH = "esquemaArch";
	public static final String LABEL_ESQUEMA_CORR = "esquemaCorr";

	
	/**
	 * Retorna el mapa de esquemas
	 * 
	 * @return Mapa con la informacion de los esquemas
	 */
	public static Map<String, String> getEsquemas() {
		if (esquemas == null)
			esquemas = new HashMap<>();

		if (esquemas.isEmpty()) {
			esquemas.put(Parameters.LABEL_ESQUEMA_FOREST,
					ForestProperties.getProperty(Parameters.ESQUEMA_FOREST_BD).getValor());
			esquemas.put(Parameters.LABEL_ESQUEMA_GRL,
					ForestProperties.getProperty(Parameters.ESQUEMA_GRL_BD).getValor());
			esquemas.put(Parameters.LABEL_ESQUEMA_BPMN,
					ForestProperties.getProperty(Parameters.ESQUEMA_BPMN_BD).getValor());
			esquemas.put(Parameters.LABEL_ESQUEMA_ARCH,
					ForestProperties.getProperty(Parameters.ESQUEMA_ARCH_BD).getValor());
			esquemas.put(Parameters.LABEL_ESQUEMA_CORR,
					ForestProperties.getProperty(Parameters.ESQUEMA_CORR_BD).getValor());
		}

		return esquemas;
	}
	
	/**
	 * Constructor privado requerido por Sonar
	 */
	private Parameters() {
	}

}