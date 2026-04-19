package model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichSuKho {
    private int id;
    private int doUongID;
    private int chiNhanhID;
    private String loaiGiaoDich; // 'Nhap', 'Xuat', 'KiemKho'
    private int soLuong;
    private Date ngayGiaoDich;
    private int nhanVienID;
    private Integer phieuNhapID; // Có thể null
    private String ghiChu;
}
