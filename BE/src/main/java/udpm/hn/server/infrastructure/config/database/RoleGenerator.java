package udpm.hn.server.infrastructure.config.database;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import udpm.hn.server.entity.Facility;
import udpm.hn.server.entity.Role;
import udpm.hn.server.infrastructure.config.database.repository.DBGFacilityRepository;
import udpm.hn.server.infrastructure.config.database.repository.DBGRoleRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleGenerator {

    private final DBGRoleRepository roleRepository;

    private final DBGFacilityRepository facilityRepository;

    @PostConstruct
    public void generate(){
        List<Facility> listFacilities = facilityRepository.findAll();
        for(Facility facility : listFacilities){
            List<Role> isHasRole = roleRepository.findAllByFacility(facility);
            if(isHasRole.isEmpty()){
                List<String> roleCodes = udpm.hn.server.infrastructure.constant.Role.getAllRoles();
                List<String> roleNames = List.of("ADMIN","Quản lý","Thành viên");
                for (int i = 0;i< roleCodes.size(); i++){
                    if(roleRepository.findByCodeAndNameAndFacility(
                            roleCodes.get(i),
                            roleNames.get(i),
                            facility
                    ).isEmpty()) {
                        Role role = new Role();
                        role.setCode(roleCodes.get(i));
                        role.setName(roleNames.get(i));
                        role.setFacility(facility);
                        roleRepository.save(role);
                    }
                }
            }
        }
    }
}
