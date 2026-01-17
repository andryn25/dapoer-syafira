package id.syafira.dapoer.service;

import id.syafira.dapoer.model.DetailOrder;
import id.syafira.dapoer.model.MenuMasakan;
import id.syafira.dapoer.model.Order;
import id.syafira.dapoer.repository.JdbcOrderRepository;
import id.syafira.dapoer.repository.OrderRepository;
import java.sql.SQLException;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Layanan untuk mengelola proses pemesanan (Order).
 * Menangani logika "Keranjang Belanja" (Shopping Cart) di memori, validasi stok
 * secara real-time,
 * dan sinkronisasi data antara pesanan dengan stok menu yang tersedia.
 */
public class OrderService {

    private final OrderRepository orderRepository = new JdbcOrderRepository();

    private static OrderService instance;

    /**
     * Mengambil instance tunggal dari OrderService (Singleton).
     */
    public static OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    // Daftar item yang sedang dipesan (keranjang belanja)
    private final ObservableList<DetailOrder> items = FXCollections.observableArrayList();

    // Properti total harga yang dapat dipantau oleh UI secara reaktif
    private final LongProperty total = new SimpleLongProperty(0);

    /**
     * Mendapatkan daftar item yang ada di keranjang belanja saat ini.
     */
    public ObservableList<DetailOrder> getItems() {
        return items;
    }

    /**
     * Properti total harga (read-only) untuk diikat (bind) ke label UI.
     */
    public ReadOnlyLongProperty totalProperty() {
        return total;
    }

    /**
     * Memvalidasi apakah jumlah pesanan valid dan stok mencukupi.
     */
    public void validateStock(MenuMasakan menu, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Jumlah harus lebih dari 0");
        }
        if (qty > menu.getStok()) {
            throw new IllegalArgumentException("Stok tidak mencukupi. Tersedia: " + menu.getStok());
        }
    }

    /**
     * Menambahkan menu ke dalam keranjang belanja.
     * Jika menu sudah ada, maka jumlahnya akan ditambahkan.
     */
    public void addItem(MenuMasakan menu, int qty) {
        validateStock(menu, qty);

        // Mencari apakah menu yang sama sudah ada di keranjang
        DetailOrder existing = items.stream()
                .filter(d -> d.getMenu().getId() == menu.getId())
                .findFirst()
                .orElse(null);

        if (existing != null) {
            // Memperbarui kuantitas jika item sudah ada
            existing.setQty(existing.getQty() + qty);
            existing.setSubtotal(existing.getMenu().getHarga() * existing.getQty());
        } else {
            // Menambah baris baru jika item belum ada
            long subtotal = menu.getHarga() * qty;
            DetailOrder detail = new DetailOrder(menu, qty, subtotal);
            items.add(detail);
        }

        // Mengurangi stok lokal (di memori) agar UI tetap sinkron sebelum disimpan ke
        // DB
        menu.setStok(menu.getStok() - qty);
        hitungTotal();
    }

    /**
     * Menghapus item dari keranjang belanja dan mengembalikan stok lokal menu
     * tersebut.
     */
    public void removeItem(DetailOrder d) {
        // Mengembalikan stok ke objek menu di memori
        d.getMenu().setStok(d.getMenu().getStok() + d.getQty());

        items.remove(d);
        hitungTotal();
    }

    /**
     * Memperbarui jumlah pesanan untuk item yang sudah ada di keranjang.
     */
    public void updateItem(DetailOrder d, int newQty) {
        int diff = d.getQty() - newQty; // Perbedaan kuantitas untuk penyesuaian stok

        // Menyesuaikan stok menu di memori
        d.getMenu().setStok(d.getMenu().getStok() + diff);

        d.setQty(newQty);
        d.setSubtotal(d.getMenu().getHarga() * newQty);
        hitungTotal();
    }

    /**
     * Menghitung ulang total harga seluruh item di keranjang.
     */
    private void hitungTotal() {
        long sum = 0;
        for (DetailOrder d : items) {
            sum += d.getSubtotal();
        }
        total.set(sum);
    }

    /**
     * Menyingkronkan stok di daftar menu (TableView utama) dengan item yang ada di
     * keranjang.
     * Sangat penting jika daftar menu di-refresh dari database sementara keranjang
     * belum kosong.
     */
    public void syncWithCart(ObservableList<MenuMasakan> allMenu) {
        for (DetailOrder detail : items) {
            for (MenuMasakan menu : allMenu) {
                if (menu.getId() == detail.getMenu().getId()) {
                    // Menyesuaikan stok pada objek menu yang baru dimuat
                    menu.setStok(menu.getStok() - detail.getQty());
                    // Menjaga agar detail pesanan merujuk pada objek menu yang sama di UI
                    detail.setMenu(menu);
                    break;
                }
            }
        }
    }

    /**
     * Menyimpan data pesanan ke database melalui repositori.
     */
    public void saveOrder(Order order) throws SQLException {
        orderRepository.save(order, items);
    }

    /**
     * Menghapus pesanan berdasarkan ID.
     */
    public void deleteOrder(int orderId) throws SQLException {
        orderRepository.deleteOrder(orderId);
    }

    /**
     * Mengosongkan keranjang belanja.
     */
    public void clear() {
        items.clear();
        total.set(0);
    }

    /**
     * Mengambil daftar pesanan yang belum dibayar.
     */
    public java.util.List<Order> getUnpaidOrders() {
        try {
            return orderRepository.findUnpaidOrders();
        } catch (SQLException e) {
            e.printStackTrace();
            return java.util.List.of();
        }
    }

    /**
     * Mengambil rincian detail dari sebuah pesanan.
     */
    public javafx.collections.ObservableList<DetailOrder> getOrderDetails(int orderId) {
        try {
            return orderRepository.findDetailsByOrderId(orderId);
        } catch (SQLException e) {
            e.printStackTrace();
            return javafx.collections.FXCollections.observableArrayList();
        }
    }
}
