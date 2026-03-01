package udpm.hn.server.core.member.myproject.service;

import udpm.hn.server.core.admin.category.modal.request.ADCategoryRequest;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaProjectSearchRequest;
import udpm.hn.server.core.member.myproject.model.request.MBMyProjectSearchRequest;

public interface MBMyProjectService {

    ResponseObject<?> getAllMyProject(MaProjectSearchRequest request);

}
