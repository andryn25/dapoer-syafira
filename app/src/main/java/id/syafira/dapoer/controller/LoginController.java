package id.syafira.dapoer.controller;

import id.syafira.dapoer.Session;
import id.syafira.dapoer.model.User;
import id.syafira.dapoer.service.LoginService;
import id.syafira.dapoer.util.AnimationHelper;
import id.syafira.dapoer.util.SceneManager;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Kontroler untuk tampilan Login.
 * Mengatur interaksi pengguna pada layar masuk, validasi kredensial,
 * animasi antarmuka, dan transisi ke dashboard utama.
 */
public class LoginController {

    @FXML
    private HBox root; // Kontainer utama untuk animasi transisi scene

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginBtn;

    @FXML
    private Button exitBtn;

    private final LoginService loginService = LoginService.getInstance();

    /**
     * Inisialisasi awal saat view dimuat.
     * Mengatur animasi masuk untuk memberikan kesan premium pada aplikasi.
     */
    @FXML
    public void initialize() {
        // Efek transisi masuk untuk seluruh layar
        AnimationHelper.sceneEntrance(root);

        // Efek muncul bertahap (slide up) untuk setiap elemen formulir
        AnimationHelper.slideInUp(usernameField);
        AnimationHelper.slideInUp(passwordField);
        AnimationHelper.slideInUp(loginBtn);
        AnimationHelper.slideInUp(exitBtn);
    }

    /**
     * Menangani aksi klik tombol Login.
     * Melakukan validasi, autentikasi, dan pengalihan halaman.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // 1. Validasi input: memastikan field tidak kosong
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username dan password wajib diisi");
            AnimationHelper.shake(errorLabel); // Efek getar pada pesan error
            return;
        }

        try {
            // 2. Autentikasi: memverifikasi kredensial ke database melalui service
            User u = loginService.authenticate(username, password);

            if (u == null) {
                // Penanganan jika login gagal
                errorLabel.setText("Username atau password salah");
                AnimationHelper.shake(errorLabel);
                AnimationHelper.shake(usernameField);
                AnimationHelper.shake(passwordField);
                return;
            }

            // 3. Manajemen Sesi: menyimpan data user yang berhasil login
            Session.setCurrentUser(u);

            // 4. Feedback Visual: animasi sukses pada tombol
            AnimationHelper.successPulse(loginBtn);

            // 5. Transisi Halaman: berpindah ke dashboard utama setelah delay singkat
            PauseTransition pause = new PauseTransition(Duration.millis(300));
            pause.setOnFinished(e -> {
                // Efek transisi keluar sebelum benar-benar berpindah scene
                AnimationHelper.sceneExit(root, () -> {
                    try {
                        SceneManager.switchScene(
                                usernameField,
                                "/view/MainView.fxml",
                                SceneManager.dashboardConfig());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            });
            pause.play();

        } catch (SQLException e) {
            // Penanganan jika terjadi kendala pada koneksi database
            errorLabel.setText("Error koneksi database");
            AnimationHelper.shake(errorLabel);
            e.printStackTrace();
        }
    }

    /**
     * Menangani aksi klik tombol Keluar.
     * Menutup aplikasi dengan efek transisi keluar yang halus.
     */
    @FXML
    private void handleExit() {
        AnimationHelper.sceneExit(root, () -> {
            Platform.exit();
        });
    }
}
