package dao.impl;

import dao.NhaCungCapDAO;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.NhaCungCap;
import utils.XJDBC;

public class NhaCungCapDAOImpl implements NhaCungCapDAO {

    private NhaCungCap mapRow(ResultSet rs) throws Exception {
        NhaCungCap n = new NhaCungCap();
        n.setId(rs.getInt("ID"));
        n.setTenNCC(rs.getNString("TenNCC"));
        n.setDiaChi(rs.getNString("DiaChi"));
        n.setSoDienThoai(rs.getString("SoDienThoai"));
        n.setEmail(rs.getString("Email"));
        n.setTrangThai(rs.getNString("TrangThai"));
        n.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return n;
    }

    @Override
    public List<NhaCungCap> findAll() {
        String sql = "SELECT ID, TenNCC, DiaChi, SoDienThoai, Email, TrangThai, CreatedAt FROM NhaCungCap ORDER BY TenNCC";
        List<NhaCungCap> list = new ArrayList<>();
        try (var rs = XJDBC.query(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public NhaCungCap findById(Integer id) {
        String sql = "SELECT ID, TenNCC, DiaChi, SoDienThoai, Email, TrangThai, CreatedAt FROM NhaCungCap WHERE ID=?";
        try (var rs = XJDBC.query(sql, id)) {
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Integer idByTen(String ten) {
        if (ten == null) return null;
        String sql = "SELECT ID FROM NhaCungCap WHERE TenNCC = ?";
        try (var rs = XJDBC.query(sql, ten)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Integer insert(NhaCungCap entity) {
        String sql = "INSERT INTO NhaCungCap (TenNCC, DiaChi, SoDienThoai, Email, TrangThai) VALUES (?, ?, ?, ?, ?)";
        try {
            int affected = XJDBC.update(sql,
                    entity.getTenNCC(),
                    entity.getDiaChi(),
                    entity.getSoDienThoai(),
                    entity.getEmail(),
                    entity.getTrangThai());
            if (affected > 0) {
                try (var rs = XJDBC.query("SELECT SCOPE_IDENTITY()")) {
                    if (rs.next()) return rs.getInt(1);
                } catch (Exception ignored) {}
                try (var rs2 = XJDBC.query("SELECT @@IDENTITY")) {
                    if (rs2.next()) return rs2.getInt(1);
                } catch (Exception ignored) {}
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(NhaCungCap entity) {
        String sql = "UPDATE NhaCungCap SET TenNCC=?, DiaChi=?, SoDienThoai=?, Email=?, TrangThai=? WHERE ID=?";
        try {
            int affected = XJDBC.update(sql,
                    entity.getTenNCC(),
                    entity.getDiaChi(),
                    entity.getSoDienThoai(),
                    entity.getEmail(),
                    entity.getTrangThai(),
                    entity.getId());
            return affected > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM NhaCungCap WHERE ID=?";
        try {
            int affected = XJDBC.update(sql, id);
            return affected > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
