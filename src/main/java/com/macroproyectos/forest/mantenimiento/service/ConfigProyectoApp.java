package com.macroproyectos.forest.mantenimiento.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.macroproyectos.forest.mantenimiento.business.util.Parameters;
import com.macroproyectos.forest.properties.ForestProperties;
import com.macroproyectos.forest.sistema.exception.ProcessException;
import com.macroproyectos.forest.sistema.log.MPLogger;

/**
 * Clase que configura la conexion a la base de datos a traves del datasource
 * 
 * @author kenneth.sanchez
 *
 */
@Configuration
@EnableTransactionManagement
@ComponentScan({ "com.macroproyectos.forest.mpdoctos.service" })
@MapperScan(basePackages = { "com.macroproyectos.forest.mantenimiento.mapper.general",
		"com.macroproyectos.forest.mantenimiento.mapper.mantenimiento",
		"com.macroproyectos.forest.mantenimiento.mapper.ventanilla", "com.macroproyectos.forest.mpdoctos.mapper.util",
		"com.macroproyectos.forest.sistema.mapper.archivo",
		"com.macroproyectos.forest.sistema.mapper.documentos.informativos",
		"com.macroproyectos.forest.sistema.mapper.general", "com.macroproyectos.forest.sistema.mapper.impresion",
		"com.macroproyectos.forest.sistema.mapper.mantenimiento", "com.macroproyectos.forest.sistema.mapper.radicado",
		"com.macroproyectos.forest.sistema.mapper.ventanilla" })
public class ConfigProyectoApp {
	private DataSource dataSource = null;

	private static org.apache.ibatis.session.Configuration configuration;
	private static SqlSession session;
	private static SqlSessionFactory sqlSessionFactory;

	/**
	 * Retorna el datasource
	 * 
	 * @return Datasource
	 */
	@Bean
	public DataSource getDataSource() {
		final JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
		dataSourceLookup.setResourceRef(true);

		if (this.dataSource == null)
			this.dataSource = dataSourceLookup.getDataSource("java:/jdbc/forest");

		return this.dataSource;
	}

	/**
	 * Bean que configura el session factory asociando el datasource
	 * 
	 * @return Objeto session factory
	 * @throws IOException
	 * @throws Exception   Excepcion del proceso
	 */
	@Bean(name = "sqlSessionFactory")
	@Primary
	public SqlSessionFactory sqlSessionFactory() throws IOException {
		if (getConfiguration() == null) {
			try (InputStream fis = this.getClass().getClassLoader()
					.getResourceAsStream(Parameters.MYBATIS_CONFIGURATION)) {
				XMLConfigBuilder parser = new XMLConfigBuilder(new InputStreamReader(fis), "", new Properties());

				setConfiguration(parser.getConfiguration());

				TransactionFactory transactionFactory = new JdbcTransactionFactory();
				ConfigProyectoApp configApp = new ConfigProyectoApp();

				Environment environment = new Environment(Parameters.MYBATIS_CONFIGURATION, transactionFactory,
						configApp.getDataSource());

				getConfiguration().setEnvironment(environment);

				Map<String, String> esquemas = new HashMap<>();
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

				getConfiguration().getVariables().putAll(esquemas);
				setConfiguration(parser.parse());
				getConfiguration().setDefaultStatementTimeout(10000);
				getConfiguration().setCacheEnabled(false);
				getConfiguration()
						.setDatabaseId(ForestProperties.getProperty(Parameters.FOREST_DATABASE_ID).getValor());
			}
		}

		return new SqlSessionFactoryBuilder().build(getConfiguration());
	}

	private static org.apache.ibatis.session.Configuration getConfiguration() {
		return configuration;
	}

	private static void setConfiguration(org.apache.ibatis.session.Configuration configuration) {
		ConfigProyectoApp.configuration = configuration;
	}

	/**
	 * Instancia la fuente de datos
	 * 
	 * @param dataSource Fuete de datos de conexion con la base de datos
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Configura el mapper para las pruebas unitarias
	 * 
	 * @param classService Clase del servicio
	 * @return Mapper asociado a la clase del servicio
	 * 
	 * @throws IOException  Excepcion de IO
	 * @throws SQLException Excepcion SQL
	 */
	public <R> R configureMapper(Class<R> classService) throws ProcessException {
		ConfigProyectoApp configApp = new ConfigProyectoApp();

		try {

			if (dataSource == null)
				dataSource = configApp.getDataSource();

			if (getSqlSessionFactory() == null)
				setSqlSessionFactory(configApp.sqlSessionFactory());

			if (getSession() == null)
				setSession(getSqlSessionFactory().openSession(false));

			return getSession().getMapper(classService);

		} catch (Exception e) {
			MPLogger.log.error("Mpdoctos configure Mapper", e);
			throw new ProcessException(106, e, "Mpdoctos configure Mapper");
		}

	}

	/**
	 * Cierra la session de la base de datos
	 */
	public static void closeSession() {
		if (getSession() != null) {
			getSession().close();
			setSession(null);
		}
	}

	private static SqlSession getSession() {
		return session;
	}

	private static void setSession(SqlSession session) {
		ConfigProyectoApp.session = session;
	}

	private static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	private static void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		ConfigProyectoApp.sqlSessionFactory = sqlSessionFactory;
	}
}