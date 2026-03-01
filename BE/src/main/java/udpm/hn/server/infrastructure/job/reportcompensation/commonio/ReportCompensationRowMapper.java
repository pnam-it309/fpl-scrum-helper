package udpm.hn.server.infrastructure.job.reportcompensation.commonio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;
import org.springframework.stereotype.Component;
import udpm.hn.server.infrastructure.job.reportcompensation.model.request.ReportCompensationRequest;

@Slf4j
@Component
public class ReportCompensationRowMapper implements RowMapper<ReportCompensationRequest> {

    @Override
    public ReportCompensationRequest mapRow(RowSet rowSet) {
        try {
            ReportCompensationRequest importExcelRequest = new ReportCompensationRequest();
            importExcelRequest.setOrderNumber(rowSet.getCurrentRowIndex());
            importExcelRequest.setName(rowSet.getColumnValue(1));
            importExcelRequest.setEmail(rowSet.getColumnValue(2));
            importExcelRequest.setNgayNay(rowSet.getColumnValue(3));
            importExcelRequest.setNgayMai(rowSet.getColumnValue(4));
            importExcelRequest.setKhoKhan(rowSet.getColumnValue(5));
            importExcelRequest.setNgayBaoCao(rowSet.getColumnValue(6));

            log.info(importExcelRequest.toString()+"llll");
            return importExcelRequest;
        } catch (Exception e) {
            return null;
        }
    }
}


