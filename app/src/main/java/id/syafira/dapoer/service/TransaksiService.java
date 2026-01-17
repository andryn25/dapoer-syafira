package id.syafira.dapoer.service;

import id.syafira.dapoer.model.Order;
import id.syafira.dapoer.model.Transaksi;
import id.syafira.dapoer.model.User;
import id.syafira.dapoer.repository.JdbcTransaksiRepository;
import id.syafira.dapoer.repository.TransaksiRepository;
import id.syafira.dapoer.Session;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Layanan untuk menangani proses final pembayaran atau transaksi.
 * Menghubungkan pesanan yang telah dibuat dengan proses pelunasan di database.
 */
public class TransaksiService {

    private final TransaksiRepository transaksiRepository = new JdbcTransaksiRepository();
    private final OrderService orderService = OrderService.getInstance();

    private static TransaksiService instance;

    /**
     * Mengambil instance tunggal dari TransaksiService (Singleton).
     */
    public static TransaksiService getInstance() {
        if (instance == null) {
            instance = new TransaksiService();
        }
        return instance;
    }

    /**
     * Membuat catatan transaksi baru berdasarkan pesanan yang sudah ada.
     * 
     * @param order      Objek Order yang akan dibayar.
     * @param totalBayar Total nominal yang harus dibayar.
     * @param uangBayar  Jumlah uang yang diberikan pelanggan.
     * @param kembalian  Jumlah uang kembalian.
     * @throws SQLException Jika terjadi kesalahan validasi pembayaran atau
     *                      database.
     */
    public void createTransaction(Order order, long totalBayar, long uangBayar, long kembalian) throws SQLException {
        // Validasi: memastikan uang yang dibayarkan cukup
        if (uangBayar < totalBayar) {
            throw new SQLException("Uang pembayaran kurang!");
        }

        // 1. Pastikan data Order sudah tersimpan di database sebelum membuat transaksi
        if (order.getIdOrder() == 0) {
            orderService.saveOrder(order);
        }

        // 2. Inisialisasi objek Transaksi
        User currentUser = Session.getCurrentUser(); // Mengambil user yang sedang login saat ini
        Transaksi transaksi = new Transaksi();
        transaksi.setOrder(order);
        transaksi.setUser(currentUser);
        transaksi.setTanggal(LocalDateTime.now());
        transaksi.setTotalBayar(totalBayar);

        // Catatan: uangBayar dan kembalian saat ini belum disimpan ke database
        // karena keterbatasan skema tabel saat ini. Namun bisa ditambahkan jika
        // diperlukan.

        // 3. Simpan data transaksi ke database
        transaksiRepository.save(transaksi);
    }

    /**
     * Mengambil seluruh riwayat transaksi yang pernah dilakukan.
     * 
     * @return Daftar transaksi.
     */
    public List<Transaksi> getAllTransactions() {
        try {
            return transaksiRepository.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
