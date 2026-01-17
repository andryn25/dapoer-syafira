package id.syafira.dapoer.controller;

import id.syafira.dapoer.model.MenuMasakan;
import id.syafira.dapoer.service.MenuMasakanService;
import id.syafira.dapoer.util.FormManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import id.syafira.dapoer.util.AlertHelper;
import java.sql.SQLException;

/**
 * Kontroler untuk mengelola Master Data Menu Masakan.
 * Mengatur katalog makanan, harga, status ketersediaan, dan stok.
 * Mendukung format ID khusus (MNU-xxx) dan input angka via Spinner.
 */
public class MenuItemController {

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
    private TableView<MenuMasakan> menuTable;
    @FXML
    private TableColumn<MenuMasakan, String> idColumn;
    @FXML
    private TableColumn<MenuMasakan, String> namaColumn;
    @FXML
    private TableColumn<MenuMasakan, Number> hargaColumn;
    @FXML
    private TableColumn<MenuMasakan, String> statusColumn;
    @FXML
    private TableColumn<MenuMasakan, Number> stokColumn;

    @FXML
    private TextField idField;
    @FXML
    private TextField namaField;
    @FXML
    private TextField hargaField;
    @FXML
    private Spinner<Integer> stokSpinner;
    @FXML
    private ComboBox<String> statusCombo;

    private final MenuMasakanService menuItemService = MenuMasakanService.getInstance();
    private FormManager formManager; // Pengendali status antarmuka form

    /**
     * Inisialisasi awal UI Menu Masakan.
     * Mengatur formatting kolom, opsi ComboBox, dan validasi Spinner.
     */
    @FXML
    public void initialize() {
        // Formatting ID agar tampil dengan prefix (contoh: MNU-001)
        idColumn.setCellValueFactory(cd -> new SimpleStringProperty(String.format("MNU-%03d", cd.getValue().getId())));
        namaColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        hargaColumn.setCellValueFactory(new PropertyValueFactory<>("harga"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        stokColumn.setCellValueFactory(new PropertyValueFactory<>("stok"));

        // Menyiapkan FormManager untuk mengontrol ketersediaan tombol
        formManager = new FormManager(newBtn, saveBtn, updateBtn, deleteBtn, editBtn, menuTable,
                namaField, hargaField, stokSpinner, statusCombo);

        // Mengisi pilihan status ketersediaan menu
        statusCombo.getItems().addAll("Tersedia", "Habis", "Tidak Aktif");

        // Mengonfigurasi Spinner untuk input angka stok (0 - 1000)
        SpinnerValueFactory<Integer> stokFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0);
        stokSpinner.setValueFactory(stokFactory);

        idField.disableProperty().set(true); // ID tidak boleh diedit manual

        // Sinkronisasi data dengan tabel
        menuTable.setItems(menuItemService.getAll());
        menuTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> showDetail(newSel));

        formManager.reset(); // Mengatur form ke mode siap pakai
        menuItemService.refresh();
    }

    /**
     * Menampilkan rincian menu yang dipilih ke bilah input.
     */
    private void showDetail(MenuMasakan m) {
        if (m == null) {
            idField.clear();
            namaField.clear();
            hargaField.clear();
            stokSpinner.getValueFactory().setValue(0);
            statusCombo.getSelectionModel().clearSelection();
        } else {
            idField.setText(String.format("MNU-%03d", m.getId()));
            namaField.setText(m.getNama());
            hargaField.setText(String.valueOf(m.getHarga()));
            stokSpinner.getValueFactory().setValue(m.getStok());
            statusCombo.setValue(m.getStatus());
            formManager.onSelection(); // Aktivasi tombol edit/hapus
        }
    }

    /**
     * Menyiapkan form untuk memasukkan data menu masakan baru.
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
        hargaField.clear();
        stokSpinner.getValueFactory().setValue(0);
        statusCombo.getSelectionModel().clearSelection();
    }

    /**
     * Menyimpan data menu masakan baru.
     */
    @FXML
    private void handleSave() {
        // Validasi input wajib
        if (namaField.getText().isEmpty() || hargaField.getText().isEmpty() || statusCombo.getValue() == null) {
            AlertHelper.showInfo("Semua field harus diisi!");
            return;
        }

        MenuMasakan m = new MenuMasakan();
        m.setNama(namaField.getText());
        m.setHarga(Long.parseLong(hargaField.getText()));
        m.setStatus(statusCombo.getValue());
        m.setStok(stokSpinner.getValue());

        try {
            AlertHelper.showConfirm("Apakah Anda yakin ingin menambahkan menu ini?", () -> {
                try {
                    menuItemService.add(m);
                    formManager.reset();
                    AlertHelper.showInfo("Data berhasil disimpan");
                } catch (SQLException e) {
                    e.printStackTrace();
                    AlertHelper.showError("Gagal menyimpan data", e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Mengaktifkan mode edit untuk rincian menu yang sedang dipilih.
     */
    @FXML
    private void handleEdit() {
        MenuMasakan selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showInfo("Pilih data yang akan diedit");
            return;
        }

        idField.setText(String.format("MNU-%03d", selected.getId()));
        namaField.setText(selected.getNama());
        hargaField.setText(String.valueOf(selected.getHarga()));
        stokSpinner.getValueFactory().setValue(selected.getStok());
        statusCombo.setValue(selected.getStatus());

        formManager.startEdit();
    }

    /**
     * Mengirimkan perubahan data menu ke database.
     */
    @FXML
    private void handleUpdate() {
        MenuMasakan selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        if (namaField.getText().isEmpty() || hargaField.getText().isEmpty() || statusCombo.getValue() == null) {
            AlertHelper.showInfo("Semua field harus diisi!");
            return;
        }

        MenuMasakan baru = new MenuMasakan(namaField.getText(), Long.parseLong(hargaField.getText()),
                statusCombo.getValue(),
                Integer.parseInt(stokSpinner.getEditor().getText()));

        try {
            AlertHelper.showConfirm("Apakah Anda yakin ingin mengupdate data ini?", () -> {
                try {
                    menuItemService.update(selected, baru);
                    formManager.reset();
                    AlertHelper.showInfo("Data berhasil diupdate");
                } catch (SQLException e) {
                    e.printStackTrace();
                    AlertHelper.showError("Gagal update data", e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Menghapus menu masakan yang dipilih.
     */
    @FXML
    private void handleDelete() {
        MenuMasakan selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showInfo("Pilih data yang akan dihapus");
            return;
        }

        try {
            AlertHelper.showConfirm("Apakah Anda yakin ingin menghapus data ini?", () -> {
                try {
                    menuItemService.delete(selected);
                    formManager.reset();
                    AlertHelper.showInfo("Data berhasil dihapus");
                } catch (SQLException e) {
                    e.printStackTrace();
                    AlertHelper.showError("Gagal menghapus data", e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Membatalkan pengisian atau pengeditan data.
     */
    @FXML
    private void handleCancel() {
        formManager.cancel();
        idField.clear();
    }
}
