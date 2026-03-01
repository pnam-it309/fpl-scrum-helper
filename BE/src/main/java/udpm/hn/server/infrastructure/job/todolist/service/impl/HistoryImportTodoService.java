package udpm.hn.server.infrastructure.job.todolist.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import udpm.hn.server.entity.HistoryImportTodo;
import udpm.hn.server.entity.Project;
import udpm.hn.server.repository.HistoryImportTodoRepository;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.repository.StaffRepository;
import udpm.hn.server.utils.UserContextHelper;

import java.util.Optional;

@Service
public class HistoryImportTodoService {
    @Autowired
    private HistoryImportTodoRepository historyImportTodoRepository;

    @Autowired
    private UserContextHelper userContextHelper;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveHistory(String fullMessage, String idProject) {
        String staffId = userContextHelper.getCurrentUserId();
        String staffEmail = staffRepository.getEmailFpt(staffId);

        Optional<Project> optionalProject = projectRepository.findById(idProject);
        if (optionalProject.isEmpty()) {
            throw new RuntimeException("Project not found with ID: " + idProject);
        }

        Project project = optionalProject.get();
        HistoryImportTodo history = new HistoryImportTodo();
        history.setMessage(fullMessage);
        history.setEmail(staffEmail);
        history.setProject(project);
        historyImportTodoRepository.save(history);
    }
}
