package id.syafira.dapoer.controller;

import id.syafira.dapoer.model.MenuMasakan;
import id.syafira.dapoer.model.Order;
import id.syafira.dapoer.model.Pelanggan;
import id.syafira.dapoer.model.User;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import id.syafira.dapoer.model.DetailOrder;
import id.syafira.dapoer.service.MenuMasakanService;
import id.syafira.dapoer.service.PelangganService;
import id.syafira.dapoer.util.AlertHelper;
import id.syafira.dapoer.service.OrderService;
import id.syafira.dapoer.Session;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.application.Platform;
import javafx.collections.ObservableList;

/**
 * Kontroler untuk modul Pembuatan Pesanan Baru (Ordering).
 * Mengelola keranjang belanja, pencarian menu, sinkronisasi stok real-time,
 * dan perekaman data pesanan ke sistem.
 */
public class OrderController {

    // --- Bagian Informasi Atas ---
    @FXML
    private Label tanggalLabel; // Menampilkan jam real-time
    @FXML
    private Label kasirLabel;

    @FXML
    private ComboBox<Pelanggan> pelangganCombo;
    @FXML
    private ComboBox<String> jenisPesananCombo;

    // --- Daftar Katalog Menu (Kiri) ---
    @FXML
    private TableView<MenuMasakan> menuTable;
    @FXML
    private TableColumn<MenuMasakan, Number> colMenuStok;
    @FXML
    private TableColumn<MenuMasakan, String> colMenuNama;
    @FXML
    private TableColumn<MenuMasakan, Number> colMenuHarga;
    @FXML
    private TextField pencarianField; // Untuk filter menu cepat
    @FXML
    private TextArea catatanArea;

    // --- Formulir Input Item (Kanan Atas) ---
    @FXML
    private Label namaMenuLabel;
    @FXML
    private Label hargaMenuLabel;
    @FXML
    private Spinner<Integer> qtySpinner;
    @FXML
    private Button tambahBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button hapusBtn;

    // --- Tabel Rincian Keranjang (Kanan Bawah) ---
    @FXML
    private TableView<DetailOrder> detailTable;
    @FXML
    private TableColumn<DetailOrder, String> colDetailMenu;
    @FXML
    private TableColumn<DetailOrder, Number> colDetailQty;
    @FXML
    private TableColumn<DetailOrder, Number> colDetailSubtotal;

    // --- Total Akhir & Aksi ---
    @FXML
    private Label totalLabel;
    @FXML
    private Button pesanBtn;
    @FXML
    private DatePicker tglKirimDate;

    private final MenuMasakanService menuItemService = MenuMasakanService.getInstance();
    private final PelangganService pelangganService = PelangganService.getInstance();
    private final OrderService orderService = OrderService.getInstance();
    private MenuMasakan selectedMenu;

    /**
     * Inisialisasi awal UI Pemesanan.
     * Mengatur jam digital, binding properti keranjang, dan pemuatan data master.
     */
    @FXML
    public void initialize() {
        try {
            // 1. Membersihkan keranjang belanja setiap kali menu pesanan dibuka
            orderService.clear();

            // 2. Konfigurasi Tabel Katalog Menu
            colMenuNama.setCellValueFactory(data -> data.getValue().namaProperty());
            colMenuHarga.setCellValueFactory(data -> data.getValue().hargaProperty());
            colMenuStok.setCellValueFactory(data -> data.getValue().stokProperty());

            menuItemService.refresh();
            ObservableList<MenuMasakan> allMenu = menuItemService.getAll();
            orderService.syncWithCart(allMenu); // Pastikan stok di UI mencerminkan isi keranjang
            menuTable.setItems(allMenu);

            // Listener: Menampilkan detail menu yang di klik ke panel input
            menuTable.getSelectionModel().selectedItemProperty().addListener(
                    (obs, old, neu) -> {
                        selectedMenu = neu;
                        if (neu != null) {
                            namaMenuLabel.setText(neu.getNama());
                            hargaMenuLabel.setText(String.format("%,d", neu.getHarga()));
                        } else {
                            namaMenuLabel.setText("-");
                            hargaMenuLabel.setText("-");
                        }
                    });

            // 3. Konfigurasi Pilihan Pelanggan
            pelangganCombo.setItems(pelangganService.getAll());
            pelangganCombo.setConverter(new StringConverter<>() {
                @Override
                public String toString(Pelanggan p) {
                    return p == null ? "" : p.getNama();
                }

                @Override
                public Pelanggan fromString(String s) {
                    return null;
                }
            });

            // 4. Konfigurasi Tabel Keranjang Belanja
            colDetailMenu.setCellValueFactory(d -> d.getValue().getMenu().namaProperty());
            colDetailQty.setCellValueFactory(d -> d.getValue().qtyProperty());
            colDetailSubtotal.setCellValueFactory(d -> d.getValue().subtotalProperty());

            detailTable.setItems(orderService.getItems());

            // Listener: Memungkinkan edit/hapus item yang sudah ada di keranjang
            detailTable.getSelectionModel().selectedItemProperty().addListener(
                    (obs, old, neu) -> {
                        if (neu != null) {
                            qtySpinner.getValueFactory().setValue(neu.getQty());
                            editBtn.setDisable(false);
                            hapusBtn.setDisable(false);
                        } else {
                            qtySpinner.getValueFactory().setValue(1);
                            editBtn.setDisable(true);
                            hapusBtn.setDisable(true);
                        }
                    });
            editBtn.setDisable(true);
            hapusBtn.setDisable(true);

            // Bind label total agar otomatis berubah saat isi keranjang berubah
            totalLabel.textProperty().bind(orderService.totalProperty().asString());

            // Menampilkan info kasir yang bertugas
            User u = Session.getCurrentUser();
            kasirLabel.setText(u != null ? u.getNama() : "-");

            // Setup Spinner kuantitas
            qtySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999, 1));
            tglKirimDate.setValue(LocalDate.now());

            // 5. Setup Jam Digital Real-time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
                tanggalLabel.setText(LocalDateTime.now().format(formatter));
            }), new KeyFrame(Duration.seconds(1)));
            clock.setCycleCount(Timeline.INDEFINITE);
            clock.play();

            // Pilihan Jenis Pesanan
            jenisPesananCombo.getItems().addAll("Delivery", "Pick up");
            jenisPesananCombo.getSelectionModel().selectFirst();

        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> AlertHelper.showError("Error", "Gagal memuat Order View"));
        }
    }

    /**
     * Memasukkan menu yang dipilih ke dalam keranjang.
     */
    @FXML
    private void handleTambahItem() {
        if (selectedMenu == null)
            return;
        try {
            int qty = qtySpinner.getValue();
            orderService.addItem(selectedMenu, qty); // Validasi stok dilakukan di dalam service

            qtySpinner.getValueFactory().setValue(1);
            menuTable.getSelectionModel().clearSelection();
        } catch (IllegalArgumentException e) {
            AlertHelper.showInfo(e.getMessage()); // Pesan jika stok kurang
        } catch (Exception e) {
            AlertHelper.showError("Error", "Gagal menambahkan item: " + e.getMessage());
        }
    }

    /**
     * Menghapus item terpilih dari keranjang belanja.
     */
    @FXML
    private void handleHapusItem() {
        DetailOrder selected = (DetailOrder) detailTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        orderService.removeItem(selected);
        detailTable.getSelectionModel().clearSelection();
    }

    /**
     * Mengubah kuantitas item yang sudah ada di keranjang.
     */
    @FXML
    private void handleEditItem() {
        DetailOrder selected = (DetailOrder) detailTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        int qty = (Integer) qtySpinner.getValue();
        orderService.updateItem(selected, qty);
        detailTable.getSelectionModel().clearSelection();
        qtySpinner.getValueFactory().setValue(1);
    }

    /**
     * Finalisasi pesanan dan menyimpannya ke database.
     */
    @FXML
    private void handlePesanSekarang() {
        // 1. Validasi Kelengkapan Pesanan
        LocalDate tglkirim = tglKirimDate.getValue();
        LocalDateTime tglKirimDateTime = LocalDateTime.of(tglkirim, LocalTime.of(10, 0));

        Pelanggan p = (Pelanggan) pelangganCombo.getSelectionModel().getSelectedItem();
        if (p == null) {
            AlertHelper.showInfo("Pelanggan belum dipilih.");
            return;
        }
        if (orderService.getItems().isEmpty()) {
            AlertHelper.showInfo("Belum ada item di detail order.");
            return;
        }

        User current = Session.getCurrentUser();
        if (current == null) {
            AlertHelper.showInfo("User belum login");
            return;
        }

        // 2. Pembuatan Objek Order
        Order order = new Order();
        order.setPelanggan(p);
        order.setUser(current);
        order.setTanggal(LocalDateTime.now());
        order.setTanggalKirim(tglKirimDateTime);
        order.setJenisPesanan(jenisPesananCombo.getValue());
        order.setTotal(orderService.totalProperty().get());
        order.setCatatan(catatanArea.getText());

        try {
            // 3. Penyimpanan secara transaksional di Database
            orderService.saveOrder(order);
            AlertHelper.showInfo("Order berhasil disimpan! Silakan lakukan pembayaran Kasir.");

            // 4. Reset form setelah sukses
            orderService.clear();
            resetForm();
        } catch (SQLException e) {
            AlertHelper.showError("Gagal menyimpan order: ", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Mengembalikan seluruh input form ke kondisi kosong.
     */
    private void resetForm() {
        menuTable.getSelectionModel().clearSelection();
        pelangganCombo.getSelectionModel().clearSelection();
        pelangganCombo.setPromptText("Pilih Pelanggan");
        jenisPesananCombo.getSelectionModel().selectFirst();
        detailTable.getItems().clear();
        tglKirimDate.setValue(LocalDate.now());
        qtySpinner.getValueFactory().setValue(1);
        catatanArea.clear();
    }
}
