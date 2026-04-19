package model;

import java.util.Date;

public class PhieuNhap {
    private int id;
    private String maPhieu;
    private Date ngayNhap;
    private double tongTien;
    private int nhaCungCapID;
    private int nhanVienID;
    private int chiNhanhID;
    private String trangThai;
    private String ghiChu;

    public PhieuNhap() {
    }

    public PhieuNhap(int id, String maPhieu, Date ngayNhap, double tongTien, int nhaCungCapID, int nhanVienID, int chiNhanhID, String trangThai, String ghiChu) {
        this.id = id;
        this.maPhieu = maPhieu;
        this.ngayNhap = ngayNhap;
        this.tongTien = tongTien;
        this.nhaCungCapID = nhaCungCapID;
        this.nhanVienID = nhanVienID;
        this.chiNhanhID = chiNhanhID;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public Date getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(Date ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public int getNhaCungCapID() {
        return nhaCungCapID;
    }

    public void setNhaCungCapID(int nhaCungCapID) {
        this.nhaCungCapID = nhaCungCapID;
    }

    public int getNhanVienID() {
        return nhanVienID;
    }

    public void setNhanVienID(int nhanVienID) {
        this.nhanVienID = nhanVienID;
    }

    public int getChiNhanhID() {
        return chiNhanhID;
    }

    public void setChiNhanhID(int chiNhanhID) {
        this.chiNhanhID = chiNhanhID;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    @Override
    public String toString() {
        return maPhieu;
    }
}
