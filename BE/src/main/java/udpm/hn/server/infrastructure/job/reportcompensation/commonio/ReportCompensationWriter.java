package udpm.hn.server.infrastructure.job.reportcompensation.commonio;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import udpm.hn.server.entity.Report;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReportCompensationWriter implements ItemWriter<Report> {

    @Transactional
    @Override
    public void write(Chunk<? extends Report> chunk) throws Exception {

    }
}
