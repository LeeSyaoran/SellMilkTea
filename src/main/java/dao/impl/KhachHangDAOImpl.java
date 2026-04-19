package dao.impl;

import dao.KhachHangDAO;
import model.KhachHang;
import utils.XJDBC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAOImpl implements KhachHangDAO {

    private KhachHang readFromResultSet(ResultSet rs) throws SQLException {
        KhachHang model = new KhachHang();
        model.setId(rs.getInt("ID"));
        model.setHoTen(rs.getString("HoTen"));
        model.setSoDienThoai(rs.getString("SoDienThoai"));
        model.setEmail(rs.getString("Email"));
        model.setDiaChi(rs.getString("DiaChi"));
        model.setDiemThuong(rs.getInt("DiemThuong"));
        model.setCapDoVIP(rs.getString("CapDoVIP"));
        model.setNgayDangKy(rs.getDate("NgayDangKy"));
        // Check if SoDonDaMua column exists
        try {
            model.setSoDonDaMua(rs.getInt("SoDonDaMua"));
        } catch (SQLException e) {
            // Column does not exist, set to 0 or handle as needed
            model.setSoDonDaMua(0);
        }
        return model;
    }

    private List<KhachHang> select(String sql, Object... args) {
        List<KhachHang> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = XJDBC.query(sql, args);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi đọc dữ liệu KhachHang", ex);
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException ex) {
                    // Bỏ qua
                }
            }
        }
        return list;
    }

    @Override
    public void insert(KhachHang model) {
        String sql = "INSERT INTO KhachHang (HoTen, SoDienThoai, Email, DiaChi, DiemThuong, CapDoVIP, NgayDangKy) VALUES (?, ?, ?, ?, ?, ?, ?)";
        XJDBC.update(sql,
                model.getHoTen(),
                model.getSoDienThoai(),
                model.getEmail(),
                model.getDiaChi(),
                model.getDiemThuong(),
                model.getCapDoVIP(),
                model.getNgayDangKy());
    }

    @Override
    public void update(KhachHang model) {
        String sql = "UPDATE KhachHang SET HoTen=?, SoDienThoai=?, Email=?, DiaChi=?, DiemThuong=?, CapDoVIP=?, NgayDangKy=? WHERE ID=?";
        XJDBC.update(sql,
                model.getHoTen(),
                model.getSoDienThoai(),
                model.getEmail(),
                model.getDiaChi(),
                model.getDiemThuong(),
                model.getCapDoVIP(),
                model.getNgayDangKy(),
                model.getId());
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM KhachHang WHERE ID=?";
        XJDBC.update(sql, id);
    }

    @Override
    public List<KhachHang> selectAll() {
        String sql = "SELECT kh.*, COUNT(hd.ID) AS SoDonDaMua " +
                     "FROM KhachHang kh " +
                     "LEFT JOIN HoaDon hd ON kh.ID = hd.KhachHangID " +
                     "GROUP BY kh.ID, kh.HoTen, kh.SoDienThoai, kh.Email, kh.DiaChi, kh.DiemThuong, kh.CapDoVIP, kh.NgayDangKy, kh.TrangThai, kh.CreatedAt, kh.UpdatedAt, kh.ThuocTinhMoRong";
        return select(sql);
    }

    @Override
    public KhachHang findById(int id) {
        String sql = "SELECT kh.*, COUNT(hd.ID) AS SoDonDaMua " +
                     "FROM KhachHang kh " +
                     "LEFT JOIN HoaDon hd ON kh.ID = hd.KhachHangID " +
                     "WHERE kh.ID=? " +
                     "GROUP BY kh.ID, kh.HoTen, kh.SoDienThoai, kh.Email, kh.DiaChi, kh.DiemThuong, kh.CapDoVIP, kh.NgayDangKy, kh.TrangThai, kh.CreatedAt, kh.UpdatedAt, kh.ThuocTinhMoRong";
        List<KhachHang> list = select(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public KhachHang findBySoDienThoai(String sdt) {
        String sql = "SELECT kh.*, COUNT(hd.ID) AS SoDonDaMua " +
                     "FROM KhachHang kh " +
                     "LEFT JOIN HoaDon hd ON kh.ID = hd.KhachHangID " +
                     "WHERE kh.SoDienThoai=? " +
                     "GROUP BY kh.ID, kh.HoTen, kh.SoDienThoai, kh.Email, kh.DiaChi, kh.DiemThuong, kh.CapDoVIP, kh.NgayDangKy, kh.TrangThai, kh.CreatedAt, kh.UpdatedAt, kh.ThuocTinhMoRong";
        List<KhachHang> list = select(sql, sdt);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<KhachHang> selectByKeyword(String keyword) {
        String sql = "SELECT kh.*, COUNT(hd.ID) AS SoDonDaMua " +
                     "FROM KhachHang kh " +
                     "LEFT JOIN HoaDon hd ON kh.ID = hd.KhachHangID " +
                     "WHERE kh.HoTen LIKE ? OR kh.SoDienThoai LIKE ? " +
                     "GROUP BY kh.ID, kh.HoTen, kh.SoDienThoai, kh.Email, kh.DiaChi, kh.DiemThuong, kh.CapDoVIP, kh.NgayDangKy, kh.TrangThai, kh.CreatedAt, kh.UpdatedAt, kh.ThuocTinhMoRong";
        return select(sql, "%" + keyword + "%", "%" + keyword + "%");
    }
    
    @Override
    public List<KhachHang> filter(String capDoVIP, int diemMin, int diemMax) {
        StringBuilder sql = new StringBuilder(
            "SELECT kh.*, COUNT(hd.ID) AS SoDonDaMua " +
            "FROM KhachHang kh " +
            "LEFT JOIN HoaDon hd ON kh.ID = hd.KhachHangID " +
            "WHERE kh.DiemThuong BETWEEN ? AND ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(diemMin);
        params.add(diemMax);

        if (capDoVIP != null && !capDoVIP.equalsIgnoreCase("Tất cả")) {
            sql.append("AND kh.CapDoVIP = ? ");
            params.add(capDoVIP);
        }

        sql.append("GROUP BY kh.ID, kh.HoTen, kh.SoDienThoai, kh.Email, kh.DiaChi, kh.DiemThuong, kh.CapDoVIP, kh.NgayDangKy, kh.TrangThai, kh.CreatedAt, kh.UpdatedAt, kh.ThuocTinhMoRong");

        return select(sql.toString(), params.toArray());
    }
}