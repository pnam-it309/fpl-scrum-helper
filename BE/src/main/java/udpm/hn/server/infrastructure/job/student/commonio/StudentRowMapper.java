package udpm.hn.server.infrastructure.job.student.commonio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;
import org.springframework.stereotype.Component;
import udpm.hn.server.infrastructure.job.student.model.request.StudentExcelRequest;

@Slf4j
@Component
public class StudentRowMapper  implements RowMapper<StudentExcelRequest> {

    @Override
    public StudentExcelRequest mapRow(RowSet rowSet) {
        try {
            StudentExcelRequest importExcelRequest = new StudentExcelRequest();
            importExcelRequest.setOrderNumber(rowSet.getCurrentRowIndex());
            importExcelRequest.setCode(rowSet.getColumnValue(1));
            importExcelRequest.setName(rowSet.getColumnValue(2));
            importExcelRequest.setEmail(rowSet.getColumnValue(3));
            importExcelRequest.setMajor(rowSet.getColumnValue(4));
            importExcelRequest.setDepartment(rowSet.getColumnValue(5));
            importExcelRequest.setFacility(rowSet.getColumnValue(6));
            log.info(importExcelRequest.toString()+"llll");
            return importExcelRequest;
        } catch (Exception e) {
            return null;
        }
    }
}


