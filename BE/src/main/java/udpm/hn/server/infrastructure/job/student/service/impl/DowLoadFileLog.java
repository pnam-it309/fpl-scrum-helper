package udpm.hn.server.infrastructure.job.student.service.impl;


import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
@Service
public class DowLoadFileLog {

    public byte[] getCsvFile() throws IOException {
//        FileSystemResource resource = new FileSystemResource("src/main/resources/log-accountability-index/nvv.csv");
//        if (!resource.exists()) {
//            throw new IOException("File không tồn tại.");
//        }
//
//        try (InputStream inputStream = resource.getInputStream()) {
//            return inputStream.readAllBytes();
//        }

        ClassPathResource resource = new ClassPathResource("log-accountability-index/nvv.csv");

        if (!resource.exists()) {
            throw new IOException("File không tồn tại.");
        }

        try (InputStream inputStream = resource.getInputStream()) {
            return inputStream.readAllBytes();
        }
    }
}

