/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

import dao.ThongKeDAO;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.EmailSender;
import utils.MsgBox;
import utils.XDate;

/**
 *
 * @author Admin
 */
public class BaoCaoForm extends javax.swing.JFrame {

    ThongKeDAO tkDAO = new ThongKeDAO();
    DecimalFormat df = new DecimalFormat("###,###,###.##");
    private boolean isInitializing = true;

    /**
     * Creates new form BaoCaoForm
     */
    public BaoCaoForm() {
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        init();
    }

    void init() {
        if (!utils.Auth.isManager()) {
            utils.MsgBox.alert(this, "Bạn không có quyền truy cập chức năng này!");
            this.dispose();
            return;
        }
        fillComboBoxLoaiBaoCao();
        fillComboBoxNam();
        fillComboBoxThang();
        fillComboBoxNgay();
        
        LocalDate now = LocalDate.now();
        if (cboNam.getItemCount() > 0) {
            cboNam.setSelectedItem(now.getYear());
        }
        cboThang.setSelectedItem("Tất cả");
        cboNgay.setSelectedItem("Tất cả");
        
        isInitializing = false;
        updateReport();
    }

    void fillComboBoxLoaiBaoCao() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboLoaiBaoCao.getModel();
        model.removeAllElements();
        model.addElement("Doanh thu theo ngày");
        model.addElement("Món bán chạy");
    }

    void fillComboBoxNam() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboNam.getModel();
        model.removeAllElements();
        List<Integer> list = tkDAO.getNam();
        for (Integer year : list) {
            model.addElement(year);
        }
    }

    void fillComboBoxThang() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboThang.getModel();
        model.removeAllElements();
        model.addElement("Tất cả");
        for (int i = 1; i <= 12; i++) {
            model.addElement(i);
        }
    }

    void fillComboBoxNgay() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboNgay.getModel();
        model.removeAllElements();
        model.addElement("Tất cả");
        for (int i = 1; i <= 31; i++) {
            model.addElement(i);
        }
    }

    private DateRange getDateRangeFromComboBoxes() {
        Object selectedYearObj = cboNam.getSelectedItem();
        Object selectedMonthObj = cboThang.getSelectedItem();
        Object selectedDayObj = cboNgay.getSelectedItem();

        if (!(selectedYearObj instanceof Integer)) {
            if (!isInitializing) { // Chỉ hiển thị thông báo sau khi khởi tạo xong
                 MsgBox.alert(this, "Năm được chọn không hợp lệ.");
            }
            return null;
        }
        
        int year = (Integer) selectedYearObj;
        LocalDate startDate;
        LocalDate endDate;

        if (selectedMonthObj.equals("Tất cả")) {
            startDate = LocalDate.of(year, 1, 1);
            endDate = LocalDate.of(year, 12, 31);
        } else if (selectedMonthObj instanceof Integer) {
            int month = (Integer) selectedMonthObj;
            if (selectedDayObj.equals("Tất cả")) {
                YearMonth yearMonth = YearMonth.of(year, month);
                startDate = yearMonth.atDay(1);
                endDate = yearMonth.atEndOfMonth();
            } else if (selectedDayObj instanceof Integer){
                int day = (Integer) selectedDayObj;
                try {
                    startDate = LocalDate.of(year, month, day);
                    endDate = startDate;
                } catch (Exception e) {
                     if (!isInitializing) MsgBox.alert(this, "Ngày không hợp lệ!");
                    return null;
                }
            } else {
                 if (!isInitializing) MsgBox.alert(this, "Ngày được chọn không hợp lệ.");
                return null;
            }
        } else {
             if (!isInitializing) MsgBox.alert(this, "Tháng được chọn không hợp lệ.");
            return null;
        }
        
        return new DateRange(Date.from(startDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()),
                               Date.from(endDate.atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant()));
    }

    void fillTableDoanhThu() {
        DefaultTableModel model = (DefaultTableModel) tblDuLieu.getModel();
        model.setColumnIdentifiers(new String[]{"Ngày", "Số Hóa Đơn", "Tổng Tiền"});
        model.setRowCount(0);

        DateRange range = getDateRangeFromComboBoxes();
        if (range == null) return;
        
        List<Object[]> list = tkDAO.getDoanhThuTheoNgay(range.getStartDate(), range.getEndDate());

        double tongDoanhThu = 0;
        int tongDon = 0;

        for (Object[] row : list) {
            Date ngay = (Date) row[0];
            String ngayFormatted = XDate.toString(ngay, "dd-MM-yyyy");
            BigDecimal doanhThu = (BigDecimal) row[2];
            
            model.addRow(new Object[]{
                ngayFormatted,
                row[1],
                df.format(doanhThu)
            });
            tongDoanhThu += doanhThu.doubleValue();
            tongDon += (int) row[1];
        }

        lblTongDoanhThu.setText("Tổng thu: " + df.format(tongDoanhThu) + " VNĐ");
        lblTongDon.setText("Tổng đơn: " + tongDon);
    }

    void fillTableMonBanChay() {
        DefaultTableModel model = (DefaultTableModel) tblDuLieu.getModel();
        model.setColumnIdentifiers(new String[]{"Tên Món", "Size", "Số Lượng Bán", "Thành Tiền"});
        model.setRowCount(0);

        DateRange range = getDateRangeFromComboBoxes();
        if (range == null) return;
            
        List<Object[]> list = tkDAO.getMonBanChay(range.getStartDate(), range.getEndDate());
        
        double tongThanhTien = 0;
        int tongSoLuong = 0;

        for (Object[] row : list) {
             BigDecimal thanhTien = (BigDecimal) row[3];
            model.addRow(new Object[]{
                row[0],
                row[1],
                row[2],
                df.format(thanhTien)
            });
            tongThanhTien += thanhTien.doubleValue();
            tongSoLuong += (int) row[2];
        }
        
        lblTongDoanhThu.setText("Tổng thu: " + df.format(tongThanhTien) + " VNĐ");
        lblTongDon.setText("Tổng số lượng: " + tongSoLuong);
    }

    private void updateReport() {
        if (isInitializing) return;
        int selectedIndex = cboLoaiBaoCao.getSelectedIndex();
        if (selectedIndex == 0) {
            fillTableDoanhThu();
        } else if (selectedIndex == 1) {
            fillTableMonBanChay();
        }
    }

    private class DateRange {
        private final Date startDate;
        private final Date endDate;

        public DateRange(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
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
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cboNam = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        btnXuatExcel = new javax.swing.JButton();
        lblTongDoanhThu = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblTongDon = new javax.swing.JLabel();
        btnLoc = new javax.swing.JButton();
        btnGuiEmail = new javax.swing.JButton();
        cboNgay = new javax.swing.JComboBox<>();
        cboLoaiBaoCao = new javax.swing.JComboBox<>();
        cboThang = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDuLieu = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(225, 177, 112));
        jPanel1.setForeground(new java.awt.Color(164, 115, 115));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(164, 115, 115));
        jLabel3.setText("Năm");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(164, 115, 115));
        jLabel1.setText("Ngày");

        cboNam.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboNam.setForeground(new java.awt.Color(164, 115, 115));
        cboNam.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(164, 115, 115));
        jLabel2.setText("Tháng");

        btnXuatExcel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnXuatExcel.setForeground(new java.awt.Color(164, 115, 115));
        btnXuatExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/send report.png"))); // NOI18N
        btnXuatExcel.setText("Xuất Excel");
        btnXuatExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuatExcelActionPerformed(evt);
            }
        });

        lblTongDoanhThu.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTongDoanhThu.setForeground(new java.awt.Color(164, 115, 115));
        lblTongDoanhThu.setText("jLabel3");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(164, 115, 115));
        jLabel4.setText("Loại Báo Cáo");

        lblTongDon.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTongDon.setForeground(new java.awt.Color(164, 115, 115));
        lblTongDon.setText("jLabel3");

        btnLoc.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLoc.setForeground(new java.awt.Color(164, 115, 115));
        btnLoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/filter.png"))); // NOI18N
        btnLoc.setText("Lọc");
        btnLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocActionPerformed(evt);
            }
        });

        btnGuiEmail.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnGuiEmail.setForeground(new java.awt.Color(164, 115, 115));
        btnGuiEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/send mail.png"))); // NOI18N
        btnGuiEmail.setText("Gửi Email");
        btnGuiEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuiEmailActionPerformed(evt);
            }
        });

        cboNgay.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboNgay.setForeground(new java.awt.Color(164, 115, 115));
        cboNgay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cboLoaiBaoCao.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboLoaiBaoCao.setForeground(new java.awt.Color(164, 115, 115));
        cboLoaiBaoCao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboLoaiBaoCao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLoaiBaoCaoActionPerformed(evt);
            }
        });

        cboThang.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cboThang.setForeground(new java.awt.Color(164, 115, 115));
        cboThang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        tblDuLieu.setModel(new javax.swing.table.DefaultTableModel(
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
        tblDuLieu.setPreferredSize(new java.awt.Dimension(1500, 800));
        tblDuLieu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDuLieuMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblDuLieu);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnXuatExcel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnGuiEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(130, 130, 130)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboNgay, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(jLabel2)
                                .addGap(32, 32, 32)
                                .addComponent(cboThang, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel3)
                                .addGap(32, 32, 32)
                                .addComponent(cboNam, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(58, 58, 58)
                                .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblTongDon, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                    .addComponent(lblTongDoanhThu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(217, 217, 217)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboLoaiBaoCao, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(86, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTongDoanhThu)
                    .addComponent(cboLoaiBaoCao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(23, 23, 23)
                .addComponent(lblTongDon)
                .addGap(61, 61, 61)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(cboNgay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(cboThang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(cboNam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnLoc)))
                .addGap(56, 56, 56)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(btnXuatExcel)
                        .addGap(36, 36, 36)
                        .addComponent(btnGuiEmail)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void btnXuatExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuatExcelActionPerformed
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("BaoCao");

            // Ghi tên cột
            Row headerRow = sheet.createRow(0);
            DefaultTableModel model = (DefaultTableModel) tblDuLieu.getModel();
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
            }

            // Ghi dữ liệu
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = model.getValueAt(i, j);
                    cell.setCellValue(value != null ? value.toString() : "");
                }
            }

            // Ghi file
            String tenBaoCao = cboLoaiBaoCao.getSelectedItem().toString().replaceAll(" ", "_");
            String ngayXuat = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String filePath = "BaoCao_" + tenBaoCao + "_" + ngayXuat + ".xlsx";

            try (FileOutputStream fileOut = new FileOutputStream(new File(filePath))) {
                workbook.write(fileOut);
            }

            MsgBox.alert(this, "Xuất file Excel thành công! \n" + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            MsgBox.alert(this, "Có lỗi xảy ra khi xuất file Excel!");
        }
    }//GEN-LAST:event_btnXuatExcelActionPerformed

    private void btnLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocActionPerformed
        updateReport();
    }//GEN-LAST:event_btnLocActionPerformed

    private void btnGuiEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuiEmailActionPerformed
        try {
            String email = MsgBox.prompt(this, "Nhập email người nhận:");
            if (email != null && !email.trim().isEmpty()) {
                // Tạo file Excel
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("BaoCao");
                DefaultTableModel model = (DefaultTableModel) tblDuLieu.getModel();

                // Ghi tên cột
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < model.getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(model.getColumnName(i));
                }

                // Ghi dữ liệu
                for (int i = 0; i < model.getRowCount(); i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Cell cell = row.createCell(j);
                        Object value = model.getValueAt(i, j);
                        cell.setCellValue(value != null ? value.toString() : "");
                    }
                }

                String tenBaoCao = cboLoaiBaoCao.getSelectedItem().toString().replaceAll(" ", "_");
                String ngayXuat = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                String filePath = "BaoCao_" + tenBaoCao + "_" + ngayXuat + ".xlsx";

                try (FileOutputStream fileOut = new FileOutputStream(new File(filePath))) {
                    workbook.write(fileOut);
                }

                // Gửi email
                // Lấy thông tin thời gian báo cáo
                String thoiGianBaoCao;
                Object selectedYearObj = cboNam.getSelectedItem();
                Object selectedMonthObj = cboThang.getSelectedItem();
                Object selectedDayObj = cboNgay.getSelectedItem();

                if (selectedYearObj instanceof Integer) {
                    int year = (Integer) selectedYearObj;
                    if (selectedMonthObj.equals("Tất cả")) {
                        thoiGianBaoCao = "cả năm " + year;
                    } else if (selectedMonthObj instanceof Integer) {
                        int month = (Integer) selectedMonthObj;
                        if (selectedDayObj.equals("Tất cả")) {
                            thoiGianBaoCao = "tháng " + month + "/" + year;
                        } else if (selectedDayObj instanceof Integer) {
                            int day = (Integer) selectedDayObj;
                            thoiGianBaoCao = "ngày " + String.format("%02d-%02d-%d", day, month, year);
                        } else {
                            thoiGianBaoCao = "không xác định";
                        }
                    } else {
                        thoiGianBaoCao = "không xác định";
                    }
                } else {
                    thoiGianBaoCao = "không xác định";
                }

                String tenLoaiBaoCao = cboLoaiBaoCao.getSelectedItem().toString();
                String subject = "Báo cáo " + tenLoaiBaoCao + " cho " + thoiGianBaoCao;
                String body = "Chào bạn,\n\nChúng tôi gửi bạn file " + tenLoaiBaoCao.toLowerCase() + " cho " + thoiGianBaoCao + " trong file đính kèm.\n\nTrân trọng,";

                EmailSender.sendEmail(email, subject, body, filePath);

                MsgBox.alert(this, "Gửi email thành công!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            MsgBox.alert(this, "Có lỗi xảy ra khi gửi email!");
        }
    }//GEN-LAST:event_btnGuiEmailActionPerformed

    private void cboLoaiBaoCaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLoaiBaoCaoActionPerformed
        updateReport();
    }//GEN-LAST:event_cboLoaiBaoCaoActionPerformed

    private void tblDuLieuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDuLieuMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblDuLieuMouseClicked

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
            java.util.logging.Logger.getLogger(BaoCaoForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BaoCaoForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BaoCaoForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BaoCaoForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BaoCaoForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuiEmail;
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnXuatExcel;
    private javax.swing.JComboBox<String> cboLoaiBaoCao;
    private javax.swing.JComboBox<String> cboNam;
    private javax.swing.JComboBox<String> cboNgay;
    private javax.swing.JComboBox<String> cboThang;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTongDoanhThu;
    private javax.swing.JLabel lblTongDon;
    private javax.swing.JTable tblDuLieu;
    // End of variables declaration//GEN-END:variables
}
