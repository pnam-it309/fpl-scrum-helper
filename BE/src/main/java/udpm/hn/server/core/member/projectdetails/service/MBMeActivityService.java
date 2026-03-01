package udpm.hn.server.core.member.projectdetails.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeFindActivityRequest;

public interface MBMeActivityService {

    ResponseObject<?> getAllActivityWhereIdTodo(final MBMeFindActivityRequest request);

    ResponseObject<?> getAllActivityWhereIdProject(final MBMeFindActivityRequest request);

    ResponseObject<?> countTotalActivitiesWhereIdProject(final MBMeFindActivityRequest request);

}
