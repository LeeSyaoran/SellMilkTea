package model;

import java.sql.Timestamp;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhaCungCap {
    private Integer id;
    private String tenNCC;
    private String diaChi;
    private String soDienThoai;
    private String email;
    private String trangThai; // HopTac / Ngung
    private Timestamp createdAt;

    @Override
    public String toString() {
        return tenNCC;
    }
}
