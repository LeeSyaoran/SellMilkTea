package model;

import java.util.Date;

public class NhanVien {

    private int id;
    private String username;
    private String passwordHash;
    private String hoTen;
    private String vaiTro;
    private Integer chiNhanhID;
    private String soDienThoai;
    private String email;
    private Date ngaySinh;
    private String gioiTinh;
    private String trangThai;

    // Constructors
    public NhanVien() {
    }

    public NhanVien(int id, String username, String passwordHash, String hoTen, String vaiTro, Integer chiNhanhID, String soDienThoai, String email, Date ngaySinh, String gioiTinh, String trangThai) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.hoTen = hoTen;
        this.vaiTro = vaiTro;
        this.chiNhanhID = chiNhanhID;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.trangThai = trangThai;
    }
    // Getters and Setters  
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }   
    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public String getHoTen() {
        return hoTen;
    }
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }
    public String getVaiTro() {
        return vaiTro;
    }
    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }
    public Integer getChiNhanhID() {
        return chiNhanhID;
    }
    public void setChiNhanhID(Integer chiNhanhID) {
        this.chiNhanhID = chiNhanhID;
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
    public Date getNgaySinh() {
        return ngaySinh;
    }
    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }
    public String getGioiTinh() {
        return gioiTinh;
    }
    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }
    public String getTrangThai() {
        return trangThai;
    }
        public void setTrangThai(String trangThai) {
            this.trangThai = trangThai;
        }
    
        @Override
        public String toString() {
            return hoTen;
        }
    }