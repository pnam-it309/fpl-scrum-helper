package udpm.hn.server.core.admin.staff.service;

import org.springframework.core.io.Resource;
import udpm.hn.server.core.admin.staff.model.request.ADCreateStaffFDM;
import udpm.hn.server.core.admin.staff.model.request.ADCreateStaffRequest;
import udpm.hn.server.core.admin.staff.model.request.ADStaffRequest;
import udpm.hn.server.core.admin.staff.model.request.ADUpdateStaffFDMRequest;
import udpm.hn.server.core.admin.staff.model.request.RoleStaffRequest;
import udpm.hn.server.core.common.base.ResponseObject;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ADStaffService {

    ResponseObject<?> getAllStaff(ADStaffRequest request);

    ResponseObject<?> getAllRole( );

    ResponseObject<?> createStaff(ADCreateStaffRequest request);

    ResponseObject<?> updateStaff(String id,ADCreateStaffRequest request);

    ResponseObject<?> deleteStaff(String id,String emailLogin);

    ResponseObject<?> detailStaff(String id);

    ResponseObject<?> staffByDepartmentFacility(String id);

    ResponseObject<?> getMajorByDepartment(String idDepartment);

    ResponseObject<?> getAllDepartmentByFacility(String idFacility);

    ResponseObject<?> createStaffByFDM(ADCreateStaffFDM request);

    ResponseObject<?> updateStaffByFDM(ADUpdateStaffFDMRequest request);

    ResponseObject<?> deleteStaffByFDM(String id,String emailLogin);

    ResponseObject<?> getRoleByStaff(String idStaff);

    ResponseObject<?> createUpdateRoleByStaff(RoleStaffRequest request);

    ResponseObject<?> deleteRoleByStaff(RoleStaffRequest request );

    ResponseObject<?> getAllStaffNoProject();

    ResponseObject<?> getStaffRoleByStaff(String idStaff);

    ResponseObject<?> getLogsImportStaff(int page, int size);

    Resource getAllCsv() throws IOException;

    ResponseObject<?> getAllStaffCount();

}