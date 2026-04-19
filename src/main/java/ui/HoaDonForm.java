/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

import dao.DoUongDAO;
import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.LoaiDoUongDAO;
import dao.SizeDAO;
import dao.ThuongHieuDAO;
import dao.ToppingDAO;
import dao.impl.DoUongDAOImpl;
import dao.impl.HoaDonDAOImpl;
import dao.impl.KhachHangDAOImpl;
import dao.impl.LoaiDoUongDAOImpl;
import dao.impl.SizeDAOImpl;
import dao.impl.ThuongHieuDAOImpl;
import dao.impl.ToppingDAOImpl;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.DoUong;
import model.HoaDon;
import model.HoaDonChiTiet;
import model.LoaiDoUong;
import model.PhieuGiamGia;
import model.Size;
import model.ThuongHieu;
import model.Topping;

/**
 *
 * @author Admin
 */
public class HoaDonForm extends javax.swing.JFrame {

    private DoUongDAO doUongDAO = new DoUongDAOImpl();
    private HoaDonDAO hoaDonDAO = new HoaDonDAOImpl();
    private LoaiDoUongDAO loaiDoUongDAO = new LoaiDoUongDAOImpl();
    private ThuongHieuDAO thuongHieuDAO = new ThuongHieuDAOImpl();
    private SizeDAO sizeDAO = new SizeDAOImpl();
    private ToppingDAO toppingDAO = new ToppingDAOImpl();
    private dao.PhieuGiamGiaDAO phieuGiamGiaDAO = new dao.PhieuGiamGiaDAO();
    private KhachHangDAO khachHangDAO = new KhachHangDAOImpl();
    private KhachHangForm khachHangForm;
    
    private List<HoaDonChiTiet> danhSachChiTiet = new ArrayList<>();
    private BigDecimal tamTinh = BigDecimal.ZERO;
    private BigDecimal giamGia = BigDecimal.ZERO;
    private int chiNhanhID = 1; // Default: Chi nhánh 1
    private boolean isUpdating = false;
        private PhieuGiamGia selectedPhieuGiamGia = null;
        private java.util.Map<String, model.KhachHang> khachHangMap = new java.util.HashMap<>();
    
        public void setKhachHangForm(KhachHangForm khachHangForm) {
            this.khachHangForm = khachHangForm;
        }
    
        /**
         * Creates new form HoaDonForm
         */
        public HoaDonForm() {
            initComponents();
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            loadData();
            setupEventHandlers();
            if (!utils.Auth.isManager()) {
                btnLichSuHoaDon.setVisible(false);
            }
        }
        
        private void loadData() {
            loadLoaiDoUong();
            loadThuongHieu();
            loadSanPham();
            // loadSize();
            loadTopping();
            loadKhachHang();
            loadPhuongThucThanhToan();
        }
        
        private void loadLoaiDoUong() {
            try {
                List<LoaiDoUong> list = loaiDoUongDAO.findAll();
                cboLoaiDoUongPOS.removeAllItems();
                cboLoaiDoUongPOS.addItem("-- Tất cả --");
                for (LoaiDoUong ld : list) {
                    cboLoaiDoUongPOS.addItem(ld.getTenLoai());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void loadThuongHieu() {
            try {
                List<ThuongHieu> list = thuongHieuDAO.findAll();
                cboThuongHieuPOS.removeAllItems();
                cboThuongHieuPOS.addItem("-- Tất cả --");
                for (ThuongHieu th : list) {
                    cboThuongHieuPOS.addItem(th.getTenThuongHieu());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void loadSanPham() {
            try {
                List<DoUong> list = doUongDAO.selectAll();
                DefaultTableModel model = (DefaultTableModel) tblSanPham.getModel();
                model.setRowCount(0);
                model.setColumnIdentifiers(new String[]{"ID", "Tên Đồ Uống", "Thương Hiệu", "Giá"});
                
                for (DoUong du : list) {
                    if (!du.isLaTopping()) {
                        String thuongHieu = "";
                        if (du.getThuongHieuID() != null) {
                            ThuongHieu th = thuongHieuDAO.findById(du.getThuongHieuID());
                            thuongHieu = th != null ? th.getTenThuongHieu() : "";
                        }
                        model.addRow(new Object[]{
                            du.getId(),
                            du.getTenDoUong(),
                            thuongHieu,
                            du.getGiaBanMacDinh().toString()
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // private void loadSize() {
        //     try {
        //         List<Size> list = sizeDAO.findAll();
        //         // cboSizePOS.removeAllItems();
        //         for (Size s : list) {
        //             // cboSizePOS.addItem(s.getTenSize());
        //         }
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //     }
        // }
        
        private void loadTopping() {
            try {
                List<DoUong> list = doUongDAO.selectAll();
                List<String> toppingNames = new ArrayList<>();
                for (DoUong du : list) {
                    if (du.isLaTopping()) {
                        toppingNames.add(du.getTenDoUong());
                    }
                }
    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void loadKhachHang() {
            cboKhachHang.removeAllItems();
            khachHangMap.clear();
            cboKhachHang.addItem("Khách lẻ");
            try {
                List<model.KhachHang> list = khachHangDAO.selectAll();
                for (model.KhachHang kh : list) {
                    cboKhachHang.addItem(kh.toString());
                    khachHangMap.put(kh.toString(), kh);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void loadPhuongThucThanhToan() {
            cboPhuongThucTT.removeAllItems();
            // Thêm các phương thức thanh toán
            cboPhuongThucTT.addItem("Tiền mặt");
            cboPhuongThucTT.addItem("Chuyển khoản");
            cboPhuongThucTT.addItem("Thẻ");
            cboPhuongThucTT.addItem("Ví điện tử");
        }
        
        private void setupEventHandlers() {
            btnThanhToan.addActionListener(e -> xuLyThanhToan());
            btnHuyHoaDon.addActionListener(e -> xuLyHuyHoaDon());
            btnXoaItem.addActionListener(e -> xuLyXoaItem());
            btnXoaAll.addActionListener(e -> xuLyXoaAll());
            btnTangSL.addActionListener(e -> xuLyTangSL());
            btnGiamSL.addActionListener(e -> xuLyGiamSL());
            btnInHoaDon.addActionListener(e -> xuLyInHoaDon());
            
            // Table sản phẩm: double-click để thêm
            tblSanPham.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        xuLyThemVaoGioHang();
                    } else if (evt.getClickCount() == 1) {
                        xuLyHienThiThongTinSanPham();
                    }
                }
            });
            
            // Keyboard shortcut: Enter để thêm nhanh (1 cái)
            tblSanPham.getInputMap(javax.swing.JComponent.WHEN_FOCUSED)
                    .put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0), "addQuick");
            tblSanPham.getActionMap().put("addQuick", new javax.swing.AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    int row = tblSanPham.getSelectedRow();
                    if (row >= 0) {
                        try {
                            int doUongID = (int) tblSanPham.getValueAt(row, 0);
                            xuLyMuaNhanh(doUongID, 1);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(HoaDonForm.this, "Lỗi: " + ex.getMessage());
                        }
                    }
                }
            });
            
            // Right-click context menu cho sản phẩm
            tblSanPham.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    if (evt.isPopupTrigger()) {
                        xuLyContextMenuSanPham(evt.getX(), evt.getY());
                    }
                }
                
                @Override
                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    if (evt.isPopupTrigger()) {
                        xuLyContextMenuSanPham(evt.getX(), evt.getY());
                    }
                }
            });
            
            // Table giỏ hàng: single-click để hiển thị chi tiết
            tblGioHang.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = tblGioHang.getSelectedRow();
                    if (row >= 0) {
                        xuLyHienThiChiTiet(row);
                    }
                }
            });
            
            // ComboBox loại đồ uống: filter sản phẩm
            cboLoaiDoUongPOS.addActionListener(e -> xuLyLocSanPham());
            
            // ComboBox thương hiệu: filter sản phẩm
            cboThuongHieuPOS.addActionListener(e -> xuLyLocSanPham());
            
            // TextField tìm kiếm: tìm kiếm theo tên
            txtSearchPOS.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    xuLyTimKiem();
                }
            });
            
    
    
            // Listener for cart table model changes
            tblGioHang.getModel().addTableModelListener(e -> {
                if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                    if (isUpdating) return; // Prevent recursion
    
                    int row = e.getFirstRow();
                    int column = e.getColumn();
    
                    if (column == 3) { // "SL" column
                        isUpdating = true;
                        try {
                            int newQuantity = Integer.parseInt(tblGioHang.getValueAt(row, 3).toString());
                            if (newQuantity < 1) {
                                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn hoặc bằng 1.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                                // Revert change by refreshing
                                 capNhatGioHang();
                            } else {
                                danhSachChiTiet.get(row).setSoLuong(newQuantity);
                                capNhatGioHang();
                                // Restore selection
                                if (row < tblGioHang.getRowCount()) {
                                    tblGioHang.setRowSelectionInterval(row, row);
                                }
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Vui lòng nhập một số nguyên hợp lệ cho số lượng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            // Revert change by refreshing
                            capNhatGioHang();
                        } finally {
                            isUpdating = false;
                        }
                    }
                }
            });
        }
        
        private void xuLyHienThiThongTinSanPham() {
            int row = tblSanPham.getSelectedRow();
            if (row >= 0) {
                int doUongID = (int) tblSanPham.getValueAt(row, 0);
                DoUong du = doUongDAO.selectById(doUongID);
                if (du != null) {
                    String message = "Tên: " + du.getTenDoUong() + "\n"
                            + "Giá: " + du.getGiaBanMacDinh() + "đ\n"
                            + "Mô tả: " + (du.getMoTa() != null ? du.getMoTa() : "Không có");
                    JOptionPane.showMessageDialog(this, message, "Thông tin sản phẩm", 
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        
    
        
        private void tinhGiamGia() {
            giamGia = BigDecimal.ZERO; // Reset discount
            if (selectedPhieuGiamGia == null || tamTinh.compareTo(BigDecimal.ZERO) == 0) {
                return;
            }
    
            // Check if the current total meets the minimum condition for the discount
            if (tamTinh.compareTo(selectedPhieuGiamGia.getDieuKienToiThieu()) < 0) {
                // No need to show a message here, as this method might be called frequently.
                // The message can be shown when the user tries to finalize payment if a voucher is selected but conditions aren't met.
                return;
            }
    
            // Apply discount based on type
            if ("PhanTram".equals(selectedPhieuGiamGia.getLoaiGiam())) {
                // Calculate percentage discount
                giamGia = tamTinh.multiply(selectedPhieuGiamGia.getGiaTriGiam())
                                 .divide(BigDecimal.valueOf(100), 0, java.math.RoundingMode.HALF_UP);
            } else if ("SoTien".equals(selectedPhieuGiamGia.getLoaiGiam())) {
                // Apply fixed amount discount
                giamGia = selectedPhieuGiamGia.getGiaTriGiam();
            }
            
            // Ensure discount doesn't exceed the subtotal
            if (giamGia.compareTo(tamTinh) > 0) {
                giamGia = tamTinh;
            }
        }
        
        /**
         * Context menu khi right-click sản phẩm
         */
        private void xuLyContextMenuSanPham(int x, int y) {
            int row = tblSanPham.rowAtPoint(new java.awt.Point(x, y));
            if (row < 0) return;
            
            tblSanPham.setRowSelectionInterval(row, row);
            
            try {
                int doUongID = (int) tblSanPham.getValueAt(row, 0);
                DoUong du = doUongDAO.selectById(doUongID);
                if (du == null) return;
                
                javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu();
                
                // Menu item: Thêm vào giỏ (dialog tùy chỉnh)
                javax.swing.JMenuItem miThemVao = new javax.swing.JMenuItem("Thêm vào giỏ");
                miThemVao.addActionListener(e -> xuLyThemVaoGioHang());
                menu.add(miThemVao);
                
                // Menu item: Mua nhanh (1 cái)
                javax.swing.JMenuItem miMuaNhanh = new javax.swing.JMenuItem("Mua nhanh (1x)");
                miMuaNhanh.addActionListener(e -> xuLyMuaNhanh(doUongID, 1));
                menu.add(miMuaNhanh);
                
                // Menu item: Mua 2 cái
                javax.swing.JMenuItem miMua2 = new javax.swing.JMenuItem("Mua 2x");
                miMua2.addActionListener(e -> xuLyMuaNhanh(doUongID, 2));
                menu.add(miMua2);
                
                // Menu item: Mua 5 cái
                javax.swing.JMenuItem miMua5 = new javax.swing.JMenuItem("Mua 5x");
                miMua5.addActionListener(e -> xuLyMuaNhanh(doUongID, 5));
                menu.add(miMua5);
                
                menu.addSeparator();
                
                // Menu item: Xem chi tiết
                javax.swing.JMenuItem miChiTiet = new javax.swing.JMenuItem("Xem chi tiết");
                miChiTiet.addActionListener(e -> xuLyHienThiThongTinSanPham());
                menu.add(miChiTiet);
                
                menu.show(tblSanPham, x, y);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        /**
         * Thêm sản phẩm nhanh (mua nhanh) - chỉ với số lượng mặc định
         */
        private void xuLyMuaNhanh(int doUongID, int soLuong) {
            try {
                DoUong du = doUongDAO.selectById(doUongID);
                if (du == null) {
                    JOptionPane.showMessageDialog(this, "Sản phẩm không tồn tại!");
                    return;
                }
                
                // Kiểm tra nếu sản phẩm này đã có trong giỏ
                boolean found = false;
                for (HoaDonChiTiet ct : danhSachChiTiet) {
                    if (ct.getDoUongID() == doUongID && 
                        (ct.getGhiChu() == null || ct.getGhiChu().isEmpty())) {
                        // Cộng gộp số lượng nếu cùng sản phẩm
                        ct.setSoLuong(ct.getSoLuong() + soLuong);
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    HoaDonChiTiet ct = new HoaDonChiTiet();
                    ct.setDoUongID(doUongID);
                    ct.setSoLuong(soLuong);
                    ct.setGhiChu(null);
                    ct.setDanhSachTopping(null);
                    danhSachChiTiet.add(ct);
                }
                
                capNhatGioHang();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        private void xuLyHienThiChiTiet(int row) {
            if (row >= 0 && row < danhSachChiTiet.size()) {
                HoaDonChiTiet ct = danhSachChiTiet.get(row);
                DoUong du = doUongDAO.selectById(ct.getDoUongID());
                if (du != null) {
                    // Lấy size name
                    String sizeName = "M";
                    if (ct.getSizeID() != null) {
                        try {
                            Size size = sizeDAO.findById(ct.getSizeID());
                            if (size != null) {
                                sizeName = size.getTenSize();
                            }
                        } catch (Exception e) {
                            // Nếu lỗi, dùng giá trị mặc định
                        }
                    }
                    
                    // Lấy giá từ chi tiết hoặc từ sản phẩm
                    BigDecimal donGia = ct.getDonGia() != null ? ct.getDonGia() : du.getGiaBanMacDinh();
                    
                    String message = "Sản phẩm: " + du.getTenDoUong() + "\n"
                            + "Size: " + sizeName + "\n"
                            + "Số lượng: " + ct.getSoLuong() + "\n"
                            + "Đơn giá: " + donGia.setScale(0, java.math.RoundingMode.HALF_UP) + "đ\n";
                    
                    // Thêm topping nếu có - hiển thị sạch sẽ
                    if (ct.getDanhSachTopping() != null && !ct.getDanhSachTopping().isEmpty()) {
                        String toppingDisplay = formatToppingForDisplay(ct.getDanhSachTopping());
                        message += "Topping: " + toppingDisplay + "\n";
                    }
                    
                    // Thêm ghi chú nếu có
                    if (ct.getGhiChu() != null && !ct.getGhiChu().isEmpty()) {
                        message += "Ghi chú: " + ct.getGhiChu();
                    } else {
                        message += "Ghi chú: Không có";
                    }
                    
                    JOptionPane.showMessageDialog(this, message, "Chi tiết sản phẩm", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        
        private void xuLyLocSanPham() {
            try {
                String loaiDoUong = cboLoaiDoUongPOS.getSelectedItem().toString();
                String thuongHieu = cboThuongHieuPOS.getSelectedItem().toString();
                
                List<DoUong> list = doUongDAO.selectAll();
                DefaultTableModel model = (DefaultTableModel) tblSanPham.getModel();
                model.setRowCount(0);
                
                for (DoUong du : list) {
                    if (!du.isLaTopping()) {
                        // Filter theo loại
                        if (!loaiDoUong.equals("-- Tất cả --")) {
                            if (du.getLoaiDoUongID() == null) continue;
                            LoaiDoUong ld = loaiDoUongDAO.findById(du.getLoaiDoUongID());
                            if (ld == null || !ld.getTenLoai().equals(loaiDoUong)) continue;
                        }
                        
                        // Filter theo thương hiệu
                        if (!thuongHieu.equals("-- Tất cả --")) {
                            if (du.getThuongHieuID() == null) continue;
                            ThuongHieu th = thuongHieuDAO.findById(du.getThuongHieuID());
                            if (th == null || !th.getTenThuongHieu().equals(thuongHieu)) continue;
                        }
                        
                        String thuongHieuText = "";
                        if (du.getThuongHieuID() != null) {
                            ThuongHieu th = thuongHieuDAO.findById(du.getThuongHieuID());
                            thuongHieuText = th != null ? th.getTenThuongHieu() : "";
                        }
                        
                        model.addRow(new Object[]{
                            du.getId(),
                            du.getTenDoUong(),
                            thuongHieuText,
                            du.getGiaBanMacDinh().toString()
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void xuLyTimKiem() {
            try {
                String keyword = txtSearchPOS.getText().trim();
                String loaiDoUong = cboLoaiDoUongPOS.getSelectedItem().toString();
                String thuongHieu = cboThuongHieuPOS.getSelectedItem().toString();
                
                List<DoUong> list;
                if (keyword.isEmpty()) {
                    list = doUongDAO.selectAll();
                } else {
                    list = doUongDAO.selectByKeyword(keyword);
                }
                
                DefaultTableModel model = (DefaultTableModel) tblSanPham.getModel();
                model.setRowCount(0);
                
                for (DoUong du : list) {
                    if (!du.isLaTopping()) {
                        // Filter theo loại
                        if (!loaiDoUong.equals("-- Tất cả --")) {
                            if (du.getLoaiDoUongID() == null) continue;
                            LoaiDoUong ld = loaiDoUongDAO.findById(du.getLoaiDoUongID());
                            if (ld == null || !ld.getTenLoai().equals(loaiDoUong)) continue;
                        }
                        
                        // Filter theo thương hiệu
                        if (!thuongHieu.equals("-- Tất cả --")) {
                            if (du.getThuongHieuID() == null) continue;
                            ThuongHieu th = thuongHieuDAO.findById(du.getThuongHieuID());
                            if (th == null || !th.getTenThuongHieu().equals(thuongHieu)) continue;
                        }
                        
                        String thuongHieuText = "";
                        if (du.getThuongHieuID() != null) {
                            ThuongHieu th = thuongHieuDAO.findById(du.getThuongHieuID());
                            thuongHieuText = th != null ? th.getTenThuongHieu() : "";
                        }
                        
                        model.addRow(new Object[]{
                            du.getId(),
                            du.getTenDoUong(),
                            thuongHieuText,
                            du.getGiaBanMacDinh().toString()
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        /**
         * Convert topping names to JSON format for database storage
         * Format: [{"ToppingID": 1, "Ten": "Trân châu đen"}, ...]
         */
        private String convertToppingToJSON(List<String> toppingNames) {
            if (toppingNames == null || toppingNames.isEmpty()) {
                return null;
            }
            
            StringBuilder json = new StringBuilder("[");
            List<Topping> allToppings = toppingDAO.findAll();
            
            for (int i = 0; i < toppingNames.size(); i++) {
                String name = toppingNames.get(i);
                Topping topping = allToppings.stream()
                        .filter(t -> t.getTenTopping().equals(name))
                        .findFirst()
                        .orElse(null);
                
                if (topping != null) {
                    if (i > 0) json.append(", ");
                    json.append("{\"ToppingID\": ").append(topping.getId()).append(", ")
                        .append("\"Ten\": \"").append(name.replace("\"", "\\\"")).append("\"}");
                }
            }
            
            json.append("]");
            return json.toString();
        }
        
        /**
         * Extract topping names from JSON format for display
         * Input: [{"ToppingID": 1, "Ten": "Trân châu đen"}, ...]
         * Output: "Trân châu đen, Kem cheese"
         */
        private String formatToppingForDisplay(String toppingJSON) {
            if (toppingJSON == null || toppingJSON.isEmpty()) {
                return "Không có";
            }
            
            try {
                StringBuilder display = new StringBuilder();
                String[] items = toppingJSON.split("\\}, \\{");
                
                for (int i = 0; i < items.length; i++) {
                    // Extract "Ten" value from JSON
                    String item = items[i].replaceAll("[\\[\\]{}]", "");
                    String[] parts = item.split(",");
                    
                    for (String part : parts) {
                        if (part.contains("\"Ten\"")) {
                            // Extract the value between quotes
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
                // Fallback: return the original if parsing fails
                return toppingJSON;
            }
        }
        
        private void xuLyThemVaoGioHang() {
            int row = tblSanPham.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!");
                return;
            }
            
            try {
                int doUongID = (int) tblSanPham.getValueAt(row, 0);
                DoUong du = doUongDAO.selectById(doUongID);
                if (du == null) {
                    JOptionPane.showMessageDialog(this, "Sản phẩm không tồn tại!");
                    return;
                }
                
                // Mở JDialog tùy chỉnh
                ThemSanPhamJDialog dialog = new ThemSanPhamJDialog(this, true, du);
                dialog.setVisible(true);
                
                // Sau khi dialog đóng, lấy kết quả trả về
                HoaDonChiTiet newChiTiet = dialog.getHoaDonChiTiet();
                
                if (newChiTiet != null) {
                    // Kiểm tra nếu sản phẩm này đã có trong giỏ với cùng tùy chọn
                    boolean found = false;
                    for (HoaDonChiTiet ct : danhSachChiTiet) {
                        boolean sameID = ct.getDoUongID() == newChiTiet.getDoUongID();
                        boolean sameSize = (ct.getSizeID() == null && newChiTiet.getSizeID() == null) || (ct.getSizeID() != null && ct.getSizeID().equals(newChiTiet.getSizeID()));
                        boolean sameTopping = (ct.getDanhSachTopping() == null && newChiTiet.getDanhSachTopping() == null) || (ct.getDanhSachTopping() != null && ct.getDanhSachTopping().equals(newChiTiet.getDanhSachTopping()));
                        boolean sameGhiChu = (ct.getGhiChu() == null && newChiTiet.getGhiChu() == null) || (ct.getGhiChu() != null && ct.getGhiChu().equals(newChiTiet.getGhiChu()));
                        
                        if (sameID && sameSize && sameTopping && sameGhiChu) {
                            // Cộng gộp số lượng
                            ct.setSoLuong(ct.getSoLuong() + newChiTiet.getSoLuong());
                            found = true;
                            break;
                        }
                    }
                    
                    if (!found) {
                        danhSachChiTiet.add(newChiTiet);
                    }
                    
                    capNhatGioHang();
                    String sizeName = "M";
                    if (newChiTiet.getSizeID() != null) {
                        try {
                           Size size = sizeDAO.findById(newChiTiet.getSizeID());
                           if (size != null) sizeName = size.getTenSize();
                        } catch(Exception e) {}
                    }
                    JOptionPane.showMessageDialog(this, 
                            "✓ Đã thêm " + newChiTiet.getSoLuong() + " " + du.getTenDoUong() + " ("+sizeName+") vào giỏ hàng!");
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        private void capNhatGioHang() {
            DefaultTableModel model = new DefaultTableModel(new String[]{"STT", "Tên Đồ Uống", "Size", "SL", "Đơn Giá", "Thành Tiền"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 3; // Allow editing only for the "SL" column
                }
            };
            tblGioHang.setModel(model);
            
            tamTinh = BigDecimal.ZERO;
            int stt = 1;
            for (HoaDonChiTiet ct : danhSachChiTiet) {
                DoUong du = doUongDAO.selectById(ct.getDoUongID());
                if (du != null) {
                    // Lấy thông tin size
                    String sizeName = "M";
                    if (ct.getSizeID() != null) {
                        try {
                            Size size = sizeDAO.findById(ct.getSizeID());
                            if (size != null) {
                                sizeName = size.getTenSize();
                            }
                        } catch (Exception e) {
                            // Nếu lỗi, dùng giá trị mặc định
                        }
                    }
                    
                    // Nếu không có donGia trong chi tiết, lấy từ sản phẩm
                    BigDecimal donGia = ct.getDonGia() != null ? ct.getDonGia() : du.getGiaBanMacDinh();
                    BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(ct.getSoLuong()));
                    tamTinh = tamTinh.add(thanhTien);
                    
                    model.addRow(new Object[]{
                        stt++,
                        du.getTenDoUong(),
                        sizeName,
                        ct.getSoLuong(),
                        donGia.setScale(0, java.math.RoundingMode.HALF_UP),
                        thanhTien.setScale(0, java.math.RoundingMode.HALF_UP)
                    });
                }
            }
            
            lblTamTinh.setText(String.format("Tạm tính: %,d đ", tamTinh.toBigInteger()));
            tinhGiamGia(); // Recalculate discount whenever subtotal changes
            capNhatTongTien();
        }
        
        private void capNhatTongTien() {
            BigDecimal tongTien = tamTinh.subtract(giamGia);
            if (tongTien.compareTo(BigDecimal.ZERO) < 0) {
                tongTien = BigDecimal.ZERO;
            }
            lblGiamGia.setText(String.format("Giảm giá: %,d đ", giamGia.toBigInteger()));
            lblTongTien.setText(String.format("Tổng tiền: %,d đ", tongTien.toBigInteger()));
        }
        
        private void xuLyXoaItem() {
            int row = tblGioHang.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa!");
                return;
            }
            
            if (row >= 0 && row < danhSachChiTiet.size()) {
                HoaDonChiTiet removed = danhSachChiTiet.remove(row);
                capNhatGioHang();
                DoUong du = doUongDAO.selectById(removed.getDoUongID());
                if (du != null) {
                    JOptionPane.showMessageDialog(this, "Đã xóa: " + du.getTenDoUong());
                }
            }
        }
        
        private void xuLyXoaAll() {
            if (danhSachChiTiet.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Giỏ hàng đã trống!");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận xóa tất cả sản phẩm?", 
                    "Xóa giỏ hàng", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                danhSachChiTiet.clear();
                capNhatGioHang();
                JOptionPane.showMessageDialog(this, "Đã xóa tất cả sản phẩm!");
            }
        }
        
        private void xuLyTangSL() {
            int row = tblGioHang.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!");
                return;
            }
            
            if (row >= 0 && row < danhSachChiTiet.size()) {
                HoaDonChiTiet ct = danhSachChiTiet.get(row);
                ct.setSoLuong(ct.getSoLuong() + 1);
                capNhatGioHang();
            }
        }
        
        private void xuLyGiamSL() {
            int row = tblGioHang.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!");
                return;
            }
            
            if (row >= 0 && row < danhSachChiTiet.size()) {
                HoaDonChiTiet ct = danhSachChiTiet.get(row);
                if (ct.getSoLuong() > 1) {
                    ct.setSoLuong(ct.getSoLuong() - 1);
                    capNhatGioHang();
                } else {
                    JOptionPane.showMessageDialog(this, "Số lượng không thể nhỏ hơn 1!");
                }
            }
        }
        
        private void xuLyThanhToan() {
            if (danhSachChiTiet.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Giỏ hàng trống!");
                return;
            }
            
            // Kiểm tra phương thức thanh toán
            String phuongThucVN = cboPhuongThucTT.getSelectedItem().toString();
            if (phuongThucVN.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phương thức thanh toán!");
                return;
            }
            
            // Convert sang mã database
            String phuongThuc = "";
            switch (phuongThucVN) {
                case "Tiền mặt":
                    phuongThuc = "TienMat";
                    break;
                case "Chuyển khoản":
                    phuongThuc = "ChuyenKhoan";
                    break;
                case "Thẻ":
                    phuongThuc = "The";
                    break;
                case "Ví điện tử":
                    phuongThuc = "ViDienTu";
                    break;
                default:
                    phuongThuc = ""; // Fallback or handle error
                    break;
            }
            
            try {
                // Validate discount conditions one last time before payment
                if (selectedPhieuGiamGia != null) {
                    if (tamTinh.compareTo(selectedPhieuGiamGia.getDieuKienToiThieu()) < 0) {
                        JOptionPane.showMessageDialog(this,
                                "Hóa đơn chưa đạt điều kiện tối thiểu (" + selectedPhieuGiamGia.getDieuKienToiThieu() + "đ) để áp dụng mã " + selectedPhieuGiamGia.getMaGiamGia() + ".",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (selectedPhieuGiamGia.getSoLuongDaDung() >= selectedPhieuGiamGia.getSoLuongToiDa()) {
                         JOptionPane.showMessageDialog(this,
                                "Mã giảm giá " + selectedPhieuGiamGia.getMaGiamGia() + " đã hết lượt sử dụng.",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                // Recalculate total with final discount
                capNhatTongTien(); // Ensure lblGiamGia and lblTongTien are up-to-date
                BigDecimal tongTien = tamTinh.subtract(giamGia);
                if (tongTien.compareTo(BigDecimal.ZERO) < 0) tongTien = BigDecimal.ZERO;
                
                String message = String.format(
                        "Tạm tính: %,d đ\n" +
                        "Giảm giá: %,d đ\n" +
                        "Tổng tiền: %,d đ\n" +
                        "Phương thức: %s\n\n" +
                        "Xác nhận thanh toán?",
                        tamTinh.toBigInteger(),
                        giamGia.toBigInteger(),
                        tongTien.toBigInteger(),
                        phuongThucVN);
                
                int confirm = JOptionPane.showConfirmDialog(this, message, 
                        "Xác nhận thanh toán", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Lưu hóa đơn
                    HoaDon hd = new HoaDon();
                    hd.setNgayLap(new Timestamp(System.currentTimeMillis()));
                    hd.setTongTien(tongTien);
                    hd.setGiamGia(giamGia);
    
                    // Set KhachHangID
                    Object selectedItem = cboKhachHang.getSelectedItem();
                    if (selectedItem != null && !selectedItem.toString().equals("Khách lẻ")) {
                        model.KhachHang kh = khachHangMap.get(selectedItem.toString());
                        if (kh != null) {
                            hd.setKhachHangID(kh.getId());
                        }
                    } else {
                        hd.setKhachHangID(null);
                    }
    
                    hd.setNhanVienID(utils.Auth.user != null ? utils.Auth.user.getId() : null); // Get current user
                    hd.setChiNhanhID(chiNhanhID);
                    hd.setPhuongThucThanhToan(phuongThuc);  // Sử dụng mã database
                    hd.setTrangThai("DaThanhToan");
                    hd.setGhiChu("");
                    
                    // Set PhieuGiamGiaID if a voucher was used
                    if (selectedPhieuGiamGia != null) {
                        hd.setPhieuGiamGiaID(selectedPhieuGiamGia.getID());
                    }
                    
                    hoaDonDAO.insert(hd);
                    int hoaDonID = hoaDonDAO.getLastInsertedId();
                    
                    // Lưu chi tiết hóa đơn
                    for (HoaDonChiTiet ct : danhSachChiTiet) {
                        ct.setHoaDonID(hoaDonID);
                        // Nếu donGia chưa được set, lấy từ sản phẩm
                        if (ct.getDonGia() == null) {
                            DoUong du = doUongDAO.selectById(ct.getDoUongID());
                            ct.setDonGia(du.getGiaBanMacDinh());
                        }
                        hoaDonDAO.insertChiTiet(ct);
                    }
                    
                    // Increment SoLuongDaDung for the used voucher
                    if (selectedPhieuGiamGia != null) {
                        phieuGiamGiaDAO.incrementSoLuongDaDung(selectedPhieuGiamGia.getMaGiamGia());
                    }
                    
                    JOptionPane.showMessageDialog(this, "Thanh toán thành công!\nMã hóa đơn: " + hoaDonID);
                    
                    // Refresh KhachHangForm if it exists
                    if (khachHangForm != null) {
                        khachHangForm.refreshTable();
                    }
    
                    xuLyHuyHoaDon();
                    // Hiển thị hóa đơn để in
                    xuLyInHoaDon();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
                e.printStackTrace();
            }
        }    
    private void xuLyHuyHoaDon() {
        danhSachChiTiet.clear();
        tamTinh = BigDecimal.ZERO;
        giamGia = BigDecimal.ZERO;
        capNhatGioHang();
        JOptionPane.showMessageDialog(this, "Đã hủy hóa đơn!");
    }
    
    private void xuLyInHoaDon() {
        if (danhSachChiTiet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!");
            return;
        }
        
        try {
            // Tạo hóa đơn với width tối đa 50 ký tự để tránh tràn
            int maxWidth = 50;
            StringBuilder receipt = new StringBuilder();
            receipt.append("========== HÓA ĐƠN ==========\n");
            receipt.append("Ngày: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())).append("\n");
            receipt.append("Chi nhánh: ").append(chiNhanhID).append("\n");
            receipt.append("=============================\n\n");
            
            receipt.append("Sản phẩm:\n");
            for (HoaDonChiTiet ct : danhSachChiTiet) {
                DoUong du = doUongDAO.selectById(ct.getDoUongID());
                if (du != null) {
                    // Lấy size name
                    String sizeName = "M";
                    if (ct.getSizeID() != null) {
                        try {
                            Size size = sizeDAO.findById(ct.getSizeID());
                            if (size != null) {
                                sizeName = size.getTenSize();
                            }
                        } catch (Exception e) {
                            // Nếu lỗi, dùng giá trị mặc định
                        }
                    }
                    
                    // Lấy giá từ chi tiết (có thể đã được điều chỉnh theo size)
                    BigDecimal donGia = ct.getDonGia() != null ? ct.getDonGia() : du.getGiaBanMacDinh();
                    BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(ct.getSoLuong()));
                    
                    // Tên sản phẩm với size
                    String tenSanPham = du.getTenDoUong() + " (" + sizeName + ")";
                    String soLuong = "x" + ct.getSoLuong();
                    String gia = String.format("%,d", thanhTien.setScale(0, java.math.RoundingMode.HALF_UP).toBigInteger()) + "đ";
                    
                    // Format dòng sản phẩm với xuống dòng tự động
                    String line = tenSanPham + " " + soLuong + " = " + gia;
                    if (line.length() > maxWidth) {
                        // Xuống dòng nếu quá dài
                        String[] words = line.split(" ");
                        String currentLine = "";
                        for (String word : words) {
                            if ((currentLine + word).length() <= maxWidth) {
                                currentLine += word + " ";
                            } else {
                                if (!currentLine.isEmpty()) {
                                    receipt.append(currentLine.trim()).append("\n");
                                }
                                currentLine = word + " ";
                            }
                        }
                        if (!currentLine.isEmpty()) {
                            receipt.append(currentLine.trim()).append("\n");
                        }
                    } else {
                        receipt.append(line).append("\n");
                    }
                    
                    // Thêm topping nếu có - xuống dòng
                    if (ct.getDanhSachTopping() != null && !ct.getDanhSachTopping().isEmpty()) {
                        String toppingLine = "  → Topping: " + ct.getDanhSachTopping();
                        if (toppingLine.length() > maxWidth) {
                            // Xuống dòng nếu topping quá dài
                            String toppingText = ct.getDanhSachTopping();
                            String[] toppingItems = toppingText.split(", ");
                            String currentTopping = "  → Topping: ";
                            for (String item : toppingItems) {
                                if ((currentTopping + item).length() <= maxWidth) {
                                    currentTopping += item + ", ";
                                } else {
                                    if (currentTopping.endsWith(", ")) {
                                        receipt.append(currentTopping.substring(0, currentTopping.length() - 2)).append("\n");
                                    } else {
                                        receipt.append(currentTopping).append("\n");
                                    }
                                    currentTopping = "         " + item + ", ";
                                }
                            }
                            if (currentTopping.endsWith(", ")) {
                                receipt.append(currentTopping.substring(0, currentTopping.length() - 2)).append("\n");
                            } else {
                                receipt.append(currentTopping).append("\n");
                            }
                        } else {
                            receipt.append(toppingLine).append("\n");
                        }
                    }
                    
                    // Thêm ghi chú nếu có - xuống dòng
                    if (ct.getGhiChu() != null && !ct.getGhiChu().isEmpty()) {
                        String ghiChuLine = "  → Ghi chú: " + ct.getGhiChu();
                        if (ghiChuLine.length() > maxWidth) {
                            // Xuống dòng nếu ghi chú quá dài
                            String ghiChuText = ct.getGhiChu();
                            String currentGhiChu = "  → Ghi chú: ";
                            int charCount = currentGhiChu.length();
                            for (char c : ghiChuText.toCharArray()) {
                                if (charCount >= maxWidth) {
                                    receipt.append(currentGhiChu.trim()).append("\n");
                                    currentGhiChu = "         " + c;
                                    charCount = currentGhiChu.length();
                                } else {
                                    currentGhiChu += c;
                                    charCount++;
                                }
                            }
                            if (!currentGhiChu.isEmpty()) {
                                receipt.append(currentGhiChu.trim()).append("\n");
                            }
                        } else {
                            receipt.append(ghiChuLine).append("\n");
                        }
                    }
                    
                    receipt.append("\n");
                }
            }
            
            receipt.append("=============================\n");
            receipt.append("Tạm tính: ").append(String.format("%,d", tamTinh.toBigInteger())).append("đ\n");
            receipt.append("Giảm giá: ").append(String.format("%,d", giamGia.toBigInteger())).append("đ\n");
            BigDecimal tongTien = tamTinh.subtract(giamGia);
            if (tongTien.compareTo(BigDecimal.ZERO) < 0) tongTien = BigDecimal.ZERO;
            receipt.append("Tổng tiền: ").append(String.format("%,d", tongTien.toBigInteger())).append("đ\n");
            receipt.append("Phương thức: ").append(cboPhuongThucTT.getSelectedItem()).append("\n");
            receipt.append("=============================\n");
            receipt.append("Cảm ơn quý khách!");
            
            // Hiển thị trong dialog
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
                // In sang file PDF
                xuLyInFilePDF(receipt.toString());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * In hóa đơn sang file PDF
     */
    private void xuLyInFilePDF(String receiptContent) {
        try {
            // Chọn vị trí lưu file
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogTitle("Lưu hóa đơn PDF");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));
            
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            fileChooser.setSelectedFile(new java.io.File("HoaDon_" + timeStamp + ".pdf"));
            
            int result = fileChooser.showSaveDialog(this);
            if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                // Đảm bảo file có đuôi .pdf
                if (!file.getName().endsWith(".pdf")) {
                    file = new java.io.File(file.getAbsolutePath() + ".pdf");
                }
                
                try {
                    // Tạo PDF sử dụng iText
                    com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                    com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
                    document.open();
                    
                    // Font hỗ trợ tiếng Việt
                    com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(
                            com.itextpdf.text.FontFactory.getFont("Times-Roman", 14, com.itextpdf.text.Font.BOLD));
                    com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(
                            com.itextpdf.text.FontFactory.getFont("Courier", 10, com.itextpdf.text.Font.NORMAL));
                    
                    // Thêm tiêu đề
                    com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("HÓA ĐƠN BÁN HÀNG", fontTitle);
                    title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    document.add(title);
                    document.add(new com.itextpdf.text.Paragraph(" ")); // Dòng trắng
                    
                    // Thêm nội dung hóa đơn
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        pnlPayment = new javax.swing.JPanel();
        pnlCart = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblGioHang = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtSearchPOS = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSanPham = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        lblGiamGia = new javax.swing.JLabel();
        lblTongTien = new javax.swing.JLabel();
        btnLichSuHoaDon = new javax.swing.JButton();
        btnThanhToan = new javax.swing.JButton();
        txtPhieuGiamGia = new javax.swing.JTextField();
        btnInHoaDon = new javax.swing.JButton();
        btnApDungPhieu = new javax.swing.JButton();
        btnHuyHoaDon = new javax.swing.JButton();
        cboKhachHang = new javax.swing.JComboBox<>();
        cboPhuongThucTT = new javax.swing.JComboBox<>();
        lblTamTinh = new javax.swing.JLabel();
        btnGiamSL = new javax.swing.JButton();
        btnTangSL = new javax.swing.JButton();
        btnXoaAll = new javax.swing.JButton();
        btnXoaItem = new javax.swing.JButton();
        btnThemVaoGioHang = new javax.swing.JButton();
        cboLoaiDoUongPOS = new javax.swing.JComboBox<>();
        cboThuongHieuPOS = new javax.swing.JComboBox<>();
        txtSoDienThoaiKhachHang = new javax.swing.JTextField();
        btnXacNhanSDTKhachHang = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(225, 177, 112));
        jPanel1.setForeground(new java.awt.Color(164, 115, 115));
        jPanel1.setPreferredSize(new java.awt.Dimension(1300, 800));

        pnlPayment.setBackground(new java.awt.Color(225, 177, 112));
        pnlPayment.setForeground(new java.awt.Color(164, 115, 115));

        pnlCart.setBackground(new java.awt.Color(225, 177, 112));
        pnlCart.setForeground(new java.awt.Color(164, 115, 115));

        tblGioHang.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(tblGioHang);

        jLabel1.setForeground(new java.awt.Color(164, 115, 115));
        jLabel1.setText("Tìm Kiếm");

        jButton1.setForeground(new java.awt.Color(164, 115, 115));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/find.png"))); // NOI18N
        jButton1.setText("Tìm Kiếm");
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);

        javax.swing.GroupLayout pnlCartLayout = new javax.swing.GroupLayout(pnlCart);
        pnlCart.setLayout(pnlCartLayout);
        pnlCartLayout.setHorizontalGroup(
            pnlCartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCartLayout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 792, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(pnlCartLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtSearchPOS, javax.swing.GroupLayout.PREFERRED_SIZE, 532, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(jButton1)
                .addGap(15, 15, 15))
        );
        pnlCartLayout.setVerticalGroup(
            pnlCartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCartLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlCartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlCartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSearchPOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1))
                    .addGroup(pnlCartLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblSanPham.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblSanPham);

        javax.swing.GroupLayout pnlPaymentLayout = new javax.swing.GroupLayout(pnlPayment);
        pnlPayment.setLayout(pnlPaymentLayout);
        pnlPaymentLayout.setHorizontalGroup(
            pnlPaymentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPaymentLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlCart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 638, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlPaymentLayout.setVerticalGroup(
            pnlPaymentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPaymentLayout.createSequentialGroup()
                .addGroup(pnlPaymentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPaymentLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlCart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPaymentLayout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 511, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(225, 177, 112));
        jPanel3.setForeground(new java.awt.Color(164, 115, 115));

        lblGiamGia.setForeground(new java.awt.Color(164, 115, 115));
        lblGiamGia.setText("Giảm Giá");

        lblTongTien.setForeground(new java.awt.Color(164, 115, 115));
        lblTongTien.setText("Tổng Tiền");

        btnLichSuHoaDon.setForeground(new java.awt.Color(164, 115, 115));
        btnLichSuHoaDon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bill history.png"))); // NOI18N
        btnLichSuHoaDon.setText("Lịch Sử Hóa Đơn");
        btnLichSuHoaDon.setBorderPainted(false);
        btnLichSuHoaDon.setContentAreaFilled(false);
        btnLichSuHoaDon.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLichSuHoaDon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLichSuHoaDonActionPerformed(evt);
            }
        });

        btnThanhToan.setForeground(new java.awt.Color(164, 115, 115));
        btnThanhToan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pay.png"))); // NOI18N
        btnThanhToan.setText("Thanh Toán");
        btnThanhToan.setBorderPainted(false);
        btnThanhToan.setContentAreaFilled(false);
        btnThanhToan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThanhToanActionPerformed(evt);
            }
        });

        txtPhieuGiamGia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhieuGiamGiaActionPerformed(evt);
            }
        });

        btnInHoaDon.setForeground(new java.awt.Color(164, 115, 115));
        btnInHoaDon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/print bill.png"))); // NOI18N
        btnInHoaDon.setText("In Hóa Đơn");
        btnInHoaDon.setBorderPainted(false);
        btnInHoaDon.setContentAreaFilled(false);
        btnInHoaDon.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        btnApDungPhieu.setForeground(new java.awt.Color(164, 115, 115));
        btnApDungPhieu.setText("Áp Dụng Phiếu");
        btnApDungPhieu.setBorderPainted(false);
        btnApDungPhieu.setContentAreaFilled(false);
        btnApDungPhieu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApDungPhieuActionPerformed(evt);
            }
        });

        btnHuyHoaDon.setForeground(new java.awt.Color(164, 115, 115));
        btnHuyHoaDon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cancel bill.png"))); // NOI18N
        btnHuyHoaDon.setText("Hủy Hóa Đơn");
        btnHuyHoaDon.setBorderPainted(false);
        btnHuyHoaDon.setContentAreaFilled(false);
        btnHuyHoaDon.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        cboKhachHang.setForeground(new java.awt.Color(164, 115, 115));
        cboKhachHang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cboPhuongThucTT.setForeground(new java.awt.Color(164, 115, 115));
        cboPhuongThucTT.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblTamTinh.setForeground(new java.awt.Color(164, 115, 115));
        lblTamTinh.setText("Tạm Tính");

        btnGiamSL.setForeground(new java.awt.Color(164, 115, 115));
        btnGiamSL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/minus.png"))); // NOI18N
        btnGiamSL.setText("Giảm Số Lượng");
        btnGiamSL.setBorderPainted(false);
        btnGiamSL.setContentAreaFilled(false);
        btnGiamSL.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        btnTangSL.setForeground(new java.awt.Color(164, 115, 115));
        btnTangSL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/plus.png"))); // NOI18N
        btnTangSL.setText("Tăng Sô Lượng");
        btnTangSL.setBorderPainted(false);
        btnTangSL.setContentAreaFilled(false);
        btnTangSL.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        btnXoaAll.setForeground(new java.awt.Color(164, 115, 115));
        btnXoaAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/delete_all.png"))); // NOI18N
        btnXoaAll.setText("Xóa All");
        btnXoaAll.setBorderPainted(false);
        btnXoaAll.setContentAreaFilled(false);
        btnXoaAll.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        btnXoaItem.setForeground(new java.awt.Color(164, 115, 115));
        btnXoaItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/delete_one.png"))); // NOI18N
        btnXoaItem.setText("Xóa");
        btnXoaItem.setBorderPainted(false);
        btnXoaItem.setContentAreaFilled(false);
        btnXoaItem.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        btnThemVaoGioHang.setForeground(new java.awt.Color(164, 115, 115));
        btnThemVaoGioHang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/add to card.png"))); // NOI18N
        btnThemVaoGioHang.setText("<html><center>Thêm Sản Phẩm<br>Vào Giỏ Hàng</center></html>");
        btnThemVaoGioHang.setBorderPainted(false);
        btnThemVaoGioHang.setContentAreaFilled(false);
        btnThemVaoGioHang.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnThemVaoGioHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemVaoGioHangActionPerformed(evt);
            }
        });

        cboLoaiDoUongPOS.setForeground(new java.awt.Color(164, 115, 115));
        cboLoaiDoUongPOS.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cboThuongHieuPOS.setForeground(new java.awt.Color(164, 115, 115));
        cboThuongHieuPOS.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboThuongHieuPOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboThuongHieuPOSActionPerformed(evt);
            }
        });

        txtSoDienThoaiKhachHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSoDienThoaiKhachHangActionPerformed(evt);
            }
        });

        btnXacNhanSDTKhachHang.setForeground(new java.awt.Color(164, 115, 115));
        btnXacNhanSDTKhachHang.setText("Xác Nhận SDT");
        btnXacNhanSDTKhachHang.setBorderPainted(false);
        btnXacNhanSDTKhachHang.setContentAreaFilled(false);
        btnXacNhanSDTKhachHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXacNhanSDTKhachHangActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(164, 115, 115));
        jLabel2.setText("\nSĐT khác Hàng");

        jLabel3.setForeground(new java.awt.Color(164, 115, 115));
        jLabel3.setText("Mã giảm giá");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblTamTinh)
                        .addGap(170, 170, 170))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblGiamGia, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(lblTongTien, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(txtSoDienThoaiKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnXacNhanSDTKhachHang))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(cboPhuongThucTT, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cboLoaiDoUongPOS, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cboThuongHieuPOS, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33)
                                .addComponent(cboKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(86, 86, 86))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtPhieuGiamGia, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnApDungPhieu, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(206, 206, 206)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnXoaItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnXoaAll, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGiamSL, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnTangSL, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnThemVaoGioHang, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btnLichSuHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHuyHoaDon))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(btnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnInHoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(7, 7, 7)))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboKhachHang, cboLoaiDoUongPOS, cboThuongHieuPOS});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboPhuongThucTT, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboLoaiDoUongPOS, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboThuongHieuPOS, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblTamTinh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSoDienThoaiKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXacNhanSDTKhachHang)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(lblGiamGia)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPhieuGiamGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnApDungPhieu)
                    .addComponent(lblTongTien)
                    .addComponent(jLabel3))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnInHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnHuyHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLichSuHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnThemVaoGioHang, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnXoaItem, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnGiamSL))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btnTangSL, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btnXoaAll)))))
                .addContainerGap(68, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboKhachHang, cboLoaiDoUongPOS, cboPhuongThucTT, cboThuongHieuPOS});

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(pnlPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1500, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 708, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLichSuHoaDonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLichSuHoaDonActionPerformed
        // TODO add your handling code here:
        new LichSuHoaDonForm().setVisible(true);
    }//GEN-LAST:event_btnLichSuHoaDonActionPerformed

    private void btnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThanhToanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnThanhToanActionPerformed

    private void txtPhieuGiamGiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhieuGiamGiaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhieuGiamGiaActionPerformed

    private void btnApDungPhieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApDungPhieuActionPerformed
                String maGiamGia = txtPhieuGiamGia.getText().trim();
        
        
                if (maGiamGia.isEmpty()) {
                        selectedPhieuGiamGia = null;
                        tinhGiamGia();
                        capNhatTongTien();
                        JOptionPane.showMessageDialog(this, "Không có mã giảm giá nào được áp dụng.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
        
                try {
                        PhieuGiamGia pgg = phieuGiamGiaDAO.selectById(maGiamGia);
            
                        if (pgg == null) {
                                selectedPhieuGiamGia = null;
                                tinhGiamGia();
                                capNhatTongTien();
                                JOptionPane.showMessageDialog(this, "Mã giảm giá không tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
            
                        if (pgg.getNgayKetThuc().before(new Date())) {
                                selectedPhieuGiamGia = null;
                                tinhGiamGia();
                                capNhatTongTien();
                                JOptionPane.showMessageDialog(this, "Mã giảm giá đã hết hạn sử dụng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
            
                        if (pgg.getSoLuongDaDung() >= pgg.getSoLuongToiDa()) {
                                selectedPhieuGiamGia = null;
                                tinhGiamGia();
                                capNhatTongTien();
                                JOptionPane.showMessageDialog(this, "Mã giảm giá đã hết lượt sử dụng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
            
                        if (tamTinh.compareTo(pgg.getDieuKienToiThieu()) < 0) {
                                selectedPhieuGiamGia = null;
                                tinhGiamGia();
                                capNhatTongTien();
                                JOptionPane.showMessageDialog(this,
                                        "Hóa đơn chưa đạt điều kiện tối thiểu (" + pgg.getDieuKienToiThieu() + "đ) để áp dụng mã này.",
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
            
                        selectedPhieuGiamGia = pgg;
                        tinhGiamGia();
                        capNhatTongTien();
                        JOptionPane.showMessageDialog(this, "Áp dụng mã giảm giá thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
                    } catch (Exception ex) {
                        selectedPhieuGiamGia = null;
                        tinhGiamGia();
                        capNhatTongTien();
                        JOptionPane.showMessageDialog(this, "Lỗi khi áp dụng mã giảm giá: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
    }//GEN-LAST:event_btnApDungPhieuActionPerformed

    private void btnThemVaoGioHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemVaoGioHangActionPerformed
        // TODO add your handling code here:
        xuLyThemVaoGioHang();
    }//GEN-LAST:event_btnThemVaoGioHangActionPerformed

    private void txtSoDienThoaiKhachHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSoDienThoaiKhachHangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSoDienThoaiKhachHangActionPerformed

    private void btnXacNhanSDTKhachHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXacNhanSDTKhachHangActionPerformed
        // TODO add your handling code here:
          String sdt = txtSoDienThoaiKhachHang.getText().trim();
 
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại khách hàng.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Basic validation for phone number format
        if (!sdt.matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ. Vui lòng kiểm tra lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            model.KhachHang kh = khachHangDAO.findBySoDienThoai(sdt);

            if (kh != null) {
                // Customer found, select them in the ComboBox
                for (int i = 0; i < cboKhachHang.getItemCount(); i++) {
                    String item = cboKhachHang.getItemAt(i);
                    // The map key is the toString() representation of the KhachHang object
                    if (khachHangMap.containsKey(item) && khachHangMap.get(item).getId() == kh.getId()) {
                        cboKhachHang.setSelectedIndex(i);
                        break;
                    }
                }
                JOptionPane.showMessageDialog(this, "Đã tìm thấy khách hàng: " + kh.getHoTen(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Customer not found
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Không tìm thấy khách hàng với SĐT này. Bạn có muốn thêm khách hàng mới không?",
                        "Không tìm thấy khách hàng",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Open KhachHangForm to add a new customer
                    KhachHangForm khf = new KhachHangForm();
                    khf.setVisible(true);
                    // Optionally, pass the phone number to the new form
                    khf.setSoDienThoai(sdt);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi tìm kiếm khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnXacNhanSDTKhachHangActionPerformed

    private void cboThuongHieuPOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboThuongHieuPOSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboThuongHieuPOSActionPerformed

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
            java.util.logging.Logger.getLogger(HoaDonForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HoaDonForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HoaDonForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HoaDonForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HoaDonForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApDungPhieu;
    private javax.swing.JButton btnGiamSL;
    private javax.swing.JButton btnHuyHoaDon;
    private javax.swing.JButton btnInHoaDon;
    private javax.swing.JButton btnLichSuHoaDon;
    private javax.swing.JButton btnTangSL;
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JButton btnThemVaoGioHang;
    private javax.swing.JButton btnXacNhanSDTKhachHang;
    private javax.swing.JButton btnXoaAll;
    private javax.swing.JButton btnXoaItem;
    private javax.swing.JComboBox<String> cboKhachHang;
    private javax.swing.JComboBox<String> cboLoaiDoUongPOS;
    private javax.swing.JComboBox<String> cboPhuongThucTT;
    private javax.swing.JComboBox<String> cboThuongHieuPOS;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblGiamGia;
    private javax.swing.JLabel lblTamTinh;
    private javax.swing.JLabel lblTongTien;
    private javax.swing.JPanel pnlCart;
    private javax.swing.JPanel pnlPayment;
    private javax.swing.JTable tblGioHang;
    private javax.swing.JTable tblSanPham;
    private javax.swing.JTextField txtPhieuGiamGia;
    private javax.swing.JTextField txtSearchPOS;
    private javax.swing.JTextField txtSoDienThoaiKhachHang;
    // End of variables declaration//GEN-END:variables
}
