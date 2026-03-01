package udpm.hn.server.core.admin.category.service;

import jakarta.validation.Valid;
import udpm.hn.server.core.admin.category.modal.request.ADCategoryRequest;
import udpm.hn.server.core.admin.category.modal.request.ADCreateUpdateCategoryRequest;
import udpm.hn.server.core.common.base.ResponseObject;

public interface ADCategoryService {

    ResponseObject<?> getAllCategory(ADCategoryRequest request);

    ResponseObject<?> createCategory(@Valid ADCreateUpdateCategoryRequest request);

    ResponseObject<?> updateCategory(String categoryId, @Valid ADCreateUpdateCategoryRequest request);

    ResponseObject<?> changeCategoryStatus(String categoryId);

    ResponseObject<?> getCategoryById(String categoryId);

}
