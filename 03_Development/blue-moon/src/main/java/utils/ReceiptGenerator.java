package utils;

import org.apache.poi.xwpf.usermodel.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public class ReceiptGenerator {
    public void generateReceipt(String filePath,
                                String residentName,
                                String campaignName,
                                Map<String, Integer> feeItems,
                                int totalAmount,
                                String accountantName) throws IOException {

        XWPFDocument doc = new XWPFDocument();

        // === HEADER ===
        addHeader(doc);

        // === BODY ===
        addTitle(doc);
        addResidentInfo(doc, residentName, campaignName);
        addFeeTable(doc, feeItems);
        addTotalAmount(doc, totalAmount);
        addStatus(doc, "Đã đóng đủ");

        // === FOOTER ===
        addFooter(doc, accountantName);

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            doc.write(out);
        }

        doc.close();
    }

    private void addHeader(XWPFDocument doc) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun run = p.createRun();
        run.setBold(true);
        run.setFontSize(14);
        run.setText("Ban quản lý chung cư Blue Moon");

        XWPFRun run2 = p.createRun();
        run2.addBreak();
        run2.setFontSize(12);
        run2.setText("Mã biên lai: " + UUID.randomUUID().toString().substring(0, 8));

        XWPFRun run3 = p.createRun();
        run3.addBreak();
        run3.setFontSize(12);
        run3.setText("Ngày lập: " + LocalDate.now());
    }

    private void addTitle(XWPFDocument doc) {
        XWPFParagraph title = doc.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = title.createRun();
        run.setBold(true);
        run.setFontSize(18);
        run.setText("BIÊN LAI THU PHÍ");
    }

    private void addResidentInfo(XWPFDocument doc, String residentName, String campaignName) {
        XWPFParagraph p1 = doc.createParagraph();
        XWPFRun r1 = p1.createRun();
        r1.setFontSize(12);
        r1.setText("Hộ dân: " + residentName);

        XWPFParagraph p2 = doc.createParagraph();
        XWPFRun r2 = p2.createRun();
        r2.setFontSize(12);
        r2.setText("Đợt thu: " + campaignName);
    }

    private void addFeeTable(XWPFDocument doc, Map<String, Integer> feeItems) {
        XWPFTable table = doc.createTable(feeItems.size() + 2, 3); // +1 cho header, +1 cho dòng tổng

        // Header row
        XWPFTableRow header = table.getRow(0);
        header.getCell(0).setText("STT");
        header.getCell(1).setText("Tên khoản thu (khoản bắt buộc)");
        header.getCell(2).setText("Số tiền (VNĐ)");

        int index = 1;
       
        for (Map.Entry<String, Integer> entry : feeItems.entrySet()) {
            XWPFTableRow row = table.getRow(index);
            row.getCell(0).setText(String.valueOf(index)); 
            row.getCell(1).setText(entry.getKey());        
            row.getCell(2).setText(utils.Utils.formatCurrency(entry.getValue()));
            index++;
        }
    }

    private void addTotalAmount(XWPFDocument doc, int total) {
        XWPFParagraph totalP = doc.createParagraph();
        totalP.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun run = totalP.createRun();
        run.setFontSize(12);
        run.setBold(true);
        run.setText("Tổng cộng: " + utils.Utils.formatCurrency(total));
    }

    private void addStatus(XWPFDocument doc, String status) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r = p.createRun();
        r.setFontSize(12);
        r.setText("Trạng thái: " + status);
    }

    private void addFooter(XWPFDocument doc, String accountantName) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(500); 

        XWPFRun r1 = p.createRun();
        r1.setFontSize(12);
        r1.setText("Người lập biểu (Kế toán): " + accountantName);

        XWPFRun r2 = p.createRun();
        r2.addBreak();
        r2.setText("Ban quản lý chung cư Blue Moon");
    }
}
