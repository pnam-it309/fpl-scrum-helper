package udpm.hn.server.core.admin.category.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.admin.category.modal.request.ADCategoryRequest;
import udpm.hn.server.core.admin.category.modal.request.ADCreateUpdateCategoryRequest;
import udpm.hn.server.core.admin.category.repository.ADCategoryRepository;
import udpm.hn.server.core.admin.category.service.ADCategoryService;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.Category;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.utils.Helper;

import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class ADCategoryServiceImpl implements ADCategoryService {

    private final ADCategoryRepository adCategoryRepository;

    @Override
    public ResponseObject<?> getAllCategory(ADCategoryRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(adCategoryRepository.getAllCategories( pageable,request)),
                HttpStatus.OK,
                "Get all category successfully"
        );
    }

    @Override
    public ResponseObject<?> createCategory(ADCreateUpdateCategoryRequest request) {

        List<Category> categoryOptional = adCategoryRepository.findAllByName(request.getCategoryName().trim());
        if (!categoryOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Thể loại đã tồn tại");
        }
        String code = Helper.generateCodeFromName(request.getCategoryName().trim());
        Category category = new Category();
        category.setCode(code);
        category.setName(Helper.replaceManySpaceToOneSpace(request.getCategoryName()));
        category.setCreatedDate(System.currentTimeMillis());
        category.setStatus(EntityStatus.ACTIVE);
        adCategoryRepository.save(category);

        return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm thể loại thành công");
    }

    @Override
    public ResponseObject<?> updateCategory(String categoryId, ADCreateUpdateCategoryRequest request) {
        if (adCategoryRepository.existsByNameAndIdNot((request.getCategoryName()), categoryId)) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Thể loại đã tồn tại");
        }

        Optional<Category> categoryOptional = adCategoryRepository.findById(categoryId);


        categoryOptional.map(category -> {
            category.setCode(Helper.generateCodeFromName(request.getCategoryName().trim()));
            category.setName(Helper.replaceManySpaceToOneSpace(  request.getCategoryName().trim()));
            category.setCreatedDate(category.getCreatedDate());
            category.setStatus(EntityStatus.ACTIVE);
            return adCategoryRepository.save(category);
        });

        return categoryOptional
                .map(subject -> new ResponseObject<>(null, HttpStatus.OK, "Cập nhật thể loại thành công"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thể loại"));
    }

    @Override
    public ResponseObject<?> changeCategoryStatus(String categoryId) {
        Optional<Category> categoryOptional = adCategoryRepository.findById(categoryId);

        categoryOptional.map(category -> {
            category.setName(category.getName());
            category.setCreatedDate(category.getCreatedDate());
            category.setStatus(category.getStatus() != EntityStatus.ACTIVE ? EntityStatus.ACTIVE : EntityStatus.INACTIVE);
            return adCategoryRepository.save(category);
        });

        return categoryOptional
                .map(subject -> new ResponseObject<>(null, HttpStatus.OK, "Đổi trạng thái thể loại thành công"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thể loại"));

    }

    @Override
    public ResponseObject<?> getCategoryById(String categoryId) {
        return adCategoryRepository.getDetailCategoryById(categoryId)
                .map(subject -> new ResponseObject<>(subject, HttpStatus.OK, "Get category successfully"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Category not found"));

    }
}
