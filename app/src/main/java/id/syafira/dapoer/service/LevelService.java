package id.syafira.dapoer.service;

import id.syafira.dapoer.model.Level;
import id.syafira.dapoer.repository.JdbcLevelRepository;
import id.syafira.dapoer.repository.LevelRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;

/**
 * Layanan untuk mengelola logika bisnis yang berkaitan dengan Level User.
 * Menggunakan pola Singleton untuk memastikan hanya ada satu instance layanan
 * di seluruh aplikasi.
 * Terhubung dengan UI secara reaktif melalui ObservableList.
 */
public class LevelService {

    private static LevelService instance;

    /**
     * Mengambil instance tunggal dari LevelService.
     * 
     * @return Instance LevelService.
     */
    public static LevelService getInstance() {
        if (instance == null) {
            instance = new LevelService();
        }
        return instance;
    }

    // List yang dipantau oleh UI JavaFX untuk pembaruan data otomatis
    private final ObservableList<Level> data = FXCollections.observableArrayList();

    private final LevelRepository repo = new JdbcLevelRepository();

    /**
     * Konstruktor privat untuk Singleton.
     * Memuat data awal dari database saat layanan pertama kali dibuat.
     */
    private LevelService() {
        try {
            data.setAll(repo.findAll());
        } catch (SQLException e) {
            // Mencatat kesalahan ke log sistem jika gagal memuat data awal
            e.printStackTrace();
        }
    }

    /**
     * Mendapatkan daftar level yang tersimpan dalam memori (cache).
     * 
     * @return ObservableList berisi objek Level.
     */
    public ObservableList<Level> getAll() {
        return data;
    }

    /**
     * Memperbarui cache data dalam memori dengan data terbaru dari database.
     */
    public void refresh() {
        try {
            data.setAll(repo.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Menambahkan level baru melalui repositori dan memperbarui daftar di memori.
     * 
     * @param levelUser Objek level baru.
     * @throws SQLException Jika gagal operasi database.
     */
    public void add(Level levelUser) throws SQLException {
        repo.insert(levelUser);
        data.add(levelUser);
    }

    /**
     * Menghapus level dari database dan dari daftar di memori.
     * 
     * @param levelUser Objek level yang akan dihapus.
     * @throws SQLException Jika gagal operasi database.
     */
    public void delete(Level levelUser) throws SQLException {
        repo.deleteById(levelUser.getIdLevel());
        data.remove(levelUser);
    }

    /**
     * Memperbarui data level yang sudah ada.
     * 
     * @param oldLevelUser Objek level lama.
     * @param newLevelUser Objek level dengan data baru.
     * @throws SQLException Jika gagal operasi database.
     */
    public void update(Level oldLevelUser, Level newLevelUser) throws SQLException {
        newLevelUser.setIdLevel(oldLevelUser.getIdLevel());
        repo.update(newLevelUser);
        int idx = data.indexOf(oldLevelUser);
        if (idx >= 0) {
            data.set(idx, newLevelUser);
        }
    }
}