package udpm.hn.server.core.admin.category.modal.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ADCreateUpdateCategoryRequest {

    @NotBlank(message = "Tên thể loại không được để trống")
    private String categoryName;

}
