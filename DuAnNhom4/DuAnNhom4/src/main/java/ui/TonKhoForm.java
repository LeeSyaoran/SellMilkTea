/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

import dao.ChiNhanhDAO;
import dao.LichSuKhoDAO;
import dao.ThongKeDAO;
import dao.TonKhoDAO;
import dao.impl.ChiNhanhDAOImpl;

import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
// import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.ChiNhanh;
import model.LichSuKho;
// import model.TonKho;
import utils.Auth;
import utils.MsgBox;
import utils.XDate;

/**
 *
 * @author Admin
 */
public class TonKhoForm extends javax.swing.JFrame {

    ChiNhanhDAO chiNhanhDAO = new ChiNhanhDAOImpl();
    ThongKeDAO thongKeDAO = new ThongKeDAO();
    TonKhoDAO tonKhoDAO = new TonKhoDAO();
    LichSuKhoDAO lichSuKhoDAO = new LichSuKhoDAO();
    DefaultTableModel tableModel;

    /**
     * Creates new form KiemKhoFomr
     */
    public TonKhoForm() {
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        init();
    }

    void init() {
        setTitle("Phiếu Kiểm Kho");
        if (!utils.Auth.isManager()) {
            utils.MsgBox.alert(this, "Bạn không có quyền truy cập chức năng này!");
            this.dispose();
            return;
        }
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = (DefaultTableModel) tblKiemKho.getModel();
        tableModel.setRowCount(0);

        lblNhanVien.setText(Auth.isLogin() ? Auth.user.getHoTen() : "Chưa đăng nhập");
        lblNhanVien.setEditable(false);

        fillComboBoxChiNhanh();
        cboNgayKiemKho.removeAllItems();
        cboNgayKiemKho.addItem(XDate.toString(new Date(), "dd-MM-yyyy"));
        cboNgayKiemKho.setEnabled(false);
        
        btnHoanThanh.setEnabled(false);
        btnLichSuKho.setEnabled(false);
    }

    void fillComboBoxChiNhanh() {
        DefaultComboBoxModel cboModel = (DefaultComboBoxModel) cboChiNhanh.getModel();
        cboModel.removeAllElements();
        try {
            List<ChiNhanh> list = chiNhanhDAO.findAll();
            for (ChiNhanh cn : list) {
                cboModel.addElement(cn);
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn dữ liệu chi nhánh!");
            e.printStackTrace();
        }
    }

    void fillTableTonKho() {
        tableModel.setRowCount(0);
        try {
            ChiNhanh chiNhanh = (ChiNhanh) cboChiNhanh.getSelectedItem();
            if (chiNhanh == null) return;

            // Giả định thongKeDAO.getTonKhoTheoChiNhanh trả về List<Object[]>
            // Mỗi Object[] chứa: {Mã Đồ Uống, Tên Đồ Uống, Số Lượng Tồn}
            List<Object[]> list = thongKeDAO.getTonKhoTheoChiNhanh(chiNhanh.getId());
            int stt = 1;
            for (Object[] row : list) {
                tableModel.addRow(new Object[]{
                    stt++,
                    row[0], // Mã Đồ Uống
                    row[1], // Tên Đồ Uống
                    row[2], // Tồn Hệ Thống
                    null,   // Tồn Thực Tế (để trống cho người dùng nhập)
                    null,   // Chênh Lệch (sẽ được tính sau)
                    ""      // Ghi Chú
                });
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn dữ liệu tồn kho!");
            e.printStackTrace();
        }
    }
    
    void canBangKho() {
        if (MsgBox.confirm(this, "Bạn có chắc chắn muốn cân bằng kho? Hành động này sẽ cập nhật lại toàn bộ số liệu tồn kho.")) {
            ChiNhanh chiNhanh = (ChiNhanh) cboChiNhanh.getSelectedItem();
            if (chiNhanh == null || !Auth.isLogin()) {
                MsgBox.alert(this, "Vui lòng chọn chi nhánh và đăng nhập!");
                return;
            }

            int errors = 0;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                try {
                    int maDoUong = (int) tableModel.getValueAt(i, 1);
                    int tonHeThong = (int) tableModel.getValueAt(i, 3);
                    Object thucTeObj = tableModel.getValueAt(i, 4);

                    if (thucTeObj == null || thucTeObj.toString().trim().isEmpty()) {
                        continue; // Bỏ qua nếu không nhập tồn thực tế
                    }
                    
                    int tonThucTe = Integer.parseInt(thucTeObj.toString());
                    if(tonThucTe < 0) {
                        MsgBox.alert(this, "Tồn thực tế không được là số âm. Dòng " + (i+1));
                        errors++;
                        continue;
                    }

                    int chenhLech = tonThucTe - tonHeThong;
                    tableModel.setValueAt(chenhLech, i, 5); // Cập nhật cột chênh lệch

                    if (chenhLech != 0) {
                        // 1. Cập nhật lại tồn kho trong DB bằng phương thức đúng
                        tonKhoDAO.updateSoLuong(maDoUong, chiNhanh.getId(), tonThucTe);

                        // 2. Ghi lại lịch sử kiểm kho
                        LichSuKho lsk = new LichSuKho();
                        lsk.setDoUongID(maDoUong);
                        lsk.setChiNhanhID(chiNhanh.getId());
                        lsk.setLoaiGiaoDich("KiemKho");
                        lsk.setSoLuong(chenhLech); // Ghi nhận giá trị chênh lệch
                        lsk.setNgayGiaoDich(new Date());
                        lsk.setNhanVienID(Auth.user.getId());
                        lsk.setGhiChu("Kiểm kho ngày " + XDate.toString(new Date(), "dd/MM/yyyy") + ". Tồn cũ: " + tonHeThong + ", Tồn mới: " + tonThucTe);
                        lichSuKhoDAO.insert(lsk);
                        
                        tableModel.setValueAt("Đã cập nhật", i, 6);
                    } else {
                        tableModel.setValueAt(" khớp", i, 6);
                    }

                } catch (NumberFormatException e) {
                    errors++;
                    tableModel.setValueAt("Lỗi nhập liệu", i, 6);
                    MsgBox.alert(this, "Vui lòng nhập số nguyên hợp lệ cho cột 'Tồn Thực Tế' ở dòng " + (i + 1));
                } catch (Exception e) {
                    errors++;
                    tableModel.setValueAt("Lỗi DB", i, 6);
                    MsgBox.alert(this, "Lỗi cập nhật dữ liệu ở dòng " + (i + 1));
                    e.printStackTrace();
                }
            }

            if (errors == 0) {
                MsgBox.alert(this, "Cân bằng kho thành công!");
                btnLichSuKho.setEnabled(true);
            } else {
                MsgBox.alert(this, "Hoàn thành với " + errors + " lỗi. Vui lòng kiểm tra lại các dòng được đánh dấu.");
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

        jPanel1 = new javax.swing.JPanel();
        btnBatDauKiem = new javax.swing.JButton();
        btnLichSuKho = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cboChiNhanh = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        lblNhanVien = new javax.swing.JTextField();
        btnHoanThanh = new javax.swing.JButton();
        cboNgayKiemKho = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKiemKho = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(225, 177, 112));
        jPanel1.setForeground(new java.awt.Color(164, 115, 115));

        btnBatDauKiem.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnBatDauKiem.setForeground(new java.awt.Color(164, 115, 115));
        btnBatDauKiem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/start.png"))); // NOI18N
        btnBatDauKiem.setText("Bắt Đầu");
        btnBatDauKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatDauKiemActionPerformed(evt);
            }
        });

        btnLichSuKho.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLichSuKho.setForeground(new java.awt.Color(164, 115, 115));
        btnLichSuKho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/warehouse history.png"))); // NOI18N
        btnLichSuKho.setText("Lịch Sử Kho");
        btnLichSuKho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLichSuKhoActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(164, 115, 115));
        jLabel2.setText("Chi Nhánh");

        cboChiNhanh.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboChiNhanh.setForeground(new java.awt.Color(164, 115, 115));
        cboChiNhanh.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboChiNhanh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboChiNhanhActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(164, 115, 115));
        jLabel4.setText("Nhân Viên");

        lblNhanVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lblNhanVienActionPerformed(evt);
            }
        });

        btnHoanThanh.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnHoanThanh.setForeground(new java.awt.Color(164, 115, 115));
        btnHoanThanh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/finish.png"))); // NOI18N
        btnHoanThanh.setText("Hoàn Thành");
        btnHoanThanh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHoanThanhActionPerformed(evt);
            }
        });

        cboNgayKiemKho.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboNgayKiemKho.setForeground(new java.awt.Color(164, 115, 115));
        cboNgayKiemKho.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboNgayKiemKho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboNgayKiemKhoActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(164, 115, 115));
        jLabel1.setText("PHIẾU KIỂM KHO");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(164, 115, 115));
        jLabel3.setText("Ngày Kiểm Kho");

        tblKiemKho.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "STT", "Mã Đồ Uống ", "Tên Đồ Uống ", "Tồn Hệ Thống", "Tồn Thực Tế", "Chênh Lệch"
            }
        ));
        jScrollPane1.setViewportView(tblKiemKho);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnHoanThanh)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(339, 339, 339)
                                    .addComponent(jLabel1))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(32, 32, 32)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cboNgayKiemKho, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cboChiNhanh, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(58, 58, 58)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnBatDauKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLichSuKho, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(156, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboChiNhanh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboNgayKiemKho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(btnBatDauKiem)
                        .addGap(18, 18, 18)
                        .addComponent(btnHoanThanh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLichSuKho))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBatDauKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatDauKiemActionPerformed
        fillTableTonKho();
        btnHoanThanh.setEnabled(true);
        btnLichSuKho.setEnabled(false);
    }//GEN-LAST:event_btnBatDauKiemActionPerformed

    private void btnLichSuKhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLichSuKhoActionPerformed
        new LichSuKhoForm().setVisible(true);
    }//GEN-LAST:event_btnLichSuKhoActionPerformed

    private void cboChiNhanhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboChiNhanhActionPerformed
        tableModel.setRowCount(0);
        btnHoanThanh.setEnabled(false);
        btnLichSuKho.setEnabled(false);
    }//GEN-LAST:event_cboChiNhanhActionPerformed

    private void lblNhanVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lblNhanVienActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblNhanVienActionPerformed

    private void btnHoanThanhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHoanThanhActionPerformed
        canBangKho();
    }//GEN-LAST:event_btnHoanThanhActionPerformed

    private void cboNgayKiemKhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboNgayKiemKhoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboNgayKiemKhoActionPerformed

  
   

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
            java.util.logging.Logger.getLogger(TonKhoForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TonKhoForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TonKhoForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TonKhoForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TonKhoForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatDauKiem;
    private javax.swing.JButton btnHoanThanh;
    private javax.swing.JButton btnLichSuKho;
    private javax.swing.JComboBox<String> cboChiNhanh;
    private javax.swing.JComboBox<String> cboNgayKiemKho;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField lblNhanVien;
    private javax.swing.JTable tblKiemKho;
    // End of variables declaration//GEN-END:variables
}