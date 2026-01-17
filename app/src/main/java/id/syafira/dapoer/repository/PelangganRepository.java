package id.syafira.dapoer.repository;

import id.syafira.dapoer.model.Pelanggan;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface untuk mengelola persistensi data pelanggan.
 * Mendefinisikan kontrak operasi CRUD (Create, Read, Update, Delete) pada tabel
 * pelanggan.
 */
public interface PelangganRepository {

    /**
     * Mengambil daftar seluruh pelanggan yang terdaftar.
     * 
     * @return List berisi objek Pelanggan.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    List<Pelanggan> findAll() throws SQLException;

    /**
     * Mencari data pelanggan berdasarkan ID uniknya.
     * 
     * @param id ID pelanggan yang dicari.
     * @return Objek Pelanggan jika ditemukan, null jika tidak.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    Pelanggan findById(int id) throws SQLException;

    /**
     * Menambahkan data pelanggan baru ke database.
     * 
     * @param pelanggan Objek Pelanggan yang akan disimpan.
     * @return Objek Pelanggan yang telah disimpan (termasuk ID otomatis).
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    Pelanggan insert(Pelanggan pelanggan) throws SQLException;

    /**
     * Memperbarui informasi pelanggan yang sudah ada.
     * 
     * @param pelanggan Objek Pelanggan dengan data terbaru.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    void update(Pelanggan pelanggan) throws SQLException;

    /**
     * Menghapus data pelanggan berdasarkan ID.
     * 
     * @param id ID pelanggan yang akan dihapus.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    void deleteById(int id) throws SQLException;
}
