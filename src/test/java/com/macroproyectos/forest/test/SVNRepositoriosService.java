package com.macroproyectos.forest.test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.macroproyectos.api.automatization.exception.APIException;
import com.macroproyectos.api.automatization.util.FileUtil;
import com.macroproyectos.forest.svn.SVNAdapter;
import com.macroproyectos.forest.svn.config.DatosConexionSVNDTO;

/**
 * Clase que permite obtener la lista de archivos SQL del repositorio SVN, los
 * cuales se utilizan en la parametrizacion de la base de datos de pruebas
 * unitarias
 * 
 * @author diego.grajales Modificado por kenneth.sanchez para adaptarse a las
 *         necesidades de automatizacion
 * 
 */
@Service
@Transactional(readOnly = true)
public class SVNRepositoriosService {
    public static final ResourceBundle PROPERTIES_SVN = ResourceBundle.getBundle("configuracionSVNScriptsBaseInicial");

    private static final String SVN_MACROPROCESO_URL = PROPERTIES_SVN.getString("svn.urlBaseScripts");
    private static final String SVN_MACROPROCESO_USUARIO = PROPERTIES_SVN.getString("svn.usuario");
    private static final String SVN_MACROPROCESO_PASS = PROPERTIES_SVN.getString("svn.password");

    private static final String SVN_INDEX_FILE = PROPERTIES_SVN.getString("svn.index.file");
    public static final String SVN_ADMIN_FILE = PROPERTIES_SVN.getString("svn.file.admin");

    /**
     * Obtiene los archivos del repositorio SVN si la bandera downloadSvnForestFiles
     * esta en true
     * 
     * @throws Exception
     */
    public static void setupSQLFilesSVN() throws Exception {
        SVNAdapter svnAdapter = new SVNAdapter();

        FileUtil.deletePath(TestConfiguration.SCRIPTS_BASE_PATH);
        Files.createDirectories(TestConfiguration.SCRIPTS_BASE_PATH);

        try {
            DatosConexionSVNDTO datosConexionSVN = new DatosConexionSVNDTO();

            datosConexionSVN.setUrlSVNBase(SVN_MACROPROCESO_URL);
            datosConexionSVN.setUserSVN(SVN_MACROPROCESO_USUARIO);
            datosConexionSVN.setPassSVN(SVN_MACROPROCESO_PASS);

            svnAdapter.setDatosConexionSVN(datosConexionSVN);
            svnAdapter.conectar();

            // Obtener la lista de archivos del repositorio SVN
            List<String> files = svnAdapter.getListEntries("", SVNAdapter.ARCHIVOS);

            if (files != null && !files.isEmpty()) {
                for (String file : files) {
                    byte[] contentFile = svnAdapter.datosConexionSVN(datosConexionSVN).conectar().getFile(file);
                    Files.write(Paths.get(TestConfiguration.SCRIPTS_BASE_PATH.toString(), file), contentFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            svnAdapter.close();
        }
    }

    /**
     * Retorna la lista de archivos SQL por orden de ejecuci�n
     * 
     * @return Lista de archivos SQL
     */
    public static List<String> getOrderFileExecution() {
        try {
            return FileUtil.getListFiles(TestConfiguration.SCRIPTS_BASE_PATH, SVN_INDEX_FILE);
        } catch (APIException e) {
            e.printStackTrace();
            return null;
        }
    }
}