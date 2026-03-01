package udpm.hn.server.infrastructure.job.todolist.commonio;


import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import udpm.hn.server.infrastructure.config.global.GlobalVariables;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Component
public class CsvHistoryToDoListWriter {


    private final AtomicInteger idCounter = new AtomicInteger(1);

    @Setter(onMethod_ = {@Autowired})
    private GlobalVariables globalVariables;

    public String sanitizeText(String input) {
        if (input == null) return "";
        return input.replaceAll("\\p{C}", "");
    }


    public String escapeCsvField(String input) {
        if (input.contains(",") || input.contains("\"")) {
            input = "\"" + input.replace("\"", "\"\"") + "\"";
        }
        return input;
    }


    public synchronized void writeHistory(String record, String message) {
        String csvFilePath = "src/main/resources/log-accountability-index/todolist.csv";
        File file = new File(csvFilePath);
        boolean isNewFile = !file.exists();

        try {
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(csvFilePath, true);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                 BufferedWriter bw = new BufferedWriter(osw)) {

                if (isNewFile) {
                    bw.write("\uFEFF");
                }

                String cleanRecord = sanitizeText(record);
                String cleanMessage = sanitizeText(message);

                String escapedRecord = escapeCsvField(cleanRecord);
                String escapedMessage = escapeCsvField(cleanMessage);

                String line = String.format("%d,%s,%s%n", idCounter.getAndIncrement(), escapedRecord, escapedMessage);
                bw.write(line);
            }

        } catch (IOException e) {
            log.error("Error writing to CSV file", e);
        }
    }
}
