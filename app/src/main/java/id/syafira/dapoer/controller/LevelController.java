package id.syafira.dapoer.controller;

import java.sql.SQLException;
import id.syafira.dapoer.model.Level;
import id.syafira.dapoer.service.LevelService;
import id.syafira.dapoer.util.FormManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import id.syafira.dapoer.util.AlertHelper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Kontroler untuk mengelola Master Data Level User.
 * Mengatur antarmuka untuk menambah, mengubah, dan menghapus level akses
 * pengguna.
 * Menggunakan FormManager untuk menyederhanakan pengelolaan status tombol dan
 * input.
 */
public class LevelController {
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
    private TextField idLevelField;
    @FXML
    private TextField namaLevelField;

    @FXML
    private TableView<Level> levelTable;
    @FXML
    private TableColumn<Level, Integer> idColumn;
    @FXML
    private TableColumn<Level, String> namaColumn;

    private final LevelService levelService = LevelService.getInstance();
    private FormManager formManager; // Pembantu manajemen status form

    /**
     * Inisialisasi awal UI Level.
     * Menghubungkan kolom tabel dengan model, memuat data, dan mengatur listener
     * seleksi.
     */
    @FXML
    private void initialize() {
        // Pemetaan kolom tabel ke properti model Level
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idLevel"));
        namaColumn.setCellValueFactory(new PropertyValueFactory<>("namaLevel"));

        // Memuat data terbaru dari database
        levelService.refresh();
        levelTable.setItems(levelService.getAll());

        idLevelField.disableProperty().set(true); // ID diatur otomatis oleh database

        // Inisialisasi pengelola formulir (FormManager)
        formManager = new FormManager(newBtn, saveBtn, updateBtn, deleteBtn, editBtn, levelTable,
                namaLevelField);

        // Menambahkan listener untuk menampilkan detail saat baris tabel dipilih
        levelTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> showDetail(newSel));

        formManager.reset(); // Mengembalikan form ke kondisi awal
    }

    /**
     * Menampilkan data level yang dipilih ke dalam field input.
     */
    private void showDetail(Level l) {
        if (l == null) {
            idLevelField.clear();
            namaLevelField.clear();
        } else {
            idLevelField.setText(Integer.toString(l.getIdLevel()));
            namaLevelField.setText(l.getNamaLevel());
            formManager.onSelection(); // Memberitahu form manager bahwa ada item yang dipilih
        }
    }

    /**
     * Menangani aksi klik tombol 'Baru'.
     * Mengosongkan form dan menyiapkan input untuk data baru.
     */
    @FXML
    private void handleNew() {
        if (formManager.isEditing()) {
            handleCancel();
            return;
        }
        formManager.startNew();
        idLevelField.clear();
        namaLevelField.clear();
    }

    /**
     * Menangani aksi simpan data level baru.
     */
    @FXML
    private void handleSave() {
        if (namaLevelField.getText().isEmpty()) {
            AlertHelper.showInfo("Nama level harus diisi");
            return;
        }

        Level lvl = new Level();
        lvl.setNamaLevel(namaLevelField.getText());

        try {
            // Meminta konfirmasi sebelum menyimpan
            AlertHelper.showConfirm("Apakah Anda yakin ingin menambahkan level ini?", () -> {
                try {
                    levelService.add(lvl);
                    formManager.reset();
                    AlertHelper.showInfo("Level berhasil ditambahkan");
                } catch (SQLException e) {
                    e.printStackTrace();
                    AlertHelper.showError("Gagal menyimpan level", e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Menangani aksi penghapusan data level.
     */
    @FXML
    private void handleDelete() {
        Level selected = levelTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showInfo("Pilih level terlebih dahulu");
            return;
        }
        try {
            AlertHelper.showConfirm("Apakah Anda yakin ingin menghapus level ini?", () -> {
                try {
                    levelService.delete(selected);
                    formManager.reset();
                    AlertHelper.showInfo("Level berhasil dihapus");
                } catch (SQLException e) {
                    e.printStackTrace();
                    AlertHelper.showError("Gagal menghapus level", e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Mempersiapkan form untuk mode pengubahan (Edit).
     */
    @FXML
    private void handleEdit() {
        if (levelTable.getSelectionModel().getSelectedItem() == null) {
            AlertHelper.showInfo("Pilih level terlebih dahulu");
            return;
        }

        Level selected = levelTable.getSelectionModel().getSelectedItem();
        idLevelField.setText(Integer.toString(selected.getIdLevel()));
        namaLevelField.setText(selected.getNamaLevel());

        formManager.startEdit();
    }

    /**
     * Menangani aksi pembaruan (Update) data level yang sudah ada.
     */
    @FXML
    private void handleUpdate() {
        if (namaLevelField.getText().isEmpty()) {
            AlertHelper.showInfo("Nama level harus diisi");
            return;
        }

        Level selected = levelTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        Level baru = new Level(namaLevelField.getText());

        try {
            AlertHelper.showConfirm("Apakah Anda yakin ingin mengubah level ini?", () -> {
                try {
                    levelService.update(selected, baru);
                    formManager.reset();
                    AlertHelper.showInfo("Level berhasil diubah");
                } catch (SQLException e) {
                    e.printStackTrace();
                    AlertHelper.showError("Gagal mengubah level", e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Membatalkan aksi pengeditan/input baru dan mengembalikan form ke kondisi
     * awal.
     */
    @FXML
    private void handleCancel() {
        formManager.cancel();
        idLevelField.clear();
    }
}