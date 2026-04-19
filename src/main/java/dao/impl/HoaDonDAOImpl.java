package dao.impl;

import dao.HoaDonDAO;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.HoaDon;
import model.HoaDonChiTiet;
import utils.XJDBC;

public class HoaDonDAOImpl implements HoaDonDAO {

    private HoaDon mapRow(ResultSet rs) throws Exception {
        HoaDon h = new HoaDon();
        h.setId(rs.getInt("ID"));
        // Generate MaHoaDon as 'HD' + right-padded 6-digit ID
        h.setMaHoaDon("HD" + String.format("%06d", rs.getInt("ID")));
        h.setNgayLap(rs.getTimestamp("NgayLap"));
        h.setTongTien(rs.getBigDecimal("TongTien"));
        h.setGiamGia(rs.getBigDecimal("GiamGia"));
        Object kh = rs.getObject("KhachHangID");
        h.setKhachHangID(kh == null ? null : rs.getInt("KhachHangID"));
        Object nv = rs.getObject("NhanVienID");
        h.setNhanVienID(nv == null ? null : rs.getInt("NhanVienID"));
        Object cn = rs.getObject("ChiNhanhID");
        h.setChiNhanhID(cn == null ? null : rs.getInt("ChiNhanhID"));
        Object pg = rs.getObject("PhieuGiamGiaID");
        h.setPhieuGiamGiaID(pg == null ? null : rs.getInt("PhieuGiamGiaID"));
        h.setPhuongThucThanhToan(rs.getNString("PhuongThucThanhToan"));
        h.setTrangThai(rs.getNString("TrangThai"));
        h.setGhiChu(rs.getNString("GhiChu"));
        h.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return h;
    }

    private HoaDonChiTiet mapRowChiTiet(ResultSet rs) throws Exception {
        HoaDonChiTiet ct = new HoaDonChiTiet();
        ct.setId(rs.getInt("ID"));
        ct.setHoaDonID(rs.getInt("HoaDonID"));
        ct.setDoUongID(rs.getInt("DoUongID"));
        Object size = rs.getObject("SizeID");
        ct.setSizeID(size == null ? null : rs.getInt("SizeID"));
        ct.setSoLuong(rs.getInt("SoLuong"));
        ct.setDonGia(rs.getBigDecimal("DonGia"));
        ct.setDanhSachTopping(rs.getNString("DanhSachTopping"));
        ct.setGhiChu(rs.getNString("GhiChu"));
        return ct;
    }

    @Override
    public void insert(HoaDon e) {
        String sql = "INSERT INTO HoaDon (NgayLap, TongTien, GiamGia, KhachHangID, NhanVienID, ChiNhanhID, PhieuGiamGiaID, PhuongThucThanhToan, TrangThai, GhiChu) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        XJDBC.update(sql, e.getNgayLap(), e.getTongTien(), e.getGiamGia(), e.getKhachHangID(), 
                e.getNhanVienID(), e.getChiNhanhID(), e.getPhieuGiamGiaID(), 
                e.getPhuongThucThanhToan(), e.getTrangThai(), e.getGhiChu());
    }

    @Override
    public void update(HoaDon e) {
        String sql = "UPDATE HoaDon SET NgayLap=?, TongTien=?, GiamGia=?, KhachHangID=?, NhanVienID=?, ChiNhanhID=?, "
                   + "PhieuGiamGiaID=?, PhuongThucThanhToan=?, TrangThai=?, GhiChu=?, UpdatedAt=GETDATE() WHERE ID=?";
        XJDBC.update(sql, e.getNgayLap(), e.getTongTien(), e.getGiamGia(), e.getKhachHangID(), 
                e.getNhanVienID(), e.getChiNhanhID(), e.getPhieuGiamGiaID(), 
                e.getPhuongThucThanhToan(), e.getTrangThai(), e.getGhiChu(), e.getId());
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM HoaDon WHERE ID=?";
        XJDBC.update(sql, id);
    }

    @Override
    public HoaDon selectById(int id) {
        String sql = "SELECT * FROM HoaDon WHERE ID=?";
        try (var rs = XJDBC.query(sql, id)) {
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public List<HoaDon> selectAll() {
        String sql = "SELECT * FROM HoaDon ORDER BY NgayLap DESC";
        List<HoaDon> list = new ArrayList<>();
        try (var rs = XJDBC.query(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<HoaDon> selectByKeyword(String keyword) {
        String kw = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        String sql = "SELECT * FROM HoaDon WHERE GhiChu LIKE ? ORDER BY NgayLap DESC";
        List<HoaDon> list = new ArrayList<>();
        try (var rs = XJDBC.query(sql, kw)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<HoaDon> search(String keyword, String trangThai) {
        StringBuilder sb = new StringBuilder("SELECT * FROM HoaDon WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sb.append("AND GhiChu LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }
        if (trangThai != null) { sb.append("AND TrangThai = ? "); params.add(trangThai); }
        sb.append("ORDER BY NgayLap DESC");
        
        List<HoaDon> list = new ArrayList<>();
        try (var rs = XJDBC.query(sb.toString(), params.toArray())) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public void insertChiTiet(HoaDonChiTiet e) {
        String sql = "INSERT INTO HoaDonChiTiet (HoaDonID, DoUongID, SizeID, SoLuong, DonGia, DanhSachTopping, GhiChu) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        XJDBC.update(sql, e.getHoaDonID(), e.getDoUongID(), e.getSizeID(), e.getSoLuong(), 
                e.getDonGia(), e.getDanhSachTopping(), e.getGhiChu());
    }

    @Override
    public void deleteChiTiet(int id) {
        String sql = "DELETE FROM HoaDonChiTiet WHERE ID=?";
        XJDBC.update(sql, id);
    }

    @Override
    public List<HoaDonChiTiet> selectChiTietByHoaDon(int hoaDonID) {
        String sql = "SELECT * FROM HoaDonChiTiet WHERE HoaDonID=?";
        List<HoaDonChiTiet> list = new ArrayList<>();
        try (var rs = XJDBC.query(sql, hoaDonID)) {
            while (rs.next()) list.add(mapRowChiTiet(rs));
        } catch (Exception e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public int getLastInsertedId() {
        String sql = "SELECT TOP 1 ID FROM HoaDon ORDER BY ID DESC";
        try (var rs = XJDBC.query(sql)) {
            if (rs.next()) return rs.getInt("ID");
        } catch (Exception e) { throw new RuntimeException(e); }
        return -1;
    }

    @Override
    public List<HoaDon> selectByNhanVienAndDate(int nhanVienID, String fromDate, String toDate) {
        String sql = "SELECT * FROM HoaDon WHERE NhanVienID=? AND CAST(NgayLap AS DATE) BETWEEN ? AND ? ORDER BY NgayLap DESC";
        List<HoaDon> list = new ArrayList<>();
        try (var rs = XJDBC.query(sql, nhanVienID, fromDate, toDate)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<HoaDon> selectByChiNhanhAndDate(int chiNhanhID, String fromDate, String toDate) {
        String sql = "SELECT * FROM HoaDon WHERE ChiNhanhID=? AND CAST(NgayLap AS DATE) BETWEEN ? AND ? ORDER BY NgayLap DESC";
        List<HoaDon> list = new ArrayList<>();
        try (var rs = XJDBC.query(sql, chiNhanhID, fromDate, toDate)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { throw new RuntimeException(e); }
        return list;
    }
}
