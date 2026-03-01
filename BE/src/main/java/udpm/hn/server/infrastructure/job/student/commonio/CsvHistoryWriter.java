package udpm.hn.server.infrastructure.job.student.commonio;


import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import udpm.hn.server.infrastructure.config.global.GlobalVariables;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Component
public class CsvHistoryWriter {

    private final AtomicInteger idCounter = new AtomicInteger(1);

    @Setter(onMethod_ = {@Autowired})
    private GlobalVariables globalVariables;


    public synchronized void writeHistory(String record, String message) {
        String csvFilePath = "src/main/resources/log-accountability-index/nvv.csv";
        File file = new File(csvFilePath);
        boolean isNewFile = !file.exists();

        try {
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter fileWriter = new FileWriter(csvFilePath, true);
                 PrintWriter writer = new PrintWriter(fileWriter)) {

                if (isNewFile) {
                    fileWriter.write("\uFEFF");
                }

                writer.printf("%d,%s,%s%n", idCounter.getAndIncrement(), record, message);
            }

        } catch (IOException e) {
            log.error("Error writing to CSV file", e);
        }
    }




}
