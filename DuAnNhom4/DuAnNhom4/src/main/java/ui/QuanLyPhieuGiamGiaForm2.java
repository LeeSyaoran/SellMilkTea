/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;
import com.toedter.calendar.JDateChooser;
import dao.PhieuGiamGiaDAO;
import model.PhieuGiamGia;
import utils.MsgBox;
import utils.XDate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Admin
 */
public class QuanLyPhieuGiamGiaForm2 extends javax.swing.JFrame {
        private PhieuGiamGiaDAO dao;
    private DefaultTableModel tblModel;
    private int row = -1;
    
    private JDateChooser DchNgayBatDau;
    private JDateChooser DchNgayKetThuc;
    

    /**
     * Creates new form QuanLyPhieuGiamGiaForm2
     */
    public QuanLyPhieuGiamGiaForm2() {
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        // Replace JComboBox with JDateChooser
        DchNgayBatDau = new JDateChooser();
        DchNgayKetThuc = new JDateChooser();

        // These lines are for positioning and might need tweaking based on the actual layout manager.
        // This is a best-effort attempt to place them where the old components were.
        DchNgayBatDau.setBounds(cboNgayBatDau.getBounds());
        DchNgayKetThuc.setBounds(cboNgayKetThuc.getBounds());
        
        // Add the new components to the panel that holds the old ones
        cboNgayBatDau.getParent().add(DchNgayBatDau);
        cboNgayKetThuc.getParent().add(DchNgayKetThuc);
        
        // Hide the old components
        cboNgayBatDau.setVisible(false);
        cboNgayKetThuc.setVisible(false);
        
        init();
    }
    private void init() {
        this.setTitle("Quản Lý Phiếu Giảm Giá");
        if (!utils.Auth.isManager()) {
            utils.MsgBox.alert(this, "Bạn không có quyền truy cập chức năng này!");
            this.dispose();
            return;
        }
        this.setLocationRelativeTo(null);
        dao = new PhieuGiamGiaDAO();

        // Setup table
        String[] columns = {"Mã GG", "Tên Chương Trình", "Loại", "Giá Trị", "Ngày BĐ", "Ngày KT", "Trạng Thái"};
        tblModel = new DefaultTableModel(columns, 0);
        TblPhieuGiamGia.setModel(tblModel);

        // Setup filter combos
        CbbTrangThai.setModel(new DefaultComboBoxModel<>(new String[]{"Tất cả", "HoatDong", "HetHan", "DaHuy", "SapDienRa"}));
        CbbLoaiGiam.setModel(new DefaultComboBoxModel<>(new String[]{"Tất cả", "PhanTram", "SoTien"}));
        CbbLoaiGiamDetail.setModel(new DefaultComboBoxModel<>(new String[]{"PhanTram", "SoTien"}));
        
        DchNgayBatDau.setDateFormatString("dd-MM-yyyy");
        DchNgayKetThuc.setDateFormatString("dd-MM-yyyy");
        
        String loaiGiam = (String) CbbLoaiGiamDetail.getSelectedItem();
        if ("PhanTram".equals(loaiGiam)) {
            TxtGiaTriGiamPhanTram.setEditable(true);
            TxtGiaTriGiamSoTien.setEditable(false);
            TxtGiaTriGiamSoTien.setText("");
        } else { // SoTien
            TxtGiaTriGiamPhanTram.setEditable(false);
            TxtGiaTriGiamSoTien.setEditable(true);
            TxtGiaTriGiamPhanTram.setText("");
        }

        fillTable();
        updateStatus();
    }

    private void fillTable() {
        tblModel.setRowCount(0);
        try {
            String keyword = TxtTimKiem.getText();
            String trangThai = (String) CbbTrangThai.getSelectedItem();
            String loaiGiam = (String) CbbLoaiGiam.getSelectedItem();

            List<PhieuGiamGia> list = dao.filter(keyword, trangThai, loaiGiam);

            for (PhieuGiamGia pgg : list) {
                Object[] rowData = {
                        pgg.getMaGiamGia(),
                        pgg.getTenChuongTrinh(),
                        pgg.getLoaiGiam(),
                        pgg.getGiaTriGiam(),
                        XDate.toString(pgg.getNgayBatDau(), "dd-MM-yyyy"),
                        XDate.toString(pgg.getNgayKetThuc(), "dd-MM-yyyy"),
                        pgg.getTrangThai()
                };
                tblModel.addRow(rowData);
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn dữ liệu!");
            e.printStackTrace();
        }
    }

    private void setForm(PhieuGiamGia pgg) {
        TxtMaGiamGia.setText(pgg.getMaGiamGia());
        TxtTenChuongTrinh.setText(pgg.getTenChuongTrinh());
        CbbLoaiGiamDetail.setSelectedItem(pgg.getLoaiGiam());
        if ("PhanTram".equals(pgg.getLoaiGiam())) {
            TxtGiaTriGiamPhanTram.setText(String.valueOf(pgg.getGiaTriGiam()));
            TxtGiaTriGiamSoTien.setText("");
        } else {
            TxtGiaTriGiamSoTien.setText(String.valueOf(pgg.getGiaTriGiam()));
            TxtGiaTriGiamPhanTram.setText("");
        }
        TxtDieuKienTT.setText(String.valueOf(pgg.getDieuKienToiThieu()));
        TxtSoLuongToiDa.setText(String.valueOf(pgg.getSoLuongToiDa()));
        DchNgayBatDau.setDate(pgg.getNgayBatDau());
        DchNgayKetThuc.setDate(pgg.getNgayKetThuc());
    }

    private PhieuGiamGia getForm() {
        if (TxtMaGiamGia.getText().trim().isEmpty() || TxtTenChuongTrinh.getText().trim().isEmpty() || (TxtGiaTriGiamPhanTram.getText().trim().isEmpty() && TxtGiaTriGiamSoTien.getText().trim().isEmpty())) {
            MsgBox.alert(this, "Vui lòng nhập đầy đủ thông tin!");
            return null;
        }

        PhieuGiamGia pgg = new PhieuGiamGia();
        pgg.setMaGiamGia(TxtMaGiamGia.getText().trim());
        pgg.setTenChuongTrinh(TxtTenChuongTrinh.getText().trim());
        pgg.setLoaiGiam((String) CbbLoaiGiamDetail.getSelectedItem());
        try {
            if ("PhanTram".equals(pgg.getLoaiGiam())) {
                pgg.setGiaTriGiam(new BigDecimal(TxtGiaTriGiamPhanTram.getText()));
            } else {
                pgg.setGiaTriGiam(new BigDecimal(TxtGiaTriGiamSoTien.getText()));
            }
            pgg.setDieuKienToiThieu(new BigDecimal(TxtDieuKienTT.getText().isEmpty() ? "0" : TxtDieuKienTT.getText()));
            pgg.setSoLuongToiDa(Integer.parseInt(TxtSoLuongToiDa.getText().isEmpty() ? "999999" : TxtSoLuongToiDa.getText()));
        } catch (NumberFormatException e) {
            MsgBox.alert(this, "Giá trị giảm, điều kiện, số lượng phải là số!");
            return null;
        }

        if (DchNgayBatDau.getDate() == null || DchNgayKetThuc.getDate() == null) {
            MsgBox.alert(this, "Vui lòng chọn ngày bắt đầu và kết thúc!");
            return null;
        }

        pgg.setNgayBatDau(DchNgayBatDau.getDate());
        pgg.setNgayKetThuc(DchNgayKetThuc.getDate());

        // Auto-determine status based on dates
        Date now = new Date();
        if (now.before(pgg.getNgayBatDau())) {
            pgg.setTrangThai("SapDienRa");
        } else if (now.after(pgg.getNgayKetThuc())) {
            pgg.setTrangThai("HetHan");
        } else {
            pgg.setTrangThai("HoatDong");
        }

        return pgg;
    }

    private void clearForm() {
        this.row = -1;
        PhieuGiamGia pgg = new PhieuGiamGia(); // Create an empty object to reset fields
        pgg.setNgayBatDau(new Date());
        pgg.setNgayKetThuc(XDate.addDays(new Date(), 30));
        pgg.setGiaTriGiam(BigDecimal.ZERO);
        pgg.setDieuKienToiThieu(BigDecimal.ZERO);
        pgg.setSoLuongToiDa(999999);
        this.setForm(pgg);
        TxtMaGiamGia.setText("");
        TxtTenChuongTrinh.setText("");
        TxtGiaTriGiamPhanTram.setText("");
        TxtGiaTriGiamSoTien.setText("");
        TxtDieuKienTT.setText("");
        TxtSoLuongToiDa.setText("");
        updateStatus();
    }

    private void updateStatus() {
        boolean edit = (this.row >= 0);
        TxtMaGiamGia.setEditable(!edit);
        BtnThem.setEnabled(!edit);
        BtnSua.setEnabled(edit);
        BtnHuy.setEnabled(edit);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        btnChon = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        BtnHuy = new javax.swing.JButton();
        BtnLamMoi = new javax.swing.JButton();
        TxtGiaTriGiamSoTien = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        BtnThem = new javax.swing.JButton();
        cboNgayBatDau = new javax.swing.JComboBox<>();
        CbbLoaiGiamDetail = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        cboNgayKetThuc = new javax.swing.JComboBox<>();
        BtnSua = new javax.swing.JButton();
        TxtGiaTriGiamPhanTram = new javax.swing.JTextField();
        TxtDieuKienTT = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        TxtSoLuongToiDa = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        CbbLoaiGiam = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TblPhieuGiamGia = new javax.swing.JTable();
        TxtTimKiem = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        BtnTimKiem = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        CbbTrangThai = new javax.swing.JComboBox<>();
        TxtMaGiamGia = new javax.swing.JTextField();
        TxtTenChuongTrinh = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1024, 658));

        jPanel1.setBackground(new java.awt.Color(225, 177, 112));
        jPanel1.setForeground(new java.awt.Color(164, 115, 115));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(164, 115, 115));
        jLabel5.setText("Tên Chương Trình");

        btnChon.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnChon.setForeground(new java.awt.Color(164, 115, 115));
        btnChon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/choose.png"))); // NOI18N
        btnChon.setText("Chọn");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(164, 115, 115));
        jLabel2.setText("Trạng Thái");

        BtnHuy.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        BtnHuy.setForeground(new java.awt.Color(164, 115, 115));
        BtnHuy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cancel.png"))); // NOI18N
        BtnHuy.setText("Hủy");
        BtnHuy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHuyActionPerformed(evt);
            }
        });

        BtnLamMoi.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        BtnLamMoi.setForeground(new java.awt.Color(164, 115, 115));
        BtnLamMoi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/refresh.png"))); // NOI18N
        BtnLamMoi.setText("Làm Mới");
        BtnLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnLamMoiActionPerformed(evt);
            }
        });

        TxtGiaTriGiamSoTien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtGiaTriGiamSoTienActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(164, 115, 115));
        jLabel12.setText("Giá Trị Giảm Số Tiền");

        BtnThem.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        BtnThem.setForeground(new java.awt.Color(164, 115, 115));
        BtnThem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/plus.png"))); // NOI18N
        BtnThem.setText("Thêm");
        BtnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnThemActionPerformed(evt);
            }
        });

        cboNgayBatDau.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboNgayBatDau.setForeground(new java.awt.Color(164, 115, 115));
        cboNgayBatDau.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboNgayBatDau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboNgayBatDauActionPerformed(evt);
            }
        });

        CbbLoaiGiamDetail.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        CbbLoaiGiamDetail.setForeground(new java.awt.Color(164, 115, 115));
        CbbLoaiGiamDetail.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        CbbLoaiGiamDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CbbLoaiGiamDetailActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(164, 115, 115));
        jLabel6.setText("Loại Giảm");

        cboNgayKetThuc.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboNgayKetThuc.setForeground(new java.awt.Color(164, 115, 115));
        cboNgayKetThuc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboNgayKetThuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboNgayKetThucActionPerformed(evt);
            }
        });

        BtnSua.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        BtnSua.setForeground(new java.awt.Color(164, 115, 115));
        BtnSua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fix.png"))); // NOI18N
        BtnSua.setText("Sửa");
        BtnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSuaActionPerformed(evt);
            }
        });

        TxtGiaTriGiamPhanTram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtGiaTriGiamPhanTramActionPerformed(evt);
            }
        });

        TxtDieuKienTT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtDieuKienTTActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(164, 115, 115));
        jLabel7.setText("Giá Trị Giảm %");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(164, 115, 115));
        jLabel10.setText("Ngày Bắt Đầu");

        TxtSoLuongToiDa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtSoLuongToiDaActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(164, 115, 115));
        jLabel8.setText("Điều Kiện Tối Thiểu");

        CbbLoaiGiam.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        CbbLoaiGiam.setForeground(new java.awt.Color(164, 115, 115));
        CbbLoaiGiam.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        CbbLoaiGiam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CbbLoaiGiamActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(164, 115, 115));
        jLabel4.setText("Mã Giảm Giá");

        TblPhieuGiamGia.setModel(new javax.swing.table.DefaultTableModel(
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
        TblPhieuGiamGia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TblPhieuGiamGiaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TblPhieuGiamGia);

        TxtTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtTimKiemActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(164, 115, 115));
        jLabel9.setText("Số Lượng Tối Đa");

        BtnTimKiem.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        BtnTimKiem.setForeground(new java.awt.Color(164, 115, 115));
        BtnTimKiem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/find.png"))); // NOI18N
        BtnTimKiem.setText("Tìm Kiếm");
        BtnTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTimKiemActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(164, 115, 115));
        jLabel1.setText("Tìm Kiếm");

        CbbTrangThai.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        CbbTrangThai.setForeground(new java.awt.Color(164, 115, 115));
        CbbTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        CbbTrangThai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CbbTrangThaiActionPerformed(evt);
            }
        });

        TxtMaGiamGia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtMaGiamGiaActionPerformed(evt);
            }
        });

        TxtTenChuongTrinh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtTenChuongTrinhActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(164, 115, 115));
        jLabel3.setText("Loại Giảm");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(164, 115, 115));
        jLabel11.setText("Ngày Kết Thúc");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(CbbLoaiGiam, 0, 135, Short.MAX_VALUE)
                            .addComponent(CbbTrangThai, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(TxtTimKiem))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BtnTimKiem))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(58, 58, 58)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(54, 54, 54)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboNgayBatDau, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboNgayKetThuc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(BtnHuy, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BtnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(BtnSua, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BtnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 133, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(TxtMaGiamGia, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(TxtTenChuongTrinh)
                            .addComponent(TxtGiaTriGiamPhanTram, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(TxtGiaTriGiamSoTien, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(TxtDieuKienTT, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                            .addComponent(TxtSoLuongToiDa)
                            .addComponent(CbbLoaiGiamDetail, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(54, 54, 54)
                .addComponent(btnChon)
                .addGap(34, 34, 34))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {TxtDieuKienTT, TxtGiaTriGiamPhanTram, TxtGiaTriGiamSoTien, TxtMaGiamGia, TxtSoLuongToiDa, TxtTenChuongTrinh});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BtnHuy, BtnLamMoi, BtnSua, BtnThem});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(TxtMaGiamGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnChon))
                                .addGap(50, 50, 50)
                                .addComponent(CbbLoaiGiamDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(TxtTenChuongTrinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(TxtGiaTriGiamPhanTram, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12)
                                    .addComponent(TxtGiaTriGiamSoTien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(26, 26, 26)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(TxtDieuKienTT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel9)
                                    .addComponent(TxtSoLuongToiDa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(cboNgayBatDau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(cboNgayKetThuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(45, 45, 45)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(BtnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(BtnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(155, 155, 155))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(BtnHuy)
                                    .addComponent(BtnLamMoi))
                                .addGap(88, 88, 88))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TxtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(BtnTimKiem))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CbbTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CbbLoaiGiam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(30, 30, 30)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {TxtDieuKienTT, TxtGiaTriGiamPhanTram, TxtGiaTriGiamSoTien, TxtMaGiamGia, TxtSoLuongToiDa, TxtTenChuongTrinh});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {BtnHuy, BtnLamMoi, BtnSua, BtnThem});

        jLayeredPane1.setLayer(jPanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 612, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnHuyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHuyActionPerformed
        // TODO add your handling code here:
        if(MsgBox.confirm(this, "Bạn có chắc muốn hủy phiếu này?")){
            String maGiamGia = TxtMaGiamGia.getText();
            try {
                dao.updateTrangThai(maGiamGia, "DaHuy");
                this.fillTable();
                this.clearForm();
                MsgBox.alert(this, "Hủy phiếu thành công!");
            } catch (Exception e){
                MsgBox.alert(this, "Hủy phiếu thất bại!");
            }
        }
    }//GEN-LAST:event_BtnHuyActionPerformed

    private void BtnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnLamMoiActionPerformed
        // TODO add your handling code here:
        clearForm();
    }//GEN-LAST:event_BtnLamMoiActionPerformed

    private void TxtGiaTriGiamSoTienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtGiaTriGiamSoTienActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtGiaTriGiamSoTienActionPerformed

    private void BtnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnThemActionPerformed
        // TODO add your handling code here:
        PhieuGiamGia pgg = getForm();
        if (pgg != null) {
            if (dao.selectById(pgg.getMaGiamGia()) != null) {
                MsgBox.alert(this, "Mã giảm giá đã tồn tại!");
                return;
            }
            try {
                dao.insert(pgg);
                this.fillTable();
                this.clearForm();
                MsgBox.alert(this, "Thêm mới thành công!");
            } catch (Exception e) {
                MsgBox.alert(this, "Thêm mới thất bại!");
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_BtnThemActionPerformed

    private void cboNgayBatDauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboNgayBatDauActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboNgayBatDauActionPerformed

    private void CbbLoaiGiamDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CbbLoaiGiamDetailActionPerformed
        // TODO add your handling code here:
        String loaiGiam = (String) CbbLoaiGiamDetail.getSelectedItem();
        if ("PhanTram".equals(loaiGiam)) {
            TxtGiaTriGiamPhanTram.setEditable(true);
            TxtGiaTriGiamSoTien.setEditable(false);
            TxtGiaTriGiamSoTien.setText("");
        } else { // SoTien
            TxtGiaTriGiamPhanTram.setEditable(false);
            TxtGiaTriGiamSoTien.setEditable(true);
            TxtGiaTriGiamPhanTram.setText("");
        }
    }//GEN-LAST:event_CbbLoaiGiamDetailActionPerformed

    private void cboNgayKetThucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboNgayKetThucActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboNgayKetThucActionPerformed

    private void BtnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSuaActionPerformed
        // TODO add your handling code here:
        PhieuGiamGia pgg = getForm();
        if (pgg != null) {
            try {
                dao.update(pgg);
                this.fillTable();
                MsgBox.alert(this, "Cập nhật thành công!");
            } catch (Exception e) {
                MsgBox.alert(this, "Cập nhật thất bại!");
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_BtnSuaActionPerformed

    private void TxtGiaTriGiamPhanTramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtGiaTriGiamPhanTramActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtGiaTriGiamPhanTramActionPerformed

    private void TxtDieuKienTTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtDieuKienTTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtDieuKienTTActionPerformed

    private void TxtSoLuongToiDaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtSoLuongToiDaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtSoLuongToiDaActionPerformed

    private void CbbLoaiGiamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CbbLoaiGiamActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CbbLoaiGiamActionPerformed

    private void TblPhieuGiamGiaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TblPhieuGiamGiaMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 1) {
            this.row = TblPhieuGiamGia.getSelectedRow();
            if (this.row >= 0) {
                String maGiamGia = (String) TblPhieuGiamGia.getValueAt(this.row, 0);
                PhieuGiamGia pgg = dao.selectById(maGiamGia);
                if (pgg != null) {
                    setForm(pgg);
                    updateStatus();
                }
            }
        }
    }//GEN-LAST:event_TblPhieuGiamGiaMouseClicked

    private void TxtTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtTimKiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtTimKiemActionPerformed

    private void BtnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTimKiemActionPerformed
        // TODO add your handling code here:
        fillTable();
    }//GEN-LAST:event_BtnTimKiemActionPerformed

    private void CbbTrangThaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CbbTrangThaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CbbTrangThaiActionPerformed

    private void TxtMaGiamGiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtMaGiamGiaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtMaGiamGiaActionPerformed

    private void TxtTenChuongTrinhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtTenChuongTrinhActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtTenChuongTrinhActionPerformed

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
            java.util.logging.Logger.getLogger(QuanLyPhieuGiamGiaForm2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QuanLyPhieuGiamGiaForm2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QuanLyPhieuGiamGiaForm2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QuanLyPhieuGiamGiaForm2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new QuanLyPhieuGiamGiaForm2().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnHuy;
    private javax.swing.JButton BtnLamMoi;
    private javax.swing.JButton BtnSua;
    private javax.swing.JButton BtnThem;
    private javax.swing.JButton BtnTimKiem;
    private javax.swing.JComboBox<String> CbbLoaiGiam;
    private javax.swing.JComboBox<String> CbbLoaiGiamDetail;
    private javax.swing.JComboBox<String> CbbTrangThai;
    private javax.swing.JTable TblPhieuGiamGia;
    private javax.swing.JTextField TxtDieuKienTT;
    private javax.swing.JTextField TxtGiaTriGiamPhanTram;
    private javax.swing.JTextField TxtGiaTriGiamSoTien;
    private javax.swing.JTextField TxtMaGiamGia;
    private javax.swing.JTextField TxtSoLuongToiDa;
    private javax.swing.JTextField TxtTenChuongTrinh;
    private javax.swing.JTextField TxtTimKiem;
    private javax.swing.JButton btnChon;
    private javax.swing.JComboBox<String> cboNgayBatDau;
    private javax.swing.JComboBox<String> cboNgayKetThuc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
