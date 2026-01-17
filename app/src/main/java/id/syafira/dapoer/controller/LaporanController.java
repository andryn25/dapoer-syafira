package id.syafira.dapoer.controller;

import id.syafira.dapoer.service.ReportService;
import id.syafira.dapoer.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Kontroler untuk modul Laporan (Reports).
 * Bertugas mengatur pemicuan pembuatan laporan, kompilasi JRXML,
 * serta menampilkan hasil laporan secara interaktif di dalam aplikasi.
 * Mendukung pembatasan akses (RBAC) untuk laporan tertentu berdasarkan peran
 * pengguna.
 */
public class LaporanController {

    // --- Tombol-tombol Laporan Master Data ---
    @FXML
    private Button btnLaporanMenu;
    @FXML
    private Button btnLaporanPelanggan;
    @FXML
    private Button btnLaporanUser;

    // --- Tombol-tombol Laporan Transaksi ---
    @FXML
    private Button btnLaporanTransaksi;
    @FXML
    private Button btnLaporanTransaksiDetail;
    @FXML
    private Button btnLaporanOrder;
    @FXML
    private Button btnLaporanOrderDetail;

    // --- Tombol-tombol Laporan Analitik ---
    @FXML
    private Button btnLaporanStokAlert;
    @FXML
    private Button btnLaporanPenjualanMenu;
    @FXML
    private Button btnLaporanPelangganSetia;
    @FXML
    private Button btnLaporanPendapatan;

    // --- Container Penampung Tampilan ---
    @FXML
    private VBox menuContainer; // Daftar pilihan laporan
    @FXML
    private BorderPane reportContainer; // Area penampil laporan
    @FXML
    private StackPane reportPlaceholder; // Tempat menyematkan Node JasperReports

    /**
     * Inisialisasi awal UI Laporan.
     * Melakukan pengecekan hak akses pengguna saat menu dimuat.
     */
    @FXML
    private void initialize() {
        checkAccess();
    }

    /**
     * Mengatur visibilitas tombol laporan berdasarkan Role pengguna (RBAC).
     * Kasir memiliki akses terbatas dibandingkan Admin atau Owner.
     */
    private void checkAccess() {
        if (!id.syafira.dapoer.Session.isLoggedIn())
            return;

        String role = id.syafira.dapoer.Session.getCurrentUser().getLevel().getNamaLevel();

        // Aturan Pembatasan untuk peran 'Kasir'
        if ("Kasir".equalsIgnoreCase(role)) {
            // Menonaktifkan laporan data pengguna yang bersifat sensitif
            btnLaporanUser.setDisable(true);

            // Menonaktifkan laporan analitik strategis
            btnLaporanPenjualanMenu.setDisable(true);
            btnLaporanPelangganSetia.setDisable(true);
            btnLaporanPendapatan.setDisable(true);
        }
        // Admin dan Owner diizinkan mengakses seluruh laporan
    }

    /**
     * Menutup tampilan laporan dan kembali ke daftar pilihan laporan.
     */
    @FXML
    private void handleBackToMenu() {
        reportPlaceholder.getChildren().clear();
        menuContainer.setVisible(true);
        reportContainer.setVisible(false);
    }

    /**
     * Beralih dari menu daftar laporan ke tampilan pratinjau laporan.
     * 
     * @param reportNode Node yang dihasilkan oleh ReportService berisi JRViewer.
     */
    private void showReportView(Node reportNode) {
        reportPlaceholder.getChildren().setAll(reportNode);
        menuContainer.setVisible(false);
        reportContainer.setVisible(true);
    }

    // --- Event Handlers untuk pemicu Laporan ---

    @FXML
    private void handleLaporanMenu() {
        try {
            Node node = ReportService.getMenuMasakanReportNode();
            showReportView(node);
        } catch (Exception e) {
            AlertHelper.showError("Error", "Gagal menampilkan laporan menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLaporanPelanggan() {
        try {
            Node node = ReportService.getPelangganReportNode();
            showReportView(node);
        } catch (Exception e) {
            AlertHelper.showError("Error", "Gagal menampilkan laporan pelanggan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLaporanUser() {
        try {
            Node node = ReportService.getPenggunaReportNode();
            showReportView(node);
        } catch (Exception e) {
            AlertHelper.showError("Error", "Gagal menampilkan laporan pengguna: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLaporanTransaksi() {
        try {
            Node node = ReportService.getTransaksiReportNode();
            showReportView(node);
        } catch (Exception e) {
            AlertHelper.showError("Error", "Gagal menampilkan laporan transaksi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLaporanTransaksiDetail() {
        try {
            Node node = ReportService.getTransaksiDetailReportNode();
            showReportView(node);
        } catch (Exception e) {
            AlertHelper.showError("Error", "Gagal menampilkan laporan transaksi detail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLaporanPenjualanMenu() {
        try {
            Node node = ReportService.getPenjualanPerMenuReportNode();
            showReportView(node);
        } catch (Exception e) {
            AlertHelper.showError("Error", "Gagal menampilkan laporan penjualan per menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLaporanPelangganSetia() {
        try {
            Node node = ReportService.getPelangganSetiaReportNode();
            showReportView(node);
        } catch (Exception e) {
            AlertHelper.showError("Error", "Gagal menampilkan laporan pelanggan setia: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLaporanStokAlert() {
        try {
            Node node = ReportService.getStokAlertReportNode();
            showReportView(node);
        } catch (Exception e) {
            AlertHelper.showError("Error", "Gagal menampilkan laporan stok alert: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLaporanPendapatan() {
        try {
            Node node = ReportService.getPendapatanReportNode();
            showReportView(node);
        } catch (Exception e) {
            AlertHelper.showError("Error", "Gagal menampilkan laporan pendapatan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLaporanOrder() {
        try {
            Node node = ReportService.getOrderReportNode();
            showReportView(node);
        } catch (Exception e) {
            AlertHelper.showError("Error", "Gagal menampilkan laporan pesanan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLaporanOrderDetail() {
        try {
            Node node = ReportService.getOrderDetailReportNode();
            showReportView(node);
        } catch (Exception e) {
            AlertHelper.showError("Error", "Gagal menampilkan laporan detail pesanan: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
