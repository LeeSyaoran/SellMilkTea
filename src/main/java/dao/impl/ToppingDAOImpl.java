package dao.impl;

import dao.ToppingDAO;
import model.Topping;
import utils.XJDBC;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ToppingDAOImpl implements ToppingDAO {

    private final String TABLE_NAME = "Topping";
    private final String SQL_SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
    private final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE ID = ?";
    private final String SQL_INSERT = "INSERT INTO " + TABLE_NAME + " (TenTopping, GiaTopping, TrangThai) VALUES (?, ?, ?)";
    private final String SQL_UPDATE = "UPDATE " + TABLE_NAME + " SET TenTopping = ?, GiaTopping = ?, TrangThai = ? WHERE ID = ?";
    private final String SQL_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE ID = ?";

    @Override
    public List<Topping> findAll() {
        List<Topping> list = new ArrayList<>();
        try (ResultSet rs = XJDBC.query(SQL_SELECT_ALL)) {
            while (rs.next()) {
                list.add(mapRowToTopping(rs));
            }
        } catch (Exception e) {
            // In a real app, use a logger instead of printStackTrace
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Topping findById(int id) {
        try (ResultSet rs = XJDBC.query(SQL_SELECT_BY_ID, id)) {
            if (rs.next()) {
                return mapRowToTopping(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert(Topping topping) {
        try {
            XJDBC.update(SQL_INSERT, topping.getTenTopping(), topping.getGiaTopping(), topping.getTrangThai());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Topping topping) {
        try {
            XJDBC.update(SQL_UPDATE, topping.getTenTopping(), topping.getGiaTopping(), topping.getTrangThai(), topping.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try {
            XJDBC.update(SQL_DELETE, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Topping mapRowToTopping(ResultSet rs) throws Exception {
        Topping topping = new Topping();
        topping.setId(rs.getInt("ID"));
        topping.setTenTopping(rs.getString("TenTopping"));
        topping.setGiaTopping(rs.getBigDecimal("GiaTopping"));
        topping.setTrangThai(rs.getString("TrangThai"));
        topping.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return topping;
    }
}
