package dao;

import java.util.List;
import model.ThuongHieu;

public interface ThuongHieuDAO {
    List<ThuongHieu> findAll();
    ThuongHieu findById(Integer id);
    Integer idByTen(String ten);
    String tenThuongHieuById(Integer id);
    Integer insert(ThuongHieu entity);
    boolean update(ThuongHieu entity);
    boolean delete(Integer id);
}