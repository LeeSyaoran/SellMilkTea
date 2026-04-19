/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

/**
 *
 * @author Admin
 */

import dao.ChiNhanhDAO;
import dao.NhanVienDAO;
import dao.impl.ChiNhanhDAOImpl;
import dao.impl.NhanVienDAOImpl;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.ChiNhanh;
import model.NhanVien;
import org.mindrot.jbcrypt.BCrypt;
import utils.Auth;
import utils.MsgBox;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class NhanVienForm extends javax.swing.JFrame {

    NhanVienDAO nhanVienDAO = new NhanVienDAOImpl();
    ChiNhanhDAO chiNhanhDAO = new ChiNhanhDAOImpl();
    int row = -1;

    /**
     * Creates new form NhanVienForm
     */
    public NhanVienForm() {
        initComponents();
        init();
    }
    
    private void init() {
        setTitle("Quản Lý Nhân Viên");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        // Security check
        if (!Auth.isManager()) {
            btnThem.setEnabled(false);
            btnLuu.setEnabled(false);
            btnXoa.setEnabled(false);
            cboVaiTro.setEnabled(false);
            cboChiNhanh.setEnabled(false);
        }
        this.fillTable();
        this.fillComboBoxChiNhanh();
        this.fillComboBoxVaiTro();
        this.fillComboBoxLocTheoVaiTro();
        this.fillComboBoxLocTheoChiNhanh();
        this.updateStatus();
    }

    void fillTable() {
        fillTableData(null, null, null); // Load all employees initially
    }

    private void fillTableData(String keyword, String vaiTro, Integer chiNhanhId) {
        DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
        model.setRowCount(0);
        try {
            List<ChiNhanh> chiNhanhList = chiNhanhDAO.findAll();
            java.util.Map<Integer, String> chiNhanhMap = new java.util.HashMap<>();
            for (ChiNhanh cn : chiNhanhList) {
                chiNhanhMap.put(cn.getId(), cn.getTenChiNhanh());
            }

            // Use the new selectByConditions method
            List<NhanVien> list = nhanVienDAO.selectByConditions(keyword, vaiTro, chiNhanhId);
            for (NhanVien nv : list) {
                String tenChiNhanh = nv.getChiNhanhID() != null ? chiNhanhMap.get(nv.getChiNhanhID()) : "N/A";
                Object[] row = {
                    nv.getId(),
                    nv.getHoTen(),
                    nv.getUsername(),
                    nv.getSoDienThoai(),
                    nv.getEmail(),
                    nv.getVaiTro(),
                    tenChiNhanh,
                    nv.getTrangThai()
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn dữ liệu nhân viên!");
            e.printStackTrace();
        }
    }
    
    void fillComboBoxVaiTro(){
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboVaiTro.getModel();
        model.removeAllElements();
        model.addElement("QuanLy");
        model.addElement("NhanVien");
    }

    void fillComboBoxLocTheoVaiTro() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboLocTheoVaiTro.getModel();
        model.removeAllElements();
        model.addElement("Tất cả"); // Add "All" option
        model.addElement("QuanLy");
        model.addElement("NhanVien");
        model.addElement("ThuNgan");
    }

    void fillComboBoxChiNhanh() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboChiNhanh.getModel();
        model.removeAllElements();
        try {
            List<ChiNhanh> list = chiNhanhDAO.findAll();
            for (ChiNhanh cn : list) {
                model.addElement(cn);
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn dữ liệu chi nhánh!");
            e.printStackTrace();
        }
    }
    
    void fillComboBoxLocTheoChiNhanh() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboLocTheoChiNhanh.getModel();
        model.removeAllElements();
        model.addElement("Tất cả"); // Add "All" option
        try {
            List<ChiNhanh> list = chiNhanhDAO.findAll();
            for (ChiNhanh cn : list) {
                model.addElement(cn);
            }
        } catch (Exception e) {
            MsgBox.alert(this, "Lỗi truy vấn dữ liệu chi nhánh!");
            e.printStackTrace();
        }
    }

    // Helper method to apply all filters and refresh the table
    private void applyFilters() {
        String keyword = txtTimKiem.getText();
        
        String vaiTro = (String) cboLocTheoVaiTro.getSelectedItem();
        if (vaiTro != null && vaiTro.equals("Tất cả")) {
            vaiTro = null;
        }

        Integer chiNhanhId = null;
        Object selectedChiNhanh = cboLocTheoChiNhanh.getSelectedItem();
        if (selectedChiNhanh instanceof ChiNhanh) {
            chiNhanhId = ((ChiNhanh) selectedChiNhanh).getId();
        } else if (selectedChiNhanh instanceof String && "Tất cả".equals(selectedChiNhanh)) {
            chiNhanhId = null; // No branch filter
        }

        fillTableData(keyword, vaiTro, chiNhanhId);
    }
    
    private boolean validateForm() {
        if (txtHoTen.getText().trim().isEmpty()) {
            MsgBox.alert(this, "Họ tên không được để trống!");
            txtHoTen.requestFocus();
            return false;
        }

        if (txtTenDangNhap.getText().trim().isEmpty()) {
            MsgBox.alert(this, "Tên đăng nhập không được để trống!");
            txtTenDangNhap.requestFocus();
            return false;
        }

        // Check for username uniqueness only for new employees
        if (txtMaNV.getText().trim().isEmpty()) { // This is a new employee
            NhanVien existingNV = nhanVienDAO.findByUsername(txtTenDangNhap.getText().trim());
            if (existingNV != null) {
                MsgBox.alert(this, "Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác!");
                txtTenDangNhap.requestFocus();
                return false;
            }
        }
        
        if (txtSoDienThoai.getText().trim().isEmpty()) {
            MsgBox.alert(this, "Số điện thoại không được để trống!");
            txtSoDienThoai.requestFocus();
            return false;
        } else if (!txtSoDienThoai.getText().trim().matches("^0[0-9]{9,10}$")) { // Simple regex for 10-11 digit phone starting with 0
            MsgBox.alert(this, "Số điện thoại không hợp lệ (phải bắt đầu bằng 0 và có 10-11 chữ số)!");
            txtSoDienThoai.requestFocus();
            return false;
        }

        if (txtEmail.getText().trim().isEmpty()) {
            MsgBox.alert(this, "Email không được để trống!");
            txtEmail.requestFocus();
            return false;
        } else if (!txtEmail.getText().trim().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) { // Standard email regex
            MsgBox.alert(this, "Email không hợp lệ!");
            txtEmail.requestFocus();
            return false;
        }
        
        // No explicit validation for VaiTro and ChiNhanh as they are combo boxes and always have a selected value (or null is handled).
        // TrangThai is radio button group, always has a selection.

        return true;
    }

    void setModel(NhanVien model) {
        txtMaNV.setText(model.getId() > 0 ? String.valueOf(model.getId()) : "");
        txtHoTen.setText(model.getHoTen());
        txtTenDangNhap.setText(model.getUsername());
        txtSoDienThoai.setText(model.getSoDienThoai());
        txtEmail.setText(model.getEmail());

        cboVaiTro.setSelectedItem(model.getVaiTro());
        
        if (model.getChiNhanhID() != null) {
            DefaultComboBoxModel cboModel = (DefaultComboBoxModel) cboChiNhanh.getModel();
            for (int i = 0; i < cboModel.getSize(); i++) {
                ChiNhanh cn = (ChiNhanh) cboModel.getElementAt(i);
                if (cn.getId() == model.getChiNhanhID()) {
                    cboChiNhanh.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            cboChiNhanh.setSelectedIndex(-1);
        }
        
        if ("HoatDong".equals(model.getTrangThai())) {
            rdoHoatDong.setSelected(true);
        } else {
            rdoNghiViec.setSelected(true);
        }
        
        updateStatus();
    }

    NhanVien getModel() {
        NhanVien model = new NhanVien();
        boolean isUpdate = !txtMaNV.getText().trim().isEmpty();

        if(isUpdate){
            model.setId(Integer.parseInt(txtMaNV.getText()));
        }

        model.setHoTen(txtHoTen.getText());
        model.setUsername(txtTenDangNhap.getText());
        
        // --- Password Handling ---
        // Since the UI for password management was removed from this form,
        // we handle passwords programmatically.
        if(isUpdate) {
            // For an existing user, we retrieve their current password hash and keep it.
            // Passwords cannot be changed from this form.
            NhanVien oldNV = nhanVienDAO.findById(model.getId());
            if(oldNV != null) {
                model.setPasswordHash(oldNV.getPasswordHash());
            }
        } else {
            // For a new user, we set a hardcoded default password.
            // IMPORTANT: This is a placeholder. In a real application, you should
            // implement a more secure way to set initial passwords (e.g., auto-generated & emailed).
            model.setPasswordHash(BCrypt.hashpw("123456", BCrypt.gensalt()));
        }

        model.setSoDienThoai(txtSoDienThoai.getText());
        model.setEmail(txtEmail.getText());
        model.setVaiTro((String) cboVaiTro.getSelectedItem());
        
        Object selectedChiNhanh = cboChiNhanh.getSelectedItem();
        if(selectedChiNhanh instanceof ChiNhanh) {
            model.setChiNhanhID(((ChiNhanh) selectedChiNhanh).getId());
        }

        model.setTrangThai(rdoHoatDong.isSelected() ? "HoatDong" : "NghiViec");
        return model;
    }

    void updateStatus() {
        boolean edit = this.row >= 0;
        txtMaNV.setEditable(false); // MaNV should never be editable by user
        btnThem.setEnabled(!edit);
        btnLuu.setEnabled(edit);
        btnXoa.setEnabled(edit);
    }
    
    void edit() {
        Integer maNV = (Integer) tblNhanVien.getValueAt(this.row, 0);
        NhanVien model = nhanVienDAO.findById(maNV);
        if (model != null) {
            this.setModel(model);
            this.updateStatus();
        }
    }

    void clear() {
        setModel(new NhanVien());
        this.row = -1;
        updateStatus();
    }

    void insert() {
        if (!Auth.isManager()) {
            MsgBox.alert(this, "Bạn không có quyền thực hiện chức năng này!");
            return;
        }
        if (!validateForm()) {
            return;
        }
        NhanVien model = getModel();
        try {
            nhanVienDAO.insert(model);
            this.fillTable();
            this.clear();
            MsgBox.alert(this, "Thêm mới thành công! Mật khẩu mặc định là '123456'.");
        } catch (Exception e) {
            MsgBox.alert(this, "Thêm mới thất bại! Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    void update() {
        if (!Auth.isManager()) {
            MsgBox.alert(this, "Bạn không có quyền thực hiện chức năng này!");
            return;
        }
        if (!validateForm()) {
            return;
        }
        NhanVien model = getModel();
        try {
            nhanVienDAO.update(model);
            this.fillTable();
            MsgBox.alert(this, "Cập nhật thành công!");
        } catch (Exception e) {
            MsgBox.alert(this, "Cập nhật thất bại! Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    void delete() {
        if (!Auth.isManager()) {
            MsgBox.alert(this, "Bạn không có quyền thực hiện chức năng này!");
            return;
        }
        if(Auth.user.getId() == Integer.parseInt(txtMaNV.getText())){
            MsgBox.alert(this, "Bạn không thể xóa chính mình!");
            return;
        }
        if (MsgBox.confirm(this, "Bạn thực sự muốn xóa nhân viên này?")) {
            Integer maNV = Integer.parseInt(txtMaNV.getText());
            try {
                nhanVienDAO.delete(maNV);
                this.fillTable();
                this.clear();
                MsgBox.alert(this, "Xóa thành công!");
            } catch (Exception e) {
                MsgBox.alert(this, "Xóa thất bại!");
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        btnMoi = new javax.swing.JButton();
        rdoHoatDong = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        txtSoDienThoai = new javax.swing.JTextField();
        btnLuu = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        btnThem = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        cboChiNhanh = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNhanVien = new javax.swing.JTable();
        rdoNghiViec = new javax.swing.JRadioButton();
        txtTenDangNhap = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtHoTen = new javax.swing.JTextField();
        txtMaNV = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cboVaiTro = new javax.swing.JComboBox<>();
        txtTimKiem = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        btnTimKiem = new javax.swing.JButton();
        cboLocTheoVaiTro = new javax.swing.JComboBox<>();
        cboLocTheoChiNhanh = new javax.swing.JComboBox<>();
        btnLamMoiTimKiem = new javax.swing.JButton();
        btnXuatExcel = new javax.swing.JButton();
        btnGuiBaoCao = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(225, 177, 112));
        jPanel1.setForeground(new java.awt.Color(164, 115, 115));

        btnMoi.setBackground(new java.awt.Color(225, 177, 112));
        btnMoi.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnMoi.setForeground(new java.awt.Color(164, 115, 115));
        btnMoi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/new.png"))); // NOI18N
        btnMoi.setText("Mới");
        btnMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoiActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdoHoatDong);
        rdoHoatDong.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        rdoHoatDong.setForeground(new java.awt.Color(164, 115, 115));
        rdoHoatDong.setText("Hoạt Động");
        rdoHoatDong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoHoatDongActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(164, 115, 115));
        jLabel2.setText("Họ Tên");

        txtSoDienThoai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSoDienThoaiActionPerformed(evt);
            }
        });

        btnLuu.setBackground(new java.awt.Color(225, 177, 112));
        btnLuu.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLuu.setForeground(new java.awt.Color(164, 115, 115));
        btnLuu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/save.png"))); // NOI18N
        btnLuu.setText("Lưu");
        btnLuu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuActionPerformed(evt);
            }
        });

        btnXoa.setBackground(new java.awt.Color(225, 177, 112));
        btnXoa.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnXoa.setForeground(new java.awt.Color(164, 115, 115));
        btnXoa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/delete_one.png"))); // NOI18N
        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(164, 115, 115));
        jLabel7.setText("Email");

        btnThem.setBackground(new java.awt.Color(225, 177, 112));
        btnThem.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnThem.setForeground(new java.awt.Color(164, 115, 115));
        btnThem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/plus.png"))); // NOI18N
        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(164, 115, 115));
        jLabel8.setText("Vai Trò");

        cboChiNhanh.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboChiNhanh.setForeground(new java.awt.Color(164, 115, 115));
        cboChiNhanh.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboChiNhanh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboChiNhanhActionPerformed(evt);
            }
        });

        tblNhanVien.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã NV", "Họ Tên", "Tên Đăng Nhập", "Số Điện Thoại", "Email", "Vai Trò", "Chi Nhánh", "Trạng Thái"
            }
        ));
        tblNhanVien.setSelectionForeground(new java.awt.Color(164, 115, 115));
        tblNhanVien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblNhanVienMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblNhanVien);

        buttonGroup1.add(rdoNghiViec);
        rdoNghiViec.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        rdoNghiViec.setForeground(new java.awt.Color(164, 115, 115));
        rdoNghiViec.setText("Nghỉ Việc");
        rdoNghiViec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoNghiViecActionPerformed(evt);
            }
        });

        txtTenDangNhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenDangNhapActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(164, 115, 115));
        jLabel10.setText("Trạng Thái");

        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(164, 115, 115));
        jLabel1.setText("Mã NV");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(164, 115, 115));
        jLabel6.setText("Số Điện Thoại");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(164, 115, 115));
        jLabel9.setText("Chi Nhánh");

        txtHoTen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHoTenActionPerformed(evt);
            }
        });

        txtMaNV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaNVActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(164, 115, 115));
        jLabel3.setText("Tên Đăng Nhập");

        cboVaiTro.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboVaiTro.setForeground(new java.awt.Color(164, 115, 115));
        cboVaiTro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboVaiTro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboVaiTroActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(164, 115, 115));
        jLabel4.setText("Tìm Kiếm");

        btnTimKiem.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnTimKiem.setForeground(new java.awt.Color(164, 115, 115));
        btnTimKiem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/filter.png"))); // NOI18N
        btnTimKiem.setText("Tìm Kiếm");
        btnTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimKiemActionPerformed(evt);
            }
        });

        cboLocTheoVaiTro.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboLocTheoVaiTro.setForeground(new java.awt.Color(164, 115, 115));
        cboLocTheoVaiTro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cboLocTheoChiNhanh.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboLocTheoChiNhanh.setForeground(new java.awt.Color(164, 115, 115));
        cboLocTheoChiNhanh.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnLamMoiTimKiem.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLamMoiTimKiem.setForeground(new java.awt.Color(164, 115, 115));
        btnLamMoiTimKiem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/new.png"))); // NOI18N
        btnLamMoiTimKiem.setText("Làm Mới");
        btnLamMoiTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiTimKiemActionPerformed(evt);
            }
        });

        btnXuatExcel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnXuatExcel.setForeground(new java.awt.Color(164, 115, 115));
        btnXuatExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/export.png"))); // NOI18N
        btnXuatExcel.setText("Xuất Excel");
        btnXuatExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuatExcelActionPerformed(evt);
            }
        });

        btnGuiBaoCao.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnGuiBaoCao.setForeground(new java.awt.Color(164, 115, 115));
        btnGuiBaoCao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/send report.png"))); // NOI18N
        btnGuiBaoCao.setText("Gửi Báo Cáo");
        btnGuiBaoCao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuiBaoCaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 844, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnThem, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(btnXoa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnMoi, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                            .addComponent(btnLuu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTimKiem)
                            .addComponent(txtMaNV, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                            .addComponent(txtHoTen)
                            .addComponent(txtTenDangNhap)
                            .addComponent(txtSoDienThoai))
                        .addGap(47, 47, 47)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(rdoHoatDong, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(rdoNghiViec, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtEmail)
                                    .addComponent(cboVaiTro, 0, 428, Short.MAX_VALUE)
                                    .addComponent(cboChiNhanh, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(102, 102, 102))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnTimKiem)
                                .addGap(44, 44, 44)
                                .addComponent(cboLocTheoVaiTro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(cboLocTheoChiNhanh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
                                .addComponent(btnLamMoiTimKiem)
                                .addGap(58, 58, 58)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnGuiBaoCao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnXuatExcel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnMoi, btnThem, btnXoa});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnTimKiem)
                                .addComponent(cboLocTheoVaiTro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboLocTheoChiNhanh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnLamMoiTimKiem)
                                .addComponent(btnXuatExcel))
                            .addComponent(jLabel4))
                        .addGap(35, 35, 35)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel2)
                                        .addGap(63, 63, 63))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txtMaNV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(12, 12, 12)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(txtTenDangNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel3))
                                        .addGap(18, 18, 18)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(txtSoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7)
                                    .addComponent(btnGuiBaoCao))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cboVaiTro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(cboChiNhanh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(rdoHoatDong)
                                    .addComponent(rdoNghiViec))))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 446, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(256, 256, 256)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnThem, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(btnLuu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnXoa, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                            .addComponent(btnMoi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnLuu, btnMoi, btnThem, btnXoa});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoiActionPerformed
        clear();
    }//GEN-LAST:event_btnMoiActionPerformed

    private void rdoHoatDongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoHoatDongActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdoHoatDongActionPerformed

    private void txtSoDienThoaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSoDienThoaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSoDienThoaiActionPerformed

    private void btnLuuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuActionPerformed
        update();
    }//GEN-LAST:event_btnLuuActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        delete();
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        insert();
    }//GEN-LAST:event_btnThemActionPerformed

    private void cboChiNhanhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboChiNhanhActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboChiNhanhActionPerformed

    private void tblNhanVienMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblNhanVienMouseClicked
        this.row = tblNhanVien.rowAtPoint(evt.getPoint());
        this.edit();
    }//GEN-LAST:event_tblNhanVienMouseClicked

    private void rdoNghiViecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoNghiViecActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdoNghiViecActionPerformed

    private void txtTenDangNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenDangNhapActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenDangNhapActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtHoTenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHoTenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHoTenActionPerformed

    private void txtMaNVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaNVActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaNVActionPerformed

    private void cboVaiTroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboVaiTroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboVaiTroActionPerformed

    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKiemActionPerformed

        applyFilters();   
    }//GEN-LAST:event_btnTimKiemActionPerformed

    private void btnLamMoiTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiTimKiemActionPerformed
        // TODO add your handling code here:
          txtTimKiem.setText("");
        cboLocTheoVaiTro.setSelectedItem("Tất cả");
        cboLocTheoChiNhanh.setSelectedItem("Tất cả");
        applyFilters();
    }//GEN-LAST:event_btnLamMoiTimKiemActionPerformed

    private void btnXuatExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuatExcelActionPerformed
          DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
        if (model.getRowCount() == 0) {
            MsgBox.alert(this, "Không có dữ liệu để xuất ra Excel.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new File("DanhSachNhanVien.xlsx"));
        
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
            }

            try (FileOutputStream out = new FileOutputStream(fileToSave)) {
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet("Nhân Viên");

                // Write header
                Row headerRow = sheet.createRow(0);
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Cell cell = headerRow.createCell(col);
                    cell.setCellValue(model.getColumnName(col));
                }

                // Write data rows
                for (int rowNum = 0; rowNum < model.getRowCount(); rowNum++) {
                    Row dataRow = sheet.createRow(rowNum + 1); // +1 because header is at row 0
                    for (int col = 0; col < model.getColumnCount(); col++) {
                        Cell cell = dataRow.createCell(col);
                        Object value = model.getValueAt(rowNum, col);
                        if (value != null) {
                            cell.setCellValue(value.toString());
                        }
                    }
                }

                workbook.write(out);
                workbook.close();
                MsgBox.alert(this, "Xuất file Excel thành công tại: " + fileToSave.getAbsolutePath());
            } catch (IOException e) {
                MsgBox.alert(this, "Lỗi khi xuất file Excel: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnXuatExcelActionPerformed

    private void btnGuiBaoCaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuiBaoCaoActionPerformed
        // TODO add your handling code here:
         new BaoCaoForm().setVisible(true);
    }//GEN-LAST:event_btnGuiBaoCaoActionPerformed
                                   

    private void cboLocTheoVaiTroActionPerformed(java.awt.event.ActionEvent evt) {                                                
        applyFilters();
    }                                               

    private void cboLocTheoChiNhanhActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        applyFilters();
    }                                                                                              
                                          

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(NhanVienForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(NhanVienForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(NhanVienForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(NhanVienForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//             
//        
//           
//                // </editor-fold>
//        
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new NhanVienForm().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuiBaoCao;
    private javax.swing.JButton btnLamMoiTimKiem;
    private javax.swing.JButton btnLuu;
    private javax.swing.JButton btnMoi;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JButton btnXuatExcel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboChiNhanh;
    private javax.swing.JComboBox<String> cboLocTheoChiNhanh;
    private javax.swing.JComboBox<String> cboLocTheoVaiTro;
    private javax.swing.JComboBox<String> cboVaiTro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rdoHoatDong;
    private javax.swing.JRadioButton rdoNghiViec;
    private javax.swing.JTable tblNhanVien;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtHoTen;
    private javax.swing.JTextField txtMaNV;
    private javax.swing.JTextField txtSoDienThoai;
    private javax.swing.JTextField txtTenDangNhap;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables
}
