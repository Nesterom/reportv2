import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.core.office.OfficeUtils;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;

import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Report {
    static String separator = File.separator;
    static String path = "C:" + separator + "Users" + separator + "roman.nesterenko" + separator + "OneDrive - Specific-Group Holding GmbH" + separator + "Documents" + separator + "Java" + separator + "PLANTILLA REGISTRO JORNADA.xlsx";
    static String pathOutput = "C:" + separator + "Users" + separator + "roman.nesterenko" + separator + "OneDrive - Specific-Group Holding GmbH" + separator + "Documents" + separator + "Java" + separator + "PLANTILLA REGISTRO JORNADA Filled.xlsx";
    static String pathPdf = "C:" + separator + "Users" + separator + "roman.nesterenko" + separator + "OneDrive - Specific-Group Holding GmbH" + separator + "Documents" + separator + "Java" + separator + "PLANTILLA REGISTRO JORNADA.pdf";
    static String officePath =  "C:" + separator + "Program Files" + separator + "LibreOffice";

    private static final String SOURCE_NAME = "libreoffice";

    public static void convert() throws IOException {
        OfficeManager officeManager = null;
        try {
            // Start an office process and connect to the started instance (on port 2002).
            officeManager = LocalOfficeManager.builder().install().officeHome(officePath).build();
            officeManager.start();
            Workbook sourceWorkbook;
            File inputFile = new File(pathOutput);
            sourceWorkbook = new XSSFWorkbook(inputFile);
            ByteArrayOutputStream baout = new ByteArrayOutputStream();
            sourceWorkbook.write(baout);
            InputStream bais = new ByteArrayInputStream(baout.toByteArray());
            File outputFile = new File(pathPdf);
            JodConverter.convert(bais).to(outputFile).execute();
            //    sourceWorkbook.();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OfficeException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } finally {
            // Stop the office process
            OfficeUtils.stopQuietly(officeManager);
        }



    }
    public static void fillExls (int id, int month, int year, ArrayList<Integer> vocationList) throws IOException, InvalidFormatException {
        int sheetNumber = 0;
        Locale spanishLocal = new Locale("es", "ES");
        File inputFile = new File(path);
        TextStyle style = TextStyle.FULL;
        Integer totalHours = 0;

        XSSFWorkbook sourceWorkbook = new XSSFWorkbook(new FileInputStream(inputFile));
        Sheet sheet =  sourceWorkbook.getSheetAt(sheetNumber);
        Row row4 = sheet.getRow(4);
        Row row5 = sheet.getRow(5);
        Row row6 = sheet.getRow(6);
        Row row7 = sheet.getRow(7);
        Row row41 = sheet.getRow(41);
        Row row50 = sheet.getRow(50);

        row4.getCell(6).setCellValue("Trabajador_Employee: " + EmployeesList.getNameSurnameById(id));
        row5.getCell(6).setCellValue("N.I.F.: " + EmployeesList.getNieById(id));
        row6.getCell(6).setCellValue("Nº Afiliación: " + EmployeesList.getAfiliacionById(id));
        YearMonth billingYearMonth = YearMonth.of(year, month);
        row7.getCell(6).setCellValue("Mes y Año: " + billingYearMonth.getMonth().getDisplayName(style, spanishLocal) + " " + year);
        row50.getCell(1).setCellValue("En________Valencia_______________, a __" + billingYearMonth.atEndOfMonth().getDayOfMonth() + "__ de __" + billingYearMonth.getMonth().getDisplayName(style, spanishLocal) + "__ de " + year);
        List <Integer> weekEnds = BillingPeriod.getWeekEnds(month,year);
        List <Integer> publickHollidays = BillingPeriod.getPublickHollidays(month,year);

        for (LocalDate day = billingYearMonth.atDay(1);
             day.isBefore(billingYearMonth.atEndOfMonth())
                     || day.isEqual(billingYearMonth.atEndOfMonth());
             day = day.plusDays(1)){
            Row row = sheet.getRow(day.getDayOfMonth() + 9);
            if (weekEnds.contains((int) row.getCell(0).getNumericCellValue())){
                CellStyle cellStyle = sourceWorkbook.createCellStyle();

                for (int i = 0; i <11; i++){
                    cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);
                    cellStyle.setBorderTop(BorderStyle.THIN);
                    Cell cell = row.getCell(i);
                    cell.setCellStyle(cellStyle);
                }
            }else if (publickHollidays.contains((int) row.getCell(0).getNumericCellValue())){
                CellStyle cellStyle = sourceWorkbook.createCellStyle();
                row.getCell(1).setCellValue("F");
                row.getCell(2).setCellValue("F");
                for (int i = 0; i <11; i++){
                    cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                    cellStyle.setFillPattern(FillPatternType.ALT_BARS);
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);
                    cellStyle.setBorderTop(BorderStyle.THIN);
                    Cell cell = row.getCell(i);
                    cell.setCellStyle(cellStyle);
                }
            }else if (vocationList.contains((int) row.getCell(0).getNumericCellValue())){
                row.getCell(1).setCellValue("V");
                row.getCell(2).setCellValue("V");
                row.getCell(3).setCellValue(8);
                totalHours+=8;
            }else {
                row.getCell(1).setCellValue("9:00");
                row.getCell(2).setCellValue("17:00");
                row.getCell(3).setCellValue(8);
                totalHours+=8;
            }

        }
        row41.getCell(3).setCellValue(totalHours);
        File outputFile = new File(pathOutput);
        sourceWorkbook.write(new FileOutputStream(outputFile));
        sourceWorkbook.close();


    }




}
