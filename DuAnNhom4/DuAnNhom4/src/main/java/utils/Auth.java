package utils;

import model.NhanVien;

/**
 * Lớp tiện ích Auth để quản lý thông tin đăng nhập của người dùng.
 * Cung cấp các phương thức để truy cập, xóa và kiểm tra trạng thái đăng nhập
 * cũng như vai trò của người dùng hiện tại.
 */
public class Auth {

    /**
     * Đối tượng NhanVien đang đăng nhập vào hệ thống.
     * Đây là một biến static để có thể truy cập từ bất kỳ đâu trong ứng dụng.
     * Nó sẽ là null nếu không có ai đăng nhập.
     */
    public static NhanVien user = null;

    /**
     * Xóa thông tin của người dùng đang đăng nhập.
     * Thường được gọi khi người dùng đăng xuất.
     */
    public static void clear() {
        Auth.user = null;
    }

    /**
     * Kiểm tra xem có người dùng nào đang đăng nhập hay không.
     * @return true nếu có người dùng đang đăng nhập, ngược lại false.
     */
    public static boolean isLogin() {
        return Auth.user != null;
    }

    /**
     * Kiểm tra xem người dùng đang đăng nhập có phải là quản lý hay không.
     * Yêu cầu người dùng phải đăng nhập trước.
     * @return true nếu người dùng đã đăng nhập và có vai trò là "QuanLy".
     */
    public static boolean isManager() {
        // isLogin() đảm bảo user không null trước khi gọi getVaiTro()
        return Auth.isLogin() && user.getVaiTro().equalsIgnoreCase("QuanLy");
    }
}
