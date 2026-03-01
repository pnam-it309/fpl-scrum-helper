package udpm.hn.server.core.admin.project.model.request;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import udpm.hn.server.core.common.base.PageableRequest;

@Getter
@Setter
@ToString
public class ADProjectSearchRequest extends PageableRequest {

    private String search;

    private String nameDepartment;

    private Integer status;
}
