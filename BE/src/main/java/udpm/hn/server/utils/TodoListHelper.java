package udpm.hn.server.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import udpm.hn.server.repository.TodoListRepository;

@Component
public class TodoListHelper {

    @Autowired
    private TodoListRepository todoListRepository;

    public String genCodeTodoList(String projectId) {
        Integer countTodoList = todoListRepository.countSimpleEntityByIdProject(projectId);
        Integer newCountTodoList = ++countTodoList;
        return "TodoList_" + newCountTodoList;
    }

    public Byte genIndexTodoList(String projectId) {
        Byte indexMax = todoListRepository.getIndexTodoListMax(projectId);
        if(indexMax == null){
            return 0;
        }
        return ++indexMax;
    }

    public Byte genIndexTodoListWHEREIDPHASE(String projectId,String idPhase) {
        Byte indexMax = todoListRepository.getIndexTodoListMaxWhereIDPHASE(projectId,idPhase);
        if(indexMax == null){
            return 0;
        }
        return ++indexMax;
    }
}
