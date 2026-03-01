package udpm.hn.server.core.admin.student.service;

import jakarta.validation.Valid;
import udpm.hn.server.core.admin.department.department.model.request.ADCreateOrUpdateDepartmentRequest;
import udpm.hn.server.core.admin.staff.model.request.ADCreateStaffFDM;
import udpm.hn.server.core.admin.staff.model.request.ADUpdateStaffFDMRequest;
import udpm.hn.server.core.admin.student.model.request.ADCreateOrUpdateStudentRequest;
import udpm.hn.server.core.admin.student.model.request.ADCreateStudentFDMRequest;
import udpm.hn.server.core.admin.student.model.request.ADUpdateStudentFDMRequest;
import udpm.hn.server.core.admin.student.model.request.StudentRequest;
import udpm.hn.server.core.common.base.ResponseObject;

import java.io.FileNotFoundException;

public interface StudentService {

    ResponseObject<?> getAllStudnet(StudentRequest request);

    ResponseObject<?> findAllStudent();

    ResponseObject<?> addStudent(@Valid ADCreateOrUpdateStudentRequest request);

    ResponseObject<?> updateStudent(@Valid ADCreateOrUpdateStudentRequest request, String id);

    ResponseObject<?> detailStudent(String id);

    ResponseObject<?> studentByDepartmentFacility(String id);

    ResponseObject<?> deleteStudentByIdStudent(String id,String emailLogin);

    ResponseObject<?> createStudentByFDM(ADCreateStudentFDMRequest request);

    ResponseObject<?> updateStudentByFDM(ADUpdateStudentFDMRequest request);

    ResponseObject<?> deleteStudent(String id,String emailLogin);

    ResponseObject<?> readCSV( );

    ResponseObject<?> getLogsImportStudent(int page, int size) throws FileNotFoundException;

}
