package dao.impl;

import dao.PhieuNhapChiTietDAO;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.PhieuNhapChiTiet;
import utils.XJDBC;

public class PhieuNhapChiTietDAOImpl implements PhieuNhapChiTietDAO {

    private PhieuNhapChiTiet mapRow(ResultSet rs) throws Exception {
        PhieuNhapChiTiet pnct = new PhieuNhapChiTiet();
        pnct.setId(rs.getInt("ID"));
        pnct.setPhieuNhapID(rs.getInt("PhieuNhapID"));
        pnct.setDoUongID(rs.getInt("DoUongID"));
        pnct.setSoLuong(rs.getInt("SoLuong"));
        pnct.setDonGia(rs.getBigDecimal("DonGia"));
        pnct.setThanhTien(rs.getBigDecimal("ThanhTien"));
        return pnct;
    }

    @Override
    public List<PhieuNhapChiTiet> selectBySql(String sql, Object... args) {
        List<PhieuNhapChiTiet> list = new ArrayList<>();
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
    public List<PhieuNhapChiTiet> selectAll() {
        String sql = "SELECT * FROM PhieuNhapChiTiet";
        return selectBySql(sql);
    }
    
    @Override
    public List<PhieuNhapChiTiet> selectByPhieuNhapID(Integer phieuNhapID) {
        String sql = "SELECT * FROM PhieuNhapChiTiet WHERE PhieuNhapID = ?";
        return selectBySql(sql, phieuNhapID);
    }

    @Override
    public PhieuNhapChiTiet selectById(Integer id) {
        String sql = "SELECT * FROM PhieuNhapChiTiet WHERE ID = ?";
        List<PhieuNhapChiTiet> list = selectBySql(sql, id);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public void insert(PhieuNhapChiTiet model) {
        String sql = "INSERT INTO PhieuNhapChiTiet (PhieuNhapID, DoUongID, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
        XJDBC.update(sql,
                model.getPhieuNhapID(),
                model.getDoUongID(),
                model.getSoLuong(),
                model.getDonGia());
    }

    @Override
    public void update(PhieuNhapChiTiet model) {
        String sql = "UPDATE PhieuNhapChiTiet SET PhieuNhapID=?, DoUongID=?, SoLuong=?, DonGia=? WHERE ID=?";
        XJDBC.update(sql,
                model.getPhieuNhapID(),
                model.getDoUongID(),
                model.getSoLuong(),
                model.getDonGia(),
                model.getId());
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM PhieuNhapChiTiet WHERE ID=?";
        XJDBC.update(sql, id);
    }
}
