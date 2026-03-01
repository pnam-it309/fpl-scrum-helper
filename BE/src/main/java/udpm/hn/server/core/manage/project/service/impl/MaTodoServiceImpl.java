package udpm.hn.server.core.manage.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.response.MaTodoSummaryResponse;
import udpm.hn.server.core.manage.project.model.response.MeDataDashboardLabelResponse;
import udpm.hn.server.core.manage.project.model.response.MeDataDashboardTodoListResponse;
import udpm.hn.server.core.manage.project.repository.MaTodoProjectRepository;
import udpm.hn.server.core.manage.project.service.MaTodoService;
import udpm.hn.server.core.manage.todo.repository.MATodoRepository;

import java.util.List;


@Service
@Validated
@RequiredArgsConstructor
public class MaTodoServiceImpl implements MaTodoService {

    @Autowired
    private MATodoRepository maTodoRepository;

    private final MaTodoProjectRepository maTodoProjectRepository;

    @Override
    public List<MeDataDashboardTodoListResponse> countTodoByTodoListAllProject(String projectId) {
        return maTodoRepository.countTodoByTodoListAllProject(projectId);
    }

    @Override
    public Integer countTodoByDueDateAllProject(String projectId, Integer statusTodo) {
        return maTodoRepository.countTodoByDueDateAllProject(projectId, statusTodo);
    }

    @Override
    public Integer countTodoByNoDueDateAllProject(String projectTodo) {
        return maTodoRepository.countTodoByNoDueDateAllProject(projectTodo);
    }

    @Override
    public List<MeDataDashboardLabelResponse> countTodoByLabelAllProject(String projectId) {
        return maTodoRepository.countTodoByLabelAllProject(projectId);
    }

    @Override
    public Integer countTodoByNoLabelAllProject(String projectId) {
        return maTodoRepository.countTodoByNoLabelAllProject(projectId);
    }

    @Override
    public List<MeDataDashboardTodoListResponse> countTodoByTodoListPhase(String projectId, String phaseId) {
        return maTodoRepository.countTodoByTodoListPhase(projectId, phaseId);
    }

    @Override
    public Integer countTodoByDueDatePhase(String projectId, String phaseId, Integer statusTodo) {
        return maTodoRepository.countTodoByDueDatePhase(projectId, phaseId, statusTodo);
    }

    @Override
    public Integer countTodoByNoDueDatePhase(String projectId, String phaseId) {
        return maTodoRepository.countTodoByNoDueDatePhase(projectId, phaseId);
    }

    @Override
    public Integer countTodoByPriorityLevel(String projectId, Integer priorityLevel) {
        return maTodoRepository.countTodoByPriorityLevel(projectId,priorityLevel);
    }

    @Override
    public ResponseObject<?> countTodoByTodoStatus(String id) {
        return new ResponseObject<>(maTodoProjectRepository.countTodoByTodoStatus(id), HttpStatus.OK, "Tìm thấy dữ liệu thành công");
    }


}
