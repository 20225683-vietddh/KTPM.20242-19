package utils;

import org.apache.poi.xwpf.usermodel.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class DocxReportGenerator {
    /**
     * Create a new file .docx
     * 
     * @param filePath     
     * @param title        
     * @param headers      
     * @param rows         
     */
    public void generateReport(String filePath, String title, List<String> headers, List<List<String>> rows) throws IOException {
        XWPFDocument document = new XWPFDocument();
 
        // Header of the report
        XWPFParagraph titlePara = document.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText(title);
        titleRun.setBold(true);
        titleRun.setFontSize(20);

        // Created date
        XWPFParagraph datePara = document.createParagraph();
        datePara.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun dateRun = datePara.createRun();
        dateRun.setText("Ngày tạo: " + LocalDate.now());
        dateRun.setItalic(true);
        dateRun.setFontSize(12);

        // Table of content
        XWPFTable table = document.createTable(rows.size() + 1, headers.size());

        // Header of table
        for (int i = 0; i < headers.size(); i++) {
            table.getRow(0).getCell(i).setText(headers.get(i));
        }

        // Rows of table
        for (int i = 0; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            for (int j = 0; j < row.size(); j++) {
                table.getRow(i + 1).getCell(j).setText(row.get(j));
            }
        }

        // Write to file
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            document.write(out);
        }

        document.close();
    }
}

