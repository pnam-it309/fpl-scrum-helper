package udpm.hn.server.infrastructure.job.staff.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import udpm.hn.server.entity.HistoryImport;
import udpm.hn.server.entity.HistoryImportStudent;
import udpm.hn.server.repository.HistoryImportRepository;
import udpm.hn.server.repository.HistoryImportStudentRepository;
import udpm.hn.server.repository.StaffRepository;
import udpm.hn.server.utils.UserContextHelper;

@Service
public class HistoryImportStaffService {
    @Autowired
    private HistoryImportRepository historyImportRepository;

    @Autowired
    private UserContextHelper userContextHelper;

    @Autowired
    private StaffRepository staffRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveHistory(String fullMessage) {
        String staffId = userContextHelper.getCurrentUserId();
        String staffEmail = staffRepository.getEmailFpt(staffId);

        HistoryImport history = new HistoryImport();
        history.setMessage(fullMessage);
        history.setEmail(staffEmail);
        historyImportRepository.save(history);
    }

}
