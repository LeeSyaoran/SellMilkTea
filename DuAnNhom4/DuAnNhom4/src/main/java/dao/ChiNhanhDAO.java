package dao;

import java.util.List;
import model.ChiNhanh;

public interface ChiNhanhDAO {
    List<ChiNhanh> findAll();
    ChiNhanh findById(int id);
    void insert(ChiNhanh model);
    void update(ChiNhanh model);
    void delete(int id);
}