package id.syafira.dapoer.util;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.util.Arrays;
import java.util.List;

/**
 * Kelas pengelola formulir (Form Manager) untuk menyinkronkan status komponen
 * UI.
 * Menangani pengaktifan/penonaktifan tombol dan pembersihan kolom input
 * berdasarkan aksi pengguna (New, Edit, Save, Cancel).
 */
public class FormManager {

    private final Button newBtn;
    private final Button saveBtn;
    private final Button updateBtn;
    private final Button deleteBtn;
    private final Button editBtn;
    private final TableView<?> table;
    private final List<Node> inputFields;

    private boolean isEditing = false;

    /**
     * Mengecek apakah formulir sedang dalam mode edit atau tambah data baru.
     * 
     * @return true jika sedang mengedit/menambah, false jika mode diam (idle).
     */
    public boolean isEditing() {
        return isEditing;
    }

    /**
     * Inisialisasi pengelola formulir dengan komponen UI terkait.
     * 
     * @param newBtn    Tombol "Baru" atau "Batal"
     * @param saveBtn   Tombol "Simpan" (untuk data baru)
     * @param updateBtn Tombol "Ubah" (untuk pembaruan data)
     * @param deleteBtn Tombol "Hapus"
     * @param editBtn   Tombol "Edit" (untuk memulakan mode ubah)
     * @param table     Tabel utama yang menampilkan data
     * @param inputs    Daftar komponen input (TextField, ComboBox, dll)
     */
    public FormManager(Button newBtn, Button saveBtn, Button updateBtn, Button deleteBtn, Button editBtn,
            TableView<?> table, Node... inputs) {
        this.newBtn = newBtn;
        this.saveBtn = saveBtn;
        this.updateBtn = updateBtn;
        this.deleteBtn = deleteBtn;
        this.editBtn = editBtn;
        this.table = table;
        this.inputFields = Arrays.asList(inputs);

        reset();
    }

    /**
     * Memulai proses penambahan data baru.
     * Membersihkan input dan mengatur status tombol ke "Cancel" dan "Save".
     */
    public void startNew() {
        isEditing = true;
        clearInputs();
        updateState();
        if (table != null)
            table.setDisable(true);

        // Aktifkan kolom input agar bisa diisi
        setInputsDisabled(false);

        // Sesuaikan status tombol
        newBtn.setText("Cancel");
        saveBtn.setDisable(false);
        deleteBtn.setDisable(true);
        updateBtn.setDisable(true);
        editBtn.setDisable(true);
    }

    /**
     * Memulai proses pengubahan data yang sudah ada.
     * Mengatur status tombol ke "Cancel" dan "Update".
     */
    public void startEdit() {
        isEditing = true;
        updateState();
        if (table != null)
            table.setDisable(true);

        setInputsDisabled(false);
        newBtn.setText("Cancel");
        saveBtn.setDisable(true); // Simpan hanya untuk data baru
        deleteBtn.setDisable(true);
        updateBtn.setDisable(false);
        editBtn.setDisable(true);
    }

    /**
     * Membatalkan operasi saat ini dan mengembalikan ke status awal.
     */
    public void cancel() {
        reset();
    }

    /**
     * Mereset formulir ke status awal (idle).
     * Menonaktifkan input dan membersihkan pilihan tabel.
     */
    public void reset() {
        isEditing = false;
        clearInputs();
        if (table != null) {
            table.setDisable(false);
            table.getSelectionModel().clearSelection();
        }
        setInputsDisabled(true);

        newBtn.setText("New");
        saveBtn.setDisable(true);
        updateBtn.setDisable(true);
        deleteBtn.setDisable(true);
        editBtn.setDisable(true); // Hanya aktif jika ada baris yang dipilih
    }

    /**
     * Dipanggil ketika sebuah baris di tabel dipilih.
     * Mengaktifkan tombol Edit dan Hapus jika tidak dalam mode pengeditan.
     */
    public void onSelection() {
        if (!isEditing) {
            editBtn.setDisable(false);
            deleteBtn.setDisable(false);
        }
    }

    /**
     * Membersihkan semua nilai dalam komponen input yang dikelola.
     */
    private void clearInputs() {
        for (Node n : inputFields) {
            if (n instanceof TextField)
                ((TextField) n).clear();
            if (n instanceof TextArea)
                ((TextArea) n).clear();
            if (n instanceof ComboBox) {
                ((ComboBox<?>) n).getSelectionModel().clearSelection();
                ((ComboBox<?>) n).setValue(null);
            }
            if (n instanceof Spinner) {
                // Untuk Spinner, bisa ditambahkan logika reset ke nilai minimal jika diperlukan
            }
        }
    }

    /**
     * Mengaktifkan atau menonaktifkan seluruh komponen input yang dikelola.
     * 
     * @param disabled true untuk menonaktifkan, false untuk mengaktifkan.
     */
    private void setInputsDisabled(boolean disabled) {
        for (Node n : inputFields) {
            n.setDisable(disabled);
        }
    }

    /**
     * Pembaruan status internal jika diperlukan (digunakan untuk logika tambahan di
     * masa depan).
     */
    private void updateState() {
        // Pembaruan status internal
    }
}
