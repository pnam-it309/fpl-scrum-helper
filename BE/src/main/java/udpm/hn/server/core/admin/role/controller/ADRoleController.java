package udpm.hn.server.core.admin.role.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.core.admin.role.model.request.ADRoleRequest;
import udpm.hn.server.core.admin.role.service.ADRoleService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_ROLE)
@RequiredArgsConstructor
public class ADRoleController {

    private final ADRoleService roleService;

    @GetMapping
    public ResponseEntity<?> getAll(ADRoleRequest hoRoleRequest) {
        return Helper.createResponseEntity(roleService.getAllRole(hoRoleRequest));
    }

}
