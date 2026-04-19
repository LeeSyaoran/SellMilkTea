package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.LichSuKho;
import utils.XJDBC;

public class LichSuKhoDAO {

    private LichSuKho readFromResultSet(ResultSet rs) throws SQLException {
        LichSuKho model = new LichSuKho();
        model.setId(rs.getInt("ID"));
        model.setDoUongID(rs.getInt("DoUongID"));
        model.setChiNhanhID(rs.getInt("ChiNhanhID"));
        model.setLoaiGiaoDich(rs.getString("LoaiGiaoDich"));
        model.setSoLuong(rs.getInt("SoLuong"));
        model.setNgayGiaoDich(rs.getTimestamp("NgayGiaoDich"));
        model.setNhanVienID(rs.getInt("NhanVienID"));
        // PhieuNhapID can be null
        Integer phieuNhapID = (Integer) rs.getObject("PhieuNhapID");
        if (phieuNhapID != null) {
            model.setPhieuNhapID(phieuNhapID);
        }
        model.setGhiChu(rs.getString("GhiChu"));
        return model;
    }

    private List<LichSuKho> select(String sql, Object... args) {
        List<LichSuKho> list = new ArrayList<>();
        try {
            ResultSet rs = null;
            try {
                rs = XJDBC.query(sql, args);
                while (rs.next()) {
                    list.add(readFromResultSet(rs));
                }
            } finally {
                if (rs != null) {
                    rs.getStatement().getConnection().close();
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return list;
    }

    public void insert(LichSuKho model) {
        String sql = "INSERT INTO LichSuKho (DoUongID, ChiNhanhID, LoaiGiaoDich, SoLuong, NgayGiaoDich, NhanVienID, PhieuNhapID, GhiChu) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        XJDBC.update(sql,
                model.getDoUongID(),
                model.getChiNhanhID(),
                model.getLoaiGiaoDich(),
                model.getSoLuong(),
                model.getNgayGiaoDich(),
                model.getNhanVienID(),
                model.getPhieuNhapID(),
                model.getGhiChu());
    }

    public void update(LichSuKho model) {
        String sql = "UPDATE LichSuKho SET DoUongID=?, ChiNhanhID=?, LoaiGiaoDich=?, SoLuong=?, NgayGiaoDich=?, NhanVienID=?, PhieuNhapID=?, GhiChu=? WHERE ID=?";
        XJDBC.update(sql,
                model.getDoUongID(),
                model.getChiNhanhID(),
                model.getLoaiGiaoDich(),
                model.getSoLuong(),
                model.getNgayGiaoDich(),
                model.getNhanVienID(),
                model.getPhieuNhapID(),
                model.getGhiChu(),
                model.getId());
    }

    public void delete(Integer id) {
        String sql = "DELETE FROM LichSuKho WHERE ID=?";
        XJDBC.update(sql, id);
    }

    public List<LichSuKho> selectAll() {
        String sql = "SELECT * FROM LichSuKho";
        return select(sql);
    }

    public LichSuKho selectById(Integer id) {
        String sql = "SELECT * FROM LichSuKho WHERE ID=?";
        List<LichSuKho> list = select(sql, id);
        return list.size() > 0 ? list.get(0) : null;
    }
    
    public List<LichSuKho> filter(String tuNgay, String denNgay, Integer chiNhanhId, String loaiGiaoDich, String tenDoUong) {
        StringBuilder sql = new StringBuilder("SELECT lsk.* FROM LichSuKho lsk");
        sql.append(" JOIN DoUong du ON lsk.DoUongID = du.ID WHERE 1=1");
        
        List<Object> params = new ArrayList<>();
        
        if (tuNgay != null && !tuNgay.isEmpty()) {
            sql.append(" AND lsk.NgayGiaoDich >= ?");
            params.add(tuNgay);
        }
        
        if (denNgay != null && !denNgay.isEmpty()) {
            sql.append(" AND lsk.NgayGiaoDich <= ?");
            params.add(denNgay);
        }
        
        if (chiNhanhId != null) {
            sql.append(" AND lsk.ChiNhanhID = ?");
            params.add(chiNhanhId);
        }
        
        if (loaiGiaoDich != null && !loaiGiaoDich.isEmpty()) {
            sql.append(" AND lsk.LoaiGiaoDich = ?");
            params.add(loaiGiaoDich);
        }
        
        if (tenDoUong != null && !tenDoUong.isEmpty()) {
            sql.append(" AND du.TenDoUong LIKE ?");
            params.add("%" + tenDoUong + "%");
        }
        
        return select(sql.toString(), params.toArray());
    }
}