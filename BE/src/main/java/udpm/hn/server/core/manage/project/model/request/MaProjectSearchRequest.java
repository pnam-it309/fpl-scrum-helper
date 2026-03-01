package udpm.hn.server.core.manage.project.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import udpm.hn.server.core.common.base.PageableRequest;

@Getter
@Setter
@ToString
public class MaProjectSearchRequest extends PageableRequest {

    private String search;

    private String nameDepartment;

    private Integer status;

    private String idUser;

}
