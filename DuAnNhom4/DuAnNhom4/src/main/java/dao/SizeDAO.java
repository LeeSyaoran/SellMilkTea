package dao;

import java.util.List;
import model.Size;

public interface SizeDAO {
    List<Size> findAll();
    Size findById(Integer id);
    Size findByTen(String tenSize);
    boolean insert(Size entity);
    boolean update(Size entity);
    boolean delete(Integer id);
}