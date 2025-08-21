package app.entre_jugones;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/entre_jugones";
    private static final String USER = "root";      // usuario por defecto en XAMPP
    private static final String PASSWORD = "";      // contraseña por defecto en XAMPP
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("[ADVERTENCIA] No se pudo conectar a MySQL: " + e.getMessage());
            return null; // no rompe la app, devuelve null
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Conexión cerrada.");
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
