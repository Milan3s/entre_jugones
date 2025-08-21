package app.entre_jugones;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.application.HostServices;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PanelDeControlController implements Initializable {

    private HostServices hostServices;

    @FXML
    private Button btnManualUsuario;
    @FXML
    private Button btnInstalarXampp;
    @FXML
    private Button btnLicencia;
    @FXML
    private Button btnIniciar;
    @FXML
    private Button btnCerrar;
    @FXML
    private Label sms_conexiondb;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comprobarConexionAsync();
    }

    // === Helpers ===
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Comprueba la conexión a MySQL de forma asíncrona
     */
    private void comprobarConexionAsync() {
        if (sms_conexiondb != null) {
            sms_conexiondb.setText("⏳ Probando conexión...");
            sms_conexiondb.getStyleClass().removeAll("db-ok", "db-ko");
        }

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                try (Connection conn = ConnectionDB.getConnection()) {
                    return conn != null && !conn.isClosed() && conn.isValid(2);
                } catch (SQLException ex) {
                    updateMessage("❌ Debes iniciar XAMPP para que funcione la aplicación");
                    return false;
                }
            }
        };

        task.setOnSucceeded(ev -> {
            boolean ok = Boolean.TRUE.equals(task.getValue());
            if (sms_conexiondb != null) {
                sms_conexiondb.getStyleClass().removeAll("db-ok", "db-ko");
                if (ok) {
                    sms_conexiondb.setText("✅ Todo correcto, puedes usar la aplicación");
                    sms_conexiondb.getStyleClass().add("db-ok");

                    // 🔒 Deshabilitar el botón de Abrir XAMPP porque ya está iniciado
                    if (btnInstalarXampp != null) {
                        btnInstalarXampp.setDisable(true);
                    }
                } else {
                    String msg = task.getMessage();
                    sms_conexiondb.setText(
                            (msg != null && !msg.isBlank())
                            ? msg
                            : "❌ Debes iniciar XAMPP para que funcione la aplicación"
                    );
                    sms_conexiondb.getStyleClass().add("db-ko");

                    // 🔓 Habilitar el botón si la conexión falla
                    if (btnInstalarXampp != null) {
                        btnInstalarXampp.setDisable(false);
                    }
                }
            }
        });

        task.setOnFailed(ev -> {
            if (sms_conexiondb != null) {
                sms_conexiondb.getStyleClass().removeAll("db-ok");
                sms_conexiondb.setText("❌ Debes iniciar XAMPP para que funcione la aplicación");
                sms_conexiondb.getStyleClass().add("db-ko");

                // 🔓 Asegurar que el botón se habilite si hubo error
                if (btnInstalarXampp != null) {
                    btnInstalarXampp.setDisable(false);
                }
            }
        });

        Thread th = new Thread(task, "db-check-panel");
        th.setDaemon(true);
        th.start();
    }

    // === BOTONES DEL PANEL ===
    @FXML
    private void accionManualUsuario(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Manual de Usuario - Instalación de XAMPP");
        alert.setHeaderText("Guía básica para instalar y usar XAMPP");

        alert.setContentText(
                "1. Descarga XAMPP desde https://www.apachefriends.org\n\n"
                + "2. Instálalo en C:\\xampp.\n\n"
                + "3. Abre 'xampp-control.exe'.\n\n"
                + "4. Pulsa 'Start' en Apache y MySQL.\n\n"
                + "5. Mantén XAMPP abierto mientras uses Entre Jugones."
        );
        alert.showAndWait();
    }

    @FXML
    private void accionInstalarXampp(ActionEvent event) {
        try {
            ProcessBuilder pb = new ProcessBuilder("C:\\xampp\\xampp-control.exe");
            pb.start();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo abrir XAMPP.\nAsegúrate de que está instalado en C:\\xampp.");
            e.printStackTrace();
        }
    }

    @FXML
    private void accionLicencia(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Licencia");
        alert.setHeaderText("Información de la licencia");
        alert.setContentText(
                "LICENCIA DE USO DEL SOFTWARE\n\n"
                + "© Desarrollado por Soujirito\n\n"
                + "Este software es de código abierto y gratuito.\n\n"
                + "Se permite usar, copiar, modificar y distribuir para fines educativos o personales.\n\n"
                + "RESTRICCIONES:\n"
                + "- Prohibida la venta o alquiler.\n"
                + "- Redistribución debe mantener este aviso.\n\n"
                + "EL SOFTWARE SE ENTREGA 'TAL CUAL', SIN GARANTÍAS."
        );
        alert.showAndWait();
    }

    @FXML
    private void accionIniciarApp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Entre Jugones - Home");
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) btnIniciar.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo iniciar la aplicación principal.\nRevisa que 'home.fxml' existe en resources.");
            e.printStackTrace();
        }
    }

    @FXML
    private void accionCerrar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
