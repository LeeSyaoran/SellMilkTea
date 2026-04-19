package dao.impl;

import dao.PhieuNhapDAO;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.PhieuNhap;
import utils.XJDBC;

public class PhieuNhapDAOImpl implements PhieuNhapDAO {

    private PhieuNhap mapRow(ResultSet rs) throws Exception {
        PhieuNhap pn = new PhieuNhap();
        pn.setId(rs.getInt("ID"));
        pn.setMaPhieu(rs.getString("MaPhieu"));
        pn.setNgayNhap(rs.getTimestamp("NgayNhap"));
        pn.setTongTien(rs.getDouble("TongTien"));
        pn.setNhaCungCapID(rs.getInt("NhaCungCapID"));
        pn.setNhanVienID(rs.getInt("NhanVienID"));
        pn.setChiNhanhID(rs.getInt("ChiNhanhID"));
        pn.setTrangThai(rs.getNString("TrangThai"));
        pn.setGhiChu(rs.getNString("GhiChu"));
        return pn;
    }
    
    @Override
    public List<PhieuNhap> selectBySql(String sql, Object... args) {
        List<PhieuNhap> list = new ArrayList<>();
        try {
            ResultSet rs = XJDBC.query(sql, args);
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PhieuNhap> selectAll() {
        String sql = "SELECT * FROM PhieuNhapHang";
        return selectBySql(sql);
    }

    @Override
    public PhieuNhap selectById(Integer id) {
        String sql = "SELECT * FROM PhieuNhapHang WHERE ID = ?";
        List<PhieuNhap> list = selectBySql(sql, id);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<PhieuNhap> findByFilters(Integer nccId, Integer cnId) {
        StringBuilder sql = new StringBuilder("SELECT * FROM PhieuNhapHang WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (nccId != null) {
            sql.append(" AND NhaCungCapID = ?");
            params.add(nccId);
        }
        if (cnId != null) {
            sql.append(" AND ChiNhanhID = ?");
            params.add(cnId);
        }

        return selectBySql(sql.toString(), params.toArray());
    }

    @Override
    public void insert(PhieuNhap model) {
        String sql = "INSERT INTO PhieuNhapHang (NgayNhap, TongTien, NhaCungCapID, NhanVienID, ChiNhanhID, TrangThai, GhiChu) VALUES (?, ?, ?, ?, ?, ?, ?)";
        XJDBC.update(sql,
                model.getNgayNhap(),
                model.getTongTien(),
                model.getNhaCungCapID(),
                model.getNhanVienID(),
                model.getChiNhanhID(),
                model.getTrangThai(),
                model.getGhiChu());
    }

    @Override
    public void update(PhieuNhap model) {
        String sql = "UPDATE PhieuNhapHang SET NgayNhap=?, TongTien=?, NhaCungCapID=?, NhanVienID=?, ChiNhanhID=?, TrangThai=?, GhiChu=? WHERE ID=?";
        XJDBC.update(sql,
                model.getNgayNhap(),
                model.getTongTien(),
                model.getNhaCungCapID(),
                model.getNhanVienID(),
                model.getChiNhanhID(),
                model.getTrangThai(),
                model.getGhiChu(),
                model.getId());
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM PhieuNhapHang WHERE ID=?";
        XJDBC.update(sql, id);
    }
}
