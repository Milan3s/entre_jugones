package app.entre_jugones;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;

public class FormEventosController implements Initializable {

    @FXML
    private ComboBox<ModelCreador> comboCreador;
    @FXML
    private ComboBox<ModelEvento> comboEvento;
    @FXML
    private ComboBox<ModelColaborador> comboColaborador;
    @FXML
    private TextField inputNombreEvento;
    @FXML
    private TextField inputCapitulo;
    @FXML
    private TextArea inputDescripcion;
    @FXML
    private DatePicker inputFechaInicio;
    @FXML
    private DatePicker inputFechaFin;
    @FXML
    private TextField inputHoraInicio;
    @FXML
    private TextField inputHoraFin;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Label lblTituloEventos;

    private final ObservableList<ModelCreador> listaCreadores = FXCollections.observableArrayList();
    private final ObservableList<ModelEvento> listaEventos = FXCollections.observableArrayList();
    private final ObservableList<ModelColaborador> listaColaboradores = FXCollections.observableArrayList();

    private ModelEvento eventoSeleccionado = null;
    private final DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaCreadores.setAll(GlobalDAO.obtenerCreadores());
        listaColaboradores.setAll(GlobalDAO.obtenerTodosLosColaboradores());

        comboCreador.setItems(listaCreadores);
        comboEvento.setItems(listaEventos);
        comboColaborador.setItems(listaColaboradores);

        comboEvento.setDisable(true);
        comboColaborador.setDisable(true);
        habilitarCamposEvento(false);
    }

    @FXML
    private void onSeleccionarCreador(ActionEvent event) {
        ModelCreador creador = comboCreador.getValue();
        eventoSeleccionado = null;
        listaEventos.clear();
        comboEvento.getItems().clear();
        limpiarCampos();

        if (creador != null) {
            List<ModelEvento> eventos = GlobalDAO.obtenerEventosPorCreador(creador.getId());
            listaEventos.setAll(eventos);
            comboEvento.setItems(listaEventos);
            comboEvento.setDisable(false);
            habilitarCamposEvento(true);
        } else {
            comboEvento.setDisable(true);
            habilitarCamposEvento(false);
        }
    }

    @FXML
    private void onSeleccionarEvento(ActionEvent event) {
        eventoSeleccionado = comboEvento.getValue();

        if (eventoSeleccionado != null) {
            inputNombreEvento.setText(eventoSeleccionado.getNombre());
            inputCapitulo.setText(eventoSeleccionado.getCapitulo());
            inputDescripcion.setText(eventoSeleccionado.getDescripcion());
            inputFechaInicio.setValue(eventoSeleccionado.getFechaInicio());
            inputFechaFin.setValue(eventoSeleccionado.getFechaFin());
            inputHoraInicio.setText(eventoSeleccionado.getHoraInicio() != null ? eventoSeleccionado.getHoraInicio().format(horaFormatter) : "");
            inputHoraFin.setText(eventoSeleccionado.getHoraFin() != null ? eventoSeleccionado.getHoraFin().format(horaFormatter) : "");

            List<ModelColaborador> colaboradores = GlobalDAO.obtenerTodosLosColaboradores();
            listaColaboradores.setAll(colaboradores);
            comboColaborador.setItems(listaColaboradores);

            List<ModelColaborador> asignados = GlobalDAO.obtenerColaboradoresPorEvento(eventoSeleccionado.getId());
            if (!asignados.isEmpty()) {
                comboColaborador.getSelectionModel().select(asignados.get(0)); // primer colaborador
            }

            comboColaborador.setDisable(false);
        } else {
            limpiarCampos();
        }
    }

    @FXML
    private void onGuardar(ActionEvent event) {
        ModelCreador creador = comboCreador.getValue();
        String nombre = inputNombreEvento.getText().trim();
        String capitulo = inputCapitulo.getText().trim();
        String descripcion = inputDescripcion.getText().trim();
        LocalDate fechaInicio = inputFechaInicio.getValue();
        LocalDate fechaFin = inputFechaFin.getValue();

        // Validar horas
        LocalTime horaInicio = parseHora(inputHoraInicio.getText().trim());
        LocalTime horaFin = parseHora(inputHoraFin.getText().trim());

        if (creador == null || nombre.isEmpty() || capitulo.isEmpty() || descripcion.isEmpty()
                || fechaInicio == null || fechaFin == null || horaInicio == null || horaFin == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Todos los campos deben estar completos y las horas en formato HH:mm.");
            return;
        }

        if (fechaFin.isBefore(fechaInicio) || (fechaFin.equals(fechaInicio) && horaFin.isBefore(horaInicio))) {
            mostrarAlerta(Alert.AlertType.ERROR, "La fecha/hora de fin no puede ser anterior a la fecha/hora de inicio.");
            return;
        }

        boolean exito;
        if (eventoSeleccionado == null) {
            ModelEvento nuevo = new ModelEvento(0, creador.getId(), nombre, capitulo, descripcion,
                    fechaInicio, fechaFin, horaInicio, horaFin);
            exito = GlobalDAO.insertarEvento(nuevo);
            if (exito) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Evento creado correctamente.");
            }
        } else {
            eventoSeleccionado.setNombre(nombre);
            eventoSeleccionado.setCapitulo(capitulo);
            eventoSeleccionado.setDescripcion(descripcion);
            eventoSeleccionado.setFechaInicio(fechaInicio);
            eventoSeleccionado.setFechaFin(fechaFin);
            eventoSeleccionado.setHoraInicio(horaInicio);
            eventoSeleccionado.setHoraFin(horaFin);

            exito = GlobalDAO.actualizarEvento(eventoSeleccionado);
            if (exito) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Evento actualizado correctamente.");
            }
        }

        if (exito) {
            cerrarVentana();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar el evento.");
        }
    }

    @FXML
    private void onEliminar(ActionEvent event) {
        if (eventoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un evento para eliminar.");
            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar eliminación");
        alerta.setContentText("¿Eliminar el evento \"" + eventoSeleccionado.getNombre() + "\"?");

        alerta.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                boolean eliminado = GlobalDAO.eliminarEvento(eventoSeleccionado.getId());
                if (eliminado) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Evento eliminado.");
                    cerrarVentana();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error al eliminar el evento.");
                }
            }
        });
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        cerrarVentana();
    }

    @FXML
    private void onSeleccionarColaborador(ActionEvent event) {
        if (eventoSeleccionado == null) return;

        ModelColaborador colaboradorSeleccionado = comboColaborador.getValue();
        List<ModelColaborador> asignados = GlobalDAO.obtenerColaboradoresPorEvento(eventoSeleccionado.getId());

        if (colaboradorSeleccionado != null) {
            boolean yaAsignado = asignados.stream().anyMatch(c -> c.getId() == colaboradorSeleccionado.getId());

            if (!yaAsignado) {
                boolean exito = GlobalDAO.asignarColaboradorAEvento(colaboradorSeleccionado.getId(), eventoSeleccionado.getId());
                if (exito) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Colaborador asignado al evento.");
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "No se pudo asignar el colaborador.");
                }
            }
        } else {
            if (!asignados.isEmpty()) {
                ModelColaborador actual = asignados.get(0);
                boolean exito = GlobalDAO.eliminarAsignacionColaborador(eventoSeleccionado.getId(), actual.getId());
                if (exito) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Colaborador desasignado del evento.");
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error al desasignar colaborador.");
                }
            }
        }
    }

    private void habilitarCamposEvento(boolean habilitar) {
        inputNombreEvento.setDisable(!habilitar);
        inputCapitulo.setDisable(!habilitar);
        inputDescripcion.setDisable(!habilitar);
        inputFechaInicio.setDisable(!habilitar);
        inputFechaFin.setDisable(!habilitar);
        inputHoraInicio.setDisable(!habilitar);
        inputHoraFin.setDisable(!habilitar);
        comboColaborador.setDisable(!habilitar);
        btnGuardar.setDisable(!habilitar);
        btnEliminar.setDisable(!habilitar);
    }

    private void limpiarCampos() {
        inputNombreEvento.clear();
        inputCapitulo.clear();
        inputDescripcion.clear();
        inputFechaInicio.setValue(null);
        inputFechaFin.setValue(null);
        inputHoraInicio.clear();
        inputHoraFin.clear();
        comboEvento.getSelectionModel().clearSelection();
        comboColaborador.getSelectionModel().clearSelection();
        comboColaborador.getItems().clear();
    }

    private LocalTime parseHora(String texto) {
        if (texto == null || texto.isEmpty()) return null;
        try {
            return LocalTime.parse(texto, horaFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
