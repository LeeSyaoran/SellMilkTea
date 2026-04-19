package dao;

import java.util.List;
import model.LoaiDoUong;

public interface LoaiDoUongDAO {
    List<LoaiDoUong> findAll();
    List<LoaiDoUong> searchByNameOrDesc(String keyword);
    LoaiDoUong findById(Integer id);
    Integer idByTen(String tenLoai);
    String tenLoaiById(Integer id);
    boolean insert(LoaiDoUong entity);
    boolean update(LoaiDoUong entity);
    boolean delete(int id); // cân nhắc xóa mềm tùy yêu cầu
}