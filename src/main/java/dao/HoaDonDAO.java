package dao;

import java.util.List;
import model.HoaDon;
import model.HoaDonChiTiet;

public interface HoaDonDAO {
    void insert(HoaDon entity);
    void update(HoaDon entity);
    void delete(int id);
    HoaDon selectById(int id);
    List<HoaDon> selectAll();
    List<HoaDon> selectByKeyword(String keyword);
    List<HoaDon> search(String keyword, String trangThai);
    
    // Chi tiết hóa đơn
    void insertChiTiet(HoaDonChiTiet entity);
    void deleteChiTiet(int id);
    List<HoaDonChiTiet> selectChiTietByHoaDon(int hoaDonID);
    
    // Hỗ trợ
    int getLastInsertedId();
    List<HoaDon> selectByNhanVienAndDate(int nhanVienID, String fromDate, String toDate);
    List<HoaDon> selectByChiNhanhAndDate(int chiNhanhID, String fromDate, String toDate);
}
