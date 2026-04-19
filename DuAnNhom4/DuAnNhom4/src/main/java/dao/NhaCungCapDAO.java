package dao;

import java.util.List;
import model.NhaCungCap;

public interface NhaCungCapDAO {
    List<NhaCungCap> findAll();
    NhaCungCap findById(Integer id);
    Integer idByTen(String ten);
    Integer insert(NhaCungCap entity);
    boolean update(NhaCungCap entity);
    boolean delete(Integer id);
}
