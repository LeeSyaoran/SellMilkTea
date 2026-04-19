package dao.impl;

import dao.DoUongDAO;
import dao.LoaiDoUongDAO;
import dao.ThuongHieuDAO;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.DoUong;
import utils.XJDBC;

public class DoUongDAOImpl implements DoUongDAO {

    private DoUong mapRow(ResultSet rs) throws Exception {
        DoUong d = new DoUong();
        d.setId(rs.getInt("ID"));
        d.setTenDoUong(rs.getNString("TenDoUong"));
        Object loai = rs.getObject("LoaiDoUongID");
        d.setLoaiDoUongID(loai == null ? null : rs.getInt("LoaiDoUongID"));
        Object th = rs.getObject("ThuongHieuID");
        d.setThuongHieuID(th == null ? null : rs.getInt("ThuongHieuID"));
        d.setGiaBanMacDinh(rs.getBigDecimal("GiaBanMacDinh"));
        d.setHinhAnh(rs.getString("HinhAnh"));
        d.setMoTa(rs.getNString("MoTa"));
        d.setLaTopping(rs.getBoolean("LaTopping"));
        d.setTrangThai(rs.getNString("TrangThai"));
        d.setThuocTinhMoRong(rs.getNString("ThuocTinhMoRong"));
        d.setCreatedAt(rs.getTimestamp("CreatedAt"));
        d.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
        return d;
    }

    @Override
    public void insert(DoUong e) {
        String sql = "INSERT INTO DoUong (TenDoUong, LoaiDoUongID, ThuongHieuID, GiaBanMacDinh, HinhAnh, MoTa, LaTopping, TrangThai, ThuocTinhMoRong) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        XJDBC.update(sql, e.getTenDoUong(), e.getLoaiDoUongID(), e.getThuongHieuID(), e.getGiaBanMacDinh(),
                e.getHinhAnh(), e.getMoTa(), e.isLaTopping(), e.getTrangThai(), e.getThuocTinhMoRong());
    }

    @Override
    public void update(DoUong e) {
        String sql = "UPDATE DoUong SET TenDoUong=?, LoaiDoUongID=?, ThuongHieuID=?, GiaBanMacDinh=?, "
                   + "HinhAnh=?, MoTa=?, LaTopping=?, TrangThai=?, ThuocTinhMoRong=?, UpdatedAt=GETDATE() WHERE ID=?";
        XJDBC.update(sql, e.getTenDoUong(), e.getLoaiDoUongID(), e.getThuongHieuID(), e.getGiaBanMacDinh(),
                e.getHinhAnh(), e.getMoTa(), e.isLaTopping(), e.getTrangThai(), e.getThuocTinhMoRong(), e.getId());
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM DoUong WHERE ID=?";
        XJDBC.update(sql, id);
    }

    @Override
    public DoUong selectById(int id) {
        String sql = "SELECT * FROM DoUong WHERE ID=?";
        try (var rs = XJDBC.query(sql, id)) {
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public List<DoUong> selectAll() {
        String sql = "SELECT * FROM DoUong ORDER BY TenDoUong";
        List<DoUong> list = new ArrayList<>();
        try (var rs = XJDBC.query(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<DoUong> selectByKeyword(String keyword) {
        String kw = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        String sql = "SELECT * FROM DoUong WHERE TenDoUong LIKE ? OR MoTa LIKE ? ORDER BY TenDoUong";
        List<DoUong> list = new ArrayList<>();
        try (var rs = XJDBC.query(sql, kw, kw)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<DoUong> selectAllWithTonKho(int chiNhanhID) {
        String sql = "SELECT d.*, ISNULL(tk.SoLuongTon, 0) AS SoLuongTon "
                   + "FROM DoUong d "
                   + "LEFT JOIN TonKho tk ON d.ID = tk.DoUongID AND tk.ChiNhanhID = ? "
                   + "ORDER BY d.TenDoUong";
        List<DoUong> list = new ArrayList<>();
        try (var rs = XJDBC.query(sql, chiNhanhID)) {
            while (rs.next()) {
                DoUong d = mapRow(rs);
                d.setSoLuongTon(rs.getInt("SoLuongTon"));
                list.add(d);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public List<DoUong> search(String keyword, String tenLoai, String tenThuongHieu, String trangThai, Integer chiNhanhID) {
        StringBuilder sb = new StringBuilder("SELECT d.*, ISNULL(tk.SoLuongTon, 0) AS SoLuongTon FROM DoUong d "
                + "LEFT JOIN LoaiDoUong l ON d.LoaiDoUongID = l.ID "
                + "LEFT JOIN ThuongHieu t ON d.ThuongHieuID = t.ID "
                + "LEFT JOIN TonKho tk ON d.ID = tk.DoUongID AND tk.ChiNhanhID = ? WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        params.add(chiNhanhID);

        if (keyword != null && !keyword.isBlank()) {
            sb.append("AND (d.TenDoUong LIKE ? OR d.MoTa LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw); params.add(kw);
        }
        if (tenLoai != null) { sb.append("AND l.TenLoai = ? "); params.add(tenLoai); }
        if (tenThuongHieu != null) { sb.append("AND t.TenThuongHieu = ? "); params.add(tenThuongHieu); }
        if (trangThai != null) { sb.append("AND d.TrangThai = ? "); params.add(trangThai); }
        sb.append("ORDER BY d.TenDoUong");

        List<DoUong> list = new ArrayList<>();
        try (var rs = XJDBC.query(sb.toString(), params.toArray())) {
            while (rs.next()) {
                 DoUong d = mapRow(rs);
                d.setSoLuongTon(rs.getInt("SoLuongTon"));
                list.add(d);
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public boolean updateTrangThai(int id, String trangThai) {
        String sql = "UPDATE DoUong SET TrangThai=?, UpdatedAt=GETDATE() WHERE ID=?";
        return XJDBC.update(sql, trangThai, id) > 0;
    }

    @Override
    public int updateGiaHangLoat(double percent) {
        // percent: +10 => tăng 10%, -5 => giảm 5%
        String sql = "UPDATE DoUong SET GiaBanMacDinh = ROUND(GiaBanMacDinh * (1 + ?/100.0), 2), UpdatedAt=GETDATE()";
        return XJDBC.update(sql, percent);
    }

    @Override
    public int getLastInsertedId() {
        String sql = "SELECT TOP 1 ID FROM DoUong ORDER BY ID DESC";
        try (var rs = XJDBC.query(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { throw new RuntimeException(e); }
        return -1;
    }
}