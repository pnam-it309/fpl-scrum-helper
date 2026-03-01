package udpm.hn.server.infrastructure.job.staff.service.impl;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Service
public class DowLoadFileLogStaff {

    public byte[] getCsvFile() throws IOException {
//        File resource = new File("src/main/resources/log-accountability-index/log.csv");
//        if (!resource.exists()) {
//            throw new IOException("File không tồn tại.");
//        }
//
//        String content = Files.readString(resource.toPath(), StandardCharsets.UTF_8);

//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        outputStream.write(0xEF);
//        outputStream.write(0xBB);
//        outputStream.write(0xBF);
//        outputStream.write(content.getBytes(StandardCharsets.UTF_8));

        ClassPathResource resource = new ClassPathResource("log-accountability-index/log.csv");

        byte[] content = FileCopyUtils.copyToByteArray(resource.getInputStream());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(0xEF);
        outputStream.write(0xBB);
        outputStream.write(0xBF);
        outputStream.write(content);

        return outputStream.toByteArray();
    }
}

