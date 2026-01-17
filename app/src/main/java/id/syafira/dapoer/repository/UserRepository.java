package id.syafira.dapoer.repository;

import id.syafira.dapoer.model.User;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface untuk mengelola persistensi data pengguna (User).
 * Menangani operasi CRUD pengguna serta pencarian berdasarkan username untuk
 * kebutuhan autentikasi.
 */
public interface UserRepository {

    /**
     * Mengambil daftar seluruh pengguna dari database.
     * 
     * @return List berisi objek User beserta informasi levelnya.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    List<User> findAll() throws SQLException;

    /**
     * Mencari data pengguna berdasarkan ID uniknya.
     * 
     * @param id ID pengguna yang dicari.
     * @return Objek User jika ditemukan, null jika tidak.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    User findById(int id) throws SQLException;

    /**
     * Menambahkan pengguna baru ke database.
     * 
     * @param user Objek User yang akan disimpan.
     * @return Objek User yang sudah tersimpan (lengkap dengan ID otomatis).
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    User insert(User user) throws SQLException;

    /**
     * Memperbarui informasi profil pengguna yang sudah ada.
     * 
     * @param user Objek User dengan data terbaru.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    void update(User user) throws SQLException;

    /**
     * Menghapus pengguna berdasarkan ID-nya.
     * 
     * @param id ID pengguna yang akan dihapus.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    void deleteById(int id) throws SQLException;

    /**
     * Mencari pengguna berdasarkan username. Sangat penting untuk proses Login.
     * 
     * @param username Username yang dicari.
     * @return Objek User yang sesuai, atau null jika tidak ditemukan.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    User findByUsername(String username) throws SQLException;
}
