package app.entre_jugones;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CreadorController implements Initializable {

    @FXML private TextField inputAlias;
    @FXML private TextField inputTwitch;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private ComboBox<ModelCreador> comboCreadores;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;

    private ModelCreador creadorExistente;

    private final ObservableList<ModelCreador> listaCreadores = FXCollections.observableArrayList();
    @FXML
    private Label lblTituloCreador;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarCreadores();
        comboCreadores.setItems(listaCreadores);
        comboCreadores.setOnAction(this::onSeleccionarCreador);
    }

    private void cargarCreadores() {
        List<ModelCreador> creadores = GlobalDAO.obtenerCreadores();
        listaCreadores.setAll(creadores);
    }

    public void setCreadorExistente(ModelCreador creador) {
        this.creadorExistente = creador;
        if (creador != null) {
            inputAlias.setText(creador.getAlias());
            inputTwitch.setText(creador.getCanalTwitch());
        }
    }

    @FXML
    private void onGuardar(ActionEvent event) {
        String alias = inputAlias.getText().trim();
        String twitch = inputTwitch.getText().trim();

        if (alias.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo obligatorio", "El campo 'alias' no puede estar vacío.");
            return;
        }

        ModelCreador nuevo = new ModelCreador();
        nuevo.setAlias(alias);
        nuevo.setCanalTwitch(twitch);

        if (GlobalDAO.insertarCreador(nuevo)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Creador insertado correctamente.");
            cargarCreadores();
            limpiarCampos();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo insertar el creador.");
        }
    }

    @FXML
    private void onEditar(ActionEvent event) {
        ModelCreador seleccionado = comboCreadores.getValue();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección", "Selecciona un creador de la lista.");
            return;
        }

        String alias = inputAlias.getText().trim();
        String twitch = inputTwitch.getText().trim();

        if (alias.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo obligatorio", "El campo 'alias' no puede estar vacío.");
            return;
        }

        seleccionado.setAlias(alias);
        seleccionado.setCanalTwitch(twitch);

        if (GlobalDAO.actualizarCreador(seleccionado)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Actualizado", "Creador actualizado correctamente.");
            cargarCreadores();
            comboCreadores.getSelectionModel().select(null);
            limpiarCampos();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el creador.");
        }
    }

    @FXML
    private void onEliminar(ActionEvent event) {
        ModelCreador seleccionado = comboCreadores.getValue();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección", "Selecciona un creador para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que deseas eliminar al creador '" + seleccionado.getAlias() + "'?");
        confirm.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                if (GlobalDAO.eliminarCreador(seleccionado.getId())) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado", "Creador eliminado correctamente.");
                    cargarCreadores();
                    comboCreadores.getSelectionModel().select(null);
                    limpiarCampos();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el creador.");
                }
            }
        });
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void onSeleccionarCreador(ActionEvent event) {
        ModelCreador seleccionado = comboCreadores.getValue();
        if (seleccionado != null) {
            inputAlias.setText(seleccionado.getAlias());
            inputTwitch.setText(seleccionado.getCanalTwitch());
        } else {
            limpiarCampos();
        }
    }

    private void limpiarCampos() {
        inputAlias.clear();
        inputTwitch.clear();
    }
}
