package udpm.hn.server.core.member.myproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.manage.project.model.request.MaProjectSearchRequest;
import udpm.hn.server.core.member.myproject.model.request.MBMyProjectSearchRequest;
import udpm.hn.server.core.member.myproject.model.response.MBMyProjectResponse;
import udpm.hn.server.infrastructure.constant.StatusProject;
import udpm.hn.server.repository.StaffProjectRepository;

import java.util.Optional;

@Repository
public interface MBMyProjectRepository extends StaffProjectRepository {

    @Query("""
   SELECT 
                row_number() over(order by p.id desc) as orderNumber,
                p.id as id,
                p.name as nameProject,
                p.code as codeProject,
                concat(d.name, ' - ', f.name) as nameDepartment,
                p.statusProject as status,
                f.id as idFacility,
                p.backgroundColor as backgroundColorProject,
                p.backgroundImage as backgroundImageProject,
                p.progress as progressProject,
                p.startTime as startTimeProject,
                p.endTime as endTimeProject,
                p.createdDate as createdDateProject
            FROM Project p
            JOIN MajorFacility mf ON p.majorFacility.id = mf.id
            JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
            JOIN Department d ON df.department.id = d.id
            JOIN Facility f ON df.facility.id = f.id
                WHERE p.status = 0 AND 
                 (:#{#request.search} IS NULL OR p.name LIKE %:#{#request.search}%
                    OR p.code LIKE %:#{#request.search}%)
                    AND (:#{#request.nameDepartment} IS NULL OR
                    d.name LIKE %:#{#request.nameDepartment}% OR
                    f.name LIKE %:#{#request.nameDepartment}% OR
                    CONCAT(d.name, '-', f.name) LIKE %:#{#request.nameDepartment}%)
                    AND (:#{#request.status} IS NULL OR p.statusProject = :status)  

""")
    Page<MBMyProjectResponse> getAllMyProject(MaProjectSearchRequest request, Pageable pageable,@Param("status") StatusProject status);

}
//
//WHERE (sp.staff.id = :#{#request.idUser}
//OR sp.student.id = :#{#request.idUser})
//AND (:#{#request.projectName} IS NULL OR p.name LIKE CONCAT('%', :#{#request.projectName}, '%'))
//AND (:#{#request.projectStatus} IS NULL OR p.statusProject = :#{#request.projectStatus})
//ORDER BY p.createdDate DESC