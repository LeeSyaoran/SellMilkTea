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
public class DoUong {
    private int id;
    private String tenDoUong;
    private Integer loaiDoUongID;
    private Integer thuongHieuID;
    private BigDecimal giaBanMacDinh;
    private String hinhAnh;
    private String moTa;
    private boolean laTopping;
    private String trangThai;
    private String thuocTinhMoRong;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer soLuongTon;
}
