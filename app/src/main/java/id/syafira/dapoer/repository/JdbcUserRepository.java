package id.syafira.dapoer.repository;

import id.syafira.dapoer.db.DatabaseHelper;
import id.syafira.dapoer.model.Level;
import id.syafira.dapoer.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC untuk UserRepository.
 * Mengelola data pengguna dengan relasi ke tabel level_user menggunakan JOIN.
 */
public class JdbcUserRepository implements UserRepository {

    // Kueri SQL untuk operasi pada tabel user dengan join ke level_user
    private static final String SQL_SELECT_ALL = "SELECT u.id_user, u.username, u.password, u.nama_user, " +
            "       l.id_level, l.nama_level " +
            "FROM user u JOIN level_user l ON u.id_level = l.id_level " +
            "ORDER BY u.id_user";

    private static final String SQL_SELECT_BY_ID = "SELECT u.id_user, u.username, u.password, u.nama_user, " +
            "       l.id_level, l.nama_level " +
            "FROM user u JOIN level_user l ON u.id_level = l.id_level " +
            "WHERE u.id_user = ?";

    private static final String SQL_SELECT_BY_USERNAME = "SELECT u.id_user, u.username, u.password, u.nama_user, " +
            "       l.id_level, l.nama_level " +
            "FROM user u JOIN level_user l ON u.id_level = l.id_level " +
            "WHERE u.username = ?";

    private static final String SQL_INSERT = "INSERT INTO user (username, password, nama_user, id_level) " +
            "VALUES (?, ?, ?, ?)";

    private static final String SQL_UPDATE = "UPDATE user SET username = ?, password = ?, nama_user = ?, id_level = ? "
            +
            "WHERE id_user = ?";

    private static final String SQL_DELETE = "DELETE FROM user WHERE id_user = ?";

    @Override
    public List<User> findAll() throws SQLException {
        List<User> result = new ArrayList<>();
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
    public User findById(int id) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    @Override
    public User findByUsername(String username) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Manual mapping di metode ini untuk kebutuhan autentikasi
                    Level lvl = new Level(rs.getString("nama_level"));
                    User u = new User(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("nama_user"),
                            lvl);
                    u.setId(rs.getInt("id_user"));
                    return u;
                }
            }
        }
        return null;
    }

    @Override
    public User insert(User user) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword()); // Disimpan dalam bentuk plain/hashed sesuai logic service
            ps.setString(3, user.getNama());
            ps.setInt(4, user.getLevel().getIdLevel());

            ps.executeUpdate();

            // Mengambil ID otomatis dari database
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
        }
        return user;
    }

    @Override
    public void update(User user) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getNama());
            ps.setInt(4, user.getLevel().getIdLevel());
            ps.setInt(5, user.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Memetakan baris hasil kueri menjadi objek User lengkap dengan objek Level
     * pendukungnya.
     */
    private User mapRow(ResultSet rs) throws SQLException {
        Level lvl = new Level(rs.getString("nama_level"));
        User u = new User(
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("nama_user"),
                lvl);
        u.setId(rs.getInt("id_user"));
        return u;
    }
}
