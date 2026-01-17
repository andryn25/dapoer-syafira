package id.syafira.dapoer.repository;

import id.syafira.dapoer.model.Level;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface untuk mengelola persistensi data objek Level.
 * Mendefinisikan kontrak operasi CRUD dasar untuk tabel level_user.
 */
public interface LevelRepository {

    /**
     * Mengambil semua data level dari database.
     * 
     * @return Daftar semua objek Level.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    List<Level> findAll() throws SQLException;

    /**
     * Mencari data level berdasarkan ID-nya.
     * 
     * @param idLevel ID level yang ingin dicari.
     * @return Objek Level jika ditemukan, null jika tidak ditemukan.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    Level findById(int idLevel) throws SQLException;

    /**
     * Menyimpan data level baru ke database.
     * 
     * @param level Objek level yang akan disimpan.
     * @return Objek level yang telah tersimpan (termasuk ID yang dihasilkan).
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    Level insert(Level level) throws SQLException;

    /**
     * Memperbarui data level yang sudah ada.
     * 
     * @param level Objek level dengan data terbaru.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    void update(Level level) throws SQLException;

    /**
     * Menghapus data level berdasarkan ID-nya.
     * 
     * @param idLevel ID level yang akan dihapus.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    void deleteById(int idLevel) throws SQLException;

}
