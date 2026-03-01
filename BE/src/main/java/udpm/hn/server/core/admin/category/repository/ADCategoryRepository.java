package udpm.hn.server.core.admin.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import udpm.hn.server.core.admin.category.modal.request.ADCategoryRequest;
import udpm.hn.server.core.admin.category.modal.response.ADCategoryResponse;
import udpm.hn.server.entity.Category;
import udpm.hn.server.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

public interface ADCategoryRepository extends CategoryRepository {
    @Query(
            value = """
        SELECT c.id AS id,
               c.name AS categoryName,
               c.code AS categoryCode,
               c.status AS categoryStatus,
               c.createdDate AS createdDate
        FROM Category c
        WHERE
            (:#{#request.categoryName} IS NULL OR c.name LIKE CONCAT('%', :#{#request.categoryName}, '%')
            OR c.code LIKE CONCAT('%', :#{#request.categoryName}, '%'))
            AND (:#{#request.categoryStatus} IS NULL OR c.status = :#{#request.categoryStatus})
            order by c.createdDate desc 
        """,
            countQuery = """
        SELECT COUNT(c.id)
        FROM Category c
        WHERE
            (:#{#request.categoryName} IS NULL OR c.name LIKE CONCAT('%', :#{#request.categoryName}, '%'))
            AND (:#{#request.categoryStatus} IS NULL OR c.status = :#{#request.categoryStatus})
        """
    )
    Page<ADCategoryResponse> getAllCategories(Pageable pageable,ADCategoryRequest request);

    List<Category> findAllByName(String name);

    boolean existsByNameAndIdNot(String name, String id);

    @Query(
            value = """
                SELECT 
                    c.id AS id, 
                    c.code AS categoryCode, 
                    c.name AS categoryName, 
                    c.createdDate AS createdDate
                FROM Category c 
                WHERE c.id = :categoryId
                """
    )
    Optional<ADCategoryResponse> getDetailCategoryById(String categoryId);

}
