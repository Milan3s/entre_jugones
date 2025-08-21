package app.entre_jugones;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class FormColaboradorController {

    @FXML
    private TextField inputAlias;
    @FXML
    private TextField inputTwitch;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Label lblTituloColaborador;
    @FXML
    private ComboBox<ModelColaborador> comboColaboradores;

    private ModelColaborador colaboradorSeleccionado;
    private final ObservableList<ModelColaborador> listaColaboradores = FXCollections.observableArrayList();
    @FXML
    private Button btnActualizar;

    public void initialize() {
        listaColaboradores.setAll(GlobalDAO.obtenerTodosLosColaboradores());
        comboColaboradores.setItems(listaColaboradores);
        comboColaboradores.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(ModelColaborador colab) {
                return (colab != null) ? colab.getAlias() : "";
            }

            @Override
            public ModelColaborador fromString(String string) {
                return null; // No se utiliza
            }
        });
        btnActualizar.setDisable(true); // al iniciar, no hay selección

        btnEliminar.setDisable(true);
    }

    @FXML
    private void onSeleccionarColaborador(ActionEvent event) {
        colaboradorSeleccionado = comboColaboradores.getValue();

        if (colaboradorSeleccionado != null) {
            inputAlias.setText(colaboradorSeleccionado.getAlias());
            inputTwitch.setText(colaboradorSeleccionado.getCanalTwitch());
            btnEliminar.setDisable(false);
            btnActualizar.setDisable(false); // habilita actualizar
        } else {
            inputAlias.clear();
            inputTwitch.clear();
            btnEliminar.setDisable(true);
            btnActualizar.setDisable(true); // deshabilita actualizar si se limpia
        }
    }

    @FXML
    private void onGuardar() {
        String alias = inputAlias.getText().trim();
        String twitch = inputTwitch.getText().trim();

        if (alias.isEmpty() || twitch.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Alias y canal Twitch no pueden estar vacíos.");
            return;
        }

        boolean exito;

        if (colaboradorSeleccionado == null) {
            ModelColaborador nuevo = new ModelColaborador();
            nuevo.setAlias(alias);
            nuevo.setCanalTwitch(twitch);
            exito = GlobalDAO.insertarColaborador(nuevo);
        } else {
            colaboradorSeleccionado.setAlias(alias);
            colaboradorSeleccionado.setCanalTwitch(twitch);
            exito = GlobalDAO.actualizarColaborador(colaboradorSeleccionado);
        }

        if (exito) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Colaborador guardado correctamente.");
            cerrarVentana();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar colaborador.");
        }
    }

    @FXML
    private void onEliminar() {
        if (colaboradorSeleccionado == null) {
            return;
        }

        boolean confirmado = mostrarConfirmacion("¿Estás seguro de eliminar este colaborador?");
        if (!confirmado) {
            return;
        }

        boolean exito = GlobalDAO.eliminarColaborador(colaboradorSeleccionado.getId());

        if (exito) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Colaborador eliminado.");
            cerrarVentana();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar al colaborador.");
        }
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private boolean mostrarConfirmacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onActualizar(ActionEvent event) {
        if (colaboradorSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Primero selecciona un colaborador a actualizar.");
            return;
        }

        String alias = inputAlias.getText().trim();
        String twitch = inputTwitch.getText().trim();

        if (alias.isEmpty() || twitch.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Alias y canal Twitch no pueden estar vacíos.");
            return;
        }

        colaboradorSeleccionado.setAlias(alias);
        colaboradorSeleccionado.setCanalTwitch(twitch);

        boolean actualizado = GlobalDAO.actualizarColaborador(colaboradorSeleccionado);

        if (actualizado) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Colaborador actualizado correctamente.");
            cerrarVentana();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo actualizar el colaborador.");
        }
    }

}
