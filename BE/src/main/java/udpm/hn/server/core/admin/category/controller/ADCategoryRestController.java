package udpm.hn.server.core.admin.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.admin.category.modal.request.ADCategoryRequest;
import udpm.hn.server.core.admin.category.modal.request.ADCreateUpdateCategoryRequest;
import udpm.hn.server.core.admin.category.service.ADCategoryService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_CATEGORY)
@RequiredArgsConstructor
public class ADCategoryRestController {

    private final ADCategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getAllCategory(ADCategoryRequest request) {
            return Helper.createResponseEntity(categoryService.getAllCategory(request));
    }

    @PostMapping("/add")
    public ResponseEntity<?> createCategory(@RequestBody ADCreateUpdateCategoryRequest request) {
        return Helper.createResponseEntity(categoryService.createCategory(request));
    }

    @PutMapping("/update/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable String categoryId, @RequestBody ADCreateUpdateCategoryRequest request) {
        return Helper.createResponseEntity(categoryService.updateCategory(categoryId, request));
    }

    @PutMapping("/{categoryId}/change-status")
    public ResponseEntity<?> changeStatusCategory(@PathVariable String categoryId) {
        return Helper.createResponseEntity(categoryService.changeCategoryStatus(categoryId));
    }

    @GetMapping("/detail/{categoryId}")
    public ResponseEntity<?> getByIdCategory(@PathVariable String categoryId) {
        return Helper.createResponseEntity(categoryService.getCategoryById(categoryId));
    }

}
