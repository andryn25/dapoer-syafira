package id.syafira.dapoer;

import id.syafira.dapoer.db.DatabaseHelper;
import id.syafira.dapoer.preloader.StartupState;
import id.syafira.dapoer.preloader.StatusNotification;
import id.syafira.dapoer.preloader.WarningNotification;
import id.syafira.dapoer.util.AlertHelper;
import id.syafira.dapoer.util.SceneManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Kelas utama aplikasi Dapoer Syafira.
 * Kelas ini menangani siklus hidup aplikasi JavaFX, termasuk notifikasi
 * preloader,
 * inisialisasi database, dan transisi ke layar login.
 */
public class DapoerSyafiraApp extends Application {

    /**
     * Menginisialisasi sumber daya aplikasi sebelum UI dimulai.
     * Melakukan pengecekan koneksi database dan mengirimkan notifikasi progres ke
     * preloader.
     */
    @Override
    public void init() {
        // Langkah 1: Beri tahu preloader bahwa inisialisasi dimulai
        notifyPreloader(new StatusNotification("Membaca konfigurasi...", 0.2));
        notifyPreloader(new StatusNotification("Mengecek database...", 0.6));

        // Langkah 2: Mencoba membangun dan memvalidasi koneksi database
        try (var c = DatabaseHelper.getConnection()) {
            if (!c.isValid(2))
                throw new RuntimeException("Koneksi DB tidak valid");

            // Tandai database siap agar lapisan UI dapat dilanjutkan
            StartupState.dbReadyProperty().set(true);
            notifyPreloader(new StatusNotification("Database OK", 1.0));
        } catch (Throwable ex) {
            // Langkah 3: Tangani kegagalan inisialisasi database
            String msg = ex.getMessage();
            if (msg == null && ex.getCause() != null)
                msg = ex.getCause().getMessage();
            if (msg == null)
                msg = ex.toString();

            // Tampilkan peringatan pada preloader dan dialog pesan kesalahan
            notifyPreloader(new WarningNotification("Gagal Inisialisasi Database. Periksa file config.properties"));
            AlertHelper.showError("Gagal Inisialisasi Database", msg);
        }
    }

    /**
     * Mengonfigurasi dan menampilkan stage login.
     * 
     * @param stage Stage utama dari sistem JavaFX.
     */
    private void showLoginStage(Stage stage) {
        try {
            // Inisialisasi stage menggunakan SceneManager dengan konfigurasi Login
            SceneManager.initStage(stage, "/view/LoginView.fxml", SceneManager.loginConfig());
            stage.show();

            // Izinkan aplikasi ditutup ketika semua jendela ditutup
            Platform.setImplicitExit(true);
        } catch (Exception e) {
            AlertHelper.showError("Gagal memuat Login View", e.getMessage());
            Platform.exit();
        }
    }

    /**
     * Titik masuk utama untuk siklus hidup UI JavaFX.
     * Menunggu kesiapan database jika belum diinisialisasi di init().
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Jika database sudah siap, segera tampilkan login
        if (StartupState.dbReadyProperty().get()) {
            showLoginStage(stage);
        } else {
            // Jika tidak, tunggu sinyal kesiapan database (dbReady)
            StartupState.dbReadyProperty().addListener((obs, oldV, newV) -> {
                if (newV) {
                    Platform.runLater(() -> showLoginStage(stage));
                }
            });
        }
    }

    /**
     * Metode main standar Java.
     * 
     * @param args Argumen baris perintah.
     */
    public static void main(String[] args) {
        launch(args);
    }
}