/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.impl;

import dao.SizeDAO;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Size;
import utils.XJDBC;

public class SizeDAOImpl implements SizeDAO {

    private Size mapRow(ResultSet rs) throws Exception {
        Size s = new Size();
        s.setId(rs.getInt("ID"));
        s.setTenSize(rs.getString("TenSize"));
        s.setHeSoGia(rs.getBigDecimal("HeSoGia"));
        return s;
    }

    @Override
    public List<Size> findAll() {
        String sql = "SELECT ID, TenSize, HeSoGia FROM Size ORDER BY TenSize";
        List<Size> list = new ArrayList<>();
        try (var rs = XJDBC.query(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Size findById(Integer id) {
        String sql = "SELECT ID, TenSize, HeSoGia FROM Size WHERE ID=?";
        try (var rs = XJDBC.query(sql, id)) {
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public Size findByTen(String tenSize) {
        String sql = "SELECT ID, TenSize, HeSoGia FROM Size WHERE TenSize=?";
        try (var rs = XJDBC.query(sql, tenSize)) {
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public boolean insert(Size entity) {
        String sql = "INSERT INTO Size (TenSize, HeSoGia) VALUES (?, ?)";
        return XJDBC.update(sql, entity.getTenSize(), entity.getHeSoGia()) > 0;
    }

    @Override
    public boolean update(Size entity) {
        String sql = "UPDATE Size SET TenSize=?, HeSoGia=? WHERE ID=?";
        return XJDBC.update(sql, entity.getTenSize(), entity.getHeSoGia(), entity.getId()) > 0;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Size WHERE ID=?";
        return XJDBC.update(sql, id) > 0;
    }
}