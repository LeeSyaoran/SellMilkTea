package dao;

import java.util.List;
import model.PhieuNhap;

public interface PhieuNhapDAO {

    List<PhieuNhap> selectBySql(String sql, Object... args);

    List<PhieuNhap> selectAll();

    PhieuNhap selectById(Integer id);
    
    List<PhieuNhap> findByFilters(Integer nccId, Integer cnId);

    void insert(PhieuNhap model);

    void update(PhieuNhap model);

    void delete(Integer id);
}
