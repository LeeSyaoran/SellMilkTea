package ui;

import dao.SizeDAO;
import dao.ToppingDAO;
import dao.impl.SizeDAOImpl;
import dao.impl.ToppingDAOImpl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import model.DoUong;
import model.HoaDonChiTiet;
import model.Size;
import model.Topping;

/**
 * 
 * @author Admin
 */
public class ThemSanPhamJDialog extends javax.swing.JDialog {

    private final DoUong doUong;
    private HoaDonChiTiet hoaDonChiTiet = null;

    private final SizeDAO sizeDAO = new SizeDAOImpl();
    private final ToppingDAO toppingDAO = new ToppingDAOImpl();

    private final Map<String, Size> sizeMap = new HashMap<>();
    private final Map<String, Topping> toppingMap = new HashMap<>();

    /**
     * Creates new form ThemSanPhamJDialog
     */
    public ThemSanPhamJDialog(java.awt.Frame parent, boolean modal, DoUong doUong) {
        super(parent, modal);
        this.doUong = doUong;
        initComponents();
        customizeComponents();
        loadData();
    }

    private void customizeComponents() {
        this.setTitle("Tùy chỉnh: " + doUong.getTenDoUong());
        this.setLocationRelativeTo(getParent());
        txtTenSanPham.setText(doUong.getTenDoUong());
        txtTenSanPham.setEditable(false);
        txtGia.setEditable(false);
        spnSoLuong.setModel(new javax.swing.SpinnerNumberModel(1, 1, 100, 1));
    }

    private void loadData() {
        loadSizes();
        loadToppings();
        updatePrice(); // Initial price calculation
    }

    private void loadSizes() {
        try {
            List<Size> sizes = sizeDAO.findAll();
            cboSize.removeAllItems();
            sizeMap.clear();
            for (Size s : sizes) {
                cboSize.addItem(s.getTenSize());
                sizeMap.put(s.getTenSize(), s);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách size.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadToppings() {
        try {
            List<Topping> toppings = toppingDAO.findAll(); // Use findAll
            toppingMap.clear();

            JComboBox<?>[] toppingComboBoxes = {cboTopping1, cboTopping2, cboTopping3};
            for (JComboBox<?> cbo : toppingComboBoxes) {
                JComboBox<String> cboString = (JComboBox<String>) cbo;
                cboString.removeAllItems();
                cboString.addItem("-- Không chọn --");
            }

            for (Topping t : toppings) {
                // Filter for active toppings here
                if ("HoatDong".equals(t.getTrangThai())) {
                    toppingMap.put(t.getTenTopping(), t);
                    for (JComboBox<?> cbo : toppingComboBoxes) {
                        ((JComboBox<String>) cbo).addItem(t.getTenTopping());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách topping.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePrice() {
        if (doUong == null) return;
        BigDecimal basePrice = doUong.getGiaBanMacDinh();
        BigDecimal finalPrice = basePrice;

        // Add price based on size
        String selectedSizeName = (String) cboSize.getSelectedItem();
        if (selectedSizeName != null) {
            Size selectedSize = sizeMap.get(selectedSizeName);
            if (selectedSize != null && selectedSize.getHeSoGia() != null) {
                finalPrice = basePrice.multiply(selectedSize.getHeSoGia());
            }
        }

        // Add price for toppings
        JComboBox<?>[] toppingComboBoxes = {cboTopping1, cboTopping2, cboTopping3};
        for (JComboBox<?> cbo : toppingComboBoxes) {
            String selectedToppingName = (String) cbo.getSelectedItem();
            if (selectedToppingName != null && !selectedToppingName.equals("-- Không chọn --")) {
                Topping selectedTopping = toppingMap.get(selectedToppingName);
                if (selectedTopping != null && selectedTopping.getGiaTopping() != null) {
                    finalPrice = finalPrice.add(selectedTopping.getGiaTopping());
                }
            }
        }
        txtGia.setText(finalPrice.setScale(0, java.math.RoundingMode.HALF_UP).toString());
    }
    
    public HoaDonChiTiet getHoaDonChiTiet() {
        return this.hoaDonChiTiet;
    }

    private String convertToppingToJSON(List<String> toppingNames) {
        if (toppingNames == null || toppingNames.isEmpty()) {
            return null;
        }
        
        List<String> validToppingNames = new ArrayList<>();
        for (String name : toppingNames) {
            if (toppingMap.containsKey(name)) {
                validToppingNames.add(name);
            }
        }

        if (validToppingNames.isEmpty()) {
            return null;
        }

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < validToppingNames.size(); i++) {
            String name = validToppingNames.get(i);
            Topping topping = toppingMap.get(name);
            if (i > 0) {
                json.append(", ");
            }
            json.append("{\"ToppingID\": ").append(topping.getId()).append(", ")
                .append("\"Ten\": \"").append(name.replace("\"", "\\\"")).append("\"}");
        }
        json.append("]");
        
        return json.toString();
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
        jLabel8 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cboTopping3 = new javax.swing.JComboBox<>();
        txtGia = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btnHuy = new javax.swing.JButton();
        cboTopping1 = new javax.swing.JComboBox<>();
        spnSoLuong = new javax.swing.JSpinner();
        txtTenSanPham = new javax.swing.JTextField();
        cboSize = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cboTopping2 = new javax.swing.JComboBox<>();
        txtGhiChu = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btnThem = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1300, 800));

        jPanel1.setBackground(new java.awt.Color(225, 177, 112));
        jPanel1.setForeground(new java.awt.Color(164, 115, 115));
        jPanel1.setToolTipText("");
        jPanel1.setPreferredSize(new java.awt.Dimension(1300, 800));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(164, 115, 115));
        jLabel8.setText("Số Lượng");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(164, 115, 115));
        jLabel2.setText("Giá");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(164, 115, 115));
        jLabel3.setText("Ghi Chú");

        cboTopping3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboTopping3.setForeground(new java.awt.Color(164, 115, 115));
        cboTopping3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboTopping3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTopping3ActionPerformed(evt);
            }
        });

        txtGia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGiaActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(164, 115, 115));
        jLabel6.setText("Topping 2");

        btnHuy.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnHuy.setForeground(new java.awt.Color(164, 115, 115));
        btnHuy.setText("Hủy");
        btnHuy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHuyActionPerformed(evt);
            }
        });

        cboTopping1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboTopping1.setForeground(new java.awt.Color(164, 115, 115));
        cboTopping1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboTopping1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTopping1ActionPerformed(evt);
            }
        });

        txtTenSanPham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenSanPhamActionPerformed(evt);
            }
        });

        cboSize.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboSize.setForeground(new java.awt.Color(164, 115, 115));
        cboSize.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSizeActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(164, 115, 115));
        jLabel5.setText("Topping 1");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(164, 115, 115));
        jLabel4.setText("Chọn Size");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(164, 115, 115));
        jLabel1.setText("Sản Phẩm");

        cboTopping2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboTopping2.setForeground(new java.awt.Color(164, 115, 115));
        cboTopping2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboTopping2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTopping2ActionPerformed(evt);
            }
        });

        txtGhiChu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGhiChuActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(164, 115, 115));
        jLabel7.setText("Topping 3");

        btnThem.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnThem.setForeground(new java.awt.Color(164, 115, 115));
        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTenSanPham)
                            .addComponent(txtGia, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                            .addComponent(txtGhiChu)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(btnThem)))
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnHuy)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboTopping3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboTopping2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboTopping1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(47, 47, 47)
                        .addComponent(jLabel8)
                        .addGap(5, 5, 5)
                        .addComponent(spnSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(525, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtTenSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(spnSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(txtGhiChu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboTopping1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboTopping2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboTopping3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(46, 46, 46)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem)
                    .addComponent(btnHuy))
                .addContainerGap(499, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboTopping3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTopping3ActionPerformed
        updatePrice();
    }//GEN-LAST:event_cboTopping3ActionPerformed

    private void txtGiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGiaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGiaActionPerformed

    private void btnHuyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHuyActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnHuyActionPerformed

    private void cboTopping1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTopping1ActionPerformed
        updatePrice();
    }//GEN-LAST:event_cboTopping1ActionPerformed

    private void txtTenSanPhamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenSanPhamActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenSanPhamActionPerformed

    private void cboSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSizeActionPerformed
        updatePrice();
    }//GEN-LAST:event_cboSizeActionPerformed

    private void cboTopping2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTopping2ActionPerformed
        updatePrice();
    }//GEN-LAST:event_cboTopping2ActionPerformed

    private void txtGhiChuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGhiChuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGhiChuActionPerformed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        try {
            HoaDonChiTiet ct = new HoaDonChiTiet();
            ct.setDoUongID(doUong.getId());
            ct.setSoLuong((Integer) spnSoLuong.getValue());
            ct.setGhiChu(txtGhiChu.getText().isBlank() ? null : txtGhiChu.getText().trim());

            // Get Size
            String selectedSizeName = (String) cboSize.getSelectedItem();
            Size selectedSize = sizeMap.get(selectedSizeName);
            ct.setSizeID(selectedSize != null ? selectedSize.getId() : null);

            // Get Toppings
            List<String> selectedToppingNames = new ArrayList<>();
            JComboBox<?>[] toppingComboBoxes = {cboTopping1, cboTopping2, cboTopping3};
            for (JComboBox<?> cbo : toppingComboBoxes) {
                String selectedToppingName = (String) cbo.getSelectedItem();
                if (selectedToppingName != null && !selectedToppingName.equals("-- Không chọn --")) {
                    selectedToppingNames.add(selectedToppingName);
                }
            }
            ct.setDanhSachTopping(convertToppingToJSON(selectedToppingNames));

            // Set final price
            ct.setDonGia(new BigDecimal(txtGia.getText()));

            this.hoaDonChiTiet = ct;
            this.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi thêm sản phẩm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnThemActionPerformed

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
            java.util.logging.Logger.getLogger(ThemSanPhamJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ThemSanPhamJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ThemSanPhamJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ThemSanPhamJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Pass null for the DoUong object for testing purposes
                ThemSanPhamJDialog dialog = new ThemSanPhamJDialog(new javax.swing.JFrame(), true, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHuy;
    private javax.swing.JButton btnThem;
    private javax.swing.JComboBox<String> cboSize;
    private javax.swing.JComboBox<String> cboTopping1;
    private javax.swing.JComboBox<String> cboTopping2;
    private javax.swing.JComboBox<String> cboTopping3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSpinner spnSoLuong;
    private javax.swing.JTextField txtGhiChu;
    private javax.swing.JTextField txtGia;
    private javax.swing.JTextField txtTenSanPham;
    // End of variables declaration//GEN-END:variables
}