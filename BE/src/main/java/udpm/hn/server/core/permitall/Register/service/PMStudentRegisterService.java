package udpm.hn.server.core.permitall.Register.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.permitall.Register.model.request.PMCreateStudentRegisterRequest;

public interface PMStudentRegisterService {

    ResponseObject<?> registerStudent(PMCreateStudentRegisterRequest request);

}
