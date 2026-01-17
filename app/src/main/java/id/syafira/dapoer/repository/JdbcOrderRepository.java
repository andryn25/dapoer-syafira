package id.syafira.dapoer.repository;

import id.syafira.dapoer.db.DatabaseHelper;
import id.syafira.dapoer.model.DetailOrder;
import id.syafira.dapoer.model.Order;

import java.sql.*;
import javafx.collections.ObservableList;

/**
 * Implementasi JDBC untuk OrderRepository.
 * Mengelola data pesanan yang kompleks yang melibatkan tabel order,
 * detail_order, dan pembaruan stok menu.
 */
public class JdbcOrderRepository implements OrderRepository {

    // Kueri SQL untuk operasi pesanan
    private static final String SQL_INSERT_ORDER = "INSERT INTO `order` " +
            "(id_user, id_pelanggan, tgl_pesan, tgl_kirim, jenis_pesanan, total, catatan) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_DETAIL = "INSERT INTO detail_order " +
            "(id_order, id_masakan, qty, subtotal) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE_STOCK = "UPDATE menu_masakan SET stok = stok - ? WHERE id_masakan = ?";

    // Kueri dasar untuk mengambil data pesanan dengan join ke user dan pelanggan
    private static final String SQL_SELECT_BASE = "SELECT o.id_order, o.tgl_pesan, o.tgl_kirim, o.jenis_pesanan, o.total, o.catatan, "
            +
            "u.id_user as u_id, u.username as u_username, u.nama_user as u_nama, " +
            "p.id_pelanggan as p_id, p.nama as p_nama " +
            "FROM `order` o " +
            "JOIN user u ON o.id_user = u.id_user " +
            "JOIN pelanggan p ON o.id_pelanggan = p.id_pelanggan ";

    private static final String SQL_FIND_ALL = SQL_SELECT_BASE + "ORDER BY o.tgl_pesan DESC";

    // Kueri untuk mencari pesanan yang belum memiliki catatan transaksi (belum
    // dibayar)
    private static final String SQL_FIND_UNPAID = SQL_SELECT_BASE +
            "LEFT JOIN transaksi t ON o.id_order = t.id_order " +
            "WHERE t.id_transaksi IS NULL " +
            "ORDER BY o.tgl_pesan DESC";

    private static final String SQL_FIND_DETAILS = "SELECT d.qty, d.subtotal, m.id_masakan, m.nama_masakan, m.harga, m.stok, m.status_masakan "
            +
            "FROM detail_order d " +
            "JOIN menu_masakan m ON d.id_masakan = m.id_masakan " +
            "WHERE d.id_order = ?";

    private static final String SQL_GET_DETAILS_FOR_DELETE = "SELECT id_masakan, qty FROM detail_order WHERE id_order = ?";
    private static final String SQL_RESTORE_STOCK = "UPDATE menu_masakan SET stok = stok + ? WHERE id_masakan = ?";
    private static final String SQL_DELETE_DETAILS = "DELETE FROM detail_order WHERE id_order = ?";
    private static final String SQL_DELETE_ORDER = "DELETE FROM `order` WHERE id_order = ?";

    @Override
    public void save(Order order, ObservableList<DetailOrder> items) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection()) {
            // Memulai transaksi database agar semua langkah (insert order, detail, update
            // stok) sukses bersamaan
            conn.setAutoCommit(false);

            try (PreparedStatement psOrder = conn.prepareStatement(SQL_INSERT_ORDER, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement psDetail = conn.prepareStatement(SQL_INSERT_DETAIL);
                    PreparedStatement psUpdateStok = conn.prepareStatement(SQL_UPDATE_STOCK)) {

                // 1) Masukkan data ke tabel `order`
                psOrder.setInt(1, order.getUser().getId());
                psOrder.setInt(2, order.getPelanggan().getId());
                psOrder.setTimestamp(3, Timestamp.valueOf(order.getTanggal()));
                psOrder.setTimestamp(4,
                        order.getTanggalKirim() == null ? null : Timestamp.valueOf(order.getTanggalKirim()));
                psOrder.setString(5, order.getJenisPesanan());
                psOrder.setLong(6, order.getTotal());
                psOrder.setString(7, order.getCatatan());

                psOrder.executeUpdate();

                // 2) Ambil ID order yang baru saja dihasilkan (AUTO_INCREMENT)
                try (ResultSet rsKeys = psOrder.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        order.setIdOrder(rsKeys.getInt(1));
                    } else {
                        throw new SQLException("Gagal mendapatkan ID order baru.");
                    }
                }

                // 3) Masukkan data ke detail_order dan perbarui stok menu secara BATCH
                for (DetailOrder d : items) {
                    int idMasakan = d.getMenu().getId();
                    int qty = d.getQty();

                    // Tambahkan ke batch insert detail
                    psDetail.setInt(1, order.getIdOrder());
                    psDetail.setInt(2, idMasakan);
                    psDetail.setInt(3, qty);
                    psDetail.setLong(4, d.getSubtotal());
                    psDetail.addBatch();

                    // Tambahkan ke batch update stok (mengurangi stok yang ada)
                    psUpdateStok.setInt(1, qty);
                    psUpdateStok.setInt(2, idMasakan);
                    psUpdateStok.addBatch();
                }
                psDetail.executeBatch();
                psUpdateStok.executeBatch();

                // 4) Melakukan Commit jika semua langkah di atas berhasil
                conn.commit();

            } catch (SQLException ex) {
                // Batalkan semua perubahan jika terjadi kesalahan di tengah jalan
                conn.rollback();
                throw ex;
            }
        }
    }

    @Override
    public java.util.List<Order> findAll() throws SQLException {
        java.util.List<Order> list = new java.util.ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL_FIND_ALL)) {

            while (rs.next()) {
                list.add(mapRowToOrder(rs));
            }
        }
        return list;
    }

    @Override
    public java.util.List<Order> findUnpaidOrders() throws SQLException {
        java.util.List<Order> list = new java.util.ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL_FIND_UNPAID)) {

            while (rs.next()) {
                list.add(mapRowToOrder(rs));
            }
        }
        return list;
    }

    /**
     * Memetakan baris hasil kueri menjadi objek Order lengkap dengan objek User dan
     * Pelanggan.
     */
    private Order mapRowToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setIdOrder(rs.getInt("id_order"));
        order.setTanggal(rs.getTimestamp("tgl_pesan").toLocalDateTime());
        Timestamp tglKirim = rs.getTimestamp("tgl_kirim");
        if (tglKirim != null) {
            order.setTanggalKirim(tglKirim.toLocalDateTime());
        }
        order.setJenisPesanan(rs.getString("jenis_pesanan"));
        order.setTotal(rs.getLong("total"));
        order.setCatatan(rs.getString("catatan"));

        // Memetakan objek User terkait
        id.syafira.dapoer.model.User user = new id.syafira.dapoer.model.User();
        user.setId(rs.getInt("u_id"));
        user.setUsername(rs.getString("u_username"));
        user.setNama(rs.getString("u_nama"));
        order.setUser(user);

        // Memetakan objek Pelanggan terkait
        id.syafira.dapoer.model.Pelanggan pelanggan = new id.syafira.dapoer.model.Pelanggan();
        pelanggan.setId(rs.getInt("p_id"));
        pelanggan.setNama(rs.getString("p_nama"));
        order.setPelanggan(pelanggan);

        return order;
    }

    @Override
    public javafx.collections.ObservableList<DetailOrder> findDetailsByOrderId(int orderId) throws SQLException {
        javafx.collections.ObservableList<DetailOrder> list = javafx.collections.FXCollections.observableArrayList();
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_FIND_DETAILS)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetailOrder detail = new DetailOrder();
                    detail.setQty(rs.getInt("qty"));
                    detail.setSubtotal(rs.getLong("subtotal"));

                    // Memetakan data menu masakan yang dipesan
                    id.syafira.dapoer.model.MenuMasakan menu = new id.syafira.dapoer.model.MenuMasakan();
                    menu.setId(rs.getInt("id_masakan"));
                    menu.setNama(rs.getString("nama_masakan"));
                    menu.setHarga(rs.getLong("harga"));
                    menu.setStok(rs.getInt("stok"));
                    menu.setStatus(rs.getString("status_masakan"));

                    detail.setMenu(menu);
                    list.add(detail);
                }
            }
        }
        return list;
    }

    @Override
    public void deleteOrder(int orderId) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false); // Memulai transaksi untuk penghapusan

            try (PreparedStatement psGet = conn.prepareStatement(SQL_GET_DETAILS_FOR_DELETE);
                    PreparedStatement psRestore = conn.prepareStatement(SQL_RESTORE_STOCK);
                    PreparedStatement psDelDetail = conn.prepareStatement(SQL_DELETE_DETAILS);
                    PreparedStatement psDelOrder = conn.prepareStatement(SQL_DELETE_ORDER)) {

                // 1. Ambil detail item untuk mengembalikan stok menu masakan sebelum dihapus
                psGet.setInt(1, orderId);
                try (ResultSet rs = psGet.executeQuery()) {
                    while (rs.next()) {
                        int idMasakan = rs.getInt("id_masakan");
                        int qty = rs.getInt("qty");

                        // Tambahkan ke batch untuk pemulihan stok
                        psRestore.setInt(1, qty);
                        psRestore.setInt(2, idMasakan);
                        psRestore.addBatch();
                    }
                }
                psRestore.executeBatch();

                // 2. Hapus semua baris di detail_order terlebih dahulu
                psDelDetail.setInt(1, orderId);
                psDelDetail.executeUpdate();

                // 3. Terakhir hapus baris di tabel `order`
                psDelOrder.setInt(1, orderId);
                psDelOrder.executeUpdate();

                conn.commit();

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }
}
