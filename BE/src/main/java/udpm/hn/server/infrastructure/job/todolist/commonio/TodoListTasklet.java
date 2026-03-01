package udpm.hn.server.infrastructure.job.todolist.commonio;

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
import udpm.hn.server.entity.PhaseTodoProject;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.infrastructure.config.global.GlobalVariables;
import udpm.hn.server.infrastructure.job.todolist.model.request.TodoExcelRequest;
import udpm.hn.server.infrastructure.job.todolist.repository.TodoExcelRepository;
import udpm.hn.server.repository.PhaseTodoProjectRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Transactional  // Đảm bảo tất cả hoặc không có gì được lưu
public class TodoListTasklet implements Tasklet {

    private final TodoExcelRepository todoExcelRepository;
    private final ItemReader<TodoExcelRequest> reader;
    private final ItemProcessor<TodoExcelRequest, Todo> processor;
    private final GlobalVariables globalVariables;
    private final PhaseTodoProjectRepository phaseTodoProjectRepository;

    public TodoListTasklet(TodoExcelRepository todoExcelRepository1, ItemReader<TodoExcelRequest> reader,
                           @Qualifier("todolistExcelProcessor") ItemProcessor<TodoExcelRequest, Todo> processor, GlobalVariables globalVariables, PhaseTodoProjectRepository phaseTodoProjectRepository) {
        this.todoExcelRepository = todoExcelRepository1;
        this.reader = reader;
        this.processor = processor;
        this.globalVariables = globalVariables;
        this.phaseTodoProjectRepository = phaseTodoProjectRepository;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<Todo> todoLists = new ArrayList<>();
        List<String> errorLogs = new ArrayList<>();

        TodoExcelRequest item;
        while ((item = reader.read()) != null) {
            try {
                Todo processed = processor.process(item);
                if (processed != null) {
                    todoLists.add(processed);
                } else {
                    errorLogs.add("Todo với mã " + item.getCode() + " bị lỗi và không được xử lý.");
                }
            } catch (Exception e) {
                errorLogs.add("Lỗi khi xử lý Todo mã " + item.getCode() + ": " + e.getMessage());
            }
        }

        if (!errorLogs.isEmpty()) {
            log.warn("Có lỗi xảy ra, không import bất kỳ todo nào. Chi tiết lỗi đã được ghi vào file log.");
            return RepeatStatus.FINISHED;
        }

        todoExcelRepository.saveAll(todoLists);
        for (Todo todo : todoLists) {
            PhaseTodoProject phaseTodoProject = new PhaseTodoProject();
            phaseTodoProject.setTodo(todo);
            phaseTodoProjectRepository.save(phaseTodoProject);
        }

        return RepeatStatus.FINISHED;
    }

}
