/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.impl;

import dao.LoaiDoUongDAO;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.LoaiDoUong;
import utils.XJDBC;

public class LoaiDoUongDAOImpl implements LoaiDoUongDAO {

    private LoaiDoUong mapRow(ResultSet rs) throws Exception {
        LoaiDoUong l = new LoaiDoUong();
        l.setId(rs.getInt("ID"));
        l.setTenLoai(rs.getNString("TenLoai"));
        l.setMoTa(rs.getNString("MoTa"));
        l.setTrangThai(rs.getNString("TrangThai"));
        l.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return l;
    }

    @Override
    public List<LoaiDoUong> findAll() {
        String sql = "SELECT ID, TenLoai, MoTa, TrangThai, CreatedAt FROM LoaiDoUong ORDER BY TenLoai";
        List<LoaiDoUong> list = new ArrayList<>();
        try (var rs = XJDBC.query(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            throw new RuntimeException("findAll LoaiDoUong lỗi: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<LoaiDoUong> searchByNameOrDesc(String keyword) {
        String kw = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        String sql = "SELECT ID, TenLoai, MoTa, TrangThai, CreatedAt FROM LoaiDoUong "
                   + "WHERE TenLoai LIKE ? OR MoTa LIKE ? ORDER BY TenLoai";
        List<LoaiDoUong> list = new ArrayList<>();
        try (var rs = XJDBC.query(sql, kw, kw)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            throw new RuntimeException("searchByNameOrDesc lỗi: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public LoaiDoUong findById(Integer id) {
        String sql = "SELECT ID, TenLoai, MoTa, TrangThai, CreatedAt FROM LoaiDoUong WHERE ID=?";
        try (var rs = XJDBC.query(sql, id)) {
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) {
            throw new RuntimeException("findById lỗi: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Integer idByTen(String tenLoai) {
        if (tenLoai == null) return null;
        String sql = "SELECT ID FROM LoaiDoUong WHERE TenLoai = ?";
        try (var rs = XJDBC.query(sql, tenLoai)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException("idByTen lỗi: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String tenLoaiById(Integer id) {
        if (id == null) return null;
        String sql = "SELECT TenLoai FROM LoaiDoUong WHERE ID = ?";
        try (var rs = XJDBC.query(sql, id)) {
            if (rs.next()) return rs.getNString(1);
        } catch (Exception e) {
            throw new RuntimeException("tenLoaiById lỗi: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean insert(LoaiDoUong entity) {
        String sql = "INSERT INTO LoaiDoUong (TenLoai, MoTa, TrangThai) VALUES (?, ?, ?)";
        return XJDBC.update(sql, entity.getTenLoai(), entity.getMoTa(), entity.getTrangThai()) > 0;
    }

    @Override
    public boolean update(LoaiDoUong entity) {
        String sql = "UPDATE LoaiDoUong SET TenLoai=?, MoTa=?, TrangThai=? WHERE ID=?";
        return XJDBC.update(sql, entity.getTenLoai(), entity.getMoTa(), entity.getTrangThai(), entity.getId()) > 0;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM LoaiDoUong WHERE ID=?";
        return XJDBC.update(sql, id) > 0;
    }
}