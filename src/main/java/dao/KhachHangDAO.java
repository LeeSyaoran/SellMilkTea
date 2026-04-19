package dao;

import model.KhachHang;
import java.util.List;

public interface KhachHangDAO {

    /**
     * Thêm một khách hàng mới vào CSDL.
     * @param model đối tượng KhachHang chứa thông tin cần thêm.
     */
    void insert(KhachHang model);

    /**
     * Cập nhật thông tin của một khách hàng trong CSDL.
     * @param model đối tượng KhachHang chứa thông tin cần cập nhật.
     */
    void update(KhachHang model);

    /**
     * Xóa một khách hàng khỏi CSDL dựa trên ID.
     * @param id ID của khách hàng cần xóa.
     */
    void delete(int id);

    /**
     * Lấy danh sách tất cả khách hàng từ CSDL.
     * @return List<KhachHang> danh sách khách hàng.
     */
    List<KhachHang> selectAll();

    /**
     * Tìm một khách hàng dựa trên ID.
     * @param id ID của khách hàng cần tìm.
     * @return KhachHang đối tượng khách hàng tìm thấy, hoặc null.
     */
    KhachHang findById(int id);
    
    /**
     * Tìm một khách hàng dựa trên số điện thoại.
     * @param sdt số điện thoại của khách hàng cần tìm.
     * @return KhachHang đối tượng khách hàng tìm thấy, hoặc null.
     */
    KhachHang findBySoDienThoai(String sdt);

    /**
     * Tìm kiếm khách hàng dựa trên từ khóa (tên hoặc SĐT).
     * @param keyword từ khóa tìm kiếm.
     * @return List<KhachHang> danh sách khách hàng phù hợp.
     */
    List<KhachHang> selectByKeyword(String keyword);
    
    /**
     * Lọc khách hàng theo cấp độ VIP và khoảng điểm thưởng.
     * @param capDoVIP Cấp độ VIP để lọc. Nếu là "Tất cả", bỏ qua điều kiện này.
     * @param diemMin Điểm thưởng tối thiểu.
     * @param diemMax Điểm thưởng tối đa.
     * @return List<KhachHang> danh sách khách hàng phù hợp.
     */
    List<KhachHang> filter(String capDoVIP, int diemMin, int diemMax);
}
