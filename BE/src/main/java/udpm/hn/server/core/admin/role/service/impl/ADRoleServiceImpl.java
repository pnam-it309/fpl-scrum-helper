package udpm.hn.server.core.admin.role.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.admin.role.model.request.ADRoleRequest;
import udpm.hn.server.core.admin.role.model.response.ADRoleResponse;
import udpm.hn.server.core.admin.role.repository.ADRoleRepository;
import udpm.hn.server.core.admin.role.service.ADRoleService;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.utils.Helper;

@Service
@RequiredArgsConstructor
public class ADRoleServiceImpl implements ADRoleService {

    private final ADRoleRepository roleRepository;

    @Override
    public ResponseObject<?> getAllRole(ADRoleRequest roleRequest) {
        Pageable page = Helper.createPageable(roleRequest, "created_date");

        // Đồng bộ tên tham số
        String searchQuery = (roleRequest.getQ() != null) ? roleRequest.getQ().trim() : "";
        String department = (roleRequest.getDepartment() != null) ? roleRequest.getDepartment().trim() : "";

        // Gọi repository với tham số đúng
        Page<ADRoleResponse> roles = roleRepository.getAllRole(page, searchQuery, department);

        return new ResponseObject<>(roles, HttpStatus.OK, "Get all roles successfully");
    }



}
