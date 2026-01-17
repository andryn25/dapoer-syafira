package id.syafira.dapoer;

import id.syafira.dapoer.model.User;

/**
 * Kelas Pengelola Sesi (Session Manager) aplikasi.
 * Bertugas menyimpan informasi pengguna yang sedang login secara global.
 * Digunakan oleh modul lain untuk pengecekan hak akses dan identitas kasir.
 */
public class Session {

    private static User currentUser; // Objek user yang sedang aktif

    /**
     * Menyimpan data pengguna ke dalam sesi setelah login berhasil.
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Mengambil data pengguna yang saat ini sedang aktif di aplikasi.
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Pintasan untuk mengambil ID unik dari pengguna yang sedang login.
     */
    public static int getCurrentUserId() {
        return currentUser.getId();
    }

    /**
     * Memeriksa apakah ada pengguna yang sedang login saat ini.
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Menghapus sesi (logout) dengan mengosongkan data pengguna.
     */
    public static void logout() {
        currentUser = null;
    }
}
