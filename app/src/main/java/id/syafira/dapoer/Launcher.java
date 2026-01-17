package id.syafira.dapoer;

/**
 * Kelas Peluncur (Launcher) Utama.
 * Digunakan sebagai titik masuk alternatif guna menghindari masalah
 * kompatibilitas
 * JavaFX Module System (modularity) pada environment tertentu.
 */
public class Launcher {
    public static void main(String[] args) {
        // Mengatur preloader yang akan digunakan saat aplikasi start
        System.setProperty("javafx.preloader", "id.syafira.dapoer.preloader.AppPreloader");

        // Memastikan aplikasi tidak langsung tertutup saat preloader selesai
        javafx.application.Platform.setImplicitExit(false);

        // Memanggil main method pada class Application utama
        DapoerSyafiraApp.main(args);
    }
}
