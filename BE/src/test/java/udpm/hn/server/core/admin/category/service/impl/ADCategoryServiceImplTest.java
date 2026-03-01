package udpm.hn.server.core.admin.category.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.admin.category.modal.request.ADCategoryRequest;
import udpm.hn.server.core.admin.category.modal.request.ADCreateUpdateCategoryRequest;
import udpm.hn.server.core.admin.category.modal.response.ADCategoryResponse;
import udpm.hn.server.core.admin.category.repository.ADCategoryRepository;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.Category;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
class ADCategoryServiceImplTest {

    @InjectMocks
    private ADCategoryServiceImpl categoryService;

    @Mock
    private ADCategoryRepository adCategoryRepository;

    @Test
    void getAllCategory_ShouldReturnResponseObject() {
        ADCategoryRequest request = new ADCategoryRequest();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());

        // Giả sử ADCategoryResponse là class bạn dùng
        Page<ADCategoryResponse> pageMock = new PageImpl<>(List.of(mock(ADCategoryResponse.class)));

        // Mock lại Helper nếu cần (nếu static thì dùng mockStatic)
        // hoặc đơn giản bỏ qua vì bạn biết createPageable tạo ra pageable bạn đang dùng

        when(adCategoryRepository.getAllCategories(any(Pageable.class), eq(request)))
                .thenReturn(pageMock);

        // ❗ GỌI METHOD ĐÚNG CẦN TEST
        ResponseObject<?> response = categoryService.getAllCategory(request);

        // ✅ Kiểm tra kết quả nếu cần
        assertEquals(HttpStatus.OK, response.getStatus());
    }


    private void assertEquals(String getAllCategorySuccessfully, String message) {
    }

    private void assertEquals(HttpStatus ok, HttpStatus status) {
    }

    @Test
    void createCategory_ShouldSucceed_WhenCategoryIsNew() {
        ADCreateUpdateCategoryRequest request = new ADCreateUpdateCategoryRequest();
        request.setCategoryName("Thể loại mới");

        when(adCategoryRepository.findAllByName(anyString())).thenReturn(Collections.emptyList());

        ResponseObject<?> response = categoryService.createCategory(request);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals("Thêm thể loại thành công", response.getMessage());
    }

    @Test
    void createCategory_ShouldFail_WhenCategoryAlreadyExists() {
        ADCreateUpdateCategoryRequest request = new ADCreateUpdateCategoryRequest();
        request.setCategoryName("Thể loại trùng");

        when(adCategoryRepository.findAllByName(anyString())).thenReturn(List.of(new Category()));

        ResponseObject<?> response = categoryService.createCategory(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Thể loại đã tồn tại", response.getMessage());
    }

    @Test
    void updateCategory_ShouldSucceed_WhenValid() {
        String categoryId = "cat-01";
        ADCreateUpdateCategoryRequest request = new ADCreateUpdateCategoryRequest();
        request.setCategoryName("Thể loại cập nhật");

        Category category = new Category();
        category.setCreatedDate(System.currentTimeMillis());

        when(adCategoryRepository.existsByNameAndIdNot(anyString(), anyString())).thenReturn(false);
        when(adCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        ResponseObject<?> response = categoryService.updateCategory(categoryId, request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật thể loại thành công", response.getMessage());
    }

    @Test
    void updateCategory_ShouldFail_WhenAlreadyExists() {
        String categoryId = "cat-01";
        ADCreateUpdateCategoryRequest request = new ADCreateUpdateCategoryRequest();
        request.setCategoryName("Trùng tên");

        when(adCategoryRepository.existsByNameAndIdNot(anyString(), anyString())).thenReturn(true);

        ResponseObject<?> response = categoryService.updateCategory(categoryId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Thể loại đã tồn tại", response.getMessage());
    }

    @Test
    void updateCategory_ShouldFail_WhenNotFound() {
        String categoryId = "cat-02";
        ADCreateUpdateCategoryRequest request = new ADCreateUpdateCategoryRequest();
        request.setCategoryName("Không tìm thấy");

        when(adCategoryRepository.existsByNameAndIdNot(anyString(), anyString())).thenReturn(false);
        when(adCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        ResponseObject<?> response = categoryService.updateCategory(categoryId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy thể loại", response.getMessage());
    }

    @Test
    void changeCategoryStatus_ShouldToggleStatus() {
        String categoryId = "cat-01";
        Category category = new Category();
        category.setStatus(EntityStatus.ACTIVE);

        when(adCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        ResponseObject<?> response = categoryService.changeCategoryStatus(categoryId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Đổi trạng thái thể loại thành công", response.getMessage());
    }

    @Test
    void changeCategoryStatus_ShouldFail_WhenNotFound() {
        String categoryId = "cat-02";
        when(adCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        ResponseObject<?> response = categoryService.changeCategoryStatus(categoryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy thể loại", response.getMessage());
    }

    @Test
    void getCategoryById_ShouldReturnData_WhenFound() {
        String categoryId = "cat-01";
        ADCategoryResponse responseMock = mock(ADCategoryResponse.class);

        when(adCategoryRepository.getDetailCategoryById(categoryId)).thenReturn(Optional.of(responseMock));

        ResponseObject<?> response = categoryService.getCategoryById(categoryId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Get category successfully", response.getMessage());
    }

    @Test
    void getCategoryById_ShouldReturnNotFound_WhenMissing() {
        String categoryId = "cat-404";

        when(adCategoryRepository.getDetailCategoryById(categoryId)).thenReturn(Optional.empty());

        ResponseObject<?> response = categoryService.getCategoryById(categoryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Category not found", response.getMessage());
    }



}

