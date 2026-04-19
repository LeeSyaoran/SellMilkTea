package dao;

import model.NhanVien;
import java.util.List;

public interface NhanVienDAO {

    /**
     * Thêm một nhân viên mới vào CSDL.
     * @param model đối tượng NhanVien chứa thông tin cần thêm.
     */
    void insert(NhanVien model);

    /**
     * Cập nhật thông tin của một nhân viên trong CSDL.
     * @param model đối tượng NhanVien chứa thông tin cần cập nhật.
     */
    void update(NhanVien model);

    /**
     * Xóa một nhân viên khỏi CSDL dựa trên ID.
     * @param id ID của nhân viên cần xóa.
     */
    void delete(int id);

    /**
     * Lấy danh sách tất cả nhân viên từ CSDL.
     * @return List<NhanVien> danh sách nhân viên.
     */
    List<NhanVien> selectAll();

    /**
     * Tìm một nhân viên dựa trên ID.
     * @param id ID của nhân viên cần tìm.
     * @return NhanVien đối tượng nhân viên tìm thấy, hoặc null.
     */
    NhanVien findById(int id);
    
    /**
     * Tìm một nhân viên dựa trên tên đăng nhập (username).
     * @param username tên đăng nhập của nhân viên cần tìm.
     * @return NhanVien đối tượng nhân viên tìm thấy, hoặc null.
     */
    NhanVien findByUsername(String username);

    /**
     * Tìm kiếm nhân viên dựa trên từ khóa (tên hoặc SĐT).
     * @param keyword từ khóa tìm kiếm.
     * @return List<NhanVien> danh sách nhân viên phù hợp.
     */
    List<NhanVien> selectByConditions(String keyword, String vaiTro, Integer chiNhanhId);
}