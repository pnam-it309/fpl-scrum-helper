package udpm.hn.server.core.manage.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.manage.project.model.request.MaProjectCreateRequest;
import udpm.hn.server.core.manage.user.model.request.MAUserRequest;
import udpm.hn.server.core.manage.user.model.request.MaUserCreateUpdateRequest;
import udpm.hn.server.core.manage.user.service.MAUserService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequestMapping(MappingConstants.API_MANAGE_USER)
@RequiredArgsConstructor
public class MAUserController {

    private final MAUserService maUserService;

    @GetMapping("/{idProject}/{idPhase}")
    public ResponseEntity<?> getAllUser(@PathVariable String idProject,@PathVariable String idPhase, MAUserRequest request) {
        return Helper.createResponseEntity(maUserService.listUserProject(request, idProject, idPhase));
    }

    @PutMapping("/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        return Helper.createResponseEntity(maUserService.deleteUser(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable String id, @RequestBody MaUserCreateUpdateRequest request) {
        return Helper.createResponseEntity(maUserService.updateProjectMembers(id, request));
    }

    @GetMapping("/facility/{idProject}")
    public ResponseEntity<?> getAllUser(@PathVariable String idProject) {
        return Helper.createResponseEntity(maUserService.idFacility(idProject));
    }

}
