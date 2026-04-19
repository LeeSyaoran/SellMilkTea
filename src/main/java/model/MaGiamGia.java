package model;

import java.util.Date;

public class MaGiamGia {

    private int id;
    private String ma;
    private String moTa;
    private String loaiGiam;
    private double giaTri;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private int soLanSuDung;
    private int soLanToiDa;
    private String trangThai;

    public MaGiamGia() {
    }

    public MaGiamGia(int id, String ma, String moTa, String loaiGiam, double giaTri,
                     Date ngayBatDau, Date ngayKetThuc,
                     int soLanSuDung, int soLanToiDa, String trangThai) {
        this.id = id;
        this.ma = ma;
        this.moTa = moTa;
        this.loaiGiam = loaiGiam;
        this.giaTri = giaTri;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.soLanSuDung = soLanSuDung;
        this.soLanToiDa = soLanToiDa;
        this.trangThai = trangThai;
    }

    // ================= Getter & Setter =================
    
    public int getId() { 
        return id; 
    }
    public void setId(int id) { 
        this.id = id; 
    }

    public String getMa() { 
        return ma; 
    }
    public void setMa(String ma) { 
        this.ma = ma; 
    }

    public String getMoTa() { 
        return moTa; 
    }
    public void setMoTa(String moTa) { 
        this.moTa = moTa; 
    }

    public String getLoaiGiam() { 
        return loaiGiam; 
    }
    public void setLoaiGiam(String loaiGiam) { 
        this.loaiGiam = loaiGiam; 
    }

    public double getGiaTri() { 
        return giaTri; 
    }
    public void setGiaTri(double giaTri) { 
        this.giaTri = giaTri; 
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

    public int getSoLanSuDung() { 
        return soLanSuDung; 
    }
    public void setSoLanSuDung(int soLanSuDung) { 
        this.soLanSuDung = soLanSuDung; 
    }

    public int getSoLanToiDa() { 
        return soLanToiDa; 
    }
    public void setSoLanToiDa(int soLanToiDa) { 
        this.soLanToiDa = soLanToiDa; 
    }

    public String getTrangThai() { 
        return trangThai; 
    }
    public void setTrangThai(String trangThai) { 
        this.trangThai = trangThai; 
    }
}
