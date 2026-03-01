package udpm.hn.server.infrastructure.job.student.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import udpm.hn.server.entity.HistoryImportStudent;
import udpm.hn.server.repository.HistoryImportStudentRepository;
import udpm.hn.server.repository.StaffRepository;
import udpm.hn.server.utils.UserContextHelper;

@Service
public class HistoryImportStudentService {
    @Autowired
    private HistoryImportStudentRepository historyImportStudentRepository;

    @Autowired
    private UserContextHelper userContextHelper;

    @Autowired
    private StaffRepository staffRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveHistory(String fullMessage) {
        String staffId = userContextHelper.getCurrentUserId();
        String staffEmail = staffRepository.getEmailFpt(staffId);

        HistoryImportStudent history = new HistoryImportStudent();
        history.setMessage(fullMessage);
        history.setEmail(staffEmail);
        historyImportStudentRepository.save(history);
    }
}
