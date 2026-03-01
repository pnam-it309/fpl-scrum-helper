package udpm.hn.server.infrastructure.job.reportcompensation.service.impl;


import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Service
public class DowLoadFileLogReport {

    public byte[] getCsvFile() throws IOException {
//        File resource = new File("src/main/resources/log-accountability-index/report.csv");
//        if (!resource.exists()) {
//            throw new IOException("File không tồn tại.");
//        }
//
//        String content = Files.readString(resource.toPath(), StandardCharsets.UTF_8);
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        outputStream.write(0xEF);
//        outputStream.write(0xBB);
//        outputStream.write(0xBF);
//        outputStream.write(content.getBytes(StandardCharsets.UTF_8));
//
//        return outputStream.toByteArray();

        ClassPathResource resource = new ClassPathResource("log-accountability-index/report.csv");
        if (!resource.exists()) {
            throw new IOException("File không tồn tại.");
        }

        try (InputStream inputStream = resource.getInputStream()) {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(0xEF);
            outputStream.write(0xBB);
            outputStream.write(0xBF);
            outputStream.write(content.getBytes(StandardCharsets.UTF_8));

            return outputStream.toByteArray();
        }
    }
}

