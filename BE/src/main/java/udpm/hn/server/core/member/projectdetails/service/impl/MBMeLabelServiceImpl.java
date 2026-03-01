package udpm.hn.server.core.member.projectdetails.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateLabelProjectRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeDeleteLabelProjectRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeUpdateLabelProjectRequest;
import udpm.hn.server.core.member.projectdetails.repository.MBMeLabelRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeProjectRepository;
import udpm.hn.server.core.member.projectdetails.service.MBMeLabelService;
import udpm.hn.server.entity.LabelProject;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MBMeLabelServiceImpl implements MBMeLabelService {

    private final MBMeLabelRepository mbMeLabelRepository;

    private final MBMeProjectRepository mbMeProjectRepository;

    @Override
    public ResponseObject<?> getAllLabelByIdTodo(String idTodo) {
        return new ResponseObject<>(
                mbMeLabelRepository.getAllLabelByIdTodo(idTodo),
                HttpStatus.OK,
                "Lay Thành công label thoe idTodo");
    }

    @Override
    public ResponseObject<?> getAllByIdProject(String idProject,String idTodo) {
        return new ResponseObject<>(
                mbMeLabelRepository.getAllByIdProject(idProject,idTodo),
                HttpStatus.OK,
                "Lay Thành công tất cả nhan theo idProject");
    }

    @Override
    public ResponseObject<?> detail(String id) {
        return null;
    }

    @Override
    public ResponseObject<?> create(MBMeCreateLabelProjectRequest request, StompHeaderAccessor headerAccessor) {
        LabelProject labelProject = new LabelProject();
        labelProject.setCode(String.valueOf(new Date().getTime()));
        labelProject.setName(request.getName());
        labelProject.setColor(request.getColor());
        labelProject.setStatus(EntityStatus.ACTIVE);
        labelProject.setProject(mbMeProjectRepository.findById(request.getProjectId()).get());
        LabelProject newLabelProject = mbMeLabelRepository.save(labelProject);
        return new ResponseObject<>(newLabelProject,HttpStatus.OK,"create thành công label Project");
    }

    @Override
    public ResponseObject<?> update(MBMeUpdateLabelProjectRequest request, StompHeaderAccessor headerAccessor) {
        Optional<LabelProject> labelProjectFind = mbMeLabelRepository.findById(request.getId());
        if (!labelProjectFind.isPresent()) {
            return new ResponseObject<>(null,HttpStatus.OK,"Label không tồn tại");
        }
        labelProjectFind.get().setName(request.getName());
        labelProjectFind.get().setColor(request.getColor());
        LabelProject newLabelProject = mbMeLabelRepository.save(labelProjectFind.get());
        return new ResponseObject<>(newLabelProject,HttpStatus.OK,"update thành công label Project");
    }

    @Override
    public ResponseObject<?> delete(MBMeDeleteLabelProjectRequest request, StompHeaderAccessor headerAccessor) {
        Optional<LabelProject> labelProjectFind = mbMeLabelRepository.findById(request.getIdLabelProject());
        if (!labelProjectFind.isPresent()) {
            return new ResponseObject<>(null,HttpStatus.OK,"Label không tồn tại");
        }
        labelProjectFind.get().setStatus(EntityStatus.INACTIVE);
        LabelProject newLabelProject = mbMeLabelRepository.save(labelProjectFind.get());
        return new ResponseObject<>(newLabelProject,HttpStatus.OK,"Xóa thành công label Project");
    }

    @Override
    public ResponseObject<?> getAllLabelSearchByIdProject(String idProject) {
        return new ResponseObject<>(
                mbMeLabelRepository.getAllLabelSearchByIdProject(idProject),
                HttpStatus.OK,
                "Lay Thành công tất cả nhan theo idProject");
    }


}
