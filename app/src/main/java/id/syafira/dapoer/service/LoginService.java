package id.syafira.dapoer.service;

import id.syafira.dapoer.model.User;
import id.syafira.dapoer.repository.JdbcUserRepository;
import id.syafira.dapoer.repository.UserRepository;

import java.sql.SQLException;

/**
 * Layanan khusus untuk menangani proses Login dan autentikasi pengguna.
 * Menggunakan pola Singleton untuk manajemen sesi login yang konsisten.
 */
public class LoginService {

    private static LoginService instance;

    /**
     * Mengambil instance tunggal dari LoginService.
     * 
     * @return Instance LoginService.
     */
    public static LoginService getInstance() {
        if (instance == null) {
            instance = new LoginService();
        }
        return instance;
    }

    private final UserRepository userRepo = new JdbcUserRepository();

    /**
     * Memvalidasi kredensial pengguna untuk masuk ke aplikasi.
     * 
     * @param username Nama pengguna yang dimasukkan.
     * @param password Kata sandi yang dimasukkan.
     * @return Objek User jika kredensial valid, null jika tidak valid.
     * @throws SQLException Jika terjadi gangguan koneksi database.
     */
    public User authenticate(String username, String password) throws SQLException {
        // Mencari user berdasarkan username melalui repositori
        User u = userRepo.findByUsername(username);

        if (u == null) {
            return null; // Username tidak ditemukan di database
        }

        // Membandingkan password yang dimasukkan dengan yang tersimpan
        // Catatan: Saat ini masih plain text, disarankan menggunakan hashing di masa
        // depan
        if (password.equals(u.getPassword())) {
            return u;
        }

        return null; // Password tidak cocok
    }
}
