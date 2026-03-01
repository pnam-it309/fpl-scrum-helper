package udpm.hn.server.core.member.projectdetails.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.common.base.TodoObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeChangeCoverTodoRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateImageRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeDeleteImageRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeUpdateNameImageRequest;
import udpm.hn.server.core.member.projectdetails.model.response.MBUpdateBackgroundProject;
import udpm.hn.server.core.member.projectdetails.repository.MBMeActivityRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeImageRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeProjectRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeTodoRepository;
import udpm.hn.server.core.member.projectdetails.service.MBMeImageService;
import udpm.hn.server.entity.Activity;
import udpm.hn.server.entity.Image;
import udpm.hn.server.entity.Project;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.infrastructure.cloudinary.CloudinaryUploadImages;
import udpm.hn.server.infrastructure.constant.StatusImage;

import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MBMeImageServiceImpl implements MBMeImageService {

    private final SimpMessagingTemplate messagingTemplate;

    private final MBMeImageRepository mbMeImageRepository;

    private final MBMeTodoRepository mbMeTodoRepository;

    private final CloudinaryUploadImages cloudinaryUploadImages;

    private final MBMeActivityRepository mbMeActivityRepository;

    private final MBMeProjectRepository mbMeProjectRepository;

    @Override
    public ResponseObject<?> add(MBMeCreateImageRequest request, StompHeaderAccessor headerAccessor) {

        Optional<Todo> todoFind = mbMeTodoRepository.findById(request.getIdTodo());
        if(todoFind.isEmpty()){
            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"không tìm thấy todo");
        }
        Image image = new Image();
        image.setTodo(todoFind.get());
        image.setName(request.getNameFileOld());
        Integer countImage = mbMeImageRepository.countImageByIdTodo(request.getIdTodo());
        if (countImage == 0) {
            image.setStatusImage(StatusImage.COVER);
        } else {
            image.setStatusImage(StatusImage.NO_COVER);
        }
        image.setUrlImage(request.getUrlImage());
        Image newImage = mbMeImageRepository.save(image);
        if (countImage == 0) {

            if (!todoFind.isPresent()) {
               return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"Không tìm thấy todo");
            }
            todoFind.get().setImageId(newImage.getId());
            todoFind.get().setUrlImage(request.getUrlImage());
            mbMeTodoRepository.save(todoFind.get());
        }

        Activity activity = new Activity();
        activity.setProjectId(request.getProjectId());
        activity.setTodoListId(request.getIdTodoList());
        activity.setTodoId(request.getIdTodo());
        activity.setImageId(newImage.getId());
        activity.setMemberCreatedId(request.getIdUser());
        activity.setContentAction("đã thêm " + request.getNameFileOld() + " vào thẻ này");
        activity.setContentActionProject("đã thêm "+request.getNameFileOld()+" vào thẻ "+todoFind.get().getName());
        activity.setUrlImage(request.getUrlImage());
        activity.setUrl(request.getUrlPath());
        TodoObject todoObject = TodoObject.builder().data(newImage)
                .dataActivity(mbMeActivityRepository.save(activity))
                .idTodoList(request.getIdTodoList())
                .idTodo(request.getIdTodo()).build();
        messagingTemplate.convertAndSend("/topic/subscribe-activity","Thêm thành công file");

        return new ResponseObject<>(null,HttpStatus.OK,"add thành công file");
    }

    @Override
    public ResponseObject<?> uploadFile(MultipartFile file) {
        try {
            return new ResponseObject<>(cloudinaryUploadImages.uploadImage(file),
                    HttpStatus.OK,
                    "Update file ảnh thành công");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseObject<>(null,HttpStatus.INTERNAL_SERVER_ERROR,"Tải file that bai");
        }
    }

    @Override
    public ResponseObject<?> findById(String id) {
        return new ResponseObject<>(mbMeImageRepository.findById(id).get(),HttpStatus.OK,"Lấy ảnh thành công");
    }

    @Override
    public ResponseObject<?> getAllByIdTodo(String idTodo) {
        return new ResponseObject<>(mbMeImageRepository.getAllByIdTodo(idTodo),HttpStatus.OK,"Get all by idTodo");

    }

    @Override
    public ResponseObject<?> updateNameImage(MBMeUpdateNameImageRequest request, StompHeaderAccessor headerAccessor) {
        Optional<Image> imageFind = mbMeImageRepository.findById(request.getId());
        if (!imageFind.isPresent()) {
            new ResponseObject<>(null,HttpStatus.NOT_FOUND,"Không tìm thấy image");
        }
        imageFind.get().setName(request.getNameImage());
        return new ResponseObject<>(mbMeImageRepository.save(imageFind.get()),HttpStatus.OK,"Update thành công tên ảnh");
    }

    @Override
    public ResponseObject<?> deleteImage(MBMeDeleteImageRequest request, StompHeaderAccessor headerAccessor) {
        Optional<Todo> todoFind = mbMeTodoRepository.findById(request.getIdTodo());
        if (request.getStatusImage().equals("0")) {
            if (!todoFind.isPresent()) {
               return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"không tìm thấy todo");
            }
            todoFind.get().setImageId(null);
            todoFind.get().setUrlImage(null);
            mbMeTodoRepository.save(todoFind.get());
        }

//        String publicId = CloundinaryUtils.extractPublicId(request.getUrlImage());
//        cloudinaryUploadImages.deleteImage(publicId);

        Activity activityFind = mbMeActivityRepository.findActivityByIdImage(request.getId());
//        activityFind.setUrlImage(null);
//        activityFind.setImageId(null);
        mbMeActivityRepository.save(activityFind);

        Activity activity = new Activity();
        activity.setProjectId(request.getProjectId());
        activity.setTodoListId(request.getIdTodoList());
        activity.setTodoId(request.getIdTodo());
        activity.setMemberCreatedId(request.getIdUser());
        activity.setContentAction("đã xóa " + request.getNameImage() + " khỏi thẻ này");
        activity.setContentActionProject("đã xóa "+request.getNameImage()+" khỏi thẻ "+todoFind.get().getName());
        activity.setUrl(request.getUrlPath());
        Activity newActivity = mbMeActivityRepository.save(activity);

        mbMeImageRepository.deleteById(request.getId());

        TodoObject todoObject = TodoObject.builder().data(mbMeTodoRepository.save(todoFind.get()))
                .dataImage(request.getId())
                .dataActivity(newActivity)
                .idTodoList(request.getIdTodoList()).
                idTodo(request.getIdTodo()).build();

        messagingTemplate.convertAndSend("/topic/subscribe-activity","Xóa thành công ảnh");

        return new ResponseObject<>(todoObject,HttpStatus.OK,"");
    }

    @Override
    public ResponseObject<?> changeCoverTodo(MBMeChangeCoverTodoRequest request, StompHeaderAccessor headerAccessor) {
        Optional<Todo> todoFind = mbMeTodoRepository.findById(request.getIdTodo());
        if (!todoFind.isPresent()) {
            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"không tìm thấy todo");
        }
        Optional<Image> imageFind = mbMeImageRepository.findById(request.getIdImage());
        if (!imageFind.isPresent()) {
            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"không tìm thấy image");
        }
        if (request.getStatus().equals("0")) { // xóa khỏi cover
            todoFind.get().setImageId(null);
            todoFind.get().setUrlImage(null);
        } else {
            todoFind.get().setImageId(request.getIdImage());
            todoFind.get().setUrlImage(request.getUrlImage());
            mbMeImageRepository.updateCoverOld(request.getIdTodo());
        }
        if (imageFind.get().getStatusImage().equals(StatusImage.COVER)) {
            imageFind.get().setStatusImage(StatusImage.NO_COVER);
        } else {
            imageFind.get().setStatusImage(StatusImage.COVER);
        }
        Image newImage = mbMeImageRepository.save(imageFind.get());

        return new ResponseObject<>( mbMeTodoRepository.save(todoFind.get()),HttpStatus.OK,"changeCoverTodo");
    }

    @Override
    public ResponseObject<?> getBackgroundByIdProject(String idProject) {
        return new ResponseObject<>(mbMeImageRepository.getBackgroundByIdProject(idProject),HttpStatus.OK,"Get all by idTodo");
    }

    @Override
    public ResponseObject<?> updateBackgroundProject(MBUpdateBackgroundProject request) {
        Project projectFind = mbMeProjectRepository.findById(request.getIdProject()).orElse(null);
        projectFind.setBackgroundColor(request.getBackgroundColor());
        projectFind.setBackgroundImage(request.getBackgroundImage());

        return new ResponseObject<>(mbMeProjectRepository.save(projectFind),HttpStatus.OK,"update background project");
    }

}
