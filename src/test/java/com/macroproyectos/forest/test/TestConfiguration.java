package com.macroproyectos.forest.test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import com.macroproyectos.api.automatization.exception.APIException;
import com.macroproyectos.api.automatization.util.FileUtil;
import com.macroproyectos.forest.mantenimiento.business.util.Parameters;
import com.macroproyectos.forest.properties.ForestProperties;
import com.macroproyectos.forest.sistema.service.ConfigApp;

/**
 * Clase que define la configuracion requerida por las pruebas unitarias
 * 
 * @author kenneth.sanchez
 *
 */
public class TestConfiguration {
    // Archivo de propiedades con los parametros a utilizar
    public static final ResourceBundle PROPERTIES_DB = ResourceBundle.getBundle("configuracionConexionBaseDatos");

    // Debe ser una base de datos local, sin embargo este parametro podria apuntar a
    // una base de datos externa
    public static final String DATABASE_URL = PROPERTIES_DB.getString("database.cadenaConexion");
    public static final String DATABASE_USER = PROPERTIES_DB.getString("database.usuario");
    public static final String DATABASE_PASSWORD = PROPERTIES_DB.getString("database.password");
    public static final String DATABASE_DRIVER = PROPERTIES_DB.getString("database.driver");
    public static final String DATABASE_JNDI = "java:/jdbc/forest";

    public static final String DATABASE_USER_ADMIN = PROPERTIES_DB.getString("database.usuario.admin");
    public static final String DATABASE_PASSWORD_ADMIN = PROPERTIES_DB.getString("database.password.admin");

    // Path donde se encuentra localizado la carpeta scripts del proyecto a nivel
    // local
    public static final Path SCRIPTS_BASE_PATH = Paths.get(System.getProperty("user.dir"), "src", "test", "resources",
            "scriptsBase");
    public static final Path SCRIPTS_PROCESS_PATH = Paths.get(System.getProperty("user.dir"), "src", "test",
            "resources", "scriptsProceso");

    public static List<String> interfacesLog;
    public static Map<String, String> sqlScriptFiles;
    public static DataSource ds;

    // Bandera que indica si se deben descargar los archivos SQL del repositorio SVN
    private static boolean downloadSvnForestFiles = true;

    // Bandera que indica si se deben ejecutar los scripts SQL del producto
    private static boolean executeSQLProduct = true;

    // Bandera que indica si se deben ejecutar los scripts SQL del proceso
    private static boolean executeSQLProcess = true;

    // Objeto de session
    public static SqlSession session;

    // Propiedades globales definidas para la base de datos
    private static Map<String, String> propiedadesGlobales;

    /**
     * Configura los parametros de las pruebas
     */
    public static void setup() {
        try {
            ResourceBundle FLAG_SCRIPTS = ResourceBundle.getBundle("flagScriptsSQL");
            executeSQLProduct = FLAG_SCRIPTS.getString("execute.sql.product").equals("1") ? true : false;
            executeSQLProcess = FLAG_SCRIPTS.getString("execute.sql.process").equals("1") ? true : false;
            downloadSvnForestFiles = FLAG_SCRIPTS.getString("download.svn.forest").equals("1") ? true : false;
        } catch (Exception e) {
            // No existe el archivo de propiedades
        }

        propiedadesGlobales = new HashMap<>();
        propiedadesGlobales.put("esquemaGrl", "grl");
        propiedadesGlobales.put("esquemaForest", "forest");
        propiedadesGlobales.put("esquemaBpmn", "bpmn");

        setupInterfacesLog();
        setupSqlScripts();

        // Creacion de la base de datos de prueba
        setupDataBase();
        setupDatasource();
        setupLog();
    }

    /**
     * Configura las interfaces que deben ser interceptadas para visualizar los logs
     * (SQL)
     */
    private static void setupInterfacesLog() {
        interfacesLog = new ArrayList<String>();

        try {
            Path pathDir = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "com", "macroproyectos",
                    "forest", "proyecto", "mapper", "module");

            FileUtil.getListFiles(pathDir).forEach(f -> interfacesLog
                    .add("com.macroproyectos.forest.proyecto.mapper.module." + f.replace(".java", "")));

        } catch (APIException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configura las archivos sql a ejecutar para inicializar la base de datos con
     * los objetos y parametros requeridos por el proceso
     */
    private static void setupSqlScripts() {
        sqlScriptFiles = new HashMap<>();
        sqlScriptFiles.put("1-scriptsDDLSistema.sql", ";");
        sqlScriptFiles.put("2-scriptsTrigger.sql", "/");
        sqlScriptFiles.put("3-scriptsProceso.sql", ";");
        sqlScriptFiles.put("4-scriptsTablas.sql", ";");
    }

    /**
     * Simula el consumos de servicios rest al servidor de aplicaciones
     */
    public static void configurarServiciosRESTMock() {
        System.setProperty("propsUrl", "http://localhost:8080/config-component/api/gdo-grl-properties/properties");

        StringBuilder properties = new StringBuilder("[\n");
        properties.append(MockProperty.getProperty("nombre_esquema_Forest", "FOREST"));
        properties.append(MockProperty.getProperty("nombre_esquema_Grl", "GRL"));
        properties.append(MockProperty.getProperty("nombre_esquema_Bpmn", "BPMN"));
        properties.delete(properties.length() - 1, properties.length());
        properties.append("]");

        stubFor(post(urlEqualTo("/config-component/api/gdo-grl-properties/properties"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalTo("[\"nombre_esquema_Forest\"]")).willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json").withBody(properties.toString())));

        // Configurar los parametros generales
    }

    /**
     * Configura la conexion con la base de datos
     */
    private static void setupDatasource() {
        try {
            // Base de datos
            SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();

            ds = new DataSource() {
                private Connection conn;

                @Override
                public PrintWriter getLogWriter() throws SQLException {
                    return null;
                }

                @Override
                public void setLogWriter(PrintWriter out) throws SQLException {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void setLoginTimeout(int seconds) throws SQLException {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int getLoginTimeout() throws SQLException {
                    return 0;
                }

                @Override
                public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
                    return null;
                }

                @Override
                public <T> T unwrap(Class<T> iface) throws SQLException {
                    return null;
                }

                @Override
                public boolean isWrapperFor(Class<?> iface) throws SQLException {
                    return false;
                }

                @Override
                public Connection getConnection() throws SQLException {
                    return getConnection(DATABASE_USER, DATABASE_PASSWORD);
                }

                @Override
                public Connection getConnection(String username, String password) throws SQLException {
                    try {
                        Class.forName(DATABASE_DRIVER);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        throw new SQLException("No se encontr� el Driver de la base de datos");
                    }

                    this.conn = DriverManager.getConnection(DATABASE_URL, username, password);

                    return this.conn;
                }
            };

            builder.bind(DATABASE_JNDI, ds);
            builder.activate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Configura los logs de las pruebas
     */
    private static void setupLog() {
        ConsoleAppender console = new ConsoleAppender();
        String PATTERN = "%d [%p|%c|%C{1}] %m%n";
        console.setLayout(new PatternLayout(PATTERN));
        console.setThreshold(Level.DEBUG);
        console.activateOptions();
        //Logger.getLogger("frt.lib.properties").addAppender(console);
        //Logger.getLogger("mp.lib.svn").addAppender(console);

        for (String interf : interfacesLog) {
            //Logger.getLogger(interf).addAppender(console);
        }
    }

    /**
     * Configura la base de datos con los parametros de las pruebas, iniciando por
     * la creacion de las tablas y finalizando con la parametrizacion del sistema
     */
    public static void setupDataBase() {

        try {
            if (downloadSvnForestFiles)
                // Descarga la ultima version de los scripts iniciales de la base de datos
                SVNRepositoriosService.setupSQLFilesSVN();

            if (executeSQLProduct)
                // Crea la base de datos de pruebas base
                crearBasePruebas();

            if (executeSQLProcess)
                // Ejecuta el script especifico del programador para la prueba del proceso
                crearProcesoPruebas(sqlScriptFiles);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea los objetos de base de datos necesarios en el proceso actual. Incluye la
     * parametrizacion del proceso.
     * 
     * @param files
     *            Mapa con el nombre del archivo a ejecutar y su respectivo
     *            separador
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private static void crearProcesoPruebas(Map<String, String> files) throws Exception {
        Set<String> scriptFiles = new TreeSet<>();
        scriptFiles.addAll(files.keySet());

        for (String file : scriptFiles) {
            ejecutarScript(Paths.get(SCRIPTS_PROCESS_PATH.toString(), file).toString(), files.get(file), DATABASE_URL,
                    DATABASE_USER, DATABASE_PASSWORD);
        }
    }

    /**
     * Crea la base de datos de pruebas base
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private static void crearBasePruebas() throws Exception {
        // Recorrer los scripts de acuerdo al orden de ejecucion
        for (String fileName : SVNRepositoriosService.getOrderFileExecution()) {

            // Identificar el archivo que se debe ejecutar con el usuario administrador
            if (fileName != null && fileName.split(">>")[0].equals(SVNRepositoriosService.SVN_ADMIN_FILE.trim()))
                ejecutarScript(Paths.get(SCRIPTS_BASE_PATH.toString(), fileName.split(">>")[0]).toString(),
                        fileName.split(">>")[1], DATABASE_URL, DATABASE_USER_ADMIN, DATABASE_PASSWORD_ADMIN);
            else
                ejecutarScript(Paths.get(SCRIPTS_BASE_PATH.toString(), fileName.split(">>")[0]).toString(),
                        fileName.split(">>")[1], DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        }
    }

    /**
     * Ejecuta el Script especifico del programador para la prueba del proyecto
     * actual
     * 
     * @param fileName
     *            Archivo donde se encuentran los scripts sql a ejecutar
     * @param delimiter
     *            Delimitador de los scripts sql
     * @param url
     *            URL de conexion a la base de datos
     * @param user
     *            Nombre de usuario de conexion
     * @param password
     *            Password de conexion
     * 
     * @throws ClassNotFoundException
     * @throws FileNotFoundException
     * @throws SQLException
     */
    @SuppressWarnings("resource")
    public static void ejecutarScript(String fileName, String delimiter, String url, String user, String password)
            throws ClassNotFoundException, FileNotFoundException, SQLException {

        System.out.println("Ejecutando el archivo: " + fileName);
        Class.forName(DATABASE_DRIVER);

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Scanner scanner = new Scanner(new File(fileName), Charset.forName("UTF-8").name()).useDelimiter(delimiter);
            Statement currentStatement = null;

            while (scanner.hasNext()) {

                String rawStatement = scanner.next();
                List<String> tmpListPartsSql = Arrays.asList(rawStatement.split("\n"));

                String comandoSql = tmpListPartsSql.stream()
                        .filter(p -> !(p.trim().startsWith("--") || p.trim().startsWith("//")
                                || p.trim().startsWith("#") || p.trim().startsWith("/*") || p.trim().startsWith("*/")
                                || p.trim().isEmpty()))
                        .map(p -> {
                            return p.trim();
                        }).collect(Collectors.joining(" "));

                if (comandoSql != null && !comandoSql.trim().isEmpty()) {
                    try {
                        comandoSql = comandoSql.replaceAll("\t", " ").trim();
                        currentStatement = connection.createStatement();
                        currentStatement.execute(comandoSql);
                    } catch (SQLException e) {
                        System.out.println(comandoSql);
                        System.out.println(
                                "***Error*** " + e.getSQLState() + "-" + e.getMessage() + "-" + e.getErrorCode());

                        if (currentStatement != null) {
                            try {
                                currentStatement.close();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } finally {
                        if (currentStatement != null) {
                            try {
                                currentStatement.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        currentStatement = null;
                    }
                }
            }
        }
    }

    @Configuration
    public static class DataAccessRESTTestContextConfiguration {

        @Bean
        public static CacheManager cacheManager() {
            SimpleCacheManager cacheManager = new SimpleCacheManager();
            cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache(
                    com.macroproyectos.forest.sistema.business.util.Parameters.CONSULTAR_FUNCIONARIOS)));

            return cacheManager;
        }
    }

    /**
     * Retorna el objeto de sesion de consulta
     * 
     * @param environment Parametro de configuracion de la sesion
     * 
     * @return Objeto de sesion relacionado con la base de datos
     * @throws IOException Excepcion de tipo IO
     */
    public SqlSessionFactory getSqlSessionFactory(String enviroment, String archivoConfigMyBatis) {
        InputStream fis = this.getClass().getClassLoader().getResourceAsStream(archivoConfigMyBatis);
        TransactionFactory transactionFactory = new JdbcTransactionFactory();

        Environment environment = new Environment(enviroment, transactionFactory, new DataSource() {
            @Override
            public PrintWriter getLogWriter() throws SQLException {
                return null;
            }

            @Override
            public void setLogWriter(PrintWriter out) throws SQLException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setLoginTimeout(int seconds) throws SQLException {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getLoginTimeout() throws SQLException {
                return 0;
            }

            @Override
            public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
                return null;
            }

            @SuppressWarnings("hiding")
            @Override
            public <T> T unwrap(Class<T> iface) throws SQLException {
                return null;
            }

            @Override
            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                return false;
            }

            @Override
            public Connection getConnection() throws SQLException {
                return null;
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return null;
            }
        });

        XMLConfigBuilder parser = new XMLConfigBuilder(new InputStreamReader(fis), "", new Properties());

        org.apache.ibatis.session.Configuration configuration = parser.getConfiguration();
        configuration.setEnvironment(environment);

        // Parametros generales usados en el xml de las consultas generales
        configuration.getVariables().putAll(propiedadesGlobales);
        configuration = parser.parse();
        configuration.setDefaultStatementTimeout(10000);
        configuration.setDatabaseId(ForestProperties.getProperty(Parameters.FOREST_DATABASE_ID).getValor());

        return new SqlSessionFactoryBuilder().build(configuration);
    }

    /**
     * Configura el mapper para las pruebas unitarias
     * 
     * @param classService Clase del servicio
     * @return Mapper asociado a la clase del servicio
     * @throws Exception Excepcion general
     */
    public <R> R configureMapper(Class<R> classService) throws Exception {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory(Parameters.MYBATIS_CONFIGURATION,
                Parameters.MYBATIS_CONFIGURATION);
        session = sqlSessionFactory.openSession(ds.getConnection());

        ConfigApp configApp = new ConfigApp();
        configApp.setDataSource(ds);

        com.macroproyectos.forest.sistema.business.util.Parameters.getEsquemas().put("esquemaForest", "forest");
        com.macroproyectos.forest.sistema.business.util.Parameters.getEsquemas().put("esquemaGrl", "grl");
        com.macroproyectos.forest.sistema.business.util.Parameters.getEsquemas().put("esquemaBpmn", "bpmn");
        com.macroproyectos.forest.sistema.business.util.Parameters.getEsquemas().put("esquemaCorr", "corr");

        return session.getMapper(classService);
    }

    static class MockProperty {
        public static String getProperty(String nombre, String tipo, String valor) {
            StringBuilder property = new StringBuilder();

            property.append("{\n");
            property.append("\"descripcion\": \"Mock\",").append("\n");
            property.append("\"id\": 0,").append("\n");
            property.append("\"modificable\": 1,").append("\n");
            property.append("\"nativa\": 1,").append("\n");
            property.append("\"nombre\": \"" + nombre + "\",").append("\n");
            property.append("\"tipo\": \"" + tipo + "\",").append("\n");
            property.append("\"valor\": \"" + valor + "\"");
            property.append("\n},");

            return property.toString();
        }

        public static String getProperty(String nombre, String valor) {
            StringBuilder property = new StringBuilder();

            property.append("{\n");
            property.append("\"descripcion\": \"Mock\",").append("\n");
            property.append("\"id\": 0,").append("\n");
            property.append("\"modificable\": 1,").append("\n");
            property.append("\"nativa\": 1,").append("\n");
            property.append("\"nombre\": \"" + nombre + "\",").append("\n");
            property.append("\"tipo\": \"string\",").append("\n");
            property.append("\"valor\": \"" + valor + "\"");
            property.append("\n},");

            return property.toString();
        }
    }
}