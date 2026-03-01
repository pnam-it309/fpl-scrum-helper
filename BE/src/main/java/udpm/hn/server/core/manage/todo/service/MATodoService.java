package udpm.hn.server.core.manage.todo.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.todo.model.request.MATodoCURequest;
import udpm.hn.server.core.manage.todo.model.request.MATodoSearchRequest;
import udpm.hn.server.core.manage.todo.model.request.MATodoStatisticsRequest;

public interface MATodoService {

    ResponseObject<?> getAllTodo(MATodoSearchRequest request);

    ResponseObject<?> fetchDataTodoByProject(String idProject);

    ResponseObject<?> getAllTodoStatistics(MATodoStatisticsRequest request);

    ResponseObject<?> countTodoByStaffProject(MATodoStatisticsRequest request);

    ResponseObject<?> getAllStaffByTodo(MATodoSearchRequest request, String idTodo);

    ResponseObject<?> getAllTodoByPhase(MATodoSearchRequest request, String idPhase);

    ResponseObject<?> createTodo(MATodoCURequest request);

    ResponseObject<?> deleteTodo(String id);

    ResponseObject<?> dataStaffProject(String id);

    ResponseObject<?> updateTodo(String id, MATodoCURequest request);


}
