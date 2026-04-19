package dao.impl;

import dao.ChiNhanhDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ChiNhanh;
import utils.XJDBC;

public class ChiNhanhDAOImpl implements ChiNhanhDAO {

    @Override
    public List<ChiNhanh> findAll() {
        String sql = "SELECT * FROM ChiNhanh";
        return select(sql);
    }

    @Override
    public ChiNhanh findById(int id) {
        String sql = "SELECT * FROM ChiNhanh WHERE ID = ?";
        List<ChiNhanh> list = select(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public void insert(ChiNhanh model) {
        String sql = "INSERT INTO ChiNhanh (TenChiNhanh, DiaChi, SoDienThoai, TrangThai) VALUES (?, ?, ?, ?)";
        XJDBC.update(sql,
                model.getTenChiNhanh(),
                model.getDiaChi(),
                model.getSoDienThoai(),
                model.getTrangThai());
    }

    @Override
    public void update(ChiNhanh model) {
        String sql = "UPDATE ChiNhanh SET TenChiNhanh = ?, DiaChi = ?, SoDienThoai = ?, TrangThai = ? WHERE ID = ?";
        XJDBC.update(sql,
                model.getTenChiNhanh(),
                model.getDiaChi(),
                model.getSoDienThoai(),
                model.getTrangThai(),
                model.getId());
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM ChiNhanh WHERE ID = ?";
        XJDBC.update(sql, id);
    }

    private List<ChiNhanh> select(String sql, Object... args) {
        List<ChiNhanh> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = XJDBC.query(sql, args);
            while (rs.next()) {
                ChiNhanh model = readFromResultSet(rs);
                list.add(model);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            XJDBC.closeQuietly(rs);
        }
        return list;
    }

    private ChiNhanh readFromResultSet(ResultSet rs) throws SQLException {
        ChiNhanh model = new ChiNhanh();
        model.setId(rs.getInt("ID"));
        model.setTenChiNhanh(rs.getString("TenChiNhanh"));
        model.setDiaChi(rs.getString("DiaChi"));
        model.setSoDienThoai(rs.getString("SoDienThoai"));
        model.setTrangThai(rs.getString("TrangThai"));
        return model;
    }
}