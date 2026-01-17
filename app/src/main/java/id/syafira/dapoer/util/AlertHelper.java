package id.syafira.dapoer.util;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

import id.syafira.dapoer.controller.MainController;

/**
 * Kelas bantuan untuk mengelola dialog peringatan (Alert) dan konfirmasi di
 * seluruh aplikasi.
 * Mendukung pengalihan otomatis ke thread UI JavaFX dan integrasi dengan custom
 * overlay di MainController.
 */
public class AlertHelper {

    /**
     * Konstruktor privat untuk mencegah instansiasi karena ini adalah kelas
     * utilitas.
     */
    private AlertHelper() {
        // Mencegah instansiasi
    }

    /**
     * Menampilkan dialog kesalahan (Error).
     * Jika saat ini berada di thread bukan UI, akan dijalankan secara asinkron di
     * thread JavaFX.
     * Secara otomatis menggunakan custom alert jika aplikasi berada di tampilan
     * utama.
     * 
     * @param msg         Judul pesan kesalahan.
     * @param contentText Detail isi pesan kesalahan.
     */
    public static void showError(String msg, String contentText) {
        // Pastikan dijalankan di JavaFX Application Thread agar UI tidak membeku/error
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> showError(msg, contentText));
            return;
        }

        // Coba tampilkan menggunakan custom overlay jika MainController tersedia
        // (Dashboard)
        MainController main = MainController.getInstance();
        if (main != null) {
            main.showCustomAlert("Error", msg + "\n" + contentText, true);
            return;
        }

        // Fallback ke Alert standar sistem (Native) jika diluar dashboard atau
        // controller tidak aktif
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(msg);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Menampilkan dialog informasi (Info).
     * 
     * @param msg Pesan informasi yang ingin disampaikan.
     */
    public static void showInfo(String msg) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> showInfo(msg));
            return;
        }

        MainController main = MainController.getInstance();
        if (main != null) {
            main.showCustomAlert("Info", msg, false);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(msg);
        alert.showAndWait();
    }

    /**
     * Menampilkan dialog konfirmasi sederhana.
     * 
     * @param msg       Pesan konfirmasi (pertanyaan).
     * @param onConfirm Kode yang akan dijalankan jika pengguna menekan tombol
     *                  OK/Konfirmasi.
     */
    public static void showConfirm(String msg, Runnable onConfirm) {
        showConfirm(null, msg, onConfirm);
    }

    /**
     * Menampilkan dialog konfirmasi dengan penentuan Window pemilik.
     * 
     * @param owner     Komponen JavaFX yang menjadi pemilik dialog (opsional).
     * @param msg       Pesan konfirmasi.
     * @param onConfirm Kode yang akan dijalankan jika pengguna menekan OK.
     */
    public static void showConfirm(Node owner, String msg, Runnable onConfirm) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> showConfirm(owner, msg, onConfirm));
            return;
        }

        // Gunakan custom confirm overlay jika berada di tampilan Dashboard
        MainController main = MainController.getInstance();
        if (main != null) {
            main.showCustomConfirm(msg, onConfirm);
            return;
        }

        // Fallback ke Native Alert CONFIRMATION
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        // Atur pemilik dialog jika owner diberikan untuk manajemen window yang lebih
        // baik
        if (owner != null && owner.getScene() != null && owner.getScene().getWindow() != null) {
            alert.initOwner(owner.getScene().getWindow());
        }
        alert.setTitle("Konfirmasi");
        alert.setHeaderText(msg);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            onConfirm.run();
        }
    }
}
