package app.entre_jugones;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controlador para la vista de gestión de eventos (ventana secundaria). Se
 * encarga de mostrar y filtrar la lista de eventos, así como sus detalles.
 */
public class HomeController {

    @FXML
    private ListView<ModelEvento> listEventos;

    @FXML
    private Label lblAlias;
    @FXML
    private Label lblTwitch;
    @FXML
    private Label lblNombreEvento;
    @FXML
    private Label lblFechaInicio;
    @FXML
    private Label lblFechaFin;
    @FXML
    private Label lblNombreColaborador;
    @FXML
    private Label lblTwitchColaborador;
    @FXML
    private DatePicker dateFiltroEvento;
    @FXML
    private TextField inputBuscarEvento;
    @FXML
    private Button btnLimpiarFiltros;
    @FXML
    private Label lblNombreCapitulo;
    @FXML
    private Label lblDescripcionEvento;
    @FXML
    private Label lblHoraInicio;
    @FXML
    private Label lblHoraFin;

    private final ObservableList<ModelEvento> eventos = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private MenuItem menuSalir;
    @FXML
    private MenuItem menuAcercaDe;
    @FXML
    private MenuItem menuRepositorio;
    @FXML
    private MenuItem menuLicencia;

    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblListaEventos1;
    @FXML
    private Label lblListaEventos;
    @FXML
    private Button btnCerrar;
    @FXML
    private Button btnAcciones;

    public void initialize() {
        configurarDatePicker();

        listEventos.setItems(eventos);

        // Celda personalizada con tooltip
        listEventos.setCellFactory(list -> new ListCell<>() {
            private final Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(ModelEvento evento, boolean empty) {
                super.updateItem(evento, empty);
                if (empty || evento == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(evento.getNombre());

                    String fechaInicio = (evento.getFechaInicio() != null)
                            ? dateFormatter.format(evento.getFechaInicio())
                            : "—";
                    String horaInicio = (evento.getHoraInicio() != null)
                            ? timeFormatter.format(evento.getHoraInicio())
                            : "--:--";

                    String fechaFin = (evento.getFechaFin() != null)
                            ? dateFormatter.format(evento.getFechaFin())
                            : "—";
                    String horaFin = (evento.getHoraFin() != null)
                            ? timeFormatter.format(evento.getHoraFin())
                            : "--:--";

                    tooltip.setText(
                            "Inicio: " + fechaInicio + " " + horaInicio + "\n"
                            + "Fin:    " + fechaFin + " " + horaFin
                    );
                    setTooltip(tooltip);
                }
            }
        });

        listEventos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> mostrarDetallesEvento(newVal)
        );

        inputBuscarEvento.textProperty().addListener((obs, oldVal, newVal) -> buscarEnTiempoReal());
        dateFiltroEvento.getEditor().textProperty().addListener((obs, oldVal, newVal) -> buscarEnTiempoReal());

        cargarTodosLosEventos();
    }

    private void configurarDatePicker() {
        dateFiltroEvento.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty())
                        ? LocalDate.parse(string, dateFormatter)
                        : null;
            }
        });
        dateFiltroEvento.setPromptText("dd-MM-yyyy");
    }

    private void cargarTodosLosEventos() {
        List<ModelEvento> lista = GlobalDAO.listarTodosLosEventos();
        eventos.setAll(lista);
    }

    private void mostrarDetallesEvento(ModelEvento evento) {
        if (evento == null) {
            lblNombreEvento.setText("—");
            lblNombreCapitulo.setText("—");
            lblDescripcionEvento.setText("—");
            lblFechaInicio.setText("—");
            lblFechaFin.setText("—");
            lblHoraInicio.setText("—");
            lblHoraFin.setText("—");
            lblAlias.setText("—");
            lblTwitch.setText("—");
            lblNombreColaborador.setText("—");
            lblTwitchColaborador.setText("—");
            return;
        }

        lblNombreEvento.setText(evento.getNombre());
        lblNombreCapitulo.setText(evento.getCapitulo() != null ? evento.getCapitulo() : "—");
        lblDescripcionEvento.setText(evento.getDescripcion() != null ? evento.getDescripcion() : "—");
        lblFechaInicio.setText(dateFormatter.format(evento.getFechaInicio()));
        lblFechaFin.setText(dateFormatter.format(evento.getFechaFin()));

        LocalTime horaInicio = evento.getHoraInicio();
        LocalTime horaFin = evento.getHoraFin();
        lblHoraInicio.setText(horaInicio != null ? timeFormatter.format(horaInicio) : "—");
        lblHoraFin.setText(horaFin != null ? timeFormatter.format(horaFin) : "—");

        ModelCreador creador = GlobalDAO.obtenerCreadores()
                .stream()
                .filter(c -> c.getId() == evento.getIdCreador())
                .findFirst()
                .orElse(null);

        if (creador != null) {
            lblAlias.setText(creador.getAlias());
            lblTwitch.setText(creador.getCanalTwitch());
        } else {
            lblAlias.setText("—");
            lblTwitch.setText("—");
        }

        List<ModelColaborador> colaboradores = GlobalDAO.obtenerColaboradoresPorEvento(evento.getId());
        if (!colaboradores.isEmpty()) {
            ModelColaborador colab = colaboradores.get(0);
            lblNombreColaborador.setText(colab.getAlias());
            lblTwitchColaborador.setText(colab.getCanalTwitch());
        } else {
            lblNombreColaborador.setText("—");
            lblTwitchColaborador.setText("—");
        }
    }

    private void buscarEnTiempoReal() {
        LocalDate fecha = dateFiltroEvento.getValue();
        String texto = inputBuscarEvento.getText().toLowerCase().trim();

        List<ModelEvento> todos = GlobalDAO.listarTodosLosEventos();

        List<ModelEvento> filtrados = todos.stream()
                .filter(e -> {
                    boolean coincideTexto = texto.isEmpty()
                            || e.getNombre().toLowerCase().contains(texto)
                            || (e.getDescripcion() != null && e.getDescripcion().toLowerCase().contains(texto));

                    boolean coincideFecha = (fecha == null || e.getFechaInicio().equals(fecha));
                    return coincideTexto && coincideFecha;
                })
                .collect(Collectors.toList());

        eventos.setAll(filtrados);
    }

    @FXML
    private void onLimpiarFiltros(ActionEvent event) {
        dateFiltroEvento.setValue(null);
        dateFiltroEvento.getEditor().clear();
        inputBuscarEvento.clear();
        cargarTodosLosEventos();
    }

    // === MENÚS ===
    @FXML
    private void onMenuSalir(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void onMenuAcercaDe(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Acerca de");
        alert.setHeaderText("Gestión de Eventos - Entre Jugones");
        alert.setContentText(
                "Aplicación para gestionar el evento Entre Jugones de la comunidad retro.\n\nVersión 1.0"
        );
        alert.showAndWait();
    }

    @FXML
    private void onMenuRepositorio(ActionEvent event) {
        try {
            App.getInstance().getHostServices().showDocument(
                    "https://github.com/Milan3s/entre_jugones_JavaFX"
            );
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error", "No se pudo abrir el repositorio en el navegador.");
        }
    }

    @FXML
    private void onMenuLicencia(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Licencia");
        alert.setHeaderText("Información de la licencia");
        alert.setContentText(
                "LICENCIA DE USO DEL SOFTWARE\n\n"
                + "© Desarrollado por Soujirito\n\n"
                + "Este software es de código abierto y se distribuye de forma gratuita.\n\n"
                + "Se concede permiso a cualquier persona que obtenga una copia de este software y de los archivos de documentación asociados "
                + "para utilizarlo, copiarlo, modificarlo y distribuirlo con fines educativos, personales o comunitarios.\n\n"
                + "RESTRICCIONES:\n"
                + "- Queda prohibida la venta, alquiler o cualquier forma de comercialización de este software o de sus derivados.\n"
                + "- Cualquier redistribución debe incluir este aviso de licencia y la atribución al autor original.\n\n"
                + "EL SOFTWARE SE ENTREGA 'TAL CUAL', SIN GARANTÍAS DE NINGÚN TIPO.\n\n"
                + "Al utilizar este software, aceptas las condiciones anteriores."
        );
        alert.showAndWait();
    }

    // auxiliar
    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void accionCerrar(ActionEvent event) {
        // Obtener la ventana actual desde el botón
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void accionAcciones(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/entre_jugones/home_acciones.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Entre Jugones - Acciones");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
