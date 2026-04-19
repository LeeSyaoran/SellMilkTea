package dao;

import utils.XJDBC;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TonKhoDAO {
    public List<Object[]> getTonKhoByChiNhanh(int chiNhanhID) {
        List<Object[]> list = new ArrayList<>();
        String sql = """
                     SELECT
                         ROW_NUMBER() OVER (ORDER BY du.ID) as STT,
                         du.ID,
                         du.TenDoUong,
                         tk.SoLuongTon
                     FROM TonKho tk
                     JOIN DoUong du ON tk.DoUongID = du.ID
                     WHERE tk.ChiNhanhID = ? AND du.LaTopping = 0
                     """;
        try {
            ResultSet rs = XJDBC.query(sql, chiNhanhID);
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("STT"),
                    rs.getInt("ID"),
                    rs.getString("TenDoUong"),
                    rs.getInt("SoLuongTon"),
                    null, // Tồn thực tế (để trống)
                    null, // Chênh lệch (để trống)
                    ""    // Ghi chú
                });
            }
            rs.getStatement().getConnection().close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void updateSoLuong(int doUongID, int chiNhanhID, int soLuongMoi) {
        String sql = """
                     MERGE TonKho AS target
                     USING (SELECT ? AS DoUongID, ? AS ChiNhanhID, ? AS SoLuongTon) AS source
                     ON (target.DoUongID = source.DoUongID AND target.ChiNhanhID = source.ChiNhanhID)
                     WHEN MATCHED THEN
                         UPDATE SET SoLuongTon = source.SoLuongTon, UpdatedAt = GETDATE()
                     WHEN NOT MATCHED THEN
                         INSERT (DoUongID, ChiNhanhID, SoLuongTon, NguongCanhBao, UpdatedAt)
                         VALUES (source.DoUongID, source.ChiNhanhID, source.SoLuongTon, 10, GETDATE());
                     """;
        XJDBC.update(sql, doUongID, chiNhanhID, soLuongMoi);
    }
}
