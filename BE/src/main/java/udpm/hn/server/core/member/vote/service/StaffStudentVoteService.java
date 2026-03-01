package udpm.hn.server.core.member.vote.service;

import udpm.hn.server.core.common.base.ResponseObject;

public interface StaffStudentVoteService {

    ResponseObject<?> getStuentStaffVote(String id);
}
