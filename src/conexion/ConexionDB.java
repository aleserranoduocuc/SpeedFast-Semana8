package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de gestionar la conexión a la base de datos MySQL.
 */
public class ConexionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/speedfast_db";
    private static final String USER = "root";
    private static final String PASSWORD = "su_contraseña";   // ← tu contraseña

    /**
     * Obtiene una conexión activa a la base de datos.
     */
    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
