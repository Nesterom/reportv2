package report;

import com.itextpdf.text.DocumentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


//added by Igor

import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@RestController
public class FileController {
    
    static String separator = File.separator;
    static String pathPdfFilled = "src/main/FilesRepository/PLANTILLA REGISTRO JORNADA FILLED.pdf";
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
    //@RequestMapping(value = "/file", method = RequestMethod.POST)
    public ResponseEntity<FileResponse> getFile(Integer id, Integer month, Integer year, @RequestParam(required = false) List<Integer> vacationList) throws IOException, DocumentException {
        System.out.println("id =" + id + " "+ month);
        PdfReport.generateReport(id, month, year, vacationList);
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
