package id.syafira.dapoer.controller;

import id.syafira.dapoer.model.DetailOrder;
import id.syafira.dapoer.model.Order;
import id.syafira.dapoer.service.OrderService;
import id.syafira.dapoer.service.ReportService;
import id.syafira.dapoer.service.TransaksiService;
import id.syafira.dapoer.util.AlertHelper;
import id.syafira.dapoer.Session;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Duration;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Kontroler untuk modul Pembayaran (Kasir).
 * Mengatur pelunasan pesanan yang sudah tercatat, perhitungan uang kembalian,
 * penghapusan pesanan bermasalah, dan pencetakan struk pembayaran.
 */
public class TransaksiController {

    // --- Panel Kiri: Daftar Pesanan Belum Lunas ---
    @FXML
    private TableView<Order> tableOrder;
    @FXML
    private TableColumn<Order, String> colTglOrder;
    @FXML
    private TableColumn<Order, String> colPelanggan;
    @FXML
    private TableColumn<Order, String> colTotalOrder;

    @FXML
    private TableView<DetailOrder> tableDetail;
    @FXML
    private TableColumn<DetailOrder, String> colDetailMenu;
    @FXML
    private TableColumn<DetailOrder, Number> colDetailQty;
    @FXML
    private TableColumn<DetailOrder, String> colDetailSubtotal;

    // --- Panel Kanan: Rincian Pembayaran ---
    @FXML
    private Label lblDateTime;
    @FXML
    private TextField fieldIdUser;
    @FXML
    private TextField fieldIdOrder;
    @FXML
    private TextField fieldTotalBayar;
    @FXML
    private TextField fieldBayar; // Tempat memasukkan nominal uang dari pelanggan
    @FXML
    private TextField fieldKembalian;
    @FXML
    private Button btnPayment;
    @FXML
    private Button btnReset;
    @FXML
    private Button btnPrint;
    @FXML
    private Button btnHapus;

    private final TransaksiService transaksiService = TransaksiService.getInstance();
    private final OrderService orderService = OrderService.getInstance();

    @FXML
    private javafx.scene.control.SplitPane formContainer; // Layer utama UI transaksi
    @FXML
    private javafx.scene.layout.BorderPane reportContainer; // Layer untuk preview struk
    @FXML
    private javafx.scene.layout.StackPane reportPlaceholder;

    private Order selectedOrder;
    private long currentTotal = 0;
    private long currentBayar = 0;

    /**
     * Inisialisasi awal UI Transaksi.
     * Mengatur tabel, jam real-time, dan memuat pesanan yang belum dibayar.
     */
    @FXML
    public void initialize() {
        setupClock();
        setupTables();
        setupSelectionListener();

        // Mengisi identitas kasir yang login
        if (Session.getCurrentUser() != null) {
            fieldIdUser.setText(String.format("USR-%03d", Session.getCurrentUser().getId()));
        }
        loadUnpaidOrders();
    }

    /**
     * Menjalankan jam digital pada antarmuka kasir.
     */
    private void setupClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            lblDateTime.setText(java.time.LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    /**
     * Konfigurasi kolom tabel untuk data pesanan dan detail item.
     */
    private void setupTables() {
        colTglOrder.setCellValueFactory(cell -> {
            if (cell.getValue().getTanggal() != null)
                return new SimpleStringProperty(cell.getValue().getTanggal()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            return new SimpleStringProperty("-");
        });
        colPelanggan.setCellValueFactory(cell -> {
            if (cell.getValue().getPelanggan() != null)
                return cell.getValue().getPelanggan().namaProperty();
            return new SimpleStringProperty("-");
        });
        colTotalOrder.setCellValueFactory(cell -> new SimpleStringProperty(formatRupiah(cell.getValue().getTotal())));

        // Konfigurasi tabel rincian item dalam satu pesanan
        colDetailMenu.setCellValueFactory(cell -> cell.getValue().getMenu().namaProperty());
        colDetailQty.setCellValueFactory(cell -> cell.getValue().qtyProperty());
        colDetailSubtotal
                .setCellValueFactory(cell -> new SimpleStringProperty(formatRupiah(cell.getValue().getSubtotal())));
    }

    /**
     * Mengatur aksi saat sebuah pesanan dipilih dari tabel daftar pesanan.
     */
    private void setupSelectionListener() {
        tableOrder.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                selectOrder(newVal);
        });
    }

    /**
     * Memuat rincian pesanan terpilih ke panel pembayaran.
     */
    private void selectOrder(Order order) {
        this.selectedOrder = order;
        fieldIdOrder.setText(String.format("ORD-%03d", order.getIdOrder()));
        currentTotal = order.getTotal();
        fieldTotalBayar.setText(formatRupiah(currentTotal));

        // Mengambil rincian item pesanan dari database
        try {
            ObservableList<DetailOrder> details = orderService.getOrderDetails(order.getIdOrder());
            tableDetail.setItems(details);
        } catch (Exception e) {
            e.printStackTrace();
        }

        fieldBayar.clear();
        fieldKembalian.clear();
        fieldBayar.requestFocus();
    }

    /**
     * Menyegarkan daftar pesanan yang berstatus belum dibayar.
     */
    private void loadUnpaidOrders() {
        List<Order> unpaid = orderService.getUnpaidOrders();
        tableOrder.setItems(FXCollections.observableArrayList(unpaid));
    }

    /**
     * Menghitung uang kembalian berdasarkan nominal pembayaran yang dimasukkan.
     */
    @FXML
    private void handleHitungKembalian() {
        String text = fieldBayar.getText().replaceAll("[^0-9]", "");
        if (text.isEmpty()) {
            currentBayar = 0;
        } else {
            try {
                currentBayar = Long.parseLong(text);
            } catch (NumberFormatException e) {
                currentBayar = 0;
            }
        }

        long kembalian = currentBayar - currentTotal;
        fieldKembalian.setText(formatRupiah(kembalian));
    }

    /**
     * Memproses pelunasan transaksi dan mencatatnya ke database.
     */
    @FXML
    private void handlePayment() {
        if (selectedOrder == null) {
            AlertHelper.showInfo("Pilih order terlebih dahulu!");
            return;
        }
        if (currentBayar < currentTotal) {
            AlertHelper.showInfo("Uang pembayaran kurang!");
            return;
        }

        try {
            // Mencatat transaksi pelunasan
            transaksiService.createTransaction(selectedOrder, currentTotal, currentBayar, currentBayar - currentTotal);
            AlertHelper.showInfo("Transaksi berhasil disimpan!");

            // Menampilkan pratinjau struk (Receipt) secara otomatis
            showReceipt(selectedOrder.getIdOrder(), currentBayar, currentBayar - currentTotal);

            // Membersihkan form untuk transaksi berikutnya
            handleReset();

        } catch (Exception e) {
            AlertHelper.showError("Gagal menyimpan transaksi: ", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Mengosongkan seluruh field input di panel pembayaran.
     */
    @FXML
    private void handleReset() {
        selectedOrder = null;
        fieldIdOrder.clear();
        fieldTotalBayar.clear();
        fieldBayar.clear();
        fieldKembalian.clear();
        tableDetail.getItems().clear();
        loadUnpaidOrders();
        currentTotal = 0;
        currentBayar = 0;
    }

    /**
     * Menutup tampilan struk dan kembali ke modul transaksi.
     */
    @FXML
    private void handleBackToTransaksi() {
        reportPlaceholder.getChildren().clear();
        formContainer.setVisible(true);
        reportContainer.setVisible(false);
    }

    /**
     * Mencetak ulang struk untuk pesanan yang saat ini sedang aktif dipilih.
     */
    @FXML
    private void handlePrintLast() {
        if (selectedOrder != null) {
            showReceipt(selectedOrder.getIdOrder(), currentBayar, currentBayar - currentTotal);
        } else {
            AlertHelper.showInfo("Pilih transaksi terlebih dahulu!");
        }
    }

    /**
     * Memanggil ReportService untuk menyematkan laporan struk ke dalam UI.
     */
    private void showReceipt(int idOrder, long bayar, long kembali) {
        try {
            javafx.scene.Node reportNode = ReportService.getReceiptNode(idOrder, bayar, kembali);
            reportPlaceholder.getChildren().setAll(reportNode);
            formContainer.setVisible(false);
            reportContainer.setVisible(true);
        } catch (Exception e) {
            AlertHelper.showError("Gagal memuat struk: ", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Menghapus pesanan yang terpilih (biasanya jika pelanggan membatalkan sebelum
     * bayar).
     */
    @FXML
    private void handleDelete() {
        if (selectedOrder == null) {
            AlertHelper.showInfo("Pilih order terlebih dahulu!");
            return;
        }

        try {
            AlertHelper.showConfirm("Yakin ingin menghapus order ini?", () -> {
                try {
                    orderService.deleteOrder(selectedOrder.getIdOrder());
                    AlertHelper.showInfo("Order berhasil dihapus!");
                    handleReset();
                } catch (Exception e) {
                    AlertHelper.showError("Gagal menghapus order: ", e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper untuk memformat angka nominal ke dalam format mata uang Rupiah.
     */
    @SuppressWarnings("deprecation")
    private String formatRupiah(long amount) {
        return NumberFormat.getCurrencyInstance(new Locale("id", "ID")).format(amount);
    }
}
