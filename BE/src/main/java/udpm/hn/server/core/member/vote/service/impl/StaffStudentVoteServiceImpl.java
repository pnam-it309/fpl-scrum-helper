package udpm.hn.server.core.member.vote.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.todotable.model.response.TodoByPhaseProjectResponse;
import udpm.hn.server.core.member.vote.model.response.StaffStudentVoteResponse;
import udpm.hn.server.core.member.vote.repository.StaffStudentVoteRepository;
import udpm.hn.server.core.member.vote.service.StaffStudentVoteService;

@Service
@Slf4j
@RequiredArgsConstructor
public class StaffStudentVoteServiceImpl implements StaffStudentVoteService {

    private final StaffStudentVoteRepository staffStudentVoteRepository;
    @Override
    public ResponseObject<?> getStuentStaffVote(String id) {
        Long currentTime = System.currentTimeMillis();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<StaffStudentVoteResponse> results = staffStudentVoteRepository.getStaffStudentVote(pageable, id, currentTime);
        return new ResponseObject<>(
                PageableObject.of(results),
                HttpStatus.OK,
                "Lấy thành công danh sách thành viên bình chọn"
        );
    }
}
