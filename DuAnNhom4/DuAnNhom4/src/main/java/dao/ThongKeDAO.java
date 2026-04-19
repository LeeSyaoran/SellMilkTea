package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import utils.XJDBC;

public class ThongKeDAO {

    private List<Object[]> getListOfArray(String sql, String[] cols, Object... args) {
        try {
            List<Object[]> list = new ArrayList<>();
            ResultSet rs = XJDBC.query(sql, args);
            while (rs.next()) {
                Object[] vals = new Object[cols.length];
                for (int i = 0; i < cols.length; i++) {
                    vals[i] = rs.getObject(cols[i]);
                }
                list.add(vals);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Object[]> getDoanhThu(String tuNgay, String denNgay, int loaiBaoCao) {
        String sql;
        String[] cols;
        switch (loaiBaoCao) {
            case 0:
                sql = "{CALL SP_ThongKeDoanhThuTheoNgay(?, ?)}";
                cols = new String[]{"Ngay", "SoHoaDon", "DoanhThu"};
                break;
            case 1:
                sql = "{CALL SP_ThongKeDoanhThuTheoThang(?, ?)}";
        cols = new String[]{"Thang", "SoHoaDon", "DoanhThu"};
                break;
            case 2:
                sql = "{CALL SP_ThongKeDoanhThuTheoNam(?, ?)}";
        cols = new String[]{"Nam", "SoHoaDon", "DoanhThu"};
                break;
            default:
                throw new IllegalArgumentException("Invalid report type");
        }
        return this.getListOfArray(sql, cols, tuNgay, denNgay);
    }
    public List<Object[]> getDoanhThuTheoNgay(Date tuNgay, Date denNgay) {
        String sql = "SELECT CAST(NgayLap AS DATE) AS Ngay, COUNT(ID) AS SoHoaDon, SUM(TongTien) AS DoanhThu FROM HoaDon WHERE NgayLap BETWEEN ? AND ? AND TrangThai = 'DaThanhToan' GROUP BY CAST(NgayLap AS DATE) ORDER BY Ngay DESC";
        String[] cols = {"Ngay", "SoHoaDon", "DoanhThu"};
        return this.getListOfArray(sql, cols, tuNgay, denNgay);
    }

    public List<Object[]> getMonBanChay(Date tuNgay, Date denNgay) {
        String sql = "SELECT du.TenDoUong, s.TenSize, SUM(hdct.SoLuong) AS SoLuongBan, SUM(hdct.ThanhTien) AS TongTien FROM HoaDonChiTiet hdct JOIN DoUong du ON hdct.DoUongID = du.ID JOIN HoaDon hd ON hdct.HoaDonID = hd.ID JOIN Size s ON hdct.SizeID = s.ID WHERE hd.NgayLap BETWEEN ? AND ? AND hd.TrangThai = 'DaThanhToan' GROUP BY du.TenDoUong, s.TenSize ORDER BY SoLuongBan DESC";
        String[] cols = {"TenDoUong", "TenSize", "SoLuongBan", "TongTien"};
        return this.getListOfArray(sql, cols, tuNgay, denNgay);
    }
    
    public List<Object[]> getTonKhoTheoChiNhanh(int chiNhanhID) {
        String sql = "SELECT tk.DoUongID, du.TenDoUong, tk.SoLuongTon " +
                     "FROM TonKho tk JOIN DoUong du ON tk.DoUongID = du.ID " +
                     "WHERE tk.ChiNhanhID = ? ORDER BY du.TenDoUong";
        String[] cols = {"DoUongID", "TenDoUong", "SoLuongTon"};
        return this.getListOfArray(sql, cols, chiNhanhID);
    }

    public List<Integer> getNam() {
        String sql = "SELECT DISTINCT YEAR(NgayLap) FROM HoaDon ORDER BY YEAR(NgayLap) DESC";
        List<Integer> list = new ArrayList<>();
        try {
            ResultSet rs = XJDBC.query(sql);
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
