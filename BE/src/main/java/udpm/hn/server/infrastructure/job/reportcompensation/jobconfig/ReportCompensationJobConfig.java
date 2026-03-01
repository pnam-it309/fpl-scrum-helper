package udpm.hn.server.infrastructure.job.reportcompensation.jobconfig;


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
import udpm.hn.server.entity.Report;
import udpm.hn.server.infrastructure.job.reportcompensation.commonio.ReportCompensationProcessor;
import udpm.hn.server.infrastructure.job.reportcompensation.commonio.ReportCompensationRowMapper;
import udpm.hn.server.infrastructure.job.reportcompensation.commonio.ReportCompensationTasklet;
import udpm.hn.server.infrastructure.job.reportcompensation.commonio.ReportCompensationWriter;
import udpm.hn.server.infrastructure.job.reportcompensation.model.request.ReportCompensationRequest;
import udpm.hn.server.infrastructure.job.reportcompensation.repository.ReportCompensationRepository;

import java.io.File;

@Configuration
@Slf4j
public class ReportCompensationJobConfig {

    @Value( "${file.upload.reportcompensation.path}")
    private String fullPath;

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager transactionManager;



    @Bean
    @StepScope
    ItemReader<ReportCompensationRequest> excelReportCompensationReader(@Value("#{jobParameters['filePath']}") String path) {
        PoiItemReader<ReportCompensationRequest> reader = new PoiItemReader<>();
        reader.setResource(new FileSystemResource(new File(fullPath + "/" + path)));
        reader.setLinesToSkip(1);
        reader.open(new ExecutionContext());
        reader.setRowMapper(excelReportCompensationRowMapper());
        reader.setLinesToSkip(1);
        return reader;
    }

    @StepScope
    private RowMapper<ReportCompensationRequest> excelReportCompensationRowMapper() {
        return new ReportCompensationRowMapper();
    }

    @Bean
    @StepScope
    @Qualifier("reportCompensationExcelProcessor")
    ItemProcessor<ReportCompensationRequest, Report> reportCompensationExcelProcessor() {
        return new ReportCompensationProcessor();
    }
    @Bean
    @StepScope
    ItemWriter<Report> excelReportCompensationWriter() {
        return new ReportCompensationWriter();
    }

    @Bean
    ReportCompensationTasklet excelReportCompensationTasklet(ReportCompensationRepository reportCompensationRepository,
                                                  ItemReader<ReportCompensationRequest> reader,
                                                  @Qualifier("reportCompensationExcelProcessor") ItemProcessor<ReportCompensationRequest, Report> processor) {
        return new ReportCompensationTasklet(reportCompensationRepository, reader, processor);
    }



    @Bean
    Step step4(JobRepository jobRepository, PlatformTransactionManager transactionManager,
               ReportCompensationTasklet reportCompensationTasklet) {
        return new StepBuilder("Report-compensation-excel-step", jobRepository)
                .tasklet(reportCompensationTasklet, transactionManager)  // Sử dụng tasklet từ bean
                .allowStartIfComplete(false)
                .build();
    }


    @Bean
    Job excelFileToDatabaseJobReportCompensation( Step step4, JobRepository jobRepository) {
        return new JobBuilder("Report-compensation-excel", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step4)
                .build();
    }



}
