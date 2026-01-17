package id.syafira.dapoer.repository;

import id.syafira.dapoer.db.DatabaseHelper;
import id.syafira.dapoer.model.Order;
import id.syafira.dapoer.model.Transaksi;
import id.syafira.dapoer.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC untuk TransaksiRepository.
 * Mengelola penyimpanan dan pengambilan data dari tabel transaksi.
 */
public class JdbcTransaksiRepository implements TransaksiRepository {

    private static final String SQL_FIND_ALL_JOIN_USER = "SELECT t.id_transaksi, t.id_order, t.tanggal, t.total_bayar, "
            +
            "u.id_user as u_id, u.username as u_username, u.nama_user as u_nama " +
            "FROM transaksi t " +
            "JOIN user u ON t.id_user = u.id_user " +
            "ORDER BY t.tanggal DESC";
    private static final String SQL_INSERT = "INSERT INTO transaksi (id_order, id_user, tanggal, total_bayar) VALUES (?, ?, ?, ?)";

    @Override
    public void save(Transaksi transaksi) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, transaksi.getOrder().getIdOrder());
            ps.setInt(2, transaksi.getUser().getId());
            ps.setTimestamp(3, Timestamp.valueOf(transaksi.getTanggal()));
            ps.setLong(4, transaksi.getTotalBayar());

            ps.executeUpdate();

            // Mengambil ID transaksi otomatis yang dihasilkan (AUTO_INCREMENT)
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    transaksi.setIdTransaksi(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Transaksi> findAll() throws SQLException {
        List<Transaksi> list = new ArrayList<>();

        // Menggunakan JOIN untuk mendapatkan nama user yang memproses transaksi
        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL_FIND_ALL_JOIN_USER)) {

            while (rs.next()) {
                Transaksi t = new Transaksi();
                t.setIdTransaksi(rs.getInt("id_transaksi"));
                t.setTanggal(rs.getTimestamp("tanggal").toLocalDateTime());
                t.setTotalBayar(rs.getLong("total_bayar"));

                // Memetakan data User (Kasir/Admin)
                User u = new User();
                u.setId(rs.getInt("u_id"));
                u.setUsername(rs.getString("u_username"));
                u.setNama(rs.getString("u_nama"));
                t.setUser(u);

                // Memetakan data Order (saat ini hanya ID sebagai pembungkus)
                Order o = new Order();
                o.setIdOrder(rs.getInt("id_order"));
                t.setOrder(o);

                list.add(t);
            }
        }
        return list;
    }
}
