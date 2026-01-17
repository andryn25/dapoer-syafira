package id.syafira.dapoer.repository;

import id.syafira.dapoer.db.DatabaseHelper;
import id.syafira.dapoer.model.Level;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC dari LevelRepository.
 * Menangani operasi database langsung ke tabel level_user menggunakan kueri SQL
 * native.
 */
public class JdbcLevelRepository implements LevelRepository {

    // Daftar kueri SQL yang digunakan dalam repositori ini
    private static final String SQL_SELECT_ALL = "SELECT id_level, nama_level FROM level_user ORDER BY id_level";
    private static final String SQL_SELECT_BY_ID = "SELECT id_level, nama_level FROM level_user WHERE id_level = ?";
    private static final String SQL_INSERT = "INSERT INTO level_user (nama_level) VALUES (?)";
    private static final String SQL_UPDATE = "UPDATE level_user SET nama_level = ? WHERE id_level = ?";
    private static final String SQL_DELETE = "DELETE FROM level_user WHERE id_level = ?";

    @Override
    public List<Level> findAll() throws SQLException {
        List<Level> result = new ArrayList<>();
        // Membuka koneksi dan mengeksekusi kueri ambil semua data
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }
        return result;
    }

    @Override
    public Level findById(int idLevel) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setInt(1, idLevel);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Level insert(Level level) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, level.getNamaLevel());
            ps.executeUpdate();

            // Mengambil ID otomatis yang dihasilkan oleh database (Auto-Increment)
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    level.setIdLevel(rs.getInt(1));
                }
            }
        }
        return level;
    }

    @Override
    public void update(Level level) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, level.getNamaLevel());
            ps.setInt(2, level.getIdLevel());
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteById(int idLevel) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, idLevel);
            ps.executeUpdate();
        }
    }

    /**
     * Memetakan satu baris hasil kueri database (ResultSet) menjadi objek Level.
     * 
     * @param rs ResultSet dari database.
     * @return Objek Level yang datanya sudah terisi.
     * @throws SQLException Jika terjadi kesalahan pembacaan kolom.
     */
    private Level mapRow(ResultSet rs) throws SQLException {
        Level levelUser = new Level();
        levelUser.setIdLevel(rs.getInt("id_level"));
        levelUser.setNamaLevel(rs.getString("nama_level"));
        return levelUser;
    }

}
