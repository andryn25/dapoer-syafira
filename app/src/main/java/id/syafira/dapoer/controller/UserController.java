package id.syafira.dapoer.controller;

import id.syafira.dapoer.model.Level;
import id.syafira.dapoer.model.User;
import id.syafira.dapoer.service.LevelService;
import id.syafira.dapoer.service.UserService;
import id.syafira.dapoer.util.AlertHelper;
import id.syafira.dapoer.util.FormManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

/**
 * Kontroler untuk mengelola Master Data Pengguna (User).
 * Mengatur akun pengguna, kata sandi, dan hubungan level akses (Role).
 * Menggunakan ComboBox kustom dengan StringConverter untuk pilihan Level.
 */
public class UserController {

    @FXML
    private TextField idField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<Level> levelComboBox;

    // Tombol operasional
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

    // Tabel data pengguna
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> idColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> levelColumn;

    private final UserService userService = UserService.getInstance();
    private final LevelService levelService = LevelService.getInstance();
    private FormManager formManager; // Pengelola siklus hidup form

    /**
     * Inisialisasi awal UI Pengguna.
     * Mengatur tabel, memuat data User & Level, serta konfigurasi converter
     * ComboBox.
     */
    @FXML
    public void initialize() {
        // Pemetaan kolom tabel ke field model User
        idColumn.setCellValueFactory(
                celldata -> new SimpleStringProperty(String.format("USR-%03d", celldata.getValue().getId())));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));

        // Pemetaan kolom level dengan penanganan data null secara aman
        levelColumn.setCellValueFactory(cd -> {
            Level lvl = cd.getValue().getLevel();
            String text = (lvl == null) ? "" : lvl.getNamaLevel();
            return new SimpleStringProperty(text);
        });

        // Memuat data dari database
        userService.refresh();
        userTable.setItems(userService.getAllUsers());

        idField.disableProperty().set(true); // ID otomatis

        levelService.refresh();
        levelComboBox.setItems(levelService.getAll());

        // Mengubah objek Level menjadi String yang terbaca manusia di ComboBox
        levelComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Level lvl) {
                return lvl == null ? "" : lvl.getNamaLevel();
            }

            @Override
            public Level fromString(String s) {
                return null; // ComboBox tidak editable, jadi dikesampingkan
            }
        });

        // Setup FormManager untuk sinkronisasi tombol
        formManager = new FormManager(newBtn, saveBtn, updateBtn, deleteBtn, editBtn, userTable,
                usernameField, passwordField, nameField, levelComboBox);

        // Menampilkan detail saat pengguna memilih baris di tabel
        userTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> showDetail(newSel));

        formManager.reset();
    }

    /**
     * Mengisi kolom input berdasarkan pengguna yang dipilih.
     */
    private void showDetail(User u) {
        if (u == null) {
            idField.clear();
            usernameField.clear();
            passwordField.clear();
            nameField.clear();
            levelComboBox.getSelectionModel().clearSelection();
            levelComboBox.setPromptText("Pilih ID User");
        } else {
            idField.setText(String.format("USR-%03d", u.getId()));
            usernameField.setText(u.getUsername());
            passwordField.setText(u.getPassword());
            nameField.setText(u.getNama());
            levelComboBox.getSelectionModel().select(u.getLevel());
            formManager.onSelection();
        }
    }

    /**
     * Mengaktifkan mode pembuatan pengguna baru.
     */
    @FXML
    private void handleNew() {
        if (formManager.isEditing()) {
            handleCancel();
            return;
        }
        formManager.startNew();
        idField.clear();
        usernameField.clear();
        passwordField.clear();
        nameField.clear();
        levelComboBox.getSelectionModel().clearSelection();
    }

    /**
     * Menyimpan akun pengguna baru ke database.
     */
    @FXML
    private void handleSave() {
        // Validasi input data wajib
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty() ||
                nameField.getText().isEmpty() || levelComboBox.getSelectionModel().isEmpty()) {
            AlertHelper.showInfo("Semua field harus diisi");
            return;
        }

        User baru = new User(
                usernameField.getText(),
                passwordField.getText(),
                nameField.getText(),
                levelComboBox.getSelectionModel().getSelectedItem());

        try {
            AlertHelper.showConfirm("Apakah Anda yakin ingin menambahkan user ini?", () -> {
                try {
                    userService.addUser(baru);
                    formManager.reset();
                    AlertHelper.showInfo("User berhasil ditambahkan");
                } catch (Exception e) {
                    AlertHelper.showError("Gagal menambahkan user", e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Menghapus akun pengguna dari sistem.
     */
    @FXML
    private void handleDelete() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showInfo("Pilih user terlebih dahulu");
            return;
        }
        try {
            AlertHelper.showConfirm("Apakah Anda yakin ingin menghapus user ini?", () -> {
                try {
                    userService.deleteUser(selected);
                    formManager.reset();
                    AlertHelper.showInfo("User berhasil dihapus");
                } catch (Exception e) {
                    AlertHelper.showError("Gagal menghapus user", e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Memasuki mode edit untuk akun yang dipilih.
     */
    @FXML
    private void handleEdit() {
        if (userTable.getSelectionModel().getSelectedItem() == null) {
            AlertHelper.showInfo("Pilih user terlebih dahulu");
            return;
        }

        User selected = userTable.getSelectionModel().getSelectedItem();
        idField.setText(String.format("USR-%03d", selected.getId()));
        usernameField.setText(selected.getUsername());
        passwordField.setText(selected.getPassword());
        nameField.setText(selected.getNama());
        levelComboBox.getSelectionModel().select(selected.getLevel());

        formManager.startEdit();
    }

    /**
     * Mengirim perubahan data akun pengguna ke database.
     */
    @FXML
    private void handleUpdate() {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty() ||
                nameField.getText().isEmpty() || levelComboBox.getValue() == null) {
            AlertHelper.showInfo("Semua field harus diisi");
            return;
        }

        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        User baru = new User(
                usernameField.getText(),
                passwordField.getText(),
                nameField.getText(),
                levelComboBox.getValue());

        try {
            AlertHelper.showConfirm("Apakah Anda yakin ingin mengupdate user ini?", () -> {
                try {
                    userService.updateUser(selected, baru);
                    formManager.reset();
                    AlertHelper.showInfo("User berhasil diupdate");
                } catch (Exception e) {
                    AlertHelper.showError("Gagal mengupdate user", e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Membatalkan manajemen user dan mengosongkan input.
     */
    @FXML
    private void handleCancel() {
        formManager.cancel();
        idField.clear();
    }
}
