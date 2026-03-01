package udpm.hn.server.infrastructure.job.student.commonio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import udpm.hn.server.entity.Student;
import udpm.hn.server.infrastructure.job.student.model.request.StudentExcelRequest;
import udpm.hn.server.infrastructure.job.student.repository.StudentExcelRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Transactional  // Đảm bảo tất cả hoặc không có gì được lưu
public class StudentTasklet implements Tasklet {

    private final StudentExcelRepository studentExcelRepository;

    private final ItemReader<StudentExcelRequest> reader;
    private final ItemProcessor<StudentExcelRequest, Student> processor;

    public StudentTasklet(StudentExcelRepository studentExcelRepository,
                          ItemReader<StudentExcelRequest> reader,
                          @Qualifier("studentExcelProcessor") ItemProcessor<StudentExcelRequest, Student> processor) {
        this.studentExcelRepository = studentExcelRepository;
        this.reader = reader;
        this.processor = processor;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<Student> students = new ArrayList<>();
        StudentExcelRequest item;

        try {
            while ((item = reader.read()) != null) {
                students.add(processor.process(item));  // Xử lý tất cả bản ghi
            }

            if (!students.isEmpty()) {
                studentExcelRepository.saveAll(students);  // Chỉ lưu khi tất cả được đọc thành công
                log.info("All students saved successfully.");

            }

        } catch (Exception e) {
            log.error("Error processing students, rolling back: ", e);
            throw e;  // Rollback nếu có lỗi
        }

        return RepeatStatus.FINISHED;
    }
}
