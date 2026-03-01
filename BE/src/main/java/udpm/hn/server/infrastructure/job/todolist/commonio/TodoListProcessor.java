package udpm.hn.server.infrastructure.job.todolist.commonio;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import udpm.hn.server.entity.Project;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.entity.TodoList;
import udpm.hn.server.core.manage.todo.repository.MAListTodoProjectRepository;
import udpm.hn.server.infrastructure.config.global.GlobalVariables;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.PriorityLevel;
import udpm.hn.server.infrastructure.constant.StatusTodo;
import udpm.hn.server.infrastructure.job.todolist.model.request.TodoExcelRequest;
import udpm.hn.server.infrastructure.job.todolist.service.impl.HistoryImportTodoService;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.TodoListHelper;
import udpm.hn.server.utils.ValidationUtil;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Component
@Slf4j
@StepScope
public class TodoListProcessor implements ItemProcessor<TodoExcelRequest, Todo> {

    @Setter(onMethod_ = {@Autowired})
    private PhaseTodoProjectRepository phaseTodoProjectRepository;

    @Setter(onMethod_ = {@Autowired})
    private PhaseRepository phaseRepository;

    @Setter(onMethod_ = {@Autowired})
    private MAListTodoProjectRepository maListTodoProjectRepository;

    @Setter(onMethod_ = {@Autowired})
    private ProjectRepository projectRepository;

    @Setter(onMethod_ = {@Autowired})
    private TodoListRepository todoListRepository;

    @Setter(onMethod_ = {@Autowired})
    private ToDoRepository toDoRepository;

    @Setter(onMethod_ = {@Autowired})
    private GlobalVariables globalVariables;

    @Setter(onMethod_ = {@Autowired})
    private TodoListHelper todoListHelper;

    @Setter(onMethod_ = {@Autowired})
    private CsvHistoryToDoListWriter csvHistoryWriter;

    @Setter(onMethod_ = {@Autowired})
    private ValidationUtil validationUtil ;

    @Setter(onMethod_ = {@Autowired})
    private final Set<String> processedCodes = new HashSet<>();

    @Setter(onMethod_ = {@Autowired})
    private HistoryImportTodoService historyImportService;

    @Value("#{jobParameters['idProject']}")
    private String idProject;

    private void saveMessageToDatabase(String record, String message) {
        String fullMessage = record + " - " + message;
        log.info("Saving message to database: " + fullMessage);

        try {
            historyImportService.saveHistory(fullMessage, idProject);
            log.info("Successfully saved message to database: " + fullMessage);
        } catch (Exception e) {
            log.error("Error saving history message: " + fullMessage, e);
        }
    }

    @Override
    public Todo process(TodoExcelRequest item) throws Exception {
        log.info("Processing todo with code: {}", idProject);

        // 1. Kiểm tra tên task
        if (item.getName().isEmpty()) {
            String error = "Tên todo không được để trống";
            saveMessageToDatabase(item.getCode(), error);
            csvHistoryWriter.writeHistory(item.getCode(), error);
            log.error(error);
            return null;
        }

        // 2. Kiểm tra mã task
        if (item.getCode().isEmpty()) {
            String error = "Mã todo không được để trống";
            saveMessageToDatabase(item.getName(), error);
            csvHistoryWriter.writeHistory(item.getName(), error);
            log.error(error);
            return null;
        }

        // 3. Kiểm tra mã trùng
        Optional<Todo> existingTodo = toDoRepository.findByCode(item.getCode());
        if (existingTodo.isPresent()) {
            String error = "Mã todo đã tồn tại";
            saveMessageToDatabase(item.getCode(), error);
            csvHistoryWriter.writeHistory(item.getCode(), error);
            log.error(error);
            return null;
        }

        // 3.5. Kiểm tra mã trùng trong file Excel
        if (!processedCodes.add(item.getCode())) {
            String error = "Mã bị trùng trong file";
            saveMessageToDatabase(item.getCode(), error);
            csvHistoryWriter.writeHistory(item.getCode(), error);
            log.error(error);
            return null;
        }

        // 5. Kiểm tra dự án tồn tại trong DB
        Optional<Project> optionalProject = projectRepository.findById(idProject);
        if (optionalProject.isEmpty()) {
            String error = "Không tìm thấy dự án" ;
            saveMessageToDatabase(item.getCode(), error);
            csvHistoryWriter.writeHistory(item.getCode(), error);
            log.error(error);
            return null;
        }

        TodoList todoList = maListTodoProjectRepository.findTodoListByProject_Id(optionalProject.get().getId())
                .stream().findFirst().orElse(null);

        if (todoList == null) {
            todoList = new TodoList();
            todoList.setIndexTodoList(todoListHelper.genIndexTodoList(optionalProject.get().getId()));
            todoList.setProject(optionalProject.get());
            maListTodoProjectRepository.save(todoList);
        }

        Todo todo = new Todo();
        todo.setCode(item.getCode());
        todo.setName(item.getName());
        todo.setStatus(EntityStatus.ACTIVE);
        todo.setStatusTodo(StatusTodo.CHUA_HOAN_THANH);
        todo.setPriorityLevel(PriorityLevel.THAP);
        todo.setDescriptions(item.getDescribe());
        todo.setIndexTodo((short) 0);
        todo.setTodoList(todoList);

        return todo;
    }

}
