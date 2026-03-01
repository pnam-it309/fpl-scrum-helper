package udpm.hn.server.core.admin.category.modal.request;

import lombok.Getter;
import lombok.Setter;
import udpm.hn.server.core.common.base.PageableRequest;
import udpm.hn.server.infrastructure.constant.EntityStatus;

@Getter
@Setter
public class ADCategoryRequest extends PageableRequest {

    private String categoryName;

    private EntityStatus categoryStatus;

}
