package app.entre_jugones;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador de la ventana de acciones generales (modal). Gestiona la apertura
 * de formularios para creadores, eventos y colaboradores.
 */
public class HomeAccionesController implements Initializable {

    @FXML
    private Button btnAccionCreador;
    @FXML
    private Button btnAccionEvento;
    @FXML
    private Button btnAccionColaborador;
    @FXML
    private Button btnCerrar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización si fuera necesaria
    }

    // --- Acción para gestionar CREADORES ---
    @FXML
    private void accionCreador(ActionEvent event) {
        abrirModal("/app/entre_jugones/form_creador.fxml", "Gestión de Creador");
    }

    // --- Acción para gestionar EVENTOS ---
    @FXML
    private void accionEventos(ActionEvent event) {
        // Aquí puedes cambiar la ruta cuando tengas el formulario de eventos
        abrirModal("/app/entre_jugones/form_eventos.fxml", "Gestión de Evento");
    }

    // --- Acción para gestionar COLABORADORES ---
    @FXML
    private void accionColaborador(ActionEvent event) {
        // Aquí puedes cambiar la ruta cuando tengas el formulario de colaboradores
        abrirModal("/app/entre_jugones/form_colaborador.fxml", "Gestión de Colaborador");
    }

    // --- Acción para cerrar la ventana actual ---
    @FXML
    private void accionCerrar(ActionEvent event) {
        Stage stage = (Stage) btnAccionCreador.getScene().getWindow();
        stage.close();
    }

    // --- Método para abrir un modal dado un FXML ---
    private void abrirModal(String rutaFXML, String tituloVentana) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            AnchorPane root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle(tituloVentana);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error al abrir modal: " + e.getMessage());
        }
    }
}
