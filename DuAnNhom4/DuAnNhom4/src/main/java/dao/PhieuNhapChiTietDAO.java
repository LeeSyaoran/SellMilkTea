package dao;

import java.util.List;
import model.PhieuNhapChiTiet;

public interface PhieuNhapChiTietDAO {

    List<PhieuNhapChiTiet> selectBySql(String sql, Object... args);

    List<PhieuNhapChiTiet> selectAll();
    
    List<PhieuNhapChiTiet> selectByPhieuNhapID(Integer phieuNhapID);

    PhieuNhapChiTiet selectById(Integer id);

    void insert(PhieuNhapChiTiet model);

    void update(PhieuNhapChiTiet model);

    void delete(Integer id);
}
