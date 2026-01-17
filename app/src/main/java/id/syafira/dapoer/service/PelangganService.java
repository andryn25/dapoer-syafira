package id.syafira.dapoer.service;

import java.sql.SQLException;

import id.syafira.dapoer.model.Pelanggan;
import id.syafira.dapoer.repository.PelangganRepository;
import id.syafira.dapoer.repository.JdbcPelangganRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Layanan untuk mengelola data pelanggan.
 * Bertanggung jawab atas logika bisnis terkait pendaftaran dan pembaruan
 * informasi pelanggan.
 */
public class PelangganService {

    private static PelangganService instance;

    /**
     * Mengambil instance tunggal dari PelangganService (Singleton).
     * 
     * @return Instance PelangganService.
     */
    public static PelangganService getInstance() {
        if (instance == null) {
            instance = new PelangganService();
        }
        return instance;
    }

    // Cache data pelanggan dalam memori untuk mempercepat respon UI
    private final ObservableList<Pelanggan> data = FXCollections.observableArrayList();

    private final PelangganRepository repo = new JdbcPelangganRepository();

    /**
     * Konstruktor privat. Memuat data pelanggan dari database saat pertama kali
     * dijalankan.
     */
    private PelangganService() {
        try {
            data.setAll(repo.findAll());
        } catch (SQLException e) {
            // Bisa ditingkatkan dengan logging atau dialog error global
            e.printStackTrace();
        }
    }

    /**
     * Mendapatkan daftar seluruh pelanggan yang ada di cache memori.
     * 
     * @return ObservableList berisi objek Pelanggan.
     */
    public ObservableList<Pelanggan> getAll() {
        return data;
    }

    /**
     * Sinkronisasi ulang data di memori dengan data terbaru dari database.
     */
    public void refresh() {
        try {
            data.setAll(repo.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mendaftarkan pelanggan baru.
     * 
     * @param p Objek pelanggan baru.
     * @throws SQLException Jika terjadi kesalahan pada database.
     */
    public void add(Pelanggan p) throws SQLException {
        repo.insert(p);
        data.add(p);
    }

    /**
     * Menghapus data pelanggan.
     * 
     * @param p Objek pelanggan yang akan dihapus.
     * @throws SQLException Jika terjadi kesalahan pada database.
     */
    public void delete(Pelanggan p) throws SQLException {
        repo.deleteById(p.getId());
        data.remove(p);
    }

    /**
     * Memperbarui detail informasi pelanggan.
     * 
     * @param oldP Data lama pelanggan.
     * @param newP Data baru pelanggan.
     * @throws SQLException Jika terjadi kesalahan pada database.
     */
    public void update(Pelanggan oldP, Pelanggan newP) throws SQLException {
        newP.setId(oldP.getId());
        repo.update(newP);
        int idx = data.indexOf(oldP);
        if (idx >= 0) {
            data.set(idx, newP);
        }
    }
}
