package id.syafira.dapoer.repository;

import id.syafira.dapoer.model.Transaksi;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface untuk mengelola persistensi data transaksi pembayaran.
 * Transaksi dicatat setelah pesanan (Order) dilunasi oleh pelanggan.
 */
public interface TransaksiRepository {

    /**
     * Menyimpan data transaksi pembayaran baru ke database.
     * 
     * @param transaksi Objek Transaksi yang akan disimpan.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    void save(Transaksi transaksi) throws SQLException;

    /**
     * Mengambil seluruh riwayat transaksi pembayaran.
     * 
     * @return List berisi daftar objek Transaksi.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    List<Transaksi> findAll() throws SQLException;
}
