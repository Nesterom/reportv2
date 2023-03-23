package report;

import com.itextpdf.text.DocumentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.List;
import java.util.Locale;


@RestController
public class FileController {
    
    public class FileResponse {
        private String fileName;
        private String fileType;
        private byte[] data;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }
    }
    
    @GetMapping("/file")
    @CrossOrigin(origins="*")
    public ResponseEntity<FileResponse> getFile(Integer id, Integer month, Integer year, @RequestParam(required = false) List<Integer> vacationList) throws IOException, DocumentException {
        PdfReport.generateReport(id, month, year, (ArrayList<Integer>) vacationList);
        YearMonth billingYearMonth = YearMonth.of(year, month);
        Locale spanishLocal = new Locale("es", "ES");
        TextStyle style = TextStyle.FULL;
        String pathPdfFilled = "src/main/FilesRepository/PLANTILLA REGISTRO JORNADA "
                + EmployeesList.getNameSurnameById(id)
                + " "
                + billingYearMonth.getMonth().getDisplayName(style, spanishLocal)
                + ".pdf";
        File file = new File(pathPdfFilled);

        byte[] fileContent = Files.readAllBytes(file.toPath());
        String fileType = Files.probeContentType(file.toPath());

        FileResponse fileResponse = new FileResponse();
        fileResponse.setFileName(file.getName());
        fileResponse.setFileType(fileType);
        fileResponse.setData(fileContent);

        return ResponseEntity.ok(fileResponse);
    }

}
