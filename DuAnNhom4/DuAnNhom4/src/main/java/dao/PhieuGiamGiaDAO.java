package dao;

import model.PhieuGiamGia;
import utils.XJDBC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhieuGiamGiaDAO {

    private PhieuGiamGia fromResultSet(ResultSet rs) throws SQLException {
        PhieuGiamGia pgg = new PhieuGiamGia();
        pgg.setID(rs.getInt("ID"));
        pgg.setMaGiamGia(rs.getString("MaGiamGia"));
        pgg.setTenChuongTrinh(rs.getString("TenChuongTrinh"));
        pgg.setLoaiGiam(rs.getString("LoaiGiam"));
        pgg.setGiaTriGiam(rs.getBigDecimal("GiaTriGiam"));
        pgg.setDieuKienToiThieu(rs.getBigDecimal("DieuKienToiThieu"));
        pgg.setSoLuongToiDa(rs.getInt("SoLuongToiDa"));
        pgg.setSoLuongDaDung(rs.getInt("SoLuongDaDung"));
        pgg.setNgayBatDau(rs.getDate("NgayBatDau"));
        pgg.setNgayKetThuc(rs.getDate("NgayKetThuc"));
        pgg.setApDungCho(rs.getString("ApDungCho"));
        pgg.setDanhSachApDung(rs.getString("DanhSachApDung"));
        pgg.setTrangThai(rs.getString("TrangThai"));
        pgg.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return pgg;
    }

    public void insert(PhieuGiamGia model) {
        String sql = "INSERT INTO PhieuGiamGia (MaGiamGia, TenChuongTrinh, LoaiGiam, GiaTriGiam, DieuKienToiThieu, SoLuongToiDa, NgayBatDau, NgayKetThuc, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        XJDBC.update(sql,
                model.getMaGiamGia(),
                model.getTenChuongTrinh(),
                model.getLoaiGiam(),
                model.getGiaTriGiam(),
                model.getDieuKienToiThieu(),
                model.getSoLuongToiDa(),
                model.getNgayBatDau(),
                model.getNgayKetThuc(),
                model.getTrangThai());
    }

    public void update(PhieuGiamGia model) {
        String sql = "UPDATE PhieuGiamGia SET TenChuongTrinh=?, LoaiGiam=?, GiaTriGiam=?, DieuKienToiThieu=?, SoLuongToiDa=?, NgayBatDau=?, NgayKetThuc=?, TrangThai=? WHERE MaGiamGia=?";
        XJDBC.update(sql,
                model.getTenChuongTrinh(),
                model.getLoaiGiam(),
                model.getGiaTriGiam(),
                model.getDieuKienToiThieu(),
                model.getSoLuongToiDa(),
                model.getNgayBatDau(),
                model.getNgayKetThuc(),
                model.getTrangThai(),
                model.getMaGiamGia());
    }
    
    public void updateTrangThai(String maGiamGia, String trangThai) {
        String sql = "UPDATE PhieuGiamGia SET TrangThai=? WHERE MaGiamGia=?";
        XJDBC.update(sql, trangThai, maGiamGia);
    }

    public void incrementSoLuongDaDung(String maGiamGia) {
        String sql = "UPDATE PhieuGiamGia SET SoLuongDaDung = SoLuongDaDung + 1 WHERE MaGiamGia = ?";
        XJDBC.update(sql, maGiamGia);
    }


    public List<PhieuGiamGia> selectAll() {
        String sql = "SELECT * FROM PhieuGiamGia";
        return select(sql);
    }

    public PhieuGiamGia selectById(String maGiamGia) {
        String sql = "SELECT * FROM PhieuGiamGia WHERE MaGiamGia=?";
        List<PhieuGiamGia> list = select(sql, maGiamGia);
        return !list.isEmpty() ? list.get(0) : null;
    }
    
    public List<PhieuGiamGia> filter(String keyword, String trangThai, String loaiGiam) {
        StringBuilder sql = new StringBuilder("SELECT * FROM PhieuGiamGia WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (MaGiamGia LIKE ? OR TenChuongTrinh LIKE ?)");
        }
        if (trangThai != null && !trangThai.equalsIgnoreCase("Tất cả")) {
            sql.append(" AND TrangThai = ?");
        }
        if (loaiGiam != null && !loaiGiam.equalsIgnoreCase("Tất cả")) {
            sql.append(" AND LoaiGiam = ?");
        }

        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (trangThai != null && !trangThai.equalsIgnoreCase("Tất cả")) {
            params.add(trangThai);
        }
        if (loaiGiam != null && !loaiGiam.equalsIgnoreCase("Tất cả")) {
            params.add(loaiGiam);
        }

        return select(sql.toString(), params.toArray());
    }


    private List<PhieuGiamGia> select(String sql, Object... args) {
        List<PhieuGiamGia> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = XJDBC.query(sql, args);
            while (rs.next()) {
                list.add(fromResultSet(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            XJDBC.closeQuietly(rs);
        }
        return list;
    }
}