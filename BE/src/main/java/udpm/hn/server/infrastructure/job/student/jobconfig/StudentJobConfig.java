package udpm.hn.server.infrastructure.job.student.jobconfig;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import udpm.hn.server.entity.Student;
import udpm.hn.server.infrastructure.job.student.commonio.StudentProcessor;
import udpm.hn.server.infrastructure.job.student.commonio.StudentRowMapper;
import udpm.hn.server.infrastructure.job.student.commonio.StudentTasklet;
import udpm.hn.server.infrastructure.job.student.commonio.StudentWriter;
import udpm.hn.server.infrastructure.job.student.model.request.StudentExcelRequest;
import udpm.hn.server.infrastructure.job.student.repository.StudentExcelRepository;

import java.io.File;
@Configuration
@Slf4j
public class StudentJobConfig {

    @Value( "${file.upload.student.path}")
    private String fullPath;

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager transactionManager;



    @Bean
    @StepScope
    ItemReader<StudentExcelRequest> excelStudentReader(@Value("#{jobParameters['filePath']}") String path) {
        PoiItemReader<StudentExcelRequest> reader = new PoiItemReader<>();
        reader.setResource(new FileSystemResource(new File(fullPath + "/" + path)));
        reader.setLinesToSkip(1);
        reader.open(new ExecutionContext());
        reader.setRowMapper(excelStudentRowMapper());
        reader.setLinesToSkip(1);
        return reader;
    }

    @StepScope
    private RowMapper<StudentExcelRequest> excelStudentRowMapper() {
        return new StudentRowMapper();
    }

//    @Bean
//    @StepScope
//    @Qualifier("excelStudentProcessor")
//    ItemProcessor<StudentExcelRequest, Student> excelStudentProcessor() {
//        return new StudentProcessor();
//    }


    @Bean
    @StepScope
    @Qualifier("studentExcelProcessor")
    ItemProcessor<StudentExcelRequest, Student> studentExcelProcessor() {
        return new StudentProcessor();
    }
    @Bean
    @StepScope
    ItemWriter<Student> excelStudentWriter() {
        return new StudentWriter();
    }

//    @Bean
//    Step step2(
//            @Qualifier("excelStudentReader") ItemReader<StudentExcelRequest> reader,
//            JobRepository jobRepository
//    ) {
//        return new StepBuilder("Student-excel-step", jobRepository)
//                .<StudentExcelRequest, Student>chunk(100, transactionManager)
//                .reader(reader)
//
//                .processor(item -> excelStudentProcessor().process(item))
//                .writer(chunk -> excelStudentWriter().write(chunk))
//                .transactionManager(transactionManager)
//                .build();
//    }

    @Bean
    StudentTasklet excelStudentTasklet(StudentExcelRepository studentExcelRepository,
                                       ItemReader<StudentExcelRequest> reader,
                                       @Qualifier("studentExcelProcessor") ItemProcessor<StudentExcelRequest, Student> processor) {
        return new StudentTasklet(studentExcelRepository, reader, processor);
    }



    @Bean
    Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager,
               StudentTasklet studentTasklet) {  // Inject bean StudentTasklet
        return new StepBuilder("Student-excel-step", jobRepository)
                .tasklet(studentTasklet, transactionManager)  // Sử dụng tasklet từ bean
                .allowStartIfComplete(false)
                .build();
    }


    @Bean
    Job excelFileToDatabaseJobStudent( Step step2, JobRepository jobRepository) {
        return new JobBuilder("Excel-File-To-Database-Job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step2)
                .build();
    }



}
