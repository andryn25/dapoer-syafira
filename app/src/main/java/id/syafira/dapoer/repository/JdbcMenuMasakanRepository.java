package id.syafira.dapoer.repository;

import id.syafira.dapoer.db.DatabaseHelper;
import id.syafira.dapoer.model.MenuMasakan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * Implementasi JDBC untuk MenuIMasakanRepository.
 * Menghubungkan aplikasi dengan tabel menu_masakan di database.
 */
public class JdbcMenuMasakanRepository implements MenuIMasakanRepository {

    private static final String SQL_SELECT_ALL = "SELECT id_masakan, nama_masakan, harga, status_masakan, stok FROM menu_masakan";
    private static final String SQL_FIND_ALL = SQL_SELECT_ALL + " ORDER BY id_masakan";
    private static final String SQL_INSERT = "INSERT INTO menu_masakan (nama_masakan, harga, status_masakan, stok) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE menu_masakan SET nama_masakan = ?, harga = ?, status_masakan = ?, stok = ? WHERE id_masakan = ?";
    private static final String SQL_DELETE = "DELETE FROM menu_masakan WHERE id_masakan = ?";

    @Override
    public ObservableList<MenuMasakan> findAll() throws SQLException {
        ObservableList<MenuMasakan> list = FXCollections.observableArrayList();

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MenuMasakan m = new MenuMasakan();
                // Memetakan hasil kueri ke objek model Java
                m.setId(rs.getInt("id_masakan"));
                m.setNama(rs.getString("nama_masakan"));
                m.setHarga(rs.getLong("harga"));
                m.setStatus(rs.getString("status_masakan"));
                m.setStok(rs.getInt("stok"));
                list.add(m);
            }
        }
        return list;
    }

    @Override
    public void save(MenuMasakan menu) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, menu.getNama());
            ps.setLong(2, menu.getHarga());
            ps.setString(3, menu.getStatus());
            ps.setInt(4, menu.getStok());
            ps.executeUpdate();

            // Mensinkronkan ID objek Java dengan ID AUTO_INCREMENT yang baru dihasilkan
            // database
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    menu.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(MenuMasakan menu) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, menu.getNama());
            ps.setLong(2, menu.getHarga());
            ps.setString(3, menu.getStatus());
            ps.setInt(4, menu.getStok());
            ps.setInt(5, menu.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int idMasakan) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, idMasakan);
            ps.executeUpdate();
        }
    }
}
