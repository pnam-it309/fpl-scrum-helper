package udpm.hn.server.infrastructure.job.todolist.service.impl;


import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DowLoadFileLogTodo {

    public byte[] getCsvFile() throws IOException {
//        File csvFile = new File("src/main/resources/log-accountability-index/todolist.csv");
//        if (!csvFile.exists()) {
//            throw new IOException("File không tồn tại.");
//        }
//
//        String content = Files.readString(csvFile.toPath(), StandardCharsets.UTF_8);
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        outputStream.write(0xEF);
//        outputStream.write(0xBB);
//        outputStream.write(0xBF);
//        outputStream.write(content.getBytes(StandardCharsets.UTF_8));
//
//        return outputStream.toByteArray();

        ClassPathResource resource = new ClassPathResource("log-accountability-index/todolist.csv");

        if (!resource.exists()) {
            throw new IOException("File không tồn tại trong resources.");
        }

        String content = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(0xEF);
        outputStream.write(0xBB);
        outputStream.write(0xBF);
        outputStream.write(content.getBytes(StandardCharsets.UTF_8));

        return outputStream.toByteArray();
    }

}

