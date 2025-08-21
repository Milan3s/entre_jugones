package app.entre_jugones;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static App instance; // 🔹 referencia estática a la aplicación
    private static Scene scene;

    public App() {
        instance = this; // cuando se crea la App, guardamos la instancia
    }

    // 🔹 Método para obtener la instancia en cualquier clase
    public static App getInstance() {
        return instance;
    }

    // 🔹 Método para obtener HostServices desde otros controladores
    public HostServices getAppHostServices() {
        return getHostServices();
    }

    @Override
    public void start(Stage stage) throws IOException {
        // ⚡ Cargar la vista panel_de_control.fxml
        FXMLLoader loader = new FXMLLoader(App.class.getResource("panel_de_control.fxml"));
        Parent root = loader.load();

        // Obtener el controlador del panel de control
        PanelDeControlController controller = loader.getController();

        // 🚀 Pasar HostServices al controlador
        controller.setHostServices(getHostServices());

        // Crear la escena principal
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Entre Jugones - Panel de Control");
        stage.show();
    }

    // Método para cambiar la raíz de la escena desde otros controladores
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
