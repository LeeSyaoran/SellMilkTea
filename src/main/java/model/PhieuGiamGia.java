package model;

import java.math.BigDecimal;
import java.util.Date;

public class PhieuGiamGia {

    private int ID;
    private String maGiamGia;
    private String tenChuongTrinh;
    private String loaiGiam;
    private BigDecimal giaTriGiam;
    private BigDecimal dieuKienToiThieu;
    private int soLuongToiDa;
    private int soLuongDaDung;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private String apDungCho;
    private String danhSachApDung;
    private String trangThai;
    private Date createdAt;

    public PhieuGiamGia() {
    }

    public PhieuGiamGia(int ID, String maGiamGia, String tenChuongTrinh, String loaiGiam, BigDecimal giaTriGiam, BigDecimal dieuKienToiThieu, int soLuongToiDa, int soLuongDaDung, Date ngayBatDau, Date ngayKetThuc, String apDungCho, String danhSachApDung, String trangThai, Date createdAt) {
        this.ID = ID;
        this.maGiamGia = maGiamGia;
        this.tenChuongTrinh = tenChuongTrinh;
        this.loaiGiam = loaiGiam;
        this.giaTriGiam = giaTriGiam;
        this.dieuKienToiThieu = dieuKienToiThieu;
        this.soLuongToiDa = soLuongToiDa;
        this.soLuongDaDung = soLuongDaDung;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.apDungCho = apDungCho;
        this.danhSachApDung = danhSachApDung;
        this.trangThai = trangThai;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getMaGiamGia() {
        return maGiamGia;
    }

    public void setMaGiamGia(String maGiamGia) {
        this.maGiamGia = maGiamGia;
    }

    public String getTenChuongTrinh() {
        return tenChuongTrinh;
    }

    public void setTenChuongTrinh(String tenChuongTrinh) {
        this.tenChuongTrinh = tenChuongTrinh;
    }

    public String getLoaiGiam() {
        return loaiGiam;
    }

    public void setLoaiGiam(String loaiGiam) {
        this.loaiGiam = loaiGiam;
    }

    public BigDecimal getGiaTriGiam() {
        return giaTriGiam;
    }

    public void setGiaTriGiam(BigDecimal giaTriGiam) {
        this.giaTriGiam = giaTriGiam;
    }

    public BigDecimal getDieuKienToiThieu() {
        return dieuKienToiThieu;
    }

    public void setDieuKienToiThieu(BigDecimal dieuKienToiThieu) {
        this.dieuKienToiThieu = dieuKienToiThieu;
    }

    public int getSoLuongToiDa() {
        return soLuongToiDa;
    }

    public void setSoLuongToiDa(int soLuongToiDa) {
        this.soLuongToiDa = soLuongToiDa;
    }

    public int getSoLuongDaDung() {
        return soLuongDaDung;
    }

    public void setSoLuongDaDung(int soLuongDaDung) {
        this.soLuongDaDung = soLuongDaDung;
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public Date getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(Date ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getApDungCho() {
        return apDungCho;
    }

    public void setApDungCho(String apDungCho) {
        this.apDungCho = apDungCho;
    }

    public String getDanhSachApDung() {
        return danhSachApDung;
    }

    public void setDanhSachApDung(String danhSachApDung) {
        this.danhSachApDung = danhSachApDung;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
