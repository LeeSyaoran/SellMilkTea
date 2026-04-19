/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;
import dao.DoUongDAO;
import dao.LoaiDoUongDAO;
import dao.ThuongHieuDAO;
import dao.TonKhoDAO;

import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import model.DoUong;
import utils.Auth;
/**
 *
 * @author huydo
 */
public class BanHangForm extends javax.swing.JFrame {
    private final DoUongDAO doUongDAO = new dao.impl.DoUongDAOImpl();
    private final LoaiDoUongDAO loaiDoUongDAO = new dao.impl.LoaiDoUongDAOImpl();
    private final ThuongHieuDAO thuongHieuDAO = new dao.impl.ThuongHieuDAOImpl();
    private final TonKhoDAO tonKhoDAO = new TonKhoDAO();
    private final int chiNhanhID = Auth.user.getChiNhanhID();
    /**
     * Creates new form BanHangForm
     */
    public BanHangForm() {
        initComponents();
        afterInit();
    }
     private void afterInit() {
        txtMa.setEditable(false);
        initCombos();
        initTable();
        bindActions();
        refreshTable();
    }
     private void initCombos() {
        // Bộ lọc
        cboTrangThai.setModel(new DefaultComboBoxModel<>(new String[]{"Tất cả", "HoatDong", "Ngung"}));
//        cboSize.setModel(new DefaultComboBoxModel<>(new String[]{"Tất cả", "S", "M", "L"})); // chỉ phục vụ filter/hiển thị
        // Loại đồ uống
        var loaiList = loaiDoUongDAO.findAll(); // List<LoaiDoUong>
        DefaultComboBoxModel<String> mLoaiFilter = new DefaultComboBoxModel<>();
        mLoaiFilter.addElement("Tất cả");
        loaiList.forEach(l -> mLoaiFilter.addElement(l.getTenLoai()));
        cboLoaiDoUong.setModel(mLoaiFilter);

        DefaultComboBoxModel<String> mLoaiDetail = new DefaultComboBoxModel<>();
        loaiList.forEach(l -> mLoaiDetail.addElement(l.getTenLoai()));
        cboLoaiDoUongDetail.setModel(mLoaiDetail);

        // Thương hiệu
        var thList = thuongHieuDAO.findAll();
        DefaultComboBoxModel<String> mThFilter = new DefaultComboBoxModel<>();
        mThFilter.addElement("Tất cả");
        thList.forEach(t -> mThFilter.addElement(t.getTenThuongHieu()));
        cboThuongHieu.setModel(mThFilter);

        DefaultComboBoxModel<String> mThDetail = new DefaultComboBoxModel<>();
        thList.forEach(t -> mThDetail.addElement(t.getTenThuongHieu()));
        cboThuongHieuDetail.setModel(mThDetail);

        // Trạng thái detail
        cboTrangThaiDetail.setModel(new DefaultComboBoxModel<>(new String[]{"HoatDong", "Ngung"}));
    }
     private void initTable() {
        tblDoUong1.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Tên đồ uống", "Loại", "Thương hiệu", "Giá mặc định", "Trạng thái", "Số Lượng Tồn"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return c == 6; }
        });
        tblDoUong1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDoUong1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onRowSelected();
        });

        // Listener for table cell edits
        tblDoUong1.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 6) { // "Số Lượng Tồn" column
                    DefaultTableModel model = (DefaultTableModel) tblDoUong1.getModel();
                    int id = (int) model.getValueAt(row, 0);
                    try {
                        int newQuantity = Integer.parseInt(model.getValueAt(row, 6).toString());
                        if (newQuantity < 0) {
                            JOptionPane.showMessageDialog(this, "Số lượng không thể âm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            refreshTable(); // Revert change
                            return;
                        }
                        tonKhoDAO.updateSoLuong(id, chiNhanhID, newQuantity);
                        JOptionPane.showMessageDialog(this, "Cập nhật số lượng tồn thành công cho đồ uống ID " + id);
                        // Optional: re-select the row after refresh if needed
                        refreshTable();
                        selectRowById(id);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Vui lòng nhập một số hợp lệ cho số lượng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        refreshTable(); // Revert change
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật số lượng tồn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        refreshTable(); // Revert change
                    }
                }
            }
        });
    }
     private void bindActions() {
        btnLoc.addActionListener(e -> applyFilter());
        btnXoaLoc.addActionListener(e -> clearFilter());
        btnThem.addActionListener(this::onThem);
        btnSua.addActionListener(this::onSua);
        btnXoa.addActionListener(this::onXoa);
        btnCapNhatGia.addActionListener(this::onCapNhatGia);
    }
     private void refreshTable() {
        List<model.DoUong> list = doUongDAO.selectAllWithTonKho(chiNhanhID);
        fillTable(list);
        lblTongMatHang.setText("Tổng mặt hàng: " + list.size());
        lblStatus.setText("Sẵn sàng");
    }
     private void applyFilter() {
        String keyword = txtSearch.getText().trim();
        String loai = safeSelected(cboLoaiDoUong);
        String th = safeSelected(cboThuongHieu);
        String tt = safeSelected(cboTrangThai);
        List<DoUong> list = doUongDAO.search(keyword, "Tất cả".equals(loai) ? null : loai,
                "Tất cả".equals(th) ? null : th,
                "Tất cả".equals(tt) ? null : tt,
                chiNhanhID);
        fillTable(list);
        lblTongMatHang.setText("Tổng mặt hàng: " + list.size());
        lblStatus.setText("Đã lọc");
    }
     private void clearFilter() {
        txtSearch.setText("");
        cboLoaiDoUong.setSelectedIndex(0);
        cboThuongHieu.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        refreshTable();
    }
    private void clear() {
        txtMa.setText("");
        txtTen.setText("");
        txtMoTa1.setText("");
        txtGiaMacDinh.setText("");
        txtSoLuongTon.setText("");
        cboLoaiDoUongDetail.setSelectedIndex(0);
        cboThuongHieuDetail.setSelectedIndex(0);
        cboTrangThaiDetail.setSelectedIndex(0);
        tblDoUong1.clearSelection();
    }
      private void fillTable(List<DoUong> list) {
        DefaultTableModel m = (DefaultTableModel) tblDoUong1.getModel();
        m.setRowCount(0);
        list.forEach(d -> m.addRow(new Object[]{
            d.getId(),
            d.getTenDoUong(),
            loaiDoUongDAO.tenLoaiById(d.getLoaiDoUongID()),
            thuongHieuDAO.tenThuongHieuById(d.getThuongHieuID()),
            d.getGiaBanMacDinh(),
            d.getTrangThai(),
            d.getSoLuongTon()
        }));
    }
      private void onRowSelected() {
        int row = tblDoUong1.getSelectedRow();
        if (row < 0) return;
        int id = (int) tblDoUong1.getValueAt(row, 0);
        DoUong d = doUongDAO.selectById(id);
        if (d == null) return;

        txtMa.setText(String.valueOf(d.getId()));
        txtTen.setText(d.getTenDoUong());
        txtGiaMacDinh.setText(d.getGiaBanMacDinh() != null ? d.getGiaBanMacDinh().toPlainString() : "");
        txtMoTa1.setText(d.getMoTa() == null ? "" : d.getMoTa());
        lblStatus.setText(d.getTrangThai());
        
        // Get quantity from table model
        Object soLuongTonObj = tblDoUong1.getValueAt(row, 6);
        txtSoLuongTon.setText(soLuongTonObj != null ? soLuongTonObj.toString() : "0");

        selectComboByName(cboLoaiDoUongDetail, loaiDoUongDAO.tenLoaiById(d.getLoaiDoUongID()));
        selectComboByName(cboThuongHieuDetail, thuongHieuDAO.tenThuongHieuById(d.getThuongHieuID()));
        selectComboByName(cboTrangThaiDetail, d.getTrangThai());
    }
private void onThem(ActionEvent e) {
        DoUong d = collectFromForm(false);
        if (d == null) return; // Validation failed in collectFromForm
        try {
            doUongDAO.insert(d);
            int newId = doUongDAO.getLastInsertedId();
            
            // The quantity is now in the DoUong object, use it to update stock
            tonKhoDAO.updateSoLuong(newId, chiNhanhID, d.getSoLuongTon());

            JOptionPane.showMessageDialog(this, "Thêm thành công");
            refreshTable();
            selectRowById(newId);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Thêm thất bại: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void onSua(ActionEvent e) {
        int row = tblDoUong1.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng trong bảng để sửa.");
            return;
        }
        DoUong d = collectFromForm(true);
        if (d == null) return; // Validation failed in collectFromForm
        try {
            // Update main drink details
            doUongDAO.update(d);

            // Update stock quantity using the value from the validated form
            tonKhoDAO.updateSoLuong(d.getId(), chiNhanhID, d.getSoLuongTon());

            JOptionPane.showMessageDialog(this, "Cập nhật thành công");
            refreshTable();
            selectRowById(d.getId());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
      private void onXoa(ActionEvent e) {
        int row = tblDoUong1.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng cần xóa");
            return;
        }
        int id = (int) tblDoUong1.getValueAt(row, 0);
        int cf = JOptionPane.showConfirmDialog(this, "Ngưng bán/xóa đồ uống ID=" + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (cf == JOptionPane.YES_OPTION) {
            // Khuyến nghị xóa mềm -> cập nhật trạng thái
            if (doUongDAO.updateTrangThai(id, "Ngung")) {
                JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái 'Ngung'");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Thao tác thất bại");
            }
        }
    }
      private void onCapNhatGia(ActionEvent e) {
        String val = JOptionPane.showInputDialog(this, "Nhập % tăng/giảm (vd -5, 10):", "0");
        if (val == null) return;
        try {
            double p = Double.parseDouble(val);
            int affected = doUongDAO.updateGiaHangLoat(p);
            JOptionPane.showMessageDialog(this, "Cập nhật giá thành công cho " + affected + " mặt hàng");
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá trị không hợp lệ");
        }
    }
      private DoUong collectFromForm(boolean includeId) {
        String ten = txtTen.getText().trim();
        String giaStr = txtGiaMacDinh.getText().trim();
        String soLuongStr = txtSoLuongTon.getText().trim();
        String loaiTen = safeSelected(cboLoaiDoUongDetail);
        String thTen = safeSelected(cboThuongHieuDetail);
        String trangThai = safeSelected(cboTrangThaiDetail);
        String moTa = txtMoTa1.getText().trim();

        // 1. Tên không được trống
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đồ uống không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTen.requestFocus();
            return null;
        }

        // 2. Giá phải là số không âm
        BigDecimal gia;
        if (giaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giá không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtGiaMacDinh.requestFocus();
            return null;
        }
        try {
            gia = new BigDecimal(giaStr);
            if (gia.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Giá phải là một số không âm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtGiaMacDinh.requestFocus();
                return null;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá không hợp lệ. Vui lòng nhập một số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtGiaMacDinh.requestFocus();
            return null;
        }

        // 3. Số lượng tồn phải là số nguyên không âm
        int soLuong;
        if (soLuongStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Số lượng tồn không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtSoLuongTon.requestFocus();
            return null;
        }
        try {
            soLuong = Integer.parseInt(soLuongStr);
            if (soLuong < 0) {
                JOptionPane.showMessageDialog(this, "Số lượng tồn phải là một số không âm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtSoLuongTon.requestFocus();
                return null;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng tồn không hợp lệ. Vui lòng nhập một số nguyên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtSoLuongTon.requestFocus();
            return null;
        }

        // 4. Phải chọn Loại và Thương hiệu
        Integer loaiId = loaiDoUongDAO.idByTen(loaiTen);
        if (loaiId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một loại đồ uống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            cboLoaiDoUongDetail.requestFocus();
            return null;
        }
        Integer thId = thuongHieuDAO.idByTen(thTen);
        if (thId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một thương hiệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            cboThuongHieuDetail.requestFocus();
            return null;
        }

        DoUong d = new DoUong();
        if (includeId) {
            try {
                d.setId(Integer.parseInt(txtMa.getText().trim()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "ID không hợp lệ để cập nhật.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        d.setTenDoUong(ten);
        d.setLoaiDoUongID(loaiId);
        d.setThuongHieuID(thId);
        d.setGiaBanMacDinh(gia);
        d.setMoTa(moTa.isEmpty() ? null : moTa);
        d.setTrangThai(trangThai);
        d.setLaTopping(false);
        d.setSoLuongTon(soLuong); // Set validated quantity

        return d;
    }
      private void selectRowById(int id) {
        DefaultTableModel m = (DefaultTableModel) tblDoUong1.getModel();
        for (int i = 0; i < m.getRowCount(); i++) {
            if (id == (int) m.getValueAt(i, 0)) {
                tblDoUong1.setRowSelectionInterval(i, i);
                tblDoUong1.scrollRectToVisible(tblDoUong1.getCellRect(i, 0, true));
                break;
            }
        }
    }
      private static String safeSelected(javax.swing.JComboBox<String> cbo) {
        Object o = cbo.getSelectedItem();
        return o == null ? "" : o.toString();
    }

    private static void selectComboByName(javax.swing.JComboBox<String> cbo, String name) {
        if (name == null) return;
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (name.equals(cbo.getItemAt(i))) {
                cbo.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        cboLoaiDoUong = new javax.swing.JComboBox<>();
        btnXoa = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        btnLoc = new javax.swing.JButton();
        cboTrangThai = new javax.swing.JComboBox<>();
        txtSearch = new javax.swing.JTextField();
        jButton16 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtMoTa1 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtSoLuongTon = new javax.swing.JTextField();
        lblTongMatHang = new javax.swing.JLabel();
        txtMa = new javax.swing.JTextField();
        txtTen = new javax.swing.JTextField();
        txtGiaMacDinh = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cboTrangThaiDetail = new javax.swing.JComboBox<>();
        btnXoaLoc = new javax.swing.JButton();
        cboLoaiDoUongDetail = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        btnLamMoi2 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        cboThuongHieuDetail = new javax.swing.JComboBox<>();
        btnCapNhatGia = new javax.swing.JButton();
        cboThuongHieu = new javax.swing.JComboBox<>();
        btnNhanVien = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDoUong1 = new javax.swing.JTable();
        btnThem = new javax.swing.JButton();
        btnPhieuGiamGia = new javax.swing.JButton();
        btnKhachHang1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAutoRequestFocus(false);
        setBackground(new java.awt.Color(225, 177, 112));
        setPreferredSize(new java.awt.Dimension(1500, 834));

        jPanel2.setBackground(new java.awt.Color(225, 177, 112));
        jPanel2.setForeground(new java.awt.Color(164, 115, 115));
        jPanel2.setPreferredSize(new java.awt.Dimension(1500, 834));

        cboLoaiDoUong.setForeground(new java.awt.Color(164, 115, 115));
        cboLoaiDoUong.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnXoa.setForeground(new java.awt.Color(164, 115, 115));
        btnXoa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/delete_one.png"))); // NOI18N
        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnSua.setForeground(new java.awt.Color(164, 115, 115));
        btnSua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fix.png"))); // NOI18N
        btnSua.setText("Sửa");

        jButton13.setBackground(new java.awt.Color(225, 177, 112));
        jButton13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton13.setForeground(new java.awt.Color(164, 115, 115));
        jButton13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/report.png"))); // NOI18N
        jButton13.setText("Báo Cáo");
        jButton13.setBorderPainted(false);
        jButton13.setContentAreaFilled(false);
        jButton13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton13.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        btnLoc.setForeground(new java.awt.Color(164, 115, 115));
        btnLoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/filter.png"))); // NOI18N
        btnLoc.setText("Lọc");

        cboTrangThai.setForeground(new java.awt.Color(164, 115, 115));
        cboTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboTrangThai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTrangThaiActionPerformed(evt);
            }
        });

        jButton16.setBackground(new java.awt.Color(225, 177, 112));
        jButton16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton16.setForeground(new java.awt.Color(164, 115, 115));
        jButton16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bill.png"))); // NOI18N
        jButton16.setText("Hóa Đơn");
        jButton16.setBorderPainted(false);
        jButton16.setContentAreaFilled(false);
        jButton16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton16.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton9.setBackground(new java.awt.Color(225, 177, 112));
        jButton9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton9.setForeground(new java.awt.Color(164, 115, 115));
        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/topping.png"))); // NOI18N
        jButton9.setText("Topping");
        jButton9.setBorderPainted(false);
        jButton9.setContentAreaFilled(false);
        jButton9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton11.setBackground(new java.awt.Color(225, 177, 112));
        jButton11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton11.setForeground(new java.awt.Color(164, 115, 115));
        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/trademark.png"))); // NOI18N
        jButton11.setText("Thương hiệu");
        jButton11.setBorderPainted(false);
        jButton11.setContentAreaFilled(false);
        jButton11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton11.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(225, 177, 112));
        jPanel1.setForeground(new java.awt.Color(164, 115, 115));

        txtMoTa1.setColumns(20);
        txtMoTa1.setRows(5);
        jScrollPane3.setViewportView(txtMoTa1);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(164, 115, 115));
        jLabel2.setText("Mô Tả");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(164, 115, 115));
        jLabel3.setText("Mã");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(164, 115, 115));
        jLabel4.setText("Tên");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(164, 115, 115));
        jLabel5.setText("Giá");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(164, 115, 115));
        jLabel7.setText("Số Lượng Tồn");

        lblTongMatHang.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTongMatHang.setForeground(new java.awt.Color(164, 115, 115));
        lblTongMatHang.setText("Tổng mặt hàng");

        txtMa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaActionPerformed(evt);
            }
        });

        txtTen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenActionPerformed(evt);
            }
        });

        txtGiaMacDinh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGiaMacDinhActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(164, 115, 115));
        jLabel8.setText("Trạng Thái");

        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(164, 115, 115));
        lblStatus.setText("Trạng Thái");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTongMatHang, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3)
                    .addComponent(jLabel2)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtSoLuongTon, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtGiaMacDinh, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtTen, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                                    .addComponent(txtMa, javax.swing.GroupLayout.Alignment.TRAILING))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTongMatHang)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(txtTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtGiaMacDinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtSoLuongTon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(164, 115, 115));
        jLabel6.setText("HOME");

        cboTrangThaiDetail.setForeground(new java.awt.Color(164, 115, 115));
        cboTrangThaiDetail.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboTrangThaiDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTrangThaiDetailActionPerformed(evt);
            }
        });

        btnXoaLoc.setForeground(new java.awt.Color(164, 115, 115));
        btnXoaLoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/delete_one.png"))); // NOI18N
        btnXoaLoc.setText("Xóa");

        cboLoaiDoUongDetail.setForeground(new java.awt.Color(164, 115, 115));
        cboLoaiDoUongDetail.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(164, 115, 115));
        jLabel1.setText("Tìm Kiếm ");

        jButton8.setBackground(new java.awt.Color(225, 177, 112));
        jButton8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton8.setForeground(new java.awt.Color(164, 115, 115));
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/size management.png"))); // NOI18N
        jButton8.setText("Quản Lý Size");
        jButton8.setBorderPainted(false);
        jButton8.setContentAreaFilled(false);
        jButton8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        btnLamMoi2.setForeground(new java.awt.Color(164, 115, 115));
        btnLamMoi2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/refresh.png"))); // NOI18N
        btnLamMoi2.setText("Làm Mới");
        btnLamMoi2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoi2ActionPerformed(evt);
            }
        });

        jButton12.setBackground(new java.awt.Color(225, 177, 112));
        jButton12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton12.setForeground(new java.awt.Color(164, 115, 115));
        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/supplier.png"))); // NOI18N
        jButton12.setText("Nhà cung cấp");
        jButton12.setBorderPainted(false);
        jButton12.setContentAreaFilled(false);
        jButton12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton12.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton14.setBackground(new java.awt.Color(225, 177, 112));
        jButton14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton14.setForeground(new java.awt.Color(164, 115, 115));
        jButton14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/check inventory.png"))); // NOI18N
        jButton14.setText("Kiểm Kho");
        jButton14.setBorderPainted(false);
        jButton14.setContentAreaFilled(false);
        jButton14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton14.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton10.setBackground(new java.awt.Color(225, 177, 112));
        jButton10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton10.setForeground(new java.awt.Color(164, 115, 115));
        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/type of drink.png"))); // NOI18N
        jButton10.setText("Loại đồ uống");
        jButton10.setBorderPainted(false);
        jButton10.setContentAreaFilled(false);
        jButton10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        cboThuongHieuDetail.setForeground(new java.awt.Color(164, 115, 115));
        cboThuongHieuDetail.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnCapNhatGia.setForeground(new java.awt.Color(164, 115, 115));
        btnCapNhatGia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/update.png"))); // NOI18N
        btnCapNhatGia.setText("Cập Nhật");

        cboThuongHieu.setForeground(new java.awt.Color(164, 115, 115));
        cboThuongHieu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnNhanVien.setBackground(new java.awt.Color(225, 177, 112));
        btnNhanVien.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnNhanVien.setForeground(new java.awt.Color(164, 115, 115));
        btnNhanVien.setIcon(new javax.swing.ImageIcon(getClass().getResource("/staff.png"))); // NOI18N
        btnNhanVien.setText("Nhân Viên");
        btnNhanVien.setBorderPainted(false);
        btnNhanVien.setContentAreaFilled(false);
        btnNhanVien.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNhanVien.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNhanVienActionPerformed(evt);
            }
        });

        tblDoUong1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8", "Title 9"
            }
        ));
        jScrollPane2.setViewportView(tblDoUong1);

        btnThem.setForeground(new java.awt.Color(164, 115, 115));
        btnThem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/plus.png"))); // NOI18N
        btnThem.setText("Thêm");

        btnPhieuGiamGia.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnPhieuGiamGia.setForeground(new java.awt.Color(164, 115, 115));
        btnPhieuGiamGia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/coupon.png"))); // NOI18N
        btnPhieuGiamGia.setText("Phiếu Giảm Giá");
        btnPhieuGiamGia.setBorderPainted(false);
        btnPhieuGiamGia.setContentAreaFilled(false);
        btnPhieuGiamGia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPhieuGiamGiaActionPerformed(evt);
            }
        });

        btnKhachHang1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnKhachHang1.setForeground(new java.awt.Color(164, 115, 115));
        btnKhachHang1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/customer.png"))); // NOI18N
        btnKhachHang1.setText("Khách Hàng");
        btnKhachHang1.setBorderPainted(false);
        btnKhachHang1.setContentAreaFilled(false);
        btnKhachHang1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnKhachHang1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnKhachHang1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKhachHang1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(480, 480, 480))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnNhanVien, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jButton10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(88, 88, 88)
                        .addComponent(btnPhieuGiamGia, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnKhachHang1, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(cboLoaiDoUong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(38, 38, 38)
                                .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(35, 35, 35)
                                .addComponent(cboThuongHieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cboThuongHieuDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(cboLoaiDoUongDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cboTrangThaiDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnXoaLoc)
                                .addGap(37, 37, 37)
                                .addComponent(btnLoc)))
                        .addGap(266, 266, 266))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 624, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(btnThem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnSua)
                                .addGap(18, 18, 18)
                                .addComponent(btnXoa)
                                .addGap(38, 38, 38)
                                .addComponent(btnCapNhatGia)
                                .addGap(40, 40, 40)
                                .addComponent(btnLamMoi2)))
                        .addGap(41, 41, 41)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnKhachHang1, btnNhanVien, jButton10, jButton11, jButton12, jButton13, jButton14, jButton16, jButton8, jButton9});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton9)
                            .addComponent(btnNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton11)
                            .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnKhachHang1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPhieuGiamGia, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(btnXoaLoc)
                            .addComponent(btnLoc))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboTrangThaiDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboThuongHieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboLoaiDoUong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboThuongHieuDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboLoaiDoUongDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnThem)
                            .addComponent(btnSua)
                            .addComponent(btnXoa)
                            .addComponent(btnCapNhatGia)
                            .addComponent(btnLamMoi2))
                        .addGap(87, 261, Short.MAX_VALUE))))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnKhachHang1, btnNhanVien, jButton10, jButton11, jButton12, jButton13, jButton14, jButton16, jButton8, jButton9});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1393, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 889, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
        new BaoCaoForm().setVisible(true);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void cboTrangThaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTrangThaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboTrangThaiActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        // TODO add your handling code here:
        HoaDonForm hd = new HoaDonForm();
        hd.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                refreshTable();
            }
        });
        hd.setVisible(true);
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        var form = new ToppingForm2();
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                initCombos();
                refreshTable();
            }
        });
        form.setVisible(true);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        var form = new ThuongHieuForm();
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                initCombos();
                refreshTable();
            }
        });
        form.setVisible(true);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void txtMaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaActionPerformed

    private void txtTenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenActionPerformed

    private void txtGiaMacDinhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGiaMacDinhActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGiaMacDinhActionPerformed

    private void cboTrangThaiDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTrangThaiDetailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboTrangThaiDetailActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        var form = new QuanLySizeForm();
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                initCombos();
                refreshTable();
            }
        });
        form.setVisible(true);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void btnLamMoi2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoi2ActionPerformed
        // TODO add your handling code here:
        refreshTable();
        clear();
    }//GEN-LAST:event_btnLamMoi2ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        var form = new NhaCungCapForm();
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                initCombos();
                refreshTable();
            }
        });
        form.setVisible(true);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        var form = new TonKhoForm();
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                refreshTable();
            }
        });
        form.setVisible(true);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        var form = new LoaiDoUongForm();
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                initCombos();
                refreshTable();
            }
        });
        form.setVisible(true);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void btnNhanVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNhanVienActionPerformed
        // TODO add your handling code here:
        new NhanVienForm().setVisible(true);
    }//GEN-LAST:event_btnNhanVienActionPerformed

    private void btnPhieuGiamGiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPhieuGiamGiaActionPerformed
        // TODO add your handling code here:
         new QuanLyPhieuGiamGiaForm2().setVisible(true);
    }//GEN-LAST:event_btnPhieuGiamGiaActionPerformed

    private void btnKhachHang1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKhachHang1ActionPerformed
        // TODO add your handling code here:
        new KhachHangForm().setVisible(true);
    }//GEN-LAST:event_btnKhachHang1ActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnXoaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BanHangForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BanHangForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BanHangForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BanHangForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BanHangForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCapNhatGia;
    private javax.swing.JButton btnKhachHang1;
    private javax.swing.JButton btnLamMoi2;
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnNhanVien;
    private javax.swing.JButton btnPhieuGiamGia;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JButton btnXoaLoc;
    private javax.swing.JComboBox<String> cboLoaiDoUong;
    private javax.swing.JComboBox<String> cboLoaiDoUongDetail;
    private javax.swing.JComboBox<String> cboThuongHieu;
    private javax.swing.JComboBox<String> cboThuongHieuDetail;
    private javax.swing.JComboBox<String> cboTrangThai;
    private javax.swing.JComboBox<String> cboTrangThaiDetail;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTongMatHang;
    private javax.swing.JTable tblDoUong1;
    private javax.swing.JTextField txtGiaMacDinh;
    private javax.swing.JTextField txtMa;
    private javax.swing.JTextArea txtMoTa1;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSoLuongTon;
    private javax.swing.JTextField txtTen;
    // End of variables declaration//GEN-END:variables
}
