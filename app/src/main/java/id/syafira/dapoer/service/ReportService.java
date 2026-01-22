package id.syafira.dapoer.service;

import id.syafira.dapoer.db.DatabaseHelper;
import id.syafira.dapoer.report.MenuMasakanReportGenerator;
import id.syafira.dapoer.report.PelangganReportGenerator;
import id.syafira.dapoer.report.PenggunaReportGenerator;
import id.syafira.dapoer.report.PelangganSetiaReportGenerator;
import id.syafira.dapoer.report.PendapatanReportGenerator;
import id.syafira.dapoer.report.StokAlertReportGenerator;
import id.syafira.dapoer.report.TransaksiDetailReportGenerator;
import id.syafira.dapoer.report.TransaksiReportGenerator;
import id.syafira.dapoer.report.PenjualanPerMenuReportGenerator;
import id.syafira.dapoer.report.OrderDetailReportGenerator;
import id.syafira.dapoer.report.OrderReportGenerator;
import id.syafira.dapoer.report.ReceiptReportGenerator;
import id.syafira.dapoer.util.ReportCompiler;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.swing.JRViewer;
import javafx.embed.swing.SwingNode;
import javafx.scene.Node;
import javax.swing.SwingUtilities;

import java.sql.Connection;

/**
 * Layanan pusat untuk pembuatan dan penampilan laporan JasperReports.
 * Bertanggung jawab menyediakan Node JavaFX yang berisi tampilan visual
 * laporan.
 * Mengintegrasikan Swing (JRViewer) ke dalam JavaFX melalui SwingNode.
 */
public class ReportService {

    /**
     * Menghasilkan Node JavaFX yang berisi laporan Jasper.
     * Digunakan untuk menyematkan (embed) laporan langsung ke dalam Scene JavaFX.
     * 
     * @param jasperPath Path ke file .jasper yang sudah dikompilasi.
     * @return Node (SwingNode) yang berisi komponen JRViewer.
     * @throws Exception Jika terjadi kesalahan saat memuat atau mengisi laporan.
     */
    public static Node getReportNode(String jasperPath) throws Exception {
        return getReportNode(jasperPath, null);
    }

    /**
     * Menghasilkan Node laporan dengan parameter tambahan.
     * 
     * @param jasperPath Path ke file .jasper.
     * @param params     Map berisi parameter untuk laporan (filter, dll).
     * @return Node laporan.
     */
    public static Node getReportNode(String jasperPath, java.util.Map<String, Object> params) throws Exception {
        // Mengisi laporan dengan data dari database
        JasperPrint jasperPrint = fillReport(jasperPath, params);

        SwingNode swingNode = new SwingNode();
        // JRViewer adalah komponen Swing, sehingga harus dijalan di Event Dispatch
        // Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            JRViewer viewer = new JRViewer(jasperPrint);
            viewer.setZoomRatio(1.2f); // Rasio zoom default untuk kenyamanan membaca
            swingNode.setContent(viewer);
        });

        return swingNode;
    }

    /**
     * Helper internal untuk memuat file .jasper dan mengisinya dengan data dari
     * koneksi database.
     */
    @SuppressWarnings("deprecation")
    private static JasperPrint fillReport(String jasperPath, java.util.Map<String, Object> params) throws Exception {
        if (params == null) {
            params = new java.util.HashMap<>();
        }
        // Mengatur locale default ke Indonesia agar format tanggal dan uang di laporan
        // konsisten
        params.put(JRParameter.REPORT_LOCALE, new java.util.Locale("id", "ID"));

        try (Connection conn = DatabaseHelper.getConnection()) {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(jasperPath);
            return JasperFillManager.fillReport(jasperReport, params, conn);
        }
    }

    // --- Daftar Getter Node Laporan untuk Berbagai Kategori ---

    /** Laporan daftar menu masakan. */
    public static Node getMenuMasakanReportNode() throws Exception {
        String jrxmlPath = MenuMasakanReportGenerator.generateJRXML();
        String jasperPath = ReportCompiler.compile(jrxmlPath); // Kompilasi otomatis
        return getReportNode(jasperPath);
    }

    /** Laporan data pelanggan. */
    public static Node getPelangganReportNode() throws Exception {
        String jrxmlPath = PelangganReportGenerator.generateJRXML();
        String jasperPath = ReportCompiler.compile(jrxmlPath);
        return getReportNode(jasperPath);
    }

    /** Laporan ringkasan transaksi. */
    public static Node getTransaksiReportNode() throws Exception {
        String jrxmlPath = TransaksiReportGenerator.generateJRXML();
        String jasperPath = ReportCompiler.compile(jrxmlPath);
        return getReportNode(jasperPath);
    }

    /** Laporan daftar pengguna aplikasi. */
    public static Node getPenggunaReportNode() throws Exception {
        String jrxmlPath = PenggunaReportGenerator.generateJRXML();
        String jasperPath = ReportCompiler.compile(jrxmlPath);
        return getReportNode(jasperPath);
    }

    /** Laporan detail rincian transaksi (termasuk item yang dibeli). */
    public static Node getTransaksiDetailReportNode() throws Exception {
        String jrxmlPath = TransaksiDetailReportGenerator.generateJRXML();
        String jasperPath = ReportCompiler.compile(jrxmlPath);
        return getReportNode(jasperPath);
    }

    /** Laporan statistik penjualan berdasar menu. */
    public static Node getPenjualanPerMenuReportNode() throws Exception {
        String jrxmlPath = PenjualanPerMenuReportGenerator.generateJRXML();
        String jasperPath = ReportCompiler.compile(jrxmlPath);
        return getReportNode(jasperPath);
    }

    /** Laporan identifikasi pelanggan setia. */
    public static Node getPelangganSetiaReportNode() throws Exception {
        String jrxmlPath = PelangganSetiaReportGenerator.generateJRXML();
        String jasperPath = ReportCompiler.compile(jrxmlPath);
        return getReportNode(jasperPath);
    }

    /** Laporan peringatan stok menu yang menipis. */
    public static Node getStokAlertReportNode() throws Exception {
        String jrxmlPath = StokAlertReportGenerator.generateJRXML();
        String jasperPath = ReportCompiler.compile(jrxmlPath);
        return getReportNode(jasperPath);
    }

    /** Laporan grafik/tabel pendapatan. */
    public static Node getPendapatanReportNode() throws Exception {
        String jrxmlPath = PendapatanReportGenerator.generateJRXML();
        String jasperPath = ReportCompiler.compile(jrxmlPath);
        return getReportNode(jasperPath);
    }

    /** Laporan daftar pesanan (order) secara umum. */
    public static Node getOrderReportNode() throws Exception {
        String jrxmlPath = OrderReportGenerator.generateJRXML();
        String jasperPath = ReportCompiler.compile(jrxmlPath);
        return getReportNode(jasperPath);
    }

    /** Laporan detail rincian dari pesanan (order). */
    public static Node getOrderDetailReportNode() throws Exception {
        String jrxmlPath = OrderDetailReportGenerator.generateJRXML();
        String jasperPath = ReportCompiler.compile(jrxmlPath);
        return getReportNode(jasperPath);
    }

    /**
     * Menghasilkan struk pembayaran (receipt) untuk transaksi tertentu.
     */
    public static Node getReceiptNode(int idOrder, long bayar, long kembali) throws Exception {
        String jrxmlPath = ReceiptReportGenerator.generateJRXML();
        String jasperPath = ReportCompiler.compile(jrxmlPath);

        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("ORDER_ID", idOrder);
        params.put("UANG_BAYAR", bayar);
        params.put("KEMBALIAN", kembali);

        return getReportNode(jasperPath, params);
    }

    /**
     * Mengekspor laporan ke file PDF.
     * 
     * @param jasperPath Path ke file .jasper.
     * @param params     Parameter laporan.
     * @param outputPath Path tujuan output file PDF.
     * @throws Exception Jika terjadi kesalahan saat proses ekspor.
     */
    public static void exportToPdf(String jasperPath, java.util.Map<String, Object> params, String outputPath)
            throws Exception {
        JasperPrint jasperPrint = fillReport(jasperPath, params);
        JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
    }

    /**
     * Mengekspor laporan ke PDF tanpa parameter tambahan.
     */
    public static void exportToPdf(String jasperPath, String outputPath) throws Exception {
        exportToPdf(jasperPath, null, outputPath);
    }
}
