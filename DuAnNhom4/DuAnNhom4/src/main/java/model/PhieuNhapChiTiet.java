package model;

import java.math.BigDecimal;

public class PhieuNhapChiTiet {
    private int id;
    private int phieuNhapID;
    private int doUongID;
    private int soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;

    public PhieuNhapChiTiet() {
    }

    public PhieuNhapChiTiet(int id, int phieuNhapID, int doUongID, int soLuong, BigDecimal donGia, BigDecimal thanhTien) {
        this.id = id;
        this.phieuNhapID = phieuNhapID;
        this.doUongID = doUongID;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPhieuNhapID() {
        return phieuNhapID;
    }

    public void setPhieuNhapID(int phieuNhapID) {
        this.phieuNhapID = phieuNhapID;
    }

    public int getDoUongID() {
        return doUongID;
    }

    public void setDoUongID(int doUongID) {
        this.doUongID = doUongID;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }
}
