package id.syafira.dapoer.service;

import id.syafira.dapoer.model.MenuMasakan;
import id.syafira.dapoer.repository.JdbcMenuMasakanRepository;
import id.syafira.dapoer.repository.MenuIMasakanRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;

/**
 * Layanan untuk mengelola logika bisnis yang berkaitan dengan Menu Masakan.
 * Mengelola daftar makanan, harga, dan ketersediaan stok dalam aplikasi.
 * Terintegrasi dengan UI melalui ObservableList untuk pembaruan data real-time.
 */
public class MenuMasakanService {

    private static MenuMasakanService instance;

    /**
     * Mengambil instance tunggal dari MenuMasakanService (Singleton).
     * 
     * @return Instance MenuMasakanService.
     */
    public static MenuMasakanService getInstance() {
        if (instance == null) {
            instance = new MenuMasakanService();
        }
        return instance;
    }

    private final MenuIMasakanRepository repo = new JdbcMenuMasakanRepository();

    // Daftar item menu yang dipantau oleh komponen UI seperti TableView
    private final ObservableList<MenuMasakan> items = FXCollections.observableArrayList();

    /**
     * Konstruktor privat. Memuat data menu dari database saat pertama kali diakses.
     */
    private MenuMasakanService() {
        try {
            items.setAll(repo.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mendapatkan daftar seluruh menu masakan yang tersimpan di memori.
     * 
     * @return ObservableList berisi objek MenuMasakan.
     */
    public ObservableList<MenuMasakan> getAll() {
        return items;
    }

    /**
     * Memperbarui daftar menu di memori dengan data terbaru dari database.
     */
    public void refresh() {
        try {
            items.setAll(repo.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Menambahkan menu masakan baru ke database dan daftar UI.
     * 
     * @param m Objek menu masakan baru.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public void add(MenuMasakan m) throws SQLException {
        repo.save(m);
        items.add(m);
    }

    /**
     * Memperbarui data menu masakan yang sudah ada.
     * 
     * @param oldM Data menu lama (sebagai acuan indeks).
     * @param newM Data menu baru yang akan disimpan.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public void update(MenuMasakan oldM, MenuMasakan newM) throws SQLException {
        newM.setId(oldM.getId());
        repo.update(newM);
        int idx = items.indexOf(oldM);
        if (idx >= 0) {
            items.set(idx, newM);
        }
    }

    /**
     * Menghapus menu masakan dari database dan daftar UI.
     * 
     * @param m Objek menu yang akan dihapus.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public void delete(MenuMasakan m) throws SQLException {
        repo.delete(m.getId());
        items.remove(m);
    }
}
