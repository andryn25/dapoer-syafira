package id.syafira.dapoer.controller;

import id.syafira.dapoer.service.ReportService;
import id.syafira.dapoer.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import id.syafira.dapoer.report.*;
import id.syafira.dapoer.util.ReportCompiler;

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
    @FXML
    private Button btnExportPdf;

    private enum ReportType {
        MENU, PELANGGAN, USER, TRANSAKSI, TRANSAKSI_DETAIL, ORDER, ORDER_DETAIL, STOK_ALERT, PENJUALAN_MENU,
        PELANGGAN_SETIA, PENDAPATAN
    }

    private ReportType currentReportType;

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
            currentReportType = ReportType.MENU;
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
            currentReportType = ReportType.PELANGGAN;
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
            currentReportType = ReportType.USER;
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
            currentReportType = ReportType.TRANSAKSI;
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
            currentReportType = ReportType.TRANSAKSI_DETAIL;
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
            currentReportType = ReportType.PENJUALAN_MENU;
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
            currentReportType = ReportType.PELANGGAN_SETIA;
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
            currentReportType = ReportType.STOK_ALERT;
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
            currentReportType = ReportType.PENDAPATAN;
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
            currentReportType = ReportType.ORDER;
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
            currentReportType = ReportType.ORDER_DETAIL;
            Node node = ReportService.getOrderDetailReportNode();
            showReportView(node);
        } catch (Exception e) {
            AlertHelper.showError("Error", "Gagal menampilkan laporan detail pesanan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExportPdf() {
        if (currentReportType == null)
            return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan PDF");
        fileChooser.setInitialFileName("Laporan_" + currentReportType.name().toLowerCase() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showSaveDialog(reportContainer.getScene().getWindow());

        if (file != null) {
            try {
                String jrxmlPath = null;
                switch (currentReportType) {
                    case MENU:
                        jrxmlPath = MenuMasakanReportGenerator.generateJRXML();
                        break;
                    case PELANGGAN:
                        jrxmlPath = PelangganReportGenerator.generateJRXML();
                        break;
                    case USER:
                        jrxmlPath = PenggunaReportGenerator.generateJRXML();
                        break;
                    case TRANSAKSI:
                        jrxmlPath = TransaksiReportGenerator.generateJRXML();
                        break;
                    case TRANSAKSI_DETAIL:
                        jrxmlPath = TransaksiDetailReportGenerator.generateJRXML();
                        break;
                    case ORDER:
                        jrxmlPath = OrderReportGenerator.generateJRXML();
                        break;
                    case ORDER_DETAIL:
                        jrxmlPath = OrderDetailReportGenerator.generateJRXML();
                        break;
                    case STOK_ALERT:
                        jrxmlPath = StokAlertReportGenerator.generateJRXML();
                        break;
                    case PENJUALAN_MENU:
                        jrxmlPath = PenjualanPerMenuReportGenerator.generateJRXML();
                        break;
                    case PELANGGAN_SETIA:
                        jrxmlPath = PelangganSetiaReportGenerator.generateJRXML();
                        break;
                    case PENDAPATAN:
                        jrxmlPath = PendapatanReportGenerator.generateJRXML();
                        break;
                }

                if (jrxmlPath != null) {
                    String jasperPath = ReportCompiler.compile(jrxmlPath);
                    ReportService.exportToPdf(jasperPath, file.getAbsolutePath());
                    AlertHelper.showInfo("Laporan berhasil disimpan ke " + file.getAbsolutePath());
                }
            } catch (Exception e) {
                AlertHelper.showError("Export Gagal", "Gagal mengekspor laporan: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
