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
 * @author Admin
 */
public class TraSuaForm extends javax.swing.JFrame {

    /**
     * Creates new form TraSuaForm
     */

    private final DoUongDAO doUongDAO = new dao.impl.DoUongDAOImpl();
    private final LoaiDoUongDAO loaiDoUongDAO = new dao.impl.LoaiDoUongDAOImpl();
    private final ThuongHieuDAO thuongHieuDAO = new dao.impl.ThuongHieuDAOImpl();
    private final TonKhoDAO tonKhoDAO = new TonKhoDAO();
    private final int chiNhanhID = Auth.user.getChiNhanhID();

    // ... existing code ...

    public TraSuaForm() {
        initComponents();
        afterInit();
    }
     private void afterInit() {
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
        if (d == null) return;
        try {
            doUongDAO.insert(d); // insert là void -> không đặt trong if
            JOptionPane.showMessageDialog(this, "Thêm thành công");
            refreshTable();
            selectRowById(doUongDAO.getLastInsertedId());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Thêm thất bại: " + ex.getMessage());
        }
    }
    // ... existing code ...
    private void onSua(ActionEvent e) {
        int row = tblDoUong1.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng cần sửa");
            return;
        }
        DoUong d = collectFromForm(true);
        if (d == null) return;
        try {
            // Update main drink details
            doUongDAO.update(d);

            // Separately, update stock quantity if it has changed
            try {
                int newQuantity = Integer.parseInt(txtSoLuongTon.getText());
                Object tableQuantityObj = tblDoUong1.getValueAt(row, 6);
                int tableQuantity = tableQuantityObj != null ? Integer.parseInt(tableQuantityObj.toString()) : 0;

                if (newQuantity != tableQuantity) {
                    if (newQuantity < 0) {
                        JOptionPane.showMessageDialog(this, "Số lượng không thể âm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    } else {
                        tonKhoDAO.updateSoLuong(d.getId(), chiNhanhID, newQuantity);
                    }
                }
            } catch (NumberFormatException ex) {
                // Ignore if the text field is not a valid number, the main update will still proceed
            }

            JOptionPane.showMessageDialog(this, "Cập nhật thành công");
            refreshTable();
            selectRowById(d.getId());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại: " + ex.getMessage());
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
        String loaiTen = safeSelected(cboLoaiDoUongDetail);
        String thTen = safeSelected(cboThuongHieuDetail);
        String trangThai = safeSelected(cboTrangThaiDetail);
        String moTa = txtMoTa1.getText().trim();

        if (ten.isEmpty()) { JOptionPane.showMessageDialog(this, "Tên không được trống"); return null; }

        BigDecimal gia = null;
        if (!giaStr.isEmpty()) {
            try { gia = new BigDecimal(giaStr); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Giá không hợp lệ"); return null; }
        }

        Integer loaiId = loaiDoUongDAO.idByTen(loaiTen);
        Integer thId = thuongHieuDAO.idByTen(thTen);

        DoUong d = new DoUong();
        if (includeId) {
            try { d.setId(Integer.parseInt(txtMa.getText().trim())); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "ID không hợp lệ"); return null; }
        }
        d.setTenDoUong(ten);
        d.setLoaiDoUongID(loaiId);
        d.setThuongHieuID(thId);
        d.setGiaBanMacDinh(gia);
        d.setMoTa(moTa.isEmpty() ? null : moTa);
        d.setTrangThai(trangThai);
        d.setLaTopping(false);
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

        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        cboLoaiDoUong = new javax.swing.JComboBox<>();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        cboThuongHieu = new javax.swing.JComboBox<>();
        cboTrangThai = new javax.swing.JComboBox<>();
        btnLoc = new javax.swing.JButton();
        btnXoaLoc = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDoUong1 = new javax.swing.JTable();
        cboLoaiDoUongDetail = new javax.swing.JComboBox<>();
        cboThuongHieuDetail = new javax.swing.JComboBox<>();
        cboTrangThaiDetail = new javax.swing.JComboBox<>();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnCapNhatGia = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        btnLamMoi2 = new javax.swing.JButton();
        btnNhanVien = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1360, 800));

        jLabel1.setText("Tìm Kiếm ");

        cboLoaiDoUong.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton8.setText("Quản Lý Size");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Topping");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setText("Loại đồ uống");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("Thương hiệu");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("Nhà cung cấp");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText("Báo Cáo");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setText("Kiểm Kho");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setText("Phiếu Nhập");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setText("Hóa Đơn");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        cboThuongHieu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cboTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboTrangThai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTrangThaiActionPerformed(evt);
            }
        });

        btnLoc.setText("Lọc");

        btnXoaLoc.setText("Xóa");

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

        cboLoaiDoUongDetail.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cboThuongHieuDetail.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cboTrangThaiDetail.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboTrangThaiDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTrangThaiDetailActionPerformed(evt);
            }
        });

        btnThem.setText("Thêm");

        btnSua.setText("Sửa");

        btnXoa.setText("Xóa");

        btnCapNhatGia.setText("Cập Nhật");

        lblStatus.setText("Trạng Thái");

        btnLamMoi2.setText("Làm Mới");
        btnLamMoi2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoi2ActionPerformed(evt);
            }
        });

        btnNhanVien.setText("Nhân Viên");
        btnNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNhanVienActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jLabel6.setText("HOME");

        txtMoTa1.setColumns(20);
        txtMoTa1.setRows(5);
        jScrollPane3.setViewportView(txtMoTa1);

        jLabel2.setText("Mô Tả");

        jLabel3.setText("Mã");

        jLabel4.setText("Tên");

        jLabel5.setText("Giá");

        jLabel7.setText("Số Lượng Tồn");

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(54, 54, 54)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTen)
                            .addComponent(txtGiaMacDinh)
                            .addComponent(txtMa)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblTongMatHang, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSoLuongTon, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
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
                            .addComponent(txtSoLuongTon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblTongMatHang))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtGiaMacDinh, txtMa, txtSoLuongTon, txtTen});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNhanVien, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(72, 72, 72)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnThem)
                                .addGap(34, 34, 34)
                                .addComponent(btnSua)
                                .addGap(56, 56, 56)
                                .addComponent(btnXoa)))
                        .addGap(39, 39, 39)
                        .addComponent(btnCapNhatGia)
                        .addGap(49, 49, 49)
                        .addComponent(btnLamMoi2))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 624, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cboLoaiDoUong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(38, 38, 38)
                                        .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(35, 35, 35)
                                        .addComponent(cboThuongHieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(cboThuongHieuDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cboLoaiDoUongDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cboTrangThaiDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnXoaLoc)
                                        .addGap(37, 37, 37)
                                        .addComponent(btnLoc)))))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(8, 8, 8))
            .addGroup(layout.createSequentialGroup()
                .addGap(562, 562, 562)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCapNhatGia, btnLamMoi2, btnSua, btnThem, btnXoa});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnNhanVien, jButton10, jButton11, jButton12, jButton13, jButton14, jButton15, jButton16, jButton8, jButton9});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboLoaiDoUong, cboLoaiDoUongDetail, cboThuongHieu, cboThuongHieuDetail, cboTrangThai, cboTrangThaiDetail});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(btnXoaLoc)
                            .addComponent(btnLoc))
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboTrangThaiDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboThuongHieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboLoaiDoUong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboThuongHieuDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboLoaiDoUongDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnThem)
                            .addComponent(btnSua)
                            .addComponent(btnXoa)
                            .addComponent(btnCapNhatGia)
                            .addComponent(btnLamMoi2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblStatus)
                        .addGap(70, 70, 70))))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnNhanVien, jButton10, jButton11, jButton12, jButton13, jButton14, jButton15, jButton16, jButton8, jButton9});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboLoaiDoUong, cboLoaiDoUongDetail, cboThuongHieu, cboThuongHieuDetail, cboTrangThai, cboTrangThaiDetail});

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
        new BaoCaoForm().setVisible(true);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
       
    }//GEN-LAST:event_jButton15ActionPerformed

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

    private void btnLamMoi2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoi2ActionPerformed
        // TODO add your handling code here:
        refreshTable();
        clear();
    }//GEN-LAST:event_btnLamMoi2ActionPerformed

    private void btnNhanVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNhanVienActionPerformed
        // TODO add your handling code here:
        new NhanVienForm().setVisible(true);

    }//GEN-LAST:event_btnNhanVienActionPerformed

    private void cboTrangThaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTrangThaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboTrangThaiActionPerformed

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
            java.util.logging.Logger.getLogger(TraSuaForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TraSuaForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TraSuaForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TraSuaForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TraSuaForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCapNhatGia;
    private javax.swing.JButton btnLamMoi2;
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnNhanVien;
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
    private javax.swing.JButton jButton15;
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
    private javax.swing.JPanel jPanel1;
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
