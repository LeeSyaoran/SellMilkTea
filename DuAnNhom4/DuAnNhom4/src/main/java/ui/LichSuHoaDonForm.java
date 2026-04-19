    /*
 * Click nbfs://nbhost/SystemFileServices/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileServices/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

import dao.DoUongDAO;
import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.SizeDAO;
import dao.ToppingDAO;
import dao.impl.DoUongDAOImpl;
import dao.impl.HoaDonDAOImpl;
import dao.impl.KhachHangDAOImpl;
import dao.impl.SizeDAOImpl;
import dao.impl.ToppingDAOImpl;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.DoUong;
import model.HoaDon;
import model.HoaDonChiTiet;
import model.KhachHang;
import model.Size;

/**
 *
 * @author Admin
 */
public class LichSuHoaDonForm extends javax.swing.JFrame {

    private HoaDonDAO hoaDonDAO = new HoaDonDAOImpl();
    private KhachHangDAO khachHangDAO = new KhachHangDAOImpl();
    private DoUongDAO doUongDAO = new DoUongDAOImpl();
    private SizeDAO sizeDAO = new SizeDAOImpl();
    private ToppingDAO toppingDAO = new ToppingDAOImpl();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public LichSuHoaDonForm() {
        initComponents();
        if (!utils.Auth.isManager()) {
            utils.MsgBox.alert(this, "Bạn không có quyền truy cập chức năng này!");
            this.dispose();
            return;
        }
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initFilters();
        loadData();
        setupEventHandlers();
    }

    private void initFilters() {
        loadTrangThaiFilter();
        loadPhuongThucFilter();
        loadDateFilter();
    }
    
    private void resetFilters() {
        cboTrangThai.setSelectedIndex(0);
        cboPhuongThucThanhToan.setSelectedIndex(0);
        cboYear.setSelectedIndex(0);
        cboMonth.setSelectedIndex(0);
        cboDay.setSelectedIndex(0);
    }
    
    private void refreshForm() {
        txtTimKiem.setText("");
        resetFilters();
        xuLyLocDuLieu();
        ((DefaultTableModel) tblLSHoaDonChiTiet.getModel()).setRowCount(0);
    }

    private void loadData() {
        loadHoaDonList();
    }

    private void loadHoaDonList() {
        try {
            List<HoaDon> list = hoaDonDAO.selectAll();
            DefaultTableModel model = (DefaultTableModel) tblLSHoaDon.getModel();
            model.setRowCount(0);
            model.setColumnIdentifiers(new String[]{"Mã HĐ", "Ngày Lập", "Khách Hàng", "Tổng Tiền", "Giảm Giá", "PTTT", "Trạng Thái"});

            for (HoaDon hd : list) {
                String khachHangName = "Khách lẻ";
                if (hd.getKhachHangID() != null) {
                    KhachHang kh = khachHangDAO.findById(hd.getKhachHangID());
                    if (kh != null) {
                        khachHangName = kh.getHoTen();
                    }
                }
                model.addRow(new Object[]{
                    hd.getMaHoaDon(),
                    dateFormat.format(hd.getNgayLap()),
                    khachHangName,
                    String.format("%,d", hd.getTongTien().longValue()) + "đ",
                    String.format("%,d", hd.getGiamGia().longValue()) + "đ",
                    formatPhuongThuc(hd.getPhuongThucThanhToan()),
                    hd.getTrangThai()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String formatPhuongThuc(String code) {
        switch (code) {
            case "TienMat":
                return "Tiền mặt";
            case "ChuyenKhoan":
                return "Chuyển khoản";
            case "The":
                return "Thẻ";
            default:
                return code;
        }
    }

    private void loadTrangThaiFilter() {
        cboTrangThai.removeAllItems();
        cboTrangThai.addItem("-- Tất cả --");
        cboTrangThai.addItem("DaThanhToan");
    }

    private void loadPhuongThucFilter() {
        cboPhuongThucThanhToan.removeAllItems();
        cboPhuongThucThanhToan.addItem("-- Tất cả --");
        cboPhuongThucThanhToan.addItem("Tiền mặt");
        cboPhuongThucThanhToan.addItem("Thẻ");
        cboPhuongThucThanhToan.addItem("Chuyển khoản");
    }

    private void loadDateFilter() {
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);

        // Load Year
        cboYear.removeAllItems();
        cboYear.addItem("-- Tất cả năm --");
        for (int y = currentYear - 5; y <= currentYear + 1; y++) {
            cboYear.addItem(String.valueOf(y));
        }

        // Load Month
        cboMonth.removeAllItems();
        cboMonth.addItem("-- Tất cả tháng --");
        for (int m = 1; m <= 12; m++) {
            cboMonth.addItem(String.format("%02d", m));
        }

        // Load Day
        cboDay.removeAllItems();
        cboDay.addItem("-- Tất cả ngày --");
        for (int d = 1; d <= 31; d++) {
            cboDay.addItem(String.format("%02d", d));
        }
    }

    private void setupEventHandlers() {
        tblLSHoaDon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                xuLyChonHoaDon();
            }
        });

        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        cboTrangThai.addActionListener(e -> xuLyLocDuLieu());
        cboPhuongThucThanhToan.addActionListener(e -> xuLyLocDuLieu());
        cboYear.addActionListener(e -> xuLyLocDuLieu());
        cboMonth.addActionListener(e -> xuLyLocDuLieu());
        cboDay.addActionListener(e -> xuLyLocDuLieu());
        btnLamMoi.addActionListener(e -> refreshForm());
        btnInHoaDon.addActionListener(e -> xuLyInHoaDon());
    }

    private void xuLyChonHoaDon() {
        int row = tblLSHoaDon.getSelectedRow();
        if (row < 0) return;

        try {
            Object cellValue = tblLSHoaDon.getValueAt(row, 0);
            if (cellValue == null) return;
            String maHoaDon = cellValue.toString();
            // Extract ID from "HD000001"
            int hoaDonID = Integer.parseInt(maHoaDon.substring(2));
            HoaDon hd = hoaDonDAO.selectById(hoaDonID);

            if (hd != null) {
                loadChiTietHoaDon(hd.getId());
                hiemThiThongTinHoaDon(hd);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void loadChiTietHoaDon(int hoaDonID) {
        try {
            List<HoaDonChiTiet> list = hoaDonDAO.selectChiTietByHoaDon(hoaDonID);
            DefaultTableModel model = (DefaultTableModel) tblLSHoaDonChiTiet.getModel();
            model.setRowCount(0);
            model.setColumnIdentifiers(new String[]{"Tên Đồ Uống", "Size", "SL", "Đơn Giá", "Thành Tiền", "Topping", "Ghi chú"});

            for (HoaDonChiTiet ct : list) {
                DoUong du = doUongDAO.selectById(ct.getDoUongID());
                String tenDoUong = (du != null) ? du.getTenDoUong() : "Sản phẩm không rõ";
                
                String size = "M"; // Default
                if (ct.getSizeID() != null) {
                    Size s = sizeDAO.findById(ct.getSizeID());
                    if (s != null) {
                        size = s.getTenSize();
                    }
                }

                String topping = ct.getDanhSachTopping() != null ? formatToppingForDisplay(ct.getDanhSachTopping()) : "Không";
                String ghiChu = ct.getGhiChu() != null ? ct.getGhiChu() : "";

                model.addRow(new Object[]{
                    tenDoUong,
                    size,
                    ct.getSoLuong(),
                    String.format("%,d", ct.getDonGia().longValue()) + "đ",
                    String.format("%,d", ct.getDonGia().multiply(java.math.BigDecimal.valueOf(ct.getSoLuong())).longValue()) + "đ",
                    topping,
                    ghiChu
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải chi tiết: " + e.getMessage());
        }
    }

    private String formatToppingForDisplay(String toppingJSON) {
        if (toppingJSON == null || toppingJSON.isEmpty()) {
            return "Không có";
        }

        try {
            StringBuilder display = new StringBuilder();
            String[] items = toppingJSON.split("\\}, \\{");

            for (int i = 0; i < items.length; i++) {
                String item = items[i].replaceAll("[\\[\\]{}]", "");
                String[] parts = item.split(",");

                for (String part : parts) {
                    if (part.contains("\"Ten\"")) {
                        int startIdx = part.indexOf("\"") + 1;
                        int lastIdx = part.lastIndexOf("\"");
                        if (lastIdx > startIdx) {
                            String toppingName = part.substring(startIdx, lastIdx);
                            if (i > 0 && display.length() > 0) {
                                display.append(", ");
                            }
                            display.append(toppingName);
                        }
                        break;
                    }
                }
            }

            return display.toString().isEmpty() ? "Không có" : display.toString();
        } catch (Exception e) {
            return toppingJSON;
        }
    }

    private void hiemThiThongTinHoaDon(HoaDon hd) {
        String info = "Mã hóa đơn: " + hd.getMaHoaDon() + "\n"
                + "Ngày lập: " + dateFormat.format(hd.getNgayLap()) + "\n"
                + "Tổng tiền: " + String.format("%,d", hd.getTongTien().longValue()) + "đ\n"
                + "Giảm giá: " + String.format("%,d", hd.getGiamGia().longValue()) + "đ\n"
                + "Phương thức: " + formatPhuongThuc(hd.getPhuongThucThanhToan()) + "\n"
                + "Trạng thái: " + hd.getTrangThai();
        JOptionPane.showMessageDialog(this, info, "Thông tin hóa đơn", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyTimKiem() {
        xuLyLocDuLieu();
    }

    private void xuLyLocDuLieu() {
        String keyword = txtTimKiem.getText().trim();
        String trangThai = cboTrangThai.getSelectedItem().toString();
        String phuongThuc = cboPhuongThucThanhToan.getSelectedItem().toString();
        String year = cboYear.getSelectedItem().toString();
        String month = cboMonth.getSelectedItem().toString();
        String day = cboDay.getSelectedItem().toString();

        try {
            List<HoaDon> list = hoaDonDAO.selectAll();
            DefaultTableModel model = (DefaultTableModel) tblLSHoaDon.getModel();
            model.setRowCount(0);

            for (HoaDon hd : list) {
                boolean matchKeyword = keyword.isEmpty() ||
                                       hd.getMaHoaDon().contains(keyword) ||
                                       (hd.getGhiChu() != null && hd.getGhiChu().contains(keyword));
                
                boolean matchTrangThai = trangThai.equals("-- Tất cả --") || hd.getTrangThai().equals(trangThai);
                boolean matchPhuongThuc = phuongThuc.equals("-- Tất cả --") || formatPhuongThuc(hd.getPhuongThucThanhToan()).equals(phuongThuc);
                
                // Date filtering
                Calendar cal = Calendar.getInstance();
                cal.setTime(hd.getNgayLap());
                int invoiceYear = cal.get(Calendar.YEAR);
                int invoiceMonth = cal.get(Calendar.MONTH) + 1;
                int invoiceDay = cal.get(Calendar.DAY_OF_MONTH);
                
                boolean matchYear = year.equals("-- Tất cả năm --") || String.valueOf(invoiceYear).equals(year);
                boolean matchMonth = month.equals("-- Tất cả tháng --") || String.format("%02d", invoiceMonth).equals(month);
                boolean matchDay = day.equals("-- Tất cả ngày --") || String.format("%02d", invoiceDay).equals(day);

                if (matchKeyword && matchTrangThai && matchPhuongThuc && matchYear && matchMonth && matchDay) {
                    String khachHangName = "Khách lẻ";
                    if (hd.getKhachHangID() != null) {
                        KhachHang kh = khachHangDAO.findById(hd.getKhachHangID());
                        if (kh != null) {
                            khachHangName = kh.getHoTen();
                        }
                    }
                    model.addRow(new Object[]{
                        hd.getMaHoaDon(),
                        dateFormat.format(hd.getNgayLap()),
                        khachHangName,
                        String.format("%,d", hd.getTongTien().longValue()) + "đ",
                        String.format("%,d", hd.getGiamGia().longValue()) + "đ",
                        formatPhuongThuc(hd.getPhuongThucThanhToan()),
                        hd.getTrangThai()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc dữ liệu: " + e.getMessage());
        }
    }

    private void xuLyInHoaDon() {
        int selectedRow = tblLSHoaDon.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để in!");
            return;
        }

        try {
            // Lấy ID hóa đơn từ bảng
            String maHoaDon = tblLSHoaDon.getValueAt(selectedRow, 0).toString();
            int hoaDonID = Integer.parseInt(maHoaDon.substring(2));

            // Lấy thông tin đầy đủ của hóa đơn và chi tiết
            HoaDon hd = hoaDonDAO.selectById(hoaDonID);
            List<HoaDonChiTiet> danhSachChiTiet = hoaDonDAO.selectChiTietByHoaDon(hoaDonID);

            if (hd == null || danhSachChiTiet.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin chi tiết cho hóa đơn này!");
                return;
            }

            // Bắt đầu tạo nội dung hóa đơn
            int maxWidth = 50;
            StringBuilder receipt = new StringBuilder();
            receipt.append("========== HÓA ĐƠN ==========\n");
            receipt.append("Mã HĐ: ").append(hd.getMaHoaDon()).append("\n");
            receipt.append("Ngày: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hd.getNgayLap())).append("\n");
            if (hd.getChiNhanhID() != null) {
                 receipt.append("Chi nhánh: ").append(hd.getChiNhanhID()).append("\n");
            }
            receipt.append("=============================\n\n");
            receipt.append("Sản phẩm:\n");

            for (HoaDonChiTiet ct : danhSachChiTiet) {
                DoUong du = doUongDAO.selectById(ct.getDoUongID());
                if (du != null) {
                    String sizeName = "M"; // Default
                    if (ct.getSizeID() != null) {
                        Size size = sizeDAO.findById(ct.getSizeID());
                        if (size != null) {
                            sizeName = size.getTenSize();
                        }
                    }

                    BigDecimal donGia = ct.getDonGia();
                    BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(ct.getSoLuong()));

                    String tenSanPham = du.getTenDoUong() + " (" + sizeName + ")";
                    receipt.append(String.format("%-30s x%d\n", tenSanPham, ct.getSoLuong()));
                    receipt.append(String.format("  Đơn giá: %,dđ\n", donGia.toBigInteger()));
                    
                    if (ct.getDanhSachTopping() != null && !ct.getDanhSachTopping().isEmpty() && !ct.getDanhSachTopping().equals("Không có")) {
                        receipt.append("  Topping: ").append(formatToppingForDisplay(ct.getDanhSachTopping())).append("\n");
                    }
                    if (ct.getGhiChu() != null && !ct.getGhiChu().isEmpty()) {
                        receipt.append("  Ghi chú: ").append(ct.getGhiChu()).append("\n");
                    }
                     receipt.append(String.format("  Thành tiền: %,dđ\n\n", thanhTien.toBigInteger()));
                }
            }

            receipt.append("=============================\n");
            BigDecimal tamTinh = hd.getTongTien().add(hd.getGiamGia());
            receipt.append(String.format("Tạm tính: %,dđ\n", tamTinh.toBigInteger()));
            receipt.append(String.format("Giảm giá: %,dđ\n", hd.getGiamGia().toBigInteger()));
            receipt.append(String.format("Tổng tiền: %,dđ\n", hd.getTongTien().toBigInteger()));
            receipt.append("Phương thức: ").append(formatPhuongThuc(hd.getPhuongThucThanhToan())).append("\n");
            receipt.append("=============================\n");
            receipt.append("Cảm ơn quý khách!");

            // Hiển thị dialog xem trước và in
            javax.swing.JTextArea textArea = new javax.swing.JTextArea(receipt.toString());
            textArea.setEditable(false);
            textArea.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 12));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            
            javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
            scrollPane.setPreferredSize(new java.awt.Dimension(450, 450));

            Object[] options = {"In PDF", "Chỉ xem"};
            int choice = JOptionPane.showOptionDialog(this, scrollPane, "Xem trước hóa đơn",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);
            
            if (choice == 0) {
                xuLyInFilePDF(receipt.toString(), hd.getMaHoaDon());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi in hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void xuLyInFilePDF(String receiptContent, String maHoaDon) {
        try {
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogTitle("Lưu hóa đơn PDF");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));
            fileChooser.setSelectedFile(new java.io.File("HoaDon_" + maHoaDon + ".pdf"));

            int result = fileChooser.showSaveDialog(this);
            if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".pdf")) {
                    file = new java.io.File(file.getAbsolutePath() + ".pdf");
                }

                try {
                    com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                    com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
                    document.open();

                    com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(
                            com.itextpdf.text.FontFactory.getFont("Times-Roman", 14, com.itextpdf.text.Font.BOLD));
                    com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(
                            com.itextpdf.text.FontFactory.getFont("Courier", 10, com.itextpdf.text.Font.NORMAL));

                    com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("HÓA ĐƠN BÁN HÀNG", fontTitle);
                    title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    document.add(title);
                    document.add(new com.itextpdf.text.Paragraph(" "));

                    String[] lines = receiptContent.split("\n");
                    for (String line : lines) {
                        document.add(new com.itextpdf.text.Paragraph(line, fontNormal));
                    }

                    document.close();
                    JOptionPane.showMessageDialog(this, 
                            "✓ Lưu hóa đơn PDF thành công!\n\nFile: " + file.getAbsolutePath());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi tạo PDF: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
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
        jLabel2 = new javax.swing.JLabel();
        btnLamMoi = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        cboPhuongThucThanhToan = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cboDay = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        cboYear = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLSHoaDon = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblLSHoaDonChiTiet = new javax.swing.JTable();
        cboMonth = new javax.swing.JComboBox<>();
        btnTimKiem = new javax.swing.JButton();
        txtTimKiem = new javax.swing.JTextField();
        btnInHoaDon = new javax.swing.JButton();
        cboTrangThai = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(153, 255, 102));

        jLabel2.setText("Trạng Thái");

        btnLamMoi.setText("Làm Mới");

        jLabel3.setText("Phương Thức Thanh Toán");

        cboPhuongThucThanhToan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setText("Ngày");

        jLabel5.setText("Tháng");

        cboDay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setText("Năm");

        cboYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        tblLSHoaDon.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblLSHoaDon);

        tblLSHoaDonChiTiet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblLSHoaDonChiTiet);

        cboMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnTimKiem.setText("Tìm Kiếm");

        btnInHoaDon.setBackground(new java.awt.Color(255, 0, 51));
        btnInHoaDon.setForeground(new java.awt.Color(255, 153, 0));
        btnInHoaDon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/print bill.png"))); // NOI18N
        btnInHoaDon.setText("In Hóa Đơn");
        btnInHoaDon.setBorderPainted(false);
        btnInHoaDon.setContentAreaFilled(false);
        btnInHoaDon.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        cboTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText("Ngày Tháng");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboPhuongThucThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(btnLamMoi)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(406, Short.MAX_VALUE)
                .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnTimKiem)
                .addGap(0, 361, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnInHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(455, 455, 455))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTimKiem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(cboPhuongThucThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnLamMoi))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(cboDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(cboMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel4)
                        .addComponent(cboYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnInHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
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
            java.util.logging.Logger.getLogger(LichSuHoaDonForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LichSuHoaDonForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LichSuHoaDonForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LichSuHoaDonForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LichSuHoaDonForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInHoaDon;
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JComboBox<String> cboDay;
    private javax.swing.JComboBox<String> cboMonth;
    private javax.swing.JComboBox<String> cboPhuongThucThanhToan;
    private javax.swing.JComboBox<String> cboTrangThai;
    private javax.swing.JComboBox<String> cboYear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblLSHoaDon;
    private javax.swing.JTable tblLSHoaDonChiTiet;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables
}
