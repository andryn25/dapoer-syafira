package id.syafira.dapoer.service;

import java.sql.SQLException;

import id.syafira.dapoer.model.Level;
import id.syafira.dapoer.model.User;
import id.syafira.dapoer.repository.JdbcUserRepository;
import id.syafira.dapoer.repository.UserRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

/**
 * Layanan untuk mengelola data Pengguna (User) dan hak aksesnya.
 * Menyediakan sinkronisasi antara database dan daftar pengguna di UI,
 * serta fitur penyaringan berdasarkan peran (role).
 */
public class UserService {

    private static UserService instance;

    /**
     * Mengambil instance tunggal dari UserService (Singleton).
     */
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    // Daftar utama seluruh pengguna yang tersimpan di memori
    private final ObservableList<User> users = FXCollections.observableArrayList();

    private final UserRepository repo = new JdbcUserRepository();

    /**
     * Konstruktor privat. Memuat seluruh data pengguna saat layanan pertama kali
     * diinisialisasi.
     */
    private UserService() {
        try {
            users.setAll(repo.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mendapatkan daftar seluruh pengguna.
     */
    public ObservableList<User> getAllUsers() {
        return users;
    }

    /**
     * Memperbarui cache data pengguna dari database.
     */
    public void refresh() {
        try {
            users.setAll(repo.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mendapatkan tampilan (view) pengguna yang disaring berdasarkan peran
     * tertentu.
     * Menggunakan FilteredList sehingga perubahan pada daftar utama otomatis
     * tercermin di sini.
     * 
     * @param level Objek Level sebagai kriteria penyaringan.
     * @return FilteredList berisi pengguna dengan role yang sesuai.
     */
    public FilteredList<User> getUsersByRole(Level level) {
        return new FilteredList<>(users, u -> u.getLevel().equals(level));
    }

    /**
     * Menambah pengguna baru.
     */
    public void addUser(User user) throws Exception {
        repo.insert(user);
        users.add(user);
    }

    /**
     * Memperbarui informasi pengguna.
     */
    public void updateUser(User oldUser, User newUser) throws Exception {
        newUser.setId(oldUser.getId());
        repo.update(newUser);
        int idx = users.indexOf(oldUser);
        if (idx >= 0) {
            users.set(idx, newUser);
        }
    }

    /**
     * Menghapus pengguna.
     */
    public void deleteUser(User user) throws Exception {
        repo.deleteById(user.getId());
        users.remove(user);
    }
}
