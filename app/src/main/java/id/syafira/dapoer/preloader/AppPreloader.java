package id.syafira.dapoer.preloader;

import id.syafira.dapoer.util.SceneManager;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AppPreloader extends Preloader {

    private static SceneManager.StageConfig preloaderConfig() {
        return new SceneManager.StageConfig()
                .title("Memulai Dapoer Syafira...")
                .style(StageStyle.TRANSPARENT)
                .resizable(false)
                .transparent(true);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Load FXML dengan SceneManager (controller auto-inject via fx:controller)
        SceneManager.initStage(stage, "/view/PreloaderView.fxml", preloaderConfig());
        stage.setResizable(false);
        stage.show();

        // Otomatis tutup preloader saat DB siap (dipicu dari init() atau retry)
        StartupState.dbReadyProperty().addListener((obs, oldV, newV) -> {
            if (newV)
                Platform.runLater(stage::close);
        });
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        // Gunakan Platform.runLater untuk memastikan update UI berjalan di FX Thread
        Platform.runLater(() -> {
            PreloaderController controller = PreloaderController.getInstance();
            if (controller == null) {
                // Retry sekali lagi setelah delay singkat jika sangat awal
                Platform.runLater(() -> {
                    PreloaderController retryController = PreloaderController.getInstance();
                    if (retryController != null) {
                        applyNotification(retryController, info);
                    }
                });
                return;
            }
            applyNotification(controller, info);
        });
    }

    private void applyNotification(PreloaderController controller, PreloaderNotification info) {
        if (info instanceof StatusNotification sn) {
            controller.showStatus(sn.message(), sn.progress());
        } else if (info instanceof WarningNotification en) {
            controller.showError(en.message());
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        // Jangan auto-hide di BEFORE_START agar bisa retry jika ada error di splash
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            // Biarkan splash tetap terbuka sampai StartupState.dbReadyProperty() berubah
            // atau sampai manual exit.
        }
    }
}
