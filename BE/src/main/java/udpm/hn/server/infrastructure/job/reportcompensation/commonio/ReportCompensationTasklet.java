package udpm.hn.server.infrastructure.job.reportcompensation.commonio;

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
import udpm.hn.server.entity.Report;
import udpm.hn.server.infrastructure.job.reportcompensation.model.request.ReportCompensationRequest;
import udpm.hn.server.infrastructure.job.reportcompensation.repository.ReportCompensationRepository;


import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Transactional  // Đảm bảo tất cả hoặc không có gì được lưu
public class ReportCompensationTasklet implements Tasklet {

    private final ReportCompensationRepository reportCompensationRepository;

    private final ItemReader<ReportCompensationRequest> reader;
    private final ItemProcessor<ReportCompensationRequest, Report> processor;

    public ReportCompensationTasklet(ReportCompensationRepository reportCompensationRepository,
                                     ItemReader<ReportCompensationRequest> reader,
                                     @Qualifier("reportCompensationExcelProcessor") ItemProcessor<ReportCompensationRequest, Report> processor) {
        this.reportCompensationRepository = reportCompensationRepository;
        this.reader = reader;
        this.processor = processor;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<Report> reports = new ArrayList<>();
        ReportCompensationRequest item;

        try {
            while ((item = reader.read()) != null) {
                reports.add(processor.process(item));
            }

            if (!reports.isEmpty()) {

                reportCompensationRepository.saveAll(reports);
                log.info("All students saved successfully.");

            }

        } catch (Exception e) {
            log.error("Error processing students, rolling back: ", e);
            throw e;  // Rollback nếu có lỗi
        }

        return RepeatStatus.FINISHED;
    }
}
