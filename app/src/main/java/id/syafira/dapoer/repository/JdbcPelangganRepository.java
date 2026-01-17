package id.syafira.dapoer.repository;

import id.syafira.dapoer.db.DatabaseHelper;
import id.syafira.dapoer.model.Pelanggan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC untuk PelangganRepository.
 * Menangani interaksi data langsung dengan tabel pelanggan di database.
 */
public class JdbcPelangganRepository implements PelangganRepository {

    // Kueri SQL untuk operasi pada tabel pelanggan
    private static final String SQL_SELECT_ALL = "SELECT id_pelanggan, nama, telepon, alamat FROM pelanggan ORDER BY id_pelanggan";
    private static final String SQL_SELECT_BY_ID = "SELECT id_pelanggan, nama, telepon, alamat FROM pelanggan WHERE id_pelanggan = ?";
    private static final String SQL_INSERT = "INSERT INTO pelanggan (nama, telepon, alamat) VALUES (?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE pelanggan SET nama = ?, telepon = ?, alamat = ? WHERE id_pelanggan = ?";
    private static final String SQL_DELETE = "DELETE FROM pelanggan WHERE id_pelanggan = ?";

    @Override
    public List<Pelanggan> findAll() throws SQLException {
        List<Pelanggan> result = new ArrayList<>();

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pelanggan p = mapRow(rs);
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public Pelanggan findById(int id) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Pelanggan insert(Pelanggan pelanggan) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, pelanggan.getNama());
            // Kolom di database bernama 'telepon', sedangkan di model Java adalah 'noHp'
            ps.setString(2, pelanggan.getNoHp());
            ps.setString(3, pelanggan.getAlamat());

            ps.executeUpdate();

            // Mengambil ID yang dihasilkan secara otomatis (AUTO_INCREMENT)
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    pelanggan.setId(generatedId);
                }
            }
        }
        return pelanggan;
    }

    @Override
    public void update(Pelanggan pelanggan) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, pelanggan.getNama());
            ps.setString(2, pelanggan.getNoHp());
            ps.setString(3, pelanggan.getAlamat());
            ps.setInt(4, pelanggan.getId());

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
     * Memetakan satu baris hasil kueri (ResultSet) menjadi objek Pelanggan.
     * 
     * @param rs ResultSet dari kueri database.
     * @return Objek Pelanggan yang sudah terisi datanya.
     * @throws SQLException Jika terjadi kesalahan pembacaan kolom.
     */
    private Pelanggan mapRow(ResultSet rs) throws SQLException {
        Pelanggan p = new Pelanggan();
        p.setId(rs.getInt("id_pelanggan"));
        p.setNama(rs.getString("nama"));
        p.setNoHp(rs.getString("telepon")); // Pemetaan kolom 'telepon' ke properti noHp
        p.setAlamat(rs.getString("alamat"));
        return p;
    }
}
