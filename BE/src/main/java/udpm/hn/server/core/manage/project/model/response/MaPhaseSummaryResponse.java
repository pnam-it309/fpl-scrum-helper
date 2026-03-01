package udpm.hn.server.core.manage.project.model.response;

import udpm.hn.server.utils.StatusPhase;

public interface MaPhaseSummaryResponse {

    Integer getAmount();

    StatusPhase getStatus();

}
