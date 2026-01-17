package id.syafira.dapoer.controller;

import java.sql.SQLException;
import id.syafira.dapoer.model.Pelanggan;
import id.syafira.dapoer.service.PelangganService;
import id.syafira.dapoer.util.AlertHelper;
import id.syafira.dapoer.util.FormManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Kontroler untuk mengelola Master Data Pelanggan.
 * Mengatur antarmuka untuk mencatat identitas pelanggan, nomor telepon, dan
 * alamat.
 * Dilengkapi dengan validasi input dan manajemen status form otomatis.
 */
public class PelangganController {

    @FXML
    private Button newBtn;
    @FXML
    private Button updateBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button editBtn;

    @FXML
    private TableView<Pelanggan> pelangganTable;
    @FXML
    private TableColumn<Pelanggan, String> idColumn;
    @FXML
    private TableColumn<Pelanggan, String> namaColumn;
    @FXML
    private TableColumn<Pelanggan, String> noHpColumn;
    @FXML
    private TableColumn<Pelanggan, String> alamatColumn;

    @FXML
    private TextField idField;
    @FXML
    private TextField namaField;
    @FXML
    private TextField noHpField;
    @FXML
    private TextArea alamatArea;

    private final PelangganService pelangganService = PelangganService.getInstance();
    private FormManager formManager; // Pembantu untuk mengatur perilaku UI

    /**
     * Inisialisasi awal UI Pelanggan.
     * Mengatur formatting ID pelanggan (CST-xxx) dan sinkronisasi data tabel.
     */
    @FXML
    public void initialize() {
        // Melakukan format tampilan ID di tabel agar lebih profesional
        idColumn.setCellValueFactory(
                celldata -> new SimpleStringProperty(String.format("CST-%03d", celldata.getValue().getId())));
        namaColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        noHpColumn.setCellValueFactory(new PropertyValueFactory<>("noHp"));
        alamatColumn.setCellValueFactory(new PropertyValueFactory<>("alamat"));

        // Memuat ulang data dari database
        pelangganService.refresh();
        pelangganTable.setItems(pelangganService.getAll());

        idField.disableProperty().set(true); // ID dihasilkan oleh sistem

        // Setup FormManager guna menangani siklus hidup elemen formulir
        formManager = new FormManager(newBtn, saveBtn, updateBtn, deleteBtn, editBtn, pelangganTable,
                namaField, noHpField, alamatArea);

        // Listener untuk memperbarui tampilan detail saat baris tabel di klik
        pelangganTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> showDetail(newSel));

        formManager.reset(); // Sembunyikan field input di awal
    }

    /**
     * Menampilkan data pelanggan yang terpilih ke komponen input.
     */
    private void showDetail(Pelanggan p) {
        if (p == null) {
            idField.clear();
            namaField.clear();
            noHpField.clear();
            alamatArea.clear();
        } else {
            idField.setText(String.format("CST-%03d", p.getId()));
            namaField.setText(p.getNama());
            noHpField.setText(p.getNoHp());
            alamatArea.setText(p.getAlamat());
            formManager.onSelection(); // Mengubah status tombol menjadi mode terpilih
        }
    }

    /**
     * Menyiapkan form untuk entry data pelanggan baru.
     */
    @FXML
    private void handleNew() {
        if (formManager.isEditing()) {
            handleCancel();
            return;
        }
        formManager.startNew();
        idField.clear();
        namaField.clear();
        noHpField.clear();
        alamatArea.clear();
    }

    /**
     * Menangani proses penyimpanan data pelanggan baru.
     */
    @FXML
    private void handleSave() {
        // Memeriksa kelengkapan data
        if (namaField.getText().isEmpty() || noHpField.getText().isEmpty() || alamatArea.getText().isEmpty()) {
            AlertHelper.showInfo("Semua field harus diisi");
            return;
        }

        Pelanggan p = new Pelanggan(namaField.getText(), noHpField.getText(), alamatArea.getText());
        try {
            AlertHelper.showConfirm("Apakah Anda yakin ingin menambahkan pelanggan ini?", () -> {
                try {
                    pelangganService.add(p);
                    formManager.reset();
                    AlertHelper.showInfo("Pelanggan berhasil ditambahkan");
                } catch (SQLException e) {
                    e.printStackTrace();
                    AlertHelper.showError("Gagal Menambahkan pelanggan", e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Menghapus data pelanggan yang dipilih dari database.
     */
    @FXML
    private void handleDelete() {
        Pelanggan selected = pelangganTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showInfo("Pilih pelanggan terlebih dahulu");
            return;
        }
        try {
            AlertHelper.showConfirm("Apakah Anda yakin ingin menghapus pelanggan ini?", () -> {
                try {
                    pelangganService.delete(selected);
                    formManager.reset();
                    AlertHelper.showInfo("Pelanggan berhasil dihapus");
                } catch (SQLException e) {
                    AlertHelper.showError("Gagal Menghapus pelanggan", e.getMessage());
                }
            });
        } catch (Exception e) {
            AlertHelper.showError("Error", "Terjadi kesalahan sistem: " + e.getMessage());
        }
    }

    /**
     * Menangani pengiriman data yang diperbarui ke database.
     */
    @FXML
    private void handleUpdate() {
        if (namaField.getText().isEmpty() || noHpField.getText().isEmpty() || alamatArea.getText().isEmpty()) {
            AlertHelper.showInfo("Semua field harus diisi");
            return;
        }

        Pelanggan selected = pelangganTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        Pelanggan baru = new Pelanggan(namaField.getText(), noHpField.getText(), alamatArea.getText());
        try {
            AlertHelper.showConfirm("Apakah Anda yakin ingin mengupdate pelanggan ini?", () -> {
                try {
                    pelangganService.update(selected, baru);
                    formManager.reset();
                    AlertHelper.showInfo("Pelanggan berhasil diupdate");
                } catch (SQLException e) {
                    AlertHelper.showError("Gagal Mengupdate pelanggan", e.getMessage());
                }
            });
        } catch (Exception e) {
            AlertHelper.showError("Error", "Terjadi kesalahan sistem: " + e.getMessage());
        }
    }

    /**
     * Memasuki mode edit untuk pelanggan yang sedang dipilih.
     */
    @FXML
    private void handleEdit() {
        if (pelangganTable.getSelectionModel().getSelectedItem() == null) {
            AlertHelper.showInfo("Pilih pelanggan terlebih dahulu");
            return;
        }

        Pelanggan selected = pelangganTable.getSelectionModel().getSelectedItem();
        idField.setText(String.format("CST-%03d", selected.getId()));
        namaField.setText(selected.getNama());
        noHpField.setText(selected.getNoHp());
        alamatArea.setText(selected.getAlamat());

        formManager.startEdit();
    }

    /**
     * Menutup formulir dan membatalkan semua perubahan yang belum disimpan.
     */
    @FXML
    private void handleCancel() {
        formManager.cancel();
        idField.clear();
    }
}
