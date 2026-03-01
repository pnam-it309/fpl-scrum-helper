package udpm.hn.server.core.member.projectdetails.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.ADCreateUpdateTypeTodoRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBJoinTypeTodoRequest;
import udpm.hn.server.core.member.projectdetails.repository.MBMeTodoRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeTypeTodoRepository;
import udpm.hn.server.core.member.projectdetails.service.MBMeTypeTodoService;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.entity.TypeTodo;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MBMeTypeTodoServiceImpl implements MBMeTypeTodoService {

    private final SimpMessagingTemplate messagingTemplate;

    private final MBMeTypeTodoRepository mbMeTypeTodoRepository;

    private final MBMeTodoRepository mbMeTodoRepository;


    @Override
    public ResponseObject<?> getAllTypeTodo() {
        return new ResponseObject<>(mbMeTypeTodoRepository.getAllTypeTodo(),
                HttpStatus.OK,
                "Get all category successfully"
        );
    }

    @Override
    public ResponseObject<?> createTypeTodo(ADCreateUpdateTypeTodoRequest request) {
        try {
            TypeTodo typeTodo = new TypeTodo();
            typeTodo.setType(request.getType());
            typeTodo.setStatus(EntityStatus.ACTIVE);

            TypeTodo savedType = mbMeTypeTodoRepository.save(typeTodo); // chờ lưu xong

            return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm loại công việc thành công");
        } catch (Exception e) {
            return new ResponseObject<>(null, HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi thêm loại công việc");
        }
    }


    @Override
    public ResponseObject<?> updateTypeTodo(String typeTodoId, ADCreateUpdateTypeTodoRequest request) {
//        if (adCategoryRepository.existsByNameAndIdNot((request.getCategoryName()), categoryId)) {
//            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Thể loại đã tồn tại");
//        }
//
//        Optional<Category> categoryOptional = adCategoryRepository.findById(categoryId);
//
//
//        categoryOptional.map(category -> {
//            category.setCode(Helper.generateCodeFromName(request.getCategoryName().trim()));
//            category.setName(Helper.replaceManySpaceToOneSpace(  request.getCategoryName().trim()));
//            category.setCreatedDate(category.getCreatedDate());
//            category.setStatus(EntityStatus.ACTIVE);
//            return adCategoryRepository.save(category);
//        });
//
//        return categoryOptional
//                .map(subject -> new ResponseObject<>(null, HttpStatus.OK, "Cập nhật thể loại thành công"))
//                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thể loại"));

        return null;
    }

    @Override
    public ResponseObject<?> changeTypeTodoStatus(ADCreateUpdateTypeTodoRequest request) {
        Optional<TypeTodo> categoryOptional = mbMeTypeTodoRepository.findById(request.getTypeId());

        categoryOptional.map(category -> {
            category.setType(category.getType());
            category.setCreatedDate(category.getCreatedDate());
            category.setStatus(category.getStatus() != EntityStatus.ACTIVE ? EntityStatus.ACTIVE : EntityStatus.INACTIVE);
            return mbMeTypeTodoRepository.save(category);
        });

        return categoryOptional
                .map(subject -> new ResponseObject<>(null, HttpStatus.OK, "Đổi trạng thái thể loại thành công"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thể loại"));

    }

    @Override
    public ResponseObject<?> getTypeTodoById(String typeTodoId) {
        return mbMeTypeTodoRepository.getTypeTodoById(typeTodoId)
                .map(subject -> new ResponseObject<>(subject, HttpStatus.OK, "Get category successfully"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Category not found"));
    }

    @Override
    public ResponseObject<?> joinTypeTodo(MBJoinTypeTodoRequest request) {
        Optional<Todo> todoFind = mbMeTodoRepository.findById(request.getIdTodo());
        if(todoFind.isEmpty()){
            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"không thấy ");
        }

        Optional<TypeTodo> typeFind = mbMeTypeTodoRepository.findById(request.getIdType());
        if(typeFind.isEmpty()){
            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"không thấy ");
        }

        todoFind.get().setTypeTodo(typeFind.get());
        mbMeTodoRepository.save(todoFind.get());
        return new ResponseObject<>(null,HttpStatus.OK,"join thành công");
    }

}
