package dao.impl;

import dao.NhanVienDAO;
import model.NhanVien;
import utils.XJDBC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAOImpl implements NhanVienDAO {

    private NhanVien readFromResultSet(ResultSet rs) throws SQLException {
        NhanVien model = new NhanVien();
        model.setId(rs.getInt("ID"));
        model.setUsername(rs.getString("Username"));
        model.setPasswordHash(rs.getString("PasswordHash"));
        model.setHoTen(rs.getString("HoTen"));
        model.setVaiTro(rs.getString("VaiTro"));
        model.setChiNhanhID(rs.getInt("ChiNhanhID"));
        model.setSoDienThoai(rs.getString("SoDienThoai"));
        model.setEmail(rs.getString("Email"));
        model.setNgaySinh(rs.getDate("NgaySinh"));
        model.setGioiTinh(rs.getString("GioiTinh"));
        model.setTrangThai(rs.getString("TrangThai"));
        return model;
    }

    private List<NhanVien> select(String sql, Object... args) {
        List<NhanVien> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = XJDBC.query(sql, args);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi đọc dữ liệu NhanVien", ex);
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException ex) {
                    // Bỏ qua lỗi khi đóng kết nối
                }
            }
        }
        return list;
    }

    @Override
    public void insert(NhanVien model) {
        String sql = "INSERT INTO NhanVien (Username, PasswordHash, HoTen, VaiTro, ChiNhanhID, SoDienThoai, Email, NgaySinh, GioiTinh, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        XJDBC.update(sql,
                model.getUsername(),
                model.getPasswordHash(),
                model.getHoTen(),
                model.getVaiTro(),
                model.getChiNhanhID(),
                model.getSoDienThoai(),
                model.getEmail(),
                model.getNgaySinh(),
                model.getGioiTinh(),
                model.getTrangThai());
    }

    @Override
    public void update(NhanVien model) {
        String sql = "UPDATE NhanVien SET Username=?, PasswordHash=?, HoTen=?, VaiTro=?, ChiNhanhID=?, SoDienThoai=?, Email=?, NgaySinh=?, GioiTinh=?, TrangThai=? WHERE ID=?";
        XJDBC.update(sql,
                model.getUsername(),
                model.getPasswordHash(),
                model.getHoTen(),
                model.getVaiTro(),
                model.getChiNhanhID(),
                model.getSoDienThoai(),
                model.getEmail(),
                model.getNgaySinh(),
                model.getGioiTinh(),
                model.getTrangThai(),
                model.getId());
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM NhanVien WHERE ID=?";
        XJDBC.update(sql, id);
    }

    @Override
    public List<NhanVien> selectAll() {
        String sql = "SELECT * FROM NhanVien";
        return select(sql);
    }

    @Override
    public NhanVien findById(int id) {
        String sql = "SELECT * FROM NhanVien WHERE ID=?";
        List<NhanVien> list = select(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public NhanVien findByUsername(String username) {
        String sql = "SELECT * FROM NhanVien WHERE Username=?";
        List<NhanVien> list = select(sql, username);
        return list.isEmpty() ? null : list.get(0);
    }

    // @Override
    // public List<NhanVien> selectByKeyword(String keyword) {
    //     String sql = "SELECT * FROM NhanVien WHERE HoTen LIKE ? OR SoDienThoai LIKE ?";
    //     return select(sql, "%" + keyword + "%", "%" + keyword + "%");
    // }

    @Override
    public List<NhanVien> selectByConditions(String keyword, String vaiTro, Integer chiNhanhId) {
        StringBuilder sql = new StringBuilder("SELECT * FROM NhanVien WHERE 1=1"); // Start with a true condition

        List<Object> args = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (HoTen LIKE ? OR Username LIKE ? OR Email LIKE ? OR SoDienThoai LIKE ?)");
            args.add("%" + keyword + "%");
            args.add("%" + keyword + "%");
            args.add("%" + keyword + "%");
            args.add("%" + keyword + "%");
        }

        if (vaiTro != null && !vaiTro.equals("Tất cả")) { // Assuming "Tất cả" is the value for no filter
            sql.append(" AND VaiTro = ?");
            args.add(vaiTro);
        }

        if (chiNhanhId != null && chiNhanhId > 0) { // Assuming 0 or -1 might indicate no filter
            sql.append(" AND ChiNhanhID = ?");
            args.add(chiNhanhId);
        }

        return select(sql.toString(), args.toArray());
    }
}