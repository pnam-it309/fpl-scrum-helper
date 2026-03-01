package udpm.hn.server.infrastructure.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Service
@Slf4j
public class LogService {

    public List<LogEntry> readCSV(String filePath) throws FileNotFoundException {
        List<LogEntry> logEntries = new ArrayList<>();

        try (FileReader reader = new FileReader(filePath)) {
                CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader("ID", "Student", "Message")
                    .build();
            try (CSVParser csvParser = new CSVParser(reader, csvFormat)) {
                for (CSVRecord csvRecord : csvParser) {
                    LogEntry logEntry = new LogEntry();
                    logEntry.setId(csvRecord.get("ID"));
                    logEntry.setStudent(csvRecord.get("Student"));
                    logEntry.setMessage(csvRecord.get("Message"));
                    logEntries.add(logEntry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logEntries;
    }

    public List<LogEntryTodoList> readCSVTodoList(String filePath) throws FileNotFoundException {
        List<LogEntryTodoList> logEntries = new ArrayList<>();

        try (FileReader reader = new FileReader(filePath)) {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader("ID", "Todo", "Message")
                    .build();
            try (CSVParser csvParser = new CSVParser(reader, csvFormat)) {
                for (CSVRecord csvRecord : csvParser) {
                    LogEntryTodoList logEntry = new LogEntryTodoList();
                    logEntry.setId(csvRecord.get("ID"));
                    logEntry.setTodo(csvRecord.get("Todo"));
                    logEntry.setMessage(csvRecord.get("Message"));
                    logEntries.add(logEntry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logEntries;
    }

}