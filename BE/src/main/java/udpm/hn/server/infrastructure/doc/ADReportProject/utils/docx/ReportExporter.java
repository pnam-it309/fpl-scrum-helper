package udpm.hn.server.infrastructure.doc.ADReportProject.utils.docx;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

public class ReportExporter {

    @Getter
    @Setter
    public static class Report {
        public String projectName;
        public String username;
        public String userCode;
        public String reportName;
        public String content;

        public Report(String projectName, String username, String reportName, String content) {
            this.projectName = projectName;
            this.username = username;
            this.reportName = reportName;
            this.content = content;
        }

        public Report(String projectName, String username,String userCode, String reportName, String content) {
            this.projectName = projectName;
            this.username = username;
            this.reportName = reportName;
            this.content = content;
            this.userCode = userCode;
        }

    }

    public static void zipFolder(String sourceFolderPath, String zipFilePath) throws IOException {
        try (ZipOutputStream zs = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            Path sourcePath = Paths.get(sourceFolderPath);

            Files.walk(sourcePath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            System.err.println("Lỗi khi zip file: " + path);
                        }
                    });
        }
    }


}
