package id.syafira.dapoer.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Kelas utilitas untuk mengelola transisi Scene (tampilan) dan konfigurasi
 * Stage (jendela) dalam aplikasi.
 * Memudahkan perpindahan antar layar seperti dari Login ke Dashboard dengan
 * konfigurasi yang fleksibel.
 */
public class SceneManager {

    /**
     * Kelas pembantu untuk mendefinisikan konfigurasi jendela (Stage) yang akan
     * dibuat.
     */
    public static class StageConfig {
        private String title = "Dapoer Syafira";
        private StageStyle style = StageStyle.DECORATED;
        private boolean resizable = true;
        private boolean maximized = false;
        private boolean transparent = false;
        private int minWidth = -1;
        private int minHeight = -1;
        private String cssPath = null;

        /**
         * Mengatur judul jendela.
         */
        public StageConfig title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Mengatur gaya jendela (misalnya DECORATED, TRANSPARENT).
         */
        public StageConfig style(StageStyle style) {
            this.style = style;
            return this;
        }

        /**
         * Mengatur apakah jendela dapat diubah ukurannya oleh pengguna.
         */
        public StageConfig resizable(boolean resizable) {
            this.resizable = resizable;
            return this;
        }

        /**
         * Mengatur apakah jendela langsung ditampilkan dalam mode maksimal.
         */
        public StageConfig maximized(boolean maximized) {
            this.maximized = maximized;
            return this;
        }

        /**
         * Mengatur apakah latar belakang jendela transparan.
         */
        public StageConfig transparent(boolean transparent) {
            this.transparent = transparent;
            return this;
        }

        /**
         * Mengatur ukuran minimal jendela.
         */
        public StageConfig minSize(int width, int height) {
            this.minWidth = width;
            this.minHeight = height;
            return this;
        }

        /**
         * Mengatur path ke file CSS kustom untuk jendela ini.
         */
        public StageConfig cssPath(String cssPath) {
            this.cssPath = cssPath;
            return this;
        }
    }

    /**
     * Berpindah ke Scene baru dengan menutup jendela yang sedang aktif.
     * 
     * @param currentNode Node yang berada di dalam jendela aktif saat ini.
     * @param fxmlPath    Path menuju file FXML untuk tampilan baru.
     * @param config      Konfigurasi untuk jendela baru yang akan dibuka.
     * @throws IOException Jika gagal memuat file FXML.
     */
    public static void switchScene(Node currentNode, String fxmlPath, StageConfig config) throws IOException {
        // Memuat desain UI dari file FXML
        Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));

        // Membuat Stage (jendela) baru berdasarkan root node dan konfigurasi
        Stage newStage = createStage(root, config);

        // Menutup jendela yang sedang aktif secara instan
        Stage currentStage = (Stage) currentNode.getScene().getWindow();
        currentStage.hide();
        currentStage.close();

        // Menampilkan jendela baru ke layar
        newStage.show();
    }

    /**
     * Membuat dan mengonfigurasi objek Stage baru.
     * 
     * @param root   Node induk utama (biasanya dari FXML).
     * @param config Objek StageConfig yang berisi pengaturan jendela.
     * @return Objek Stage yang sudah siap ditampilkan.
     */
    private static Stage createStage(Parent root, StageConfig config) {
        Stage stage = new Stage();
        applyIcon(stage);

        // Inisialisasi gaya jendela (harus dilakukan sebelum jendela ditampilkan)
        stage.initStyle(config.style);

        // Membuat Scene baru
        Scene scene = new Scene(root);

        // Mengatur transparansi lapisan scene jika dikonfigurasi demikian
        if (config.transparent) {
            scene.setFill(Color.TRANSPARENT);
        }

        // Menambahkan stylesheet CSS jika tersedia dalam konfigurasi
        if (config.cssPath != null) {
            scene.getStylesheets().add(
                    SceneManager.class.getResource(config.cssPath).toExternalForm());
        }

        stage.setScene(scene);
        stage.setTitle(config.title);
        stage.setResizable(config.resizable);

        // Mengatur batasan ukuran minimal jendela
        if (config.minWidth > 0) {
            stage.setMinWidth(config.minWidth);
        }
        if (config.minHeight > 0) {
            stage.setMinHeight(config.minHeight);
        }

        // Mengatur mode tampilan maksimal
        if (config.maximized) {
            stage.setMaximized(true);
        }

        stage.centerOnScreen();

        return stage;
    }

    /**
     * Menyediakan preset konfigurasi untuk jendela Login.
     * 
     * @return Objek StageConfig untuk tampilan Login.
     */
    public static StageConfig loginConfig() {
        return new StageConfig()
                .title("Login - Dapoer Syafira")
                .style(StageStyle.TRANSPARENT)
                .resizable(false)
                .transparent(true);
    }

    /**
     * Menyediakan preset konfigurasi untuk jendela Dashboard utama.
     * 
     * @return Objek StageConfig untuk tampilan Dashboard.
     */
    public static StageConfig dashboardConfig() {
        return new StageConfig()
                .title("Dashboard - Dapoer Syafira")
                .style(StageStyle.DECORATED)
                .resizable(true)
                .maximized(true)
                .minSize(1280, 720);
    }

    /**
     * Menginisialisasi Stage yang sudah ada (biasanya dari Application.start()).
     * 
     * @param stage    Objek Stage utama aplikasi.
     * @param fxmlPath Path menuju file FXML.
     * @param config   Konfigurasi Stage.
     * @throws IOException Jika terjadi kesalahan saat memuat file FXML.
     */
    public static void initStage(Stage stage, String fxmlPath, StageConfig config) throws IOException {
        Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
        applyIcon(stage);

        stage.initStyle(config.style);

        Scene scene = new Scene(root);

        if (config.transparent) {
            scene.setFill(Color.TRANSPARENT);
        }

        if (config.cssPath != null) {
            scene.getStylesheets().add(
                    SceneManager.class.getResource(config.cssPath).toExternalForm());
        }

        stage.setScene(scene);
        stage.setTitle(config.title);
        stage.setResizable(config.resizable);

        if (config.minWidth > 0) {
            stage.setMinWidth(config.minWidth);
        }
        if (config.minHeight > 0) {
            stage.setMinHeight(config.minHeight);
        }

        if (config.maximized) {
            stage.setMaximized(true);
        }

        stage.centerOnScreen();
    }

    /**
     * Memasang ikon resmi aplikasi ke jendela.
     * 
     * @param stage Jendela yang akan dipasangi ikon.
     */
    private static void applyIcon(Stage stage) {
        try {
            Image icon = new Image(SceneManager.class.getResourceAsStream("/images/logo_icon.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            // Jika ikon gagal dimuat, catat kesalahan ke konsol sistem
            System.err.println("Gagal memuat icon aplikasi di SceneManager: " + e.getMessage());
        }
    }
}
