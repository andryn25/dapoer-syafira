package id.syafira.dapoer.repository;

import id.syafira.dapoer.model.MenuMasakan;
import javafx.collections.ObservableList;

import java.sql.SQLException;

/**
 * Interface untuk mengelola persistensi data menu masakan.
 * Menggunakan ObservableList agar perubahan data dapat langsung dipantau oleh
 * komponen UI JavaFX.
 */
public interface MenuIMasakanRepository {

    /**
     * Mengambil seluruh daftar menu masakan dari database.
     * 
     * @return ObservableList berisi objek MenuMasakan.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    ObservableList<MenuMasakan> findAll() throws SQLException;

    /**
     * Menyimpan data menu masakan baru.
     * 
     * @param menu Objek MenuMasakan yang akan disimpan.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    void save(MenuMasakan menu) throws SQLException;

    /**
     * Memperbarui data menu masakan yang sudah ada.
     * 
     * @param menu Objek MenuMasakan dengan data yang telah diubah.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    void update(MenuMasakan menu) throws SQLException;

    /**
     * Menghapus menu masakan berdasarkan ID.
     * 
     * @param idMasakan ID unik masakan yang akan dihapus.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    void delete(int idMasakan) throws SQLException;
}
