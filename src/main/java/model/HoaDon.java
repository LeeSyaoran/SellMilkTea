/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Admin
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HoaDon {
    private int id;
    private String maHoaDon;
    private Timestamp ngayLap;
    private BigDecimal tongTien;
    private BigDecimal giamGia;
    private Integer khachHangID;
    private Integer nhanVienID;
    private Integer chiNhanhID;
    private Integer phieuGiamGiaID;
    private String phuongThucThanhToan;
    private String trangThai;
    private String ghiChu;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
