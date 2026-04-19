/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

import dao.LoaiDoUongDAO;
import dao.impl.LoaiDoUongDAOImpl;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import model.LoaiDoUong;

/**
 *
 * @author Admin
 */
public class LoaiDoUongForm extends javax.swing.JFrame {

    /**
     * Creates new form LoaiDoUongForm
     */
    private final LoaiDoUongDAO dao = new LoaiDoUongDAOImpl();

    public LoaiDoUongForm() {
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        afterInit();
    }

    private void afterInit() {
        initTable();
        initTrangThaiCombo();
        bindActions();
        loadData();
        if (!utils.Auth.isManager()) {
            btnThem.setEnabled(false);
            btnSua.setEnabled(false);
            btnXoa.setEnabled(false);
        }
    }

    private void initTrangThaiCombo() {
        // Khởi tạo giá trị thực tế thay cho "Item 1..4"
        cboTrangThai.setModel(new DefaultComboBoxModel<>(new String[]{"HoatDong", "Ngung"}));
    }

    private void initTable() {
        // Đặt lại header cột rõ ràng, không editable
        tblLoaiDoUong.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Tên loại", "Mô tả", "Trạng thái"}
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        tblLoaiDoUong.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLoaiDoUong.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onRowSelected();
            }
        });
    }

    private void bindActions() {
        btnThem.addActionListener(this::onThem);
        btnSua.addActionListener(this::onSua);
        btnXoa.addActionListener(this::onXoa);
        btnLamMoi.addActionListener(e -> loadData());
        btnLoc.addActionListener(e -> applyFilter());
    }

    private void loadData() {
        List<LoaiDoUong> list;
        try {
            list = dao.findAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }
        DefaultTableModel m = (DefaultTableModel) tblLoaiDoUong.getModel();
        m.setRowCount(0);
        list.forEach(l -> m.addRow(new Object[]{l.getId(), l.getTenLoai(), l.getMoTa(), l.getTrangThai()}));
        lblTongLoai.setText("Tổng loại: " + list.size());
        clearForm();
    }

    private void applyFilter() {
        String kw = txtSearchLoai.getText().trim();
        List<LoaiDoUong> list = dao.searchByNameOrDesc(kw);
        DefaultTableModel m = (DefaultTableModel) tblLoaiDoUong.getModel();
        m.setRowCount(0);
        list.forEach(l -> m.addRow(new Object[]{l.getId(), l.getTenLoai(), l.getMoTa(), l.getTrangThai()}));
        lblTongLoai.setText("Tổng loại: " + list.size());
    }

    private void onRowSelected() {
        int row = tblLoaiDoUong.getSelectedRow();
        if (row < 0) {
            return;
        }
        txtMaLoai.setText(String.valueOf(tblLoaiDoUong.getValueAt(row, 0)));
        txtTenLoai.setText(String.valueOf(tblLoaiDoUong.getValueAt(row, 1)));
        Object moTa = tblLoaiDoUong.getValueAt(row, 2);
        txtMoTa.setText(moTa == null ? "" : String.valueOf(moTa));
        cboTrangThai.setSelectedItem(String.valueOf(tblLoaiDoUong.getValueAt(row, 3)));
    }

    private void onThem(ActionEvent e) {
        if (!utils.Auth.isManager()) {
            utils.MsgBox.alert(this, "Bạn không có quyền thực hiện chức năng này!");
            return;
        }
        LoaiDoUong l = collectFromForm(false);
        if (l == null) {
            return;
        }
        boolean ok = dao.insert(l);
        JOptionPane.showMessageDialog(this, ok ? "Thêm thành công" : "Thêm thất bại");
        if (ok) {
            loadData();
        }
    }

    private void onSua(ActionEvent e) {
        if (!utils.Auth.isManager()) {
            utils.MsgBox.alert(this, "Bạn không có quyền thực hiện chức năng này!");
            return;
        }
        LoaiDoUong l = collectFromForm(true);
        if (l == null) {
            return;
        }
        boolean ok = dao.update(l);
        JOptionPane.showMessageDialog(this, ok ? "Cập nhật thành công" : "Cập nhật thất bại");
        if (ok) {
            loadData();
            selectRowById(l.getId());
        }
    }

    private void onXoa(ActionEvent e) {
        if (!utils.Auth.isManager()) {
            utils.MsgBox.alert(this, "Bạn không có quyền thực hiện chức năng này!");
            return;
        }
        int row = tblLoaiDoUong.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng để xóa");
            return;
        }
        int id = (int) tblLoaiDoUong.getValueAt(row, 0);
        int cf = JOptionPane.showConfirmDialog(this, "Xóa loại ID=" + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (cf == JOptionPane.YES_OPTION) {
            boolean ok = dao.delete(id);
            JOptionPane.showMessageDialog(this, ok ? "Đã xóa" : "Xóa thất bại (có thể đang được tham chiếu)");
            if (ok) {
                loadData();
            }
        }
    }

    private LoaiDoUong collectFromForm(boolean includeId) {
        String ten = txtTenLoai.getText().trim();
        String moTa = txtMoTa.getText().trim();
        String tt = String.valueOf(cboTrangThai.getSelectedItem());
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên loại không được trống");
            return null;
        }
        LoaiDoUong l = new LoaiDoUong();
        if (includeId) {
            try {
                l.setId(Integer.parseInt(txtMaLoai.getText().trim()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "ID không hợp lệ");
                return null;
            }
        }
        l.setTenLoai(ten);
        l.setMoTa(moTa.isEmpty() ? null : moTa);
        l.setTrangThai(tt);
        return l;
    }

    private void selectRowById(int id) {
        DefaultTableModel m = (DefaultTableModel) tblLoaiDoUong.getModel();
        for (int i = 0; i < m.getRowCount(); i++) {
            if (id == (int) m.getValueAt(i, 0)) {
                tblLoaiDoUong.setRowSelectionInterval(i, i);
                tblLoaiDoUong.scrollRectToVisible(tblLoaiDoUong.getCellRect(i, 0, true));
                break;
            }
        }
    }

    private void clearForm() {
        txtMaLoai.setText("");
        txtTenLoai.setText("");
        txtMoTa.setText("");
        cboTrangThai.setSelectedItem("HoatDong");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnThem = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        txtSearchLoai = new javax.swing.JTextField();
        txtMaLoai = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtMoTa = new javax.swing.JTextArea();
        btnLoc = new javax.swing.JButton();
        cboTrangThai = new javax.swing.JComboBox<>();
        txtTenLoai = new javax.swing.JTextField();
        lblTongLoai = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblLoaiDoUong = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(225, 177, 112));
        setPreferredSize(new java.awt.Dimension(950, 500));

        jPanel1.setBackground(new java.awt.Color(225, 177, 112));
        jPanel1.setForeground(new java.awt.Color(164, 115, 115));
        jPanel1.setPreferredSize(new java.awt.Dimension(950, 500));

        btnThem.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnThem.setForeground(new java.awt.Color(164, 115, 115));
        btnThem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/plus.png"))); // NOI18N
        btnThem.setText("Thêm");

        btnXoa.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnXoa.setForeground(new java.awt.Color(164, 115, 115));
        btnXoa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/delete_one.png"))); // NOI18N
        btnXoa.setText("Xóa");

        btnLamMoi.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLamMoi.setForeground(new java.awt.Color(164, 115, 115));
        btnLamMoi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/refresh.png"))); // NOI18N
        btnLamMoi.setText("Làm Mới");

        btnSua.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnSua.setForeground(new java.awt.Color(164, 115, 115));
        btnSua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fix.png"))); // NOI18N
        btnSua.setText("Sửa");

        txtSearchLoai.setText("Tìm Kiếm");

        txtMaLoai.setText("Mã Loại");

        txtMoTa.setColumns(20);
        txtMoTa.setRows(5);
        jScrollPane1.setViewportView(txtMoTa);

        btnLoc.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLoc.setForeground(new java.awt.Color(164, 115, 115));
        btnLoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/filter.png"))); // NOI18N
        btnLoc.setText("Lọc");

        cboTrangThai.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboTrangThai.setForeground(new java.awt.Color(164, 115, 115));
        cboTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboTrangThai.setPreferredSize(new java.awt.Dimension(1500, 22));

        txtTenLoai.setText("Tên Loại");

        lblTongLoai.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTongLoai.setForeground(new java.awt.Color(164, 115, 115));
        lblTongLoai.setText("Tổng Loại");

        tblLoaiDoUong.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblLoaiDoUong);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtMaLoai)
                    .addComponent(txtTenLoai)
                    .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(txtSearchLoai, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(66, 66, 66)
                        .addComponent(btnLoc))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnLamMoi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnXoa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSua, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnThem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(lblTongLoai, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearchLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLoc))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(txtMaLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtTenLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(btnThem)
                            .addGap(18, 18, 18)
                            .addComponent(btnSua)
                            .addGap(18, 18, 18)
                            .addComponent(btnXoa)
                            .addGap(18, 18, 18)
                            .addComponent(btnLamMoi)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTongLoai))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 934, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(LoaiDoUongForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoaiDoUongForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoaiDoUongForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoaiDoUongForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoaiDoUongForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cboTrangThai;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTongLoai;
    private javax.swing.JTable tblLoaiDoUong;
    private javax.swing.JTextField txtMaLoai;
    private javax.swing.JTextArea txtMoTa;
    private javax.swing.JTextField txtSearchLoai;
    private javax.swing.JTextField txtTenLoai;
    // End of variables declaration//GEN-END:variables
}
