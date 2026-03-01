package udpm.hn.server.infrastructure.job.todolist.commonio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;
import org.springframework.stereotype.Component;
import udpm.hn.server.infrastructure.job.todolist.model.request.TodoExcelRequest;

@Slf4j
@Component
public class TodoListRowMapper implements RowMapper<TodoExcelRequest> {

    @Override
    public TodoExcelRequest mapRow(RowSet rowSet) {
        try {
            TodoExcelRequest importExcelRequest = new TodoExcelRequest();
            importExcelRequest.setOrderNumber(rowSet.getCurrentRowIndex());
            importExcelRequest.setCode(rowSet.getColumnValue(1));
            importExcelRequest.setName(rowSet.getColumnValue(2));
            importExcelRequest.setDescribe(rowSet.getColumnValue(3));
//            importExcelRequest.setTodoList(rowSet.getColumnValue(4));
            log.info(importExcelRequest.toString()+"llll");
            return importExcelRequest;
        } catch (Exception e) {
            return null;
        }
    }
}


