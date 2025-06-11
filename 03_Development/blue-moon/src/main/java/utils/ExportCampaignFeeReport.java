package utils;

import models.CampaignFee;
import models.Fee;
import services.TrackCampaignFeeService;
import dto.campaignfee.PaidFeeResponseDTO;
import org.apache.poi.xwpf.usermodel.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExportCampaignFeeReport {
    private final TrackCampaignFeeService service;
    
    public ExportCampaignFeeReport() {
        try {
            this.service = new TrackCampaignFeeService();
        } catch (SQLException e) {
            throw new RuntimeException("Không thể khởi tạo TrackCampaignFeeService", e);
        }
    }
    
    /**
     * Xuất báo cáo đợt thu phí ra file Word
     * 
     * @param campaignFee Đợt thu phí cần xuất báo cáo
     * @param accountantName Tên kế toán
     * @return String đường dẫn file đã tạo
     * @throws SQLException Lỗi database
     * @throws IOException Lỗi ghi file
     */
    public String exportReport(CampaignFee campaignFee, String accountantName) throws SQLException, IOException {
        // Tạo tên file tự động và lưu vào thư mục output
        String fileName = "bao_cao_thong_ke_dot_thu_phi_" + campaignFee.getId() + ".docx";
        String filePath = "output/" + fileName;
        File fileToSave = new File(filePath);
        
        // Tạo document Word
        XWPFDocument document = new XWPFDocument();
        
        // HEADER - Ban quản lý chung cư Blue Moon
        XWPFParagraph headerPara = document.createParagraph();
        headerPara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun headerRun = headerPara.createRun();
        headerRun.setText("BAN QUẢN LÝ CHUNG CƯ BLUE MOON");
        headerRun.setBold(true);
        headerRun.setFontSize(16);
        headerRun.setFontFamily("Times New Roman");
        
        // Dòng trống
        document.createParagraph();
        
        // TIÊU ĐỀ
        XWPFParagraph titlePara = document.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText("THỐNG KÊ ĐỢT THU PHÍ: " + campaignFee.getName().toUpperCase());
        titleRun.setBold(true);
        titleRun.setFontSize(18);
        titleRun.setFontFamily("Times New Roman");
        
        // Dòng trống
        document.createParagraph();
        
        // Kiểm tra nếu đợt thu phí chưa được assign
        if (!service.isCampaignFeeAssigned(campaignFee.getId())) {
            XWPFParagraph notiPara = document.createParagraph();
            notiPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun notiRun = notiPara.createRun();
            notiRun.setText("Đợt thu phí này chưa có khoản thu nào!");
            notiRun.setItalic(true);
            notiRun.setFontSize(14);
            notiRun.setFontFamily("Times New Roman");
        } else {
            // PHẦN KHOẢN THU BẮT BUỘC
            createCompulsoryFeesSection(document, campaignFee);
            
            // PHẦN KHOẢN THU TỰ NGUYỆN
            createOptionalFeesSection(document, campaignFee);
        }
        
        // FOOTER
        createFooter(document, accountantName);
        
        // Ghi file
        try (FileOutputStream out = new FileOutputStream(fileToSave)) {
            document.write(out);
        }
        
        document.close();
        return filePath;
    }
    
    private void createCompulsoryFeesSection(XWPFDocument document, CampaignFee campaignFee) throws SQLException {
        // Tiêu đề phần khoản thu bắt buộc
        XWPFParagraph compulsoryTitle = document.createParagraph();
        XWPFRun compulsoryTitleRun = compulsoryTitle.createRun();
        compulsoryTitleRun.setText("I. DANH SÁCH CÁC KHOẢN THU BẮT BUỘC");
        compulsoryTitleRun.setBold(true);
        compulsoryTitleRun.setFontSize(14);
        compulsoryTitleRun.setFontFamily("Times New Roman");
        
        // Tạo bảng
        List<Fee> compulsoryFees = getCompulsoryFees(campaignFee);
        
        if (compulsoryFees.isEmpty()) {
            XWPFParagraph notiPara = document.createParagraph();
            XWPFRun notiRun = notiPara.createRun();
            notiRun.setText("Không có khoản thu bắt buộc nào.");
            notiRun.setItalic(true);
            notiRun.setFontFamily("Times New Roman");
        } else {
            XWPFTable table = document.createTable(compulsoryFees.size() + 1, 4);
            table.setWidth("100%");
            
            // Header bảng
            XWPFTableRow headerRow = table.getRow(0);
            setCellText(headerRow.getCell(0), "Tên khoản thu", true);
            setCellText(headerRow.getCell(1), "Số tiền cần nộp", true);
            setCellText(headerRow.getCell(2), "Số tiền đã nộp", true);
            setCellText(headerRow.getCell(3), "Còn thiếu/Trạng thái", true);
            
            // Dữ liệu bảng
            for (int i = 0; i < compulsoryFees.size(); i++) {
                Fee fee = compulsoryFees.get(i);
                PaidFeeResponseDTO paidInfo = service.getExpectedAndPaidAmount(campaignFee.getId(), fee.getId());
                
                XWPFTableRow row = table.getRow(i + 1);
                setCellText(row.getCell(0), fee.getName(), false);
                setCellText(row.getCell(1), Utils.formatCurrency(paidInfo.getExpectedAmount()) + " đồng", false);
                setCellText(row.getCell(2), Utils.formatCurrency(paidInfo.getPaidAmount()) + " đồng", false);
                
                if (paidInfo.isEnough()) {
                    setCellText(row.getCell(3), "Đã thu đủ", false);
                } else {
                    setCellText(row.getCell(3), Utils.formatCurrency(paidInfo.remainingAmount()) + " đồng", false);
                }
            }
            
            // Tổng kết
            document.createParagraph();
            XWPFParagraph summaryPara = document.createParagraph();
            XWPFRun summaryRun = summaryPara.createRun();
            summaryRun.setText("Tổng số tiền cần thu: " + Utils.formatCurrency(service.getExpectedAmount(campaignFee.getId())) + " đồng");
            summaryRun.setBold(true);
            summaryRun.setFontFamily("Times New Roman");
            
            XWPFParagraph totalPaidPara = document.createParagraph();
            XWPFRun totalPaidRun = totalPaidPara.createRun();
            totalPaidRun.setText("Tổng số tiền đã thu (bắt buộc): " + Utils.formatCurrency(service.getTotalCompulsoryPaidAmount(campaignFee.getId())) + " đồng");
            totalPaidRun.setBold(true);
            totalPaidRun.setFontFamily("Times New Roman");
        }
        
        // Dòng trống
        document.createParagraph();
    }
    
    private void createOptionalFeesSection(XWPFDocument document, CampaignFee campaignFee) throws SQLException {
        // Tiêu đề phần khoản thu tự nguyện
        XWPFParagraph optionalTitle = document.createParagraph();
        XWPFRun optionalTitleRun = optionalTitle.createRun();
        optionalTitleRun.setText("II. DANH SÁCH CÁC KHOẢN THU TỰ NGUYỆN");
        optionalTitleRun.setBold(true);
        optionalTitleRun.setFontSize(14);
        optionalTitleRun.setFontFamily("Times New Roman");
        
        List<Fee> optionalFees = getOptionalFees(campaignFee);
        
        if (optionalFees.isEmpty()) {
            XWPFParagraph notiPara = document.createParagraph();
            XWPFRun notiRun = notiPara.createRun();
            notiRun.setText("Không có khoản thu tự nguyện nào.");
            notiRun.setItalic(true);
            notiRun.setFontFamily("Times New Roman");
        } else {
            XWPFTable table = document.createTable(optionalFees.size() + 1, 2);
            table.setWidth("100%");
            
            // Header bảng
            XWPFTableRow headerRow = table.getRow(0);
            setCellText(headerRow.getCell(0), "Tên khoản thu", true);
            setCellText(headerRow.getCell(1), "Số tiền đã thu", true);
            
            // Dữ liệu bảng
            for (int i = 0; i < optionalFees.size(); i++) {
                Fee fee = optionalFees.get(i);
                PaidFeeResponseDTO paidInfo = service.getExpectedAndPaidAmount(campaignFee.getId(), fee.getId());
                
                XWPFTableRow row = table.getRow(i + 1);
                setCellText(row.getCell(0), fee.getName(), false);
                setCellText(row.getCell(1), Utils.formatCurrency(paidInfo.getPaidAmount()) + " đồng", false);
            }
            
            // Tổng kết
            document.createParagraph();
            XWPFParagraph totalOptionalPara = document.createParagraph();
            XWPFRun totalOptionalRun = totalOptionalPara.createRun();
            totalOptionalRun.setText("Tổng số tiền đã thu (tự nguyện): " + Utils.formatCurrency(service.getTotalOptionalPaidAmount(campaignFee.getId())) + " đồng");
            totalOptionalRun.setBold(true);
            totalOptionalRun.setFontFamily("Times New Roman");
        }
        
        // Dòng trống
        document.createParagraph();
    }
    
    private void createFooter(XWPFDocument document, String accountantName) {
        // Ngày tháng
        XWPFParagraph datePara = document.createParagraph();
        datePara.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun dateRun = datePara.createRun();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Ngày' dd 'tháng' MM 'năm' yyyy");
        dateRun.setText(today.format(formatter));
        dateRun.setFontFamily("Times New Roman");
        dateRun.setFontSize(12);
        
        // Chữ ký
        XWPFParagraph signaturePara = document.createParagraph();
        signaturePara.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun signatureRun = signaturePara.createRun();
        signatureRun.setText("Kế toán");
        signatureRun.setBold(true);
        signatureRun.setFontFamily("Times New Roman");
        signatureRun.setFontSize(12);
        
        // Dòng trống cho chữ ký
        document.createParagraph();
        document.createParagraph();
        
        // Tên kế toán
        XWPFParagraph namePara = document.createParagraph();
        namePara.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun nameRun = namePara.createRun();
        nameRun.setText(accountantName);
        nameRun.setBold(true);
        nameRun.setFontFamily("Times New Roman");
        nameRun.setFontSize(12);
    }
    
    private void setCellText(XWPFTableCell cell, String text, boolean isBold) {
        XWPFParagraph para = cell.getParagraphs().get(0);
        para.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = para.createRun();
        run.setText(text);
        run.setBold(isBold);
        run.setFontFamily("Times New Roman");
        run.setFontSize(12);
    }
    
    private List<Fee> getCompulsoryFees(CampaignFee campaignFee) {
        List<Fee> fees = campaignFee.getFees();
        List<Fee> compulsoryFees = new ArrayList<>();
        for (Fee fee : fees) {
            if (fee.getIsMandatory()) {
                compulsoryFees.add(fee);
            }
        }
        return compulsoryFees;
    }
    
    private List<Fee> getOptionalFees(CampaignFee campaignFee) {
        List<Fee> fees = campaignFee.getFees();
        List<Fee> optionalFees = new ArrayList<>();
        for (Fee fee : fees) {
            if (!fee.getIsMandatory()) {
                optionalFees.add(fee);
            }
        }
        return optionalFees;
    }
} 