/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import model.DoUong;

public interface DoUongDAO {
    void insert(DoUong entity);
    void update(DoUong entity);
    void delete(int id);
    DoUong selectById(int id);
    List<DoUong> selectAll();
    List<DoUong> selectByKeyword(String keyword);
    List<DoUong> selectAllWithTonKho(int chiNhanhID);

    // Bổ sung cho UI
    List<DoUong> search(String keyword, String tenLoai, String tenThuongHieu, String trangThai, Integer chiNhanhID);
    boolean updateTrangThai(int id, String trangThai);
    int updateGiaHangLoat(double percent);
    int getLastInsertedId();
}