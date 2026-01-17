package id.syafira.dapoer.repository;

import id.syafira.dapoer.model.Order;
import id.syafira.dapoer.model.DetailOrder;
import javafx.collections.ObservableList;

import java.sql.SQLException;

/**
 * Interface untuk mengelola persistensi data pesanan (Order).
 * Menangani pembuatan pesanan baru beserta detailnya, serta pengambilan data
 * pesanan yang belum dibayar.
 */
public interface OrderRepository {
    /**
     * Menyimpan data pesanan baru beserta daftar item detail pesanan.
     * Operasi ini harus bersifat transaksional (all or nothing).
     * 
     * @param order Objek Order yang akan disimpan.
     * @param items Daftar detail item pesanan.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    void save(Order order, ObservableList<DetailOrder> items) throws SQLException;

    /**
     * Mengambil seluruh data pesanan dari database.
     * 
     * @return List berisi objek Order.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    java.util.List<Order> findAll() throws SQLException;

    /**
     * Mengambil daftar pesanan yang belum dilakukan pembayaran.
     * 
     * @return List berisi objek Order yang belum lunas.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    java.util.List<Order> findUnpaidOrders() throws SQLException;

    /**
     * Mengambil detail item untuk sebuah ID pesanan tertentu.
     * 
     * @param orderId ID pesanan yang dicari detailnya.
     * @return ObservableList berisi DetailOrder.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    javafx.collections.ObservableList<DetailOrder> findDetailsByOrderId(int orderId) throws SQLException;

    /**
     * Menghapus data pesanan dan detailnya secara permanen.
     * 
     * @param orderId ID pesanan yang akan dihapus.
     * @throws SQLException Jika terjadi kesalahan akses database.
     */
    void deleteOrder(int orderId) throws SQLException;
}
