/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.impl;

import dao.ThuongHieuDAO;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.ThuongHieu;
import utils.XJDBC;

public class ThuongHieuDAOImpl implements ThuongHieuDAO {

    private ThuongHieu mapRow(ResultSet rs) throws Exception {
        ThuongHieu t = new ThuongHieu();
        t.setId(rs.getInt("ID"));
        t.setTenThuongHieu(rs.getNString("TenThuongHieu"));
        t.setMoTa(rs.getNString("MoTa"));
        t.setTrangThai(rs.getNString("TrangThai"));
        t.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return t;
    }

    @Override
    public List<ThuongHieu> findAll() {
        String sql = "SELECT ID, TenThuongHieu, MoTa, TrangThai, CreatedAt FROM ThuongHieu ORDER BY TenThuongHieu";
        List<ThuongHieu> list = new ArrayList<>();
        try (var rs = XJDBC.query(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public ThuongHieu findById(Integer id) {
        String sql = "SELECT ID, TenThuongHieu, MoTa, TrangThai, CreatedAt FROM ThuongHieu WHERE ID=?";
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
        String sql = "SELECT ID FROM ThuongHieu WHERE TenThuongHieu = ?";
        try (var rs = XJDBC.query(sql, ten)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public String tenThuongHieuById(Integer id) {
        if (id == null) return null;
        String sql = "SELECT TenThuongHieu FROM ThuongHieu WHERE ID = ?";
        try (var rs = XJDBC.query(sql, id)) {
            if (rs.next()) return rs.getNString(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // --- Bổ sung CRUD ---
    @Override
    public Integer insert(ThuongHieu entity) {
        String sql = "INSERT INTO ThuongHieu (TenThuongHieu, MoTa, TrangThai) VALUES (?, ?, ?)";
        try {
            int affected = XJDBC.update(sql,
                    entity.getTenThuongHieu(),
                    entity.getMoTa(),
                    entity.getTrangThai());
            if (affected > 0) {
                try (var rs = XJDBC.query("SELECT SCOPE_IDENTITY()")) {
                    if (rs.next()) return rs.getInt(1);
                } catch (Exception ignored) {
                }
                try (var rs2 = XJDBC.query("SELECT @@IDENTITY")) {
                    if (rs2.next()) return rs2.getInt(1);
                } catch (Exception ignored) {
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(ThuongHieu entity) {
        String sql = "UPDATE ThuongHieu SET TenThuongHieu=?, MoTa=?, TrangThai=? WHERE ID=?";
        try {
            int affected = XJDBC.update(sql,
                    entity.getTenThuongHieu(),
                    entity.getMoTa(),
                    entity.getTrangThai(),
                    entity.getId());
            return affected > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM ThuongHieu WHERE ID=?";
        try {
            int affected = XJDBC.update(sql, id);
            return affected > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}