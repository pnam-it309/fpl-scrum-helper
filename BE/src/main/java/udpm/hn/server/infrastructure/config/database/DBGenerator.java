package udpm.hn.server.infrastructure.config.database;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffRole;

import udpm.hn.server.entity.TypeTodo;
import udpm.hn.server.infrastructure.constant.Role;

import udpm.hn.server.infrastructure.config.database.repository.DBGRoleRepository;
import udpm.hn.server.infrastructure.config.database.repository.DBGStaffRepository;
import udpm.hn.server.infrastructure.config.database.repository.DBGStaffRoleRepository;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.repository.TypeTodoRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Lazy(false)
public class DBGenerator {
    @Value("${db.generator.is-generated}")
    private String isGenerated;

    @Value("${db.generator.user-email}")
    private String userEmail;

    @Value("${db.generator.user-code}")
    private String userCode;

    @Value("${db.generator.user-name}")
    private String userName;

    private final DBGStaffRepository staffRepository;

    private final DBGRoleRepository roleRepository;

    private final DBGStaffRoleRepository staffRoleRepository;

    private final TypeTodoRepository typeTodoRepository;

    @PostConstruct
    public void init() {
        if ("true".equals(isGenerated)) {

            generateRole();
            generateData();

        }
    }

    private void generateData() {
        Optional<Staff> staffOptional = staffRepository.findByEmailFpt(userEmail);
        Staff staff;
        if (staffOptional.isEmpty()) {
            staff = new Staff();
            staff.setEmailFpt(userEmail);
            staff.setEmailFe(userEmail);
            staff.setCode(userCode);
            staff.setName(userName);
            staff.setStatus(EntityStatus.ACTIVE);
            staffRepository.save(staff);
        }else {
            staff = staffOptional.get();
        }

        addRoleToUser(staff, Role.ADMIN.name());


//        addRoleToUser(staff, "MANAGE");
//        addRoleToUser(staff, "MEMBER");
    }

    private void generateRole() {
        createRoleIfNotExist(Role.ADMIN.name(), Role.getVietnameseNameByRole(Role.ADMIN.name()));
        createRoleIfNotExist(Role.QUAN_LY.name(), Role.getVietnameseNameByRole(Role.QUAN_LY.name()));
        createRoleIfNotExist(Role.THANH_VIEN.name(), Role.getVietnameseNameByRole(Role.THANH_VIEN.name()));
    }

    private void createRoleIfNotExist(String roleCode,String roleName) {
        if (roleRepository.findByCode(roleCode).isEmpty()) {
            udpm.hn.server.entity.Role role = new udpm.hn.server.entity.Role();
            role.setCode(roleCode);
            role.setName(roleName);
            role.setStatus(EntityStatus.ACTIVE);
            roleRepository.save(role);
        }
    }

    private void addRoleToUser(Staff staff, String roleName) {
        Optional<udpm.hn.server.entity.Role> roleOptional = roleRepository.findByCode(roleName);

        if (roleOptional.isPresent()) {
            udpm.hn.server.entity.Role role = roleOptional.get();
            // Kiểm tra xem user đã có role này chưa
            if (!staffRoleRepository.existsByStaffAndRole(staff, role)) {
                StaffRole roleUser = new StaffRole();
                roleUser.setStaff(staff);
                roleUser.setRole(role);
                roleUser.setStatus(EntityStatus.ACTIVE);
                staffRoleRepository.save(roleUser);
            }
        }
    }

    private void genTypeTodo(){
        List<TypeTodo> types = List.of(
                new TypeTodo("TASK"),
                new TypeTodo("BUG"),
                new TypeTodo("IMPROVEMENT"),
                new TypeTodo("KHÁC")
        );
        typeTodoRepository.saveAll(types);

    }

}
