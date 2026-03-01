package udpm.hn.server.infrastructure.job.staff.jobconfig;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
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
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffMajorFacility;
import udpm.hn.server.entity.Student;
import udpm.hn.server.infrastructure.job.staff.commonio.StaffProcessor;
import udpm.hn.server.infrastructure.job.staff.commonio.StaffRowMapper;
import udpm.hn.server.infrastructure.job.staff.commonio.StaffTasklet;
import udpm.hn.server.infrastructure.job.staff.commonio.StaffWriter;
import udpm.hn.server.infrastructure.job.staff.model.dto.TransferStaffRole;
import udpm.hn.server.infrastructure.job.staff.model.request.StaffExcelRequest;
import udpm.hn.server.infrastructure.job.staff.repository.ConfigStaffCustomRepository;
import udpm.hn.server.infrastructure.job.student.commonio.StudentProcessor;
import udpm.hn.server.infrastructure.job.student.commonio.StudentRowMapper;
import udpm.hn.server.infrastructure.job.student.commonio.StudentTasklet;
import udpm.hn.server.infrastructure.job.student.commonio.StudentWriter;
import udpm.hn.server.infrastructure.job.student.model.request.StudentExcelRequest;
import udpm.hn.server.infrastructure.job.student.repository.StudentExcelRepository;
import udpm.hn.server.repository.StaffMajorFacilityRepository;

import java.io.File;

@Configuration
@Slf4j
@EnableTransactionManagement
public class StaffJobConfig {

    @Setter(onMethod_ = {@Autowired}, onParam_ = {@Qualifier("transactionManager")})
    private PlatformTransactionManager transactionManager;

    @Value("${file.upload.staff.path}")
    private String fullPath;

    @Bean
    @StepScope
    public ItemReader<StaffExcelRequest> excelStaffReader(@Value("#{jobParameters['staffExcelFileName']}") String path) {
        log.info("Đọc file staff từ: {}", fullPath + "/" + path);
        PoiItemReader<StaffExcelRequest> reader = new PoiItemReader<>();
        reader.setResource(new FileSystemResource(new File(fullPath + "/" + path)));
        reader.setLinesToSkip(1);
        reader.open(new ExecutionContext());
        reader.setRowMapper(excelStaffRowMapper());
        reader.setLinesToSkip(1);
        try {
            reader.afterPropertiesSet();  // 👈 QUAN TRỌNG
        } catch (Exception e) {
            log.error("Lỗi khi init reader", e);
        }
        return reader;
    }

    @StepScope
    private RowMapper<StaffExcelRequest> excelStaffRowMapper() {
        return new StaffRowMapper();
    }

    @Bean
    @StepScope
    @Qualifier("staffExcelProcessor")
    ItemProcessor<StaffExcelRequest, TransferStaffRole> staffExcelProcessor() {
        return new StaffProcessor();
    }
    @Bean
    @StepScope
    ItemWriter<Staff> exStaffItemWriter() {
        return new StaffWriter();
    }

    @Bean
    StaffTasklet excelStaffTasklet(ConfigStaffCustomRepository configStaffCustomRepository,
                                   ItemReader<StaffExcelRequest> reader,
                                   StaffMajorFacilityRepository staffMajorFacilityRepository,
                                   @Qualifier("staffExcelProcessor") ItemProcessor<StaffExcelRequest, TransferStaffRole> processor) {
        return new StaffTasklet(configStaffCustomRepository,staffMajorFacilityRepository, reader, processor);
    }



    @Bean(name = "staffStep3")
    Step staffstep3(JobRepository jobRepository, PlatformTransactionManager transactionManager,
               StaffTasklet staffTasklet) {  // Inject bean StudentTasklet
        return new StepBuilder("staff-step3", jobRepository)
                .tasklet(staffTasklet, transactionManager)  // Sử dụng tasklet từ bean
                .allowStartIfComplete(false)
                .build();
    }


    @Bean
    Job saveStaffDataToDatabaseJob(@Qualifier("staffStep3") Step staffStep3, JobRepository jobRepository) {
        return new JobBuilder("Excel-File-To-Database-Job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(staffStep3)
                .build();
    }

}
