package udpm.hn.server.infrastructure.job.report;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class BaseExport<T> {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<T> list;

    public BaseExport(List<T> list) {
        this.list = list;
        workbook = new XSSFWorkbook();
    }

    public BaseExport<T> writeHeaderLine(String[] headers) {
        sheet = workbook.createSheet("data export");
        Row row = sheet.createRow(0);
        CellStyle style = getHeaderStyle();

        for (int i = 0; i < headers.length; i++) {
            createCell(row, i, headers[i], style);
        }

        return this;
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }
        cell.setCellStyle(style);
    }

    private CellStyle getHeaderStyle() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(12);
        font.setFontName("Times New Roman");
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    public BaseExport<T> writeDataLines(String[] fields, Class<T> type) {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        font.setFontName("Times New Roman");
        style.setFont(font);

        for (T data : list) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            for (String fieldName : fields) {
                try {
                    Field field = type.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value = field.get(data);
                    createCell(row, columnCount++, value, style);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Auto-size all columns
        for (int i = 0; i < fields.length; i++) {
            sheet.setColumnWidth(i, 20 * 256);
        }

        return this;
    }

    public XSSFWorkbook getWorkbook() {
        return workbook;
    }

    public void export(HttpServletResponse response) throws Exception {

        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        workbook.close();
        out.close();

    }

    public void exprortBase64(HttpServletResponse response) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();
        
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        byteArrayOutputStream.close();
        String base64EncodedString = Base64.getEncoder().encodeToString(byteArray);
        ServletOutputStream out = response.getOutputStream();
        out.write(base64EncodedString.getBytes());
        out.close();
        
    }
}
