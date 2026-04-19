package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Topping {
    private Integer id;
    private String tenTopping;
    private BigDecimal giaTopping;
    private String trangThai;
    private Timestamp createdAt;

    public Topping() {}

    public Topping(Integer id, String tenTopping, BigDecimal giaTopping, String trangThai) {
        this.id = id;
        this.tenTopping = tenTopping;
        this.giaTopping = giaTopping;
        this.trangThai = trangThai;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTenTopping() {
        return tenTopping;
    }

    public void setTenTopping(String tenTopping) {
        this.tenTopping = tenTopping;
    }

    public BigDecimal getGiaTopping() {
        return giaTopping;
    }

    public void setGiaTopping(BigDecimal giaTopping) {
        this.giaTopping = giaTopping;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return tenTopping;
    }
}
