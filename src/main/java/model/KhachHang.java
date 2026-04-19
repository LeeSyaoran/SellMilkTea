package model;

import java.util.Date;

public class KhachHang {

    private int id;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private int diemThuong;
    private String capDoVIP;
    private Date ngayDangKy;
    private String trangThai;
    private int soDonDaMua;

    public KhachHang() {
    }

    public KhachHang(int id, String hoTen, String soDienThoai, String email, String diaChi, int diemThuong, String capDoVIP, Date ngayDangKy, String trangThai) {
        this.id = id;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
        this.diemThuong = diemThuong;
        this.capDoVIP = capDoVIP;
        this.ngayDangKy = ngayDangKy;
        this.trangThai = trangThai;
    }

    public int getSoDonDaMua() {
        return soDonDaMua;
    }

    public void setSoDonDaMua(int soDonDaMua) {
        this.soDonDaMua = soDonDaMua;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public int getDiemThuong() {
        return diemThuong;
    }

    public void setDiemThuong(int diemThuong) {
        this.diemThuong = diemThuong;
    }

    public String getCapDoVIP() {
        return capDoVIP;
    }

    public void setCapDoVIP(String capDoVIP) {
        this.capDoVIP = capDoVIP;
    }

    public Date getNgayDangKy() {
        return ngayDangKy;
    }

    public void setNgayDangKy(Date ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return hoTen + " - " + soDienThoai;
    }
}